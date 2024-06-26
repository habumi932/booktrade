package com.hangbui.booktrade;

public class Constants {
    // Extras
    public static final String EXTRA_CURRENT_USER = "com.hangbui.booktrade.EXTRA_CURRENT_USER";
    public static final String EXTRA_BOOKS = "com.hangbui.booktrade.EXTRA_BOOKS";
    public static final String EXTRA_FRIEND_IDS = "com.hangbui.booktrade.EXTRA_FRIEND_IDS";
    public static final String EXTRA_FRIEND_REQUESTS_IDS = "com.hangbui.booktrade.EXTRA_FRIEND_REQUESTS_IDS";

    // Users table
    public static final String USERS_TABLE = "users";
    public static final String USERS_TABLE_COL_ID = "id";
    public static final String USERS_TABLE_COL_EMAIL = "email";
    public static final String USERS_TABLE_COL_NAME = "name";
    public static final String USERS_TABLE_COL_PHOTO_URL = "photoUrl";
    public static final String USERS_TABLE_COL_UNIVERSITY = "university";

    // Books table
    public static final String BOOKS_TABLE = "books";
    public static final String BOOKS_TABLE_COL_BOOK_ID = "bookId";
    public static final String BOOKS_TABLE_COL_OWNER_ID = "ownerId";
    public static final String BOOKS_TABLE_COL_NAME = "name";
    public static final String BOOKS_TABLE_COL_AUTHORS = "authors";
    public static final String BOOKS_TABLE_COL_GENRE = "genre";
    public static final String BOOKS_TABLE_COL_DESCRIPTION = "description";

    // Book requests table
    public static final String BOOK_REQUESTS_TABLE = "book_requests";
    public static final String BOOK_REQUESTS_TABLE_COL_BOOK_ID = "bookId";
    public static final String BOOK_REQUESTS_TABLE_COL_SENDER_ID = "senderId";
    public static final String BOOK_REQUESTS_TABLE_COL_SENDER_NAME = "senderName";
    public static final String BOOK_REQUESTS_TABLE_COL_SENDER_UNIVERSITY = "senderUniversity";
    public static final String BOOK_REQUESTS_TABLE_COL_RECEIVER_ID = "receiverId";
    public static final String BOOK_REQUESTS_TABLE_COL_STATUS = "status";

    // Book request status
    public static final String BOOK_REQUEST_STATUS_REQUESTED = "REQUESTED";


    // Friendship table
    public static final String FRIENDSHIPS_TABLE = "friendships";
    public static final String FRIENDSHIPS_TABLE_COL_SENDER_ID = "senderId";
    public static final String FRIENDSHIPS_TABLE_COL_RECEIVER_ID = "receiverId";
    public static final String FRIENDSHIPS_TABLE_COL_STATUS = "status";

    // Friendship status
    public static final String FRIENDSHIP_STATUS_RECEIVED = "RECEIVED";
    public static final String FRIENDSHIP_STATUS_ACCEPTED = "ACCEPTED";
    public static final String FRIENDSHIP_STATUS_REQUESTED = "REQUESTED";

    // MISC
    public static final String PADDING = "   ";
}

