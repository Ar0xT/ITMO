package server.app;

import common.commandsabstraction.CommandResult;
import common.network.CommandPacket;
import common.network.ResponsePacket;
import server.*;
import server.CommandProcessor;
import server.network.ConnectionAcceptor;
import server.network.RequestReader;
import server.network.ResponseSender;
import server.utilities.*;
import server.utilities.db.*;
import server.utilities.db.repository.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerApplication {
    private static final Logger LOGGER = Logger.getLogger(ServerApplication.class.getName());

    private final int port;
    private final CollectionManager collectionManager;
    private final PersistenceContext persistenceContext;
    private final ServerCommandRegistry commandRegistry;
    private final CommandProcessor commandProcessor;
    private volatile boolean running = true;

    public ServerApplication(int port) {
        this.port = port;
        this.collectionManager = new CollectionManager();

        DbConfig dbConfig = DbConfig.fromEnvOrDefaults();
        DatabaseClientProxy dbProxy = new DatabaseClientProxy(new PostgresConnectionProvider(dbConfig));
        UserRepository userRepository = new PostgresUserRepository(dbProxy);
        StudioRepository studioRepository = new PostgresStudioRepository();
        BandRepository bandRepository = new PostgresBandRepository(dbProxy, studioRepository);
        this.persistenceContext = new PersistenceContext(bandRepository, studioRepository, collectionManager, dbProxy);
        AuthService authService = new AuthService(userRepository);

        loadCollectionFromDb(bandRepository);
        this.commandRegistry = new ServerCommandRegistry(collectionManager, persistenceContext, authService);
        this.commandProcessor = new CommandProcessor(commandRegistry, authService);
    }

    private void loadCollectionFromDb(BandRepository bandRepository) {
        try {
            Map<String, common.models.MusicBand> bands = bandRepository.loadAll();
            collectionManager.replaceAll(bands);
            LOGGER.info("Loaded collection from DB. Size: " + collectionManager.size());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load collection from DB. Starting empty.", e);
        }
    }

    public void start() {
        ConnectionAcceptor connectionAcceptor = new ConnectionAcceptor();
        RequestReader requestReader = new RequestReader();
        ResponseSender responseSender = new ResponseSender();
        ForkJoinPool readPool = new ForkJoinPool();
        ExecutorService processPool = Executors.newCachedThreadPool();
        ExecutorService responsePool = Executors.newCachedThreadPool();
        BufferedReader serverConsoleReader = new BufferedReader(new InputStreamReader(System.in));

        LOGGER.info("Server started on port " + port + ". Server-only commands: exit_server");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(500);
            while (running) {
                handleServerConsole(serverConsoleReader);
                handleClientConnection(serverSocket, connectionAcceptor, requestReader, responseSender,
                        readPool, processPool, responsePool);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server failed to start or crashed.", e);
        } finally {
            shutdownPools(readPool, processPool, responsePool);
            LOGGER.info("Server has shut down.");
        }
    }

    private void handleServerConsole(BufferedReader reader) {
        try {
            if (reader.ready()) {
                String serverCommand = reader.readLine();
                if (serverCommand != null) {
                    serverCommand = serverCommand.trim();
                    if ("exit_server".equals(serverCommand)) {
                        LOGGER.info("Shutdown received.");
                        running = false;
                        return;
                    }
                    if (!serverCommand.isEmpty()) {
                        CommandResult r = commandProcessor.processServerCommand(serverCommand);
                        LOGGER.info("Server command '" + serverCommand + "' result: " + r.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to read server console command.", e);
        }
    }

    private void handleClientConnection(ServerSocket serverSocket, ConnectionAcceptor acceptor,
                                        RequestReader reader, ResponseSender sender,
                                        ForkJoinPool readPool, ExecutorService processPool, ExecutorService responsePool) {
        try {
            Socket clientSocket = acceptor.accept(serverSocket);
            if (clientSocket == null) return;
            LOGGER.info("New connection from " + clientSocket.getRemoteSocketAddress());
            readPool.submit(() -> handleClientRequest(clientSocket, reader, sender, processPool, responsePool));
        } catch (SocketTimeoutException ignored) {
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to accept client request.", e);
        }
    }

    private void handleClientRequest(Socket clientSocket, RequestReader reader, ResponseSender sender,
                                     ExecutorService processPool, ExecutorService responsePool) {
        try {
            CommandPacket commandPacket = reader.read(clientSocket);
            LOGGER.info("Request received: " + commandPacket.getCommandName());
            processPool.submit(() -> {
                CommandResult result = commandProcessor.processClientCommand(commandPacket);
                responsePool.submit(() -> {
                    try {
                        sender.send(clientSocket, new ResponsePacket(result));
                        LOGGER.info("Response sent for command: " + commandPacket.getCommandName());
                    } catch (Exception e) {
                        LOGGER.log(Level.WARNING, "Failed to send response.", e);
                    } finally {
                        closeQuietly(clientSocket);
                    }
                });
            });
        } catch (java.io.EOFException e) {
            LOGGER.info("Client disconnected before sending a complete request.");
            closeQuietly(clientSocket);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to read client request.", e);
            closeQuietly(clientSocket);
        }
    }

    private void shutdownPools(ForkJoinPool readPool, ExecutorService processPool, ExecutorService responsePool) {
        readPool.shutdown();
        processPool.shutdown();
        responsePool.shutdown();
        try {
            readPool.awaitTermination(5, TimeUnit.SECONDS);
            processPool.awaitTermination(5, TimeUnit.SECONDS);
            responsePool.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void closeQuietly(Socket socket) {
        try { socket.close(); } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close client socket.", e);
        }
    }
}