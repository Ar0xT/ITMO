package server.utilities.db.repository;

import common.models.Coordinates;
import common.models.MusicBand;
import common.models.MusicGenre;
import common.models.Studio;
import server.utilities.db.BandInsertResult;
import server.utilities.db.DatabaseClientProxy;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostgresBandRepository implements BandRepository {
    private final DatabaseClientProxy db;
    private final StudioRepository studioRepository;

    public PostgresBandRepository(DatabaseClientProxy db, StudioRepository studioRepository) {
        this.db = db;
        this.studioRepository = studioRepository;
    }

    @Override
    public Map<String, MusicBand> loadAll() throws SQLException {
        return db.execute(connection -> {
            Map<String, MusicBand> bands = new HashMap<>();
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT b.band_key, b.id, b.name, b.coord_x, b.coord_y, b.creation_date, "
                            + "b.number_of_participants, b.singles_count, b.establishment_date, "
                            + "b.genre, b.owner_login, s.name AS studio_name "
                            + "FROM music_bands b LEFT JOIN studios s ON b.studio_id = s.id")) {
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String key = rs.getString("band_key");
                        bands.put(key, mapBand(rs));
                    }
                }
            }
            return bands;
        });
    }

    @Override
    public BandInsertResult insert(Connection connection, String key, MusicBand band, String ownerLogin) throws SQLException {
        Long studioId = studioRepository.findOrCreate(connection, band.getStudio() == null ? null : band.getStudio().getName());
        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO music_bands (band_key, name, coord_x, coord_y, number_of_participants, "
                        + "singles_count, establishment_date, genre, studio_id, owner_login) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, creation_date")) {
            stmt.setString(1, key);
            stmt.setString(2, band.getName());
            stmt.setDouble(3, band.getCoordinates().getX());
            stmt.setInt(4, band.getCoordinates().getY());
            stmt.setLong(5, band.getNumberOfParticipants());
            if (band.getSinglesCount() == null) stmt.setNull(6, java.sql.Types.INTEGER);
            else stmt.setInt(6, band.getSinglesCount());
            if (band.getEstablishmentDate() == null) stmt.setNull(7, java.sql.Types.DATE);
            else stmt.setDate(7, Date.valueOf(band.getEstablishmentDate()));
            if (band.getGenre() == null) stmt.setNull(8, java.sql.Types.VARCHAR);
            else stmt.setString(8, band.getGenre().name());
            if (studioId == null) stmt.setNull(9, java.sql.Types.BIGINT);
            else stmt.setLong(9, studioId);
            stmt.setString(10, ownerLogin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Timestamp created = rs.getTimestamp("creation_date");
                    java.util.Date creationDate = created == null ? new java.util.Date() : new java.util.Date(created.getTime());
                    return new BandInsertResult(rs.getLong("id"), creationDate);
                }
            }
            throw new SQLException("Insert failed to return generated id.");
        }
    }

    @Override
    public boolean update(Connection connection, long id, MusicBand band, String ownerLogin) throws SQLException {
        Long studioId = studioRepository.findOrCreate(connection, band.getStudio() == null ? null : band.getStudio().getName());
        try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE music_bands SET name = ?, coord_x = ?, coord_y = ?, number_of_participants = ?, "
                        + "singles_count = ?, establishment_date = ?, genre = ?, studio_id = ? "
                        + "WHERE id = ? AND owner_login = ?")) {
            stmt.setString(1, band.getName());
            stmt.setDouble(2, band.getCoordinates().getX());
            stmt.setInt(3, band.getCoordinates().getY());
            stmt.setLong(4, band.getNumberOfParticipants());
            if (band.getSinglesCount() == null) stmt.setNull(5, java.sql.Types.INTEGER);
            else stmt.setInt(5, band.getSinglesCount());
            if (band.getEstablishmentDate() == null) stmt.setNull(6, java.sql.Types.DATE);
            else stmt.setDate(6, Date.valueOf(band.getEstablishmentDate()));
            if (band.getGenre() == null) stmt.setNull(7, java.sql.Types.VARCHAR);
            else stmt.setString(7, band.getGenre().name());
            if (studioId == null) stmt.setNull(8, java.sql.Types.BIGINT);
            else stmt.setLong(8, studioId);
            stmt.setLong(9, id);
            stmt.setString(10, ownerLogin);
            return stmt.executeUpdate() == 1;
        }
    }

    @Override
    public int deleteByKey(Connection connection, String key, String ownerLogin) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM music_bands WHERE band_key = ? AND owner_login = ?")) {
            stmt.setString(1, key);
            stmt.setString(2, ownerLogin);
            return stmt.executeUpdate();
        }
    }

    @Override
    public int deleteByKeys(Connection connection, List<String> keys, String ownerLogin) throws SQLException {
        if (keys.isEmpty()) return 0;
        try (PreparedStatement stmt = connection.prepareStatement(
                "DELETE FROM music_bands WHERE owner_login = ? AND band_key = ANY (?)")) {
            stmt.setString(1, ownerLogin);
            Array keyArray = connection.createArrayOf("text", keys.toArray());
            stmt.setArray(2, keyArray);
            int deleted = stmt.executeUpdate();
            if (deleted != keys.size()) {
                throw new SQLException("Expected to delete " + keys.size() + " rows, but deleted " + deleted + ".");
            }
            return deleted;
        }
    }

    private MusicBand mapBand(ResultSet rs) throws SQLException {
        String name = rs.getString("name");
        double x = rs.getDouble("coord_x");
        int y = rs.getInt("coord_y");
        Long participants = rs.getLong("number_of_participants");
        Integer singles = rs.getObject("singles_count", Integer.class);
        Date establishmentDate = rs.getDate("establishment_date");
        java.time.LocalDate establishment = establishmentDate == null ? null : establishmentDate.toLocalDate();
        String genreText = rs.getString("genre");
        MusicGenre genre = genreText == null ? null : MusicGenre.valueOf(genreText);
        String studioName = rs.getString("studio_name");
        Studio studio = studioName == null ? null : new Studio(studioName);
        MusicBand band = new MusicBand(name, new Coordinates(x, y), participants, singles, establishment, genre, studio);
        band.setId(rs.getLong("id"));
        Timestamp created = rs.getTimestamp("creation_date");
        if (created != null) band.setCreationDate(new java.util.Date(created.getTime()));
        band.setOwnerLogin(rs.getString("owner_login"));
        return band;
    }
}