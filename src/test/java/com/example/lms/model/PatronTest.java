package com.example.lms.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatronTest {

    private Patron patron;

    @BeforeEach
    public void setUp() {
        // This method will be executed before each test
        patron = new Patron();
    }

    @AfterEach
    public void tearDown() {
        // This method will be executed after each test
        patron = null;
    }

    @Test
    public void testConstructorAndGetters() {
        // Given
        String id = "123";
        String name = "Jane Doe";
        String address = "123 Main St";
        String contact = "555-1234";

        // When
        Patron patron = new Patron(id, name, address, contact);

        // Then
        assertEquals(id, patron.getId());
        assertEquals(name, patron.getName());
        assertEquals(address, patron.getAddress());
        assertEquals(contact, patron.getContact());
    }

    @Test
    public void testSetters() {
        // Given
        String id = "123";
        String name = "Jane Doe";
        String address = "123 Main St";
        String contact = "555-1234";

        // When
        patron.setId(id);
        patron.setName(name);
        patron.setAddress(address);
        patron.setContact(contact);

        // Then
        assertEquals(id, patron.getId());
        assertEquals(name, patron.getName());
        assertEquals(address, patron.getAddress());
        assertEquals(contact, patron.getContact());
    }

}