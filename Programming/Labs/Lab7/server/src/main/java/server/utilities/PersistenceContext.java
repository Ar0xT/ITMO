package server.utilities;

import common.models.MusicBand;
import server.utilities.db.BandInsertResult;
import server.utilities.db.repository.BandRepository;
import server.utilities.db.DatabaseClientProxy;
import server.utilities.db.repository.StudioRepository;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Synchronization point between the in-memory collection and the database.
 * All modification operations are registered as pending and applied atomically via flush().
 * The PostgreSQL transaction is executed first; only upon success is the in-memory collection updated.
 * If the database transaction fails, the in-memory state remains unchanged.
 *
 *
 * while flush() is serialized by a ReentrantLock to prevent concurrent DB transactions.
 */
public class PersistenceContext {
    private final BandRepository bandRepository;
    private final StudioRepository studioRepository;
    private final CollectionManager collectionManager;
    private final DatabaseClientProxy dbProxy;

    private final ThreadLocal<List<DatabaseOperation>> pendingDbOps = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<Runnable>> pendingMemOps = ThreadLocal.withInitial(ArrayList::new);

    private final ReentrantLock lock = new ReentrantLock();

    public PersistenceContext(BandRepository bandRepository,
                              StudioRepository studioRepository,
                              CollectionManager collectionManager,
                              DatabaseClientProxy dbProxy) {
        this.bandRepository = bandRepository;
        this.studioRepository = studioRepository;
        this.collectionManager = collectionManager;
        this.dbProxy = dbProxy;
    }

    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    public MusicBand getBandById(long id) {
        return collectionManager.getBandById(id);
    }

    public boolean containsKey(String key) {
        return collectionManager.containsKey(key);
    }

    public void insert(String key, MusicBand sourceBand, String owner) {
        if (collectionManager.containsKey(key)) {
            throw new IllegalStateException("Key '" + key + "' already exists.");
        }
        MusicBand managedCopy = MusicBand.createServerManagedCopy(sourceBand);
        pendingDbOps.get().add(connection -> {
            BandInsertResult result = bandRepository.insert(connection, key, sourceBand, owner);
            managedCopy.setId(result.id());
            managedCopy.setCreationDate(result.creationDate());
            managedCopy.setOwnerLogin(owner);
        });
        pendingMemOps.get().add(() -> collectionManager.add(key, managedCopy));
    }

    public void update(long id, MusicBand newBandData, String owner) {
        MusicBand existing = collectionManager.getBandById(id);
        if (existing == null) {
            throw new IllegalStateException("No band found with ID " + id);
        }
        if (existing.getOwnerLogin() == null || !existing.getOwnerLogin().equals(owner)) {
            throw new IllegalStateException("Permission denied: you do not own band " + id + ".");
        }
        MusicBand managedCopy = MusicBand.createServerManagedCopy(newBandData);
        managedCopy.setId(id);
        managedCopy.setCreationDate(existing.getCreationDate());
        managedCopy.setOwnerLogin(owner);

        pendingDbOps.get().add(connection -> {
            if (!bandRepository.update(connection, id, newBandData, owner)) {
                throw new SQLException("Update failed - permission denied or band not found in DB.");
            }
        });
        pendingMemOps.get().add(() -> {
            for (Map.Entry<String, MusicBand> entry : collectionManager.snapshotEntries()) {
                if (entry.getValue().getId() == id) {
                    collectionManager.add(entry.getKey(), managedCopy);
                    break;
                }
            }
        });
    }

    public void deleteByKey(String key, String owner) {
        MusicBand band = collectionManager.get(key);
        if (band == null) {
            throw new IllegalStateException("No element found with key '" + key + "'.");
        }
        if (band.getOwnerLogin() == null || !band.getOwnerLogin().equals(owner)) {
            throw new IllegalStateException("Permission denied: you do not own key '" + key + "'.");
        }
        pendingDbOps.get().add(connection -> {
            if (bandRepository.deleteByKey(connection, key, owner) == 0) {
                throw new SQLException("Permission denied or band not found in DB.");
            }
        });
        pendingMemOps.get().add(() -> collectionManager.remove(key));
    }

    public void deleteByKeys(List<String> keys, String owner) {
        if (keys.isEmpty()) return;
        pendingDbOps.get().add(connection -> bandRepository.deleteByKeys(connection, keys, owner));
        pendingMemOps.get().add(() -> collectionManager.removeKeys(keys));
    }

    /**
     * Applies all pending operations atomically: PostgreSQL transaction first, then in-memory update.
     * Pending operations are always cleared (committed or rolled back).
     */
    public void flush() throws SQLException {
        lock.lock();
        try {
            List<DatabaseOperation> dbOps = pendingDbOps.get();
            List<Runnable> memOps = pendingMemOps.get();
            if (dbOps.isEmpty() && memOps.isEmpty()) return;

            dbProxy.executeInTransaction(connection -> {
                for (DatabaseOperation op : dbOps) op.execute(connection);
                return null;
            });
            for (Runnable op : memOps) op.run();
        } finally {
            clear();
            lock.unlock();
        }
    }

    public void clear() {
        pendingDbOps.get().clear();
        pendingMemOps.get().clear();
    }

    @FunctionalInterface
    private interface DatabaseOperation {
        void execute(Connection connection) throws SQLException;
    }
}