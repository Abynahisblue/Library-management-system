package com.example.lms.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    public void testConstructorAndGetters() {
        // Given
        String id = "1";
        String author = "John Doe";
        String title = "JUnit Testing";
        String status = "Available";

        // When
        Book book = new Book(id, author, title, status);

        // Then
        assertEquals(id, book.getId());
        assertEquals(author, book.getAuthor());
        assertEquals(title, book.getTitle());
        assertEquals(status, book.getStatus());
    }

    @Test
    public void testSetters() {
        // Given
        Book book = new Book();
        String id = "1";
        String author = "John Doe";
        String title = "JUnit Testing";
        String status = "Available";

        // When
        book.setId(id);
        book.setAuthor(author);
        book.setTitle(title);
        book.setStatus(status);

        // Then
        assertEquals(id, book.getId());
        assertEquals(author, book.getAuthor());
        assertEquals(title, book.getTitle());
        assertEquals(status, book.getStatus());
    }

    @Test
    public void testToString() {
        // Given
        String id = "1";
        String author = "John Doe";
        String title = "JUnit Testing";
        String status = "Available";
        Book book = new Book(id, author, title, status);

        // When
        String expectedString = "Book{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", status='" + status + '\'' +
                '}';

        // Then
        assertEquals(expectedString, book.toString());
    }
}
