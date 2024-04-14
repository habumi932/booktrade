package com.hangbui.booktrade;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Book implements Parcelable {
    private String bookId;
    private String ownerId;
    private String name;
    private String authors;
    private String genre;
    private String description;

    public Book() {
    }
    public Book(String bookId, String ownerId, String name, String authors, String genre, String description) {
        this.bookId = bookId;
        this.ownerId = ownerId;
        this.name = name;
        this.authors = authors;
        this.genre = genre;
        this.description = description;
    }

    protected Book(Parcel in) {
        bookId = in.readString();
        ownerId = in.readString();
        name = in.readString();
        authors = in.readString();
        genre = in.readString();
        description = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(bookId);
        parcel.writeString(ownerId);
        parcel.writeString(name);
        parcel.writeString(authors);
        parcel.writeString(genre);
        parcel.writeString(description);
    }
}
