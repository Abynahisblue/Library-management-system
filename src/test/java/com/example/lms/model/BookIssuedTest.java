package com.example.lms.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookIssuedTest {

    @Test
    public void testConstructorAndGetters() {
        // Given
        String issueId = "123";
        String date = "2023-07-17";
        String patronId = "456";
        String bookId = "789";

        // When
        BookIssued bookIssued = new BookIssued(issueId, date, patronId, bookId);

        // Then
        assertEquals(issueId, bookIssued.getIssueId());
        assertEquals(date, bookIssued.getDate());
        assertEquals(patronId, bookIssued.getPatronId());
        assertEquals(bookId, bookIssued.getBookId());
    }

    @Test
    public void testSetters() {
        // Given
        BookIssued bookIssued = new BookIssued();
        String issueId = "123";
        String date = "2023-07-17";
        String patronId = "456";
        String bookId = "789";

        // When
        bookIssued.setIssueId(issueId);
        bookIssued.setDate(date);
        bookIssued.setPatronId(patronId);
        bookIssued.setBookId(bookId);

        // Then
        assertEquals(issueId, bookIssued.getIssueId());
        assertEquals(date, bookIssued.getDate());
        assertEquals(patronId, bookIssued.getPatronId());
        assertEquals(bookId, bookIssued.getBookId());
    }

    @Test
    public void testToString() {
        // Given
        String issueId = "123";
        String date = "2023-07-17";
        String patronId = "456";
        String bookId = "789";
        BookIssued bookIssued = new BookIssued(issueId, date, patronId, bookId);

        // When
        String expectedString = "BookIssued{" +
                "issueid='" + issueId + '\'' +
                ", date='" + date + '\'' +
                ", bookId='" + bookId + '\'' +
                ", patronId='" + patronId + '\'' +
                '}';

        // Then
        assertEquals(expectedString, bookIssued.toString());
    }
}
