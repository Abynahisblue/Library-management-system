package com.example.lms.model;

public class BookIssued {
    private String issueId;
    private String date;
    private String patronId;
    private String bookId;

    public BookIssued(String issueId, String date, String userId, String bookId) {
        this.issueId = issueId;
        this.date = date;
        this.patronId = userId;
        this.bookId = bookId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getPatronId() {
        return patronId;
    }

    public void setPatronId(String patronId) {
        this.patronId = patronId;
    }
}
