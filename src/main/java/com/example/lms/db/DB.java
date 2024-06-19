package com.example.lms.db;

import com.example.lms.model.Book;
import com.example.lms.model.BookIssued;
import com.example.lms.model.BookReturn;
import com.example.lms.model.Patron;

import java.util.LinkedList;

public class DB {
    public static LinkedList<Book> books = new LinkedList<>();
    public static LinkedList<BookIssued> bookIssued = new LinkedList<>();
    public static LinkedList<BookReturn> booksReturned = new LinkedList<>();
    public static LinkedList<Patron> patrons = new LinkedList<>();
}
