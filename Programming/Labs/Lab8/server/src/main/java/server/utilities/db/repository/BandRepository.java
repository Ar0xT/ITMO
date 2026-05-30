package server.utilities.db.repository;

import common.models.MusicBand;
import server.utilities.db.BandInsertResult;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface BandRepository {
    Map<String, MusicBand> loadAll() throws SQLException;
    BandInsertResult insert(Connection connection, String key, MusicBand band, String ownerLogin) throws SQLException;
    boolean update(Connection connection, long id, MusicBand band, String ownerLogin) throws SQLException;
    int deleteByKey(Connection connection, String key, String ownerLogin) throws SQLException;
    int deleteByKeys(Connection connection, List<String> keys, String ownerLogin) throws SQLException;
}