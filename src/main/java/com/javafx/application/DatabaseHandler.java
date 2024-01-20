package com.javafx.application;

import com.javafx.application.DatabaseHandler;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHandler {
    private static DatabaseHandler handler = null;
    private static final String DB_URL = "jdbc:h2:file:./src/main/resources/db;user=thuanc177;password=Admin@123";
    private static Connection conn = null;
    private DatabaseHandler() {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(DB_URL);
            createDatabaseAndTableIfNotExist();

            // Add shutdown hook to close connection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }));
        } catch (SQLException e) {
            System.err.println("SQLException while setting up the database: " + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.err.println("ClassNotFoundException while setting up the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static DatabaseHandler getInstance() {
        if (handler == null) {
            handler = new DatabaseHandler();
        }
        return handler;
    }

    public Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = DriverManager.getConnection(DB_URL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }


    static void createDatabaseAndTableIfNotExist() {
        try {
            String sqlSongList = "CREATE TABLE IF NOT EXISTS songlist (" +
                    "id INT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "artist VARCHAR(255), " +
                    "album VARCHAR(255), " +
                    "length VARCHAR(255), " +
                    "audiopath VARCHAR(255))";
            PreparedStatement statementSongList = conn.prepareStatement(sqlSongList);
            statementSongList.execute();

            String sqlUserInfo = "CREATE TABLE IF NOT EXISTS userinfo (" +
                    "username VARCHAR(255) PRIMARY KEY, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "salt VARCHAR(255) NOT NULL, " +
                    "ADMIN_PRIVILEGE BOOLEAN NOT NULL DEFAULT FALSE)";
            PreparedStatement statementUserInfo = conn.prepareStatement(sqlUserInfo);
            statementUserInfo.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(String table, String[] columnNames, String[] values) throws SQLException {
        StringBuilder columns = new StringBuilder();
        StringBuilder vals = new StringBuilder();

        for (String columnName : columnNames) {
            columns.append(columnName).append(",");
        }

        for (String value : values) {
            vals.append("'").append(value).append("',");
        }

        columns = new StringBuilder(columns.substring(0, columns.length() - 1)); // remove last ","
        vals = new StringBuilder(vals.substring(0, vals.length() - 1)); // remove last ","

        String sql = "INSERT INTO " + table + " (" + columns + ") VALUES (" + vals + ")";

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.execute();
    }

    public ResultSet select(String table, String[] columnNames, String whereClause) throws SQLException {
        StringBuilder columns = new StringBuilder();

        for (String columnName : columnNames) {
            columns.append(columnName).append(",");
        }

        columns = new StringBuilder(columns.substring(0, columns.length() - 1)); // remove last ","

        String sql = "SELECT " + columns + " FROM " + table + " WHERE " + whereClause;

        PreparedStatement statement = conn.prepareStatement(sql);
        return statement.executeQuery();
    }

    public void update(String table, String[] columnNames, String[] values, String whereClause) throws SQLException {
        StringBuilder set = new StringBuilder();

        for (int i = 0; i < columnNames.length; i++) {
            set.append(columnNames[i]).append("='").append(values[i]).append("',");
        }

        set = new StringBuilder(set.substring(0, set.length() - 1)); // remove last ","

        String sql = "UPDATE " + table + " SET " + set + " WHERE " + whereClause;

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.execute();
    }

    public void delete(String table, String whereClause) throws SQLException {
        String sql = "DELETE FROM " + table + " WHERE " + whereClause;

        PreparedStatement statement = conn.prepareStatement(sql);
        statement.execute();
    }

    public List<Song> getAllSongs() throws SQLException {
        try {
            String sql = "SELECT * FROM songlist";
            PreparedStatement statement = conn.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            List<Song> songs = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String title = resultSet.getString("title");
                String artist = resultSet.getString("artist");
                String album = resultSet.getString("album");
                String length = resultSet.getString("length");
                songs.add(new Song(id, title, artist, album, length));
            }
            return songs;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    public String getSongPath(int id) throws SQLException {
        String sql = "SELECT audiopath FROM songlist WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("audiopath");
        }
        return null;
    }
    public void insertSong(Song song) throws SQLException {
        String sql = "INSERT INTO SONGLIST (id, title, album, artist, length, audiopath) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setInt(1, song.getId());
        statement.setString(2, song.getTitle());
        statement.setString(3, song.getAlbum());
        statement.setString(4, song.getArtist());
        statement.setString(5, song.getLength());
        statement.setString(6, song.getAudioPath());
        statement.execute();
    }

    public void deleteSong(int id) {
        try {
            String sql = "DELETE FROM songlist WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSongNoAudio(Song updatedSong) {
        try {
            String sql = "UPDATE songlist SET title = ?, album = ?, artist = ?, length = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, updatedSong.getTitle());
            statement.setString(2, updatedSong.getAlbum());
            statement.setString(3, updatedSong.getArtist());
            statement.setString(4, updatedSong.getLength());
            statement.setInt(5, updatedSong.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateSongWithNewAudio(Song updatedSong) {
        try {
            String sql = "UPDATE songlist SET title = ?, album = ?, artist = ?, length = ?, audiopath = ? WHERE id = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, updatedSong.getTitle());
            statement.setString(2, updatedSong.getAlbum());
            statement.setString(3, updatedSong.getArtist());
            statement.setString(4, updatedSong.getLength());
            statement.setString(5, updatedSong.getAudioPath());
            statement.setInt(6, updatedSong.getId());
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}