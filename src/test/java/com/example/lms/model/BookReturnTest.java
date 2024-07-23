package com.example.lms.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BookReturnTest {

    @Test
    public void testConstructorAndGettersWithFine() {
        // Given
        String id = "1";
        String issuedDate = "2023-07-10";
        String returnedDate = "2023-07-17";
        float fine = 10.0f;

        // When
        BookReturn bookReturn = new BookReturn(id, issuedDate, returnedDate, fine);

        // Then
        assertEquals(id, bookReturn.getId());
        assertEquals(issuedDate, bookReturn.getIssuedDate());
        assertEquals(returnedDate, bookReturn.getReturnedDate());
        assertEquals(fine, bookReturn.getFine());
    }

    @Test
    public void testConstructorAndGettersWithoutFine() {
        // Given
        String id = "1";
        String issuedDate = "2023-07-10";
        String returnedDate = "2023-07-17";

        // When
        BookReturn bookReturn = new BookReturn(id, issuedDate, returnedDate);

        // Then
        assertEquals(id, bookReturn.getId());
        assertEquals(issuedDate, bookReturn.getIssuedDate());
        assertEquals(returnedDate, bookReturn.getReturnedDate());
    }

    @Test
    public void testSetters() {
        // Given
        BookReturn bookReturn = new BookReturn("1", "2023-07-10", "2023-07-17", 10.0f);
        String newId = "2";
        String newIssuedDate = "2023-07-11";
        String newReturnedDate = "2023-07-18";
        float newFine = 15.0f;

        // When
        bookReturn.setId(newId);
        bookReturn.setIssuedDate(newIssuedDate);
        bookReturn.setReturnedDate(newReturnedDate);
        bookReturn.setFine(newFine);

        // Then
        assertEquals(newId, bookReturn.getId());
        assertEquals(newIssuedDate, bookReturn.getIssuedDate());
        assertEquals(newReturnedDate, bookReturn.getReturnedDate());
        assertEquals(newFine, bookReturn.getFine());
    }
}
