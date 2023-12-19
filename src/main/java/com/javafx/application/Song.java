package com.javafx.application;

public class Song {
    private int id;
    private String title;
    private String album;
    private String artist;
    private String length;

    public Song(int id, String title, String album, String artist, String length) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.length = length;
    }

    public Song() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }
}