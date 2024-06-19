package com.example.lms.model;

public class BookReturn {
    private String id;
    private String issuedDate;
    private String returnedDate;
    private String fine;
    private String issueId;

    public BookReturn(String id, String issuedDate, String returnedDate, String fine, String issueId) {
        this.id = id;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;
        this.fine = fine;
        this.issueId = issueId;
    }

    public String getFine() {
        return fine;
    }

    public void setFine(String fine) {
        this.fine = fine;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public BookReturn(String id, String issuedDate, String returnedDate) {
        this.id = id;
        this.issuedDate = issuedDate;
        this.returnedDate = returnedDate;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(String returnedDate) {
        this.returnedDate = returnedDate;
    }
}
