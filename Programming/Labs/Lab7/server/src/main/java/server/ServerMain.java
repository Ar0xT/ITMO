package server;

import common.commandsabstraction.CommandResult;
import common.network.CommandPacket;
import common.network.ResponsePacket;
import server.utilities.AuthService;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;
import server.utilities.db.DatabaseClientProxy;
import server.utilities.db.DbConfig;
import server.utilities.db.PostgresConnectionProvider;
import server.utilities.db.StudioRepository;
import server.utilities.db.UserRepository;

import java.io.BufferedReader;
import java.io.EOFException;
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

public class ServerMain {
    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        int port = 5000;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port <= 0 || port > 65535) {
                    throw new NumberFormatException("out of range");
                }
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid port: " + args[0] + ". Use value in range 1..65535.");
                return;
            }
        }

        CollectionManager collectionManager = new CollectionManager();

        DbConfig dbConfig = DbConfig.fromEnvOrDefaults();
        DatabaseClientProxy dbProxy = new DatabaseClientProxy(new PostgresConnectionProvider(dbConfig));
        UserRepository userRepository = new UserRepository(dbProxy);
        StudioRepository studioRepository = new StudioRepository();
        BandRepository bandRepository = new BandRepository(dbProxy, studioRepository);
        AuthService authService = new AuthService(userRepository);

        try {
            Map<String, common.models.MusicBand> bands = bandRepository.loadAll();
            collectionManager.replaceAll(bands);
            LOGGER.info("Loaded collection from DB. Size: " + collectionManager.size());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not load collection from DB. Starting empty.", e);
        }

        ServerCommandRegistry commandRegistry = new ServerCommandRegistry(collectionManager, bandRepository, authService);
        CommandProcessor commandProcessor = new CommandProcessor(commandRegistry, authService);
        ConnectionAcceptor connectionAcceptor = new ConnectionAcceptor();
        RequestReader requestReader = new RequestReader();
        ResponseSender responseSender = new ResponseSender();

        ForkJoinPool readPool = new ForkJoinPool();
        ExecutorService processPool = Executors.newCachedThreadPool();
        ExecutorService responsePool = Executors.newCachedThreadPool();

        boolean running = true;
        BufferedReader serverConsoleReader = new BufferedReader(new InputStreamReader(System.in));

        LOGGER.info("Server started on port " + port + ". Server-only commands: exit_server");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(500);

            while (running) {
                try {
                    if (serverConsoleReader.ready()) {
                        String serverCommand = serverConsoleReader.readLine();
                        if (serverCommand != null) {
                            serverCommand = serverCommand.trim();

                            if ("exit_server".equals(serverCommand)) {
                                LOGGER.info("Shutdown received.");
                                running = false;
                                continue;
                            }

                            if (!serverCommand.isEmpty()) {
                                CommandResult serverResult = commandProcessor.processServerCommand(serverCommand);
                                LOGGER.info("Server command '" + serverCommand + "' result: " + serverResult.getMessage());
                            }
                        }
                    }
                } catch (IOException e) {
                    LOGGER.log(Level.WARNING, "Failed to read server console command.", e);
                }


                try {
                    Socket clientSocket = connectionAcceptor.accept(serverSocket);
                    if (clientSocket == null) {
                        continue;
                    }

                    LOGGER.info("New connection from " + clientSocket.getRemoteSocketAddress());
                    readPool.submit(() -> {
                        try {
                            CommandPacket commandPacket = requestReader.read(clientSocket);
                            LOGGER.info("Request received: " + commandPacket.getCommandName());

                            processPool.submit(() -> {
                                CommandResult result = commandProcessor.processClientCommand(commandPacket);
                                responsePool.submit(() -> {
                                    try {
                                        responseSender.send(clientSocket, new ResponsePacket(result));
                                        LOGGER.info("Response sent for command: " + commandPacket.getCommandName());
                                    } catch (Exception e) {
                                        LOGGER.log(Level.WARNING, "Failed to send response.", e);
                                    } finally {
                                        try {
                                            clientSocket.close();
                                        } catch (IOException e) {
                                            LOGGER.log(Level.WARNING, "Failed to close client socket.", e);
                                        }
                                    }
                                });
                            });
                        } catch (EOFException e) {
                            LOGGER.info("Client disconnected before sending a complete request.");
                            closeQuietly(clientSocket);
                        } catch (Exception e) {
                            LOGGER.log(Level.WARNING, "Failed to read client request.", e);
                            closeQuietly(clientSocket);
                        }
                    });
                } catch (SocketTimeoutException ignored) {
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to accept client request.", e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Server failed to start or crashed.", e);
        }

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

        LOGGER.info("Server has shut down.");
    }

    private static void closeQuietly(Socket socket) {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to close client socket.", e);
        }
    }
}