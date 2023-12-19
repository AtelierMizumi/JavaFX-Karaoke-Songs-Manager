package com.javafx.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
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

    private void createDatabaseAndTableIfNotExist() {
        try {
            String sqlSongList = "CREATE TABLE IF NOT EXISTS songlist (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "title VARCHAR(255) NOT NULL, " +
                    "artist VARCHAR(255), " +
                    "album VARCHAR(255), " +
                    "length VARCHAR(255), " +
                    "audiodata BLOB)";
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

    public List<Song> getAllSongs() {
        List<Song> SONGLIST = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM SONGLIST");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Song song = new Song();
                song.setId(resultSet.getInt("id"));
                song.setTitle(resultSet.getString("title"));
                song.setAlbum(resultSet.getString("album"));
                song.setArtist(resultSet.getString("artist"));
                song.setLength(resultSet.getString("length"));
                SONGLIST.add(song);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return SONGLIST;
    }
    public void addSong(Song song, String audioFilePath) {
        try {
            PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO SONGLIST (title, album, artist, length, audiodata) VALUES (?, ?, ?, ?, ?)");
            preparedStatement.setString(1, song.getTitle());
            preparedStatement.setString(2, song.getAlbum());
            preparedStatement.setString(3, song.getArtist());
            preparedStatement.setString(4, song.getLength());

            File audioFile = new File(audioFilePath);
            FileInputStream fis = new FileInputStream(audioFile);
            preparedStatement.setBinaryStream(5, fis, (int) audioFile.length());

            preparedStatement.executeUpdate();
        } catch (SQLException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}