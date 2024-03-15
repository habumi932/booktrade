package com.hangbui.booktrade;

public class Book {
    private String id;
    private String ownerId;
    private String name;
    private String authors;
    private String genre;
    private String description;

    public Book() {
    }
    public Book(String id, String ownerId, String name, String authors, String genre, String description) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.authors = authors;
        this.genre = genre;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
