package com.hangbui.booktrade;

public class TradeRequest {
    private String requestId;
    private String senderId;
    private String senderName;
    private String senderUniversity;
    private String receiverId;
    private String bookId;
    private String status;

    public TradeRequest() {
    }

    public TradeRequest(String requestId, String senderId, String senderName, String senderUniversity, String receiverId, String bookId, String status) {
        this.requestId = requestId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderUniversity = senderUniversity;
        this.receiverId = receiverId;
        this.bookId = bookId;
        this.status = status;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderUniversity() {
        return senderUniversity;
    }

    public void setSenderUniversity(String senderUniversity) {
        this.senderUniversity = senderUniversity;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
