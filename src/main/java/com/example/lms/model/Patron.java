package com.example.lms.model;

public class Patron {
    private String id;
    private String name;
    private String address;
    private String contact;

    public Patron(){

    }

    public Patron(String id, String name, String email, String contact) {
        this.id = id;
        this.name = name;
        this.address = email;
        this.contact = contact;
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
