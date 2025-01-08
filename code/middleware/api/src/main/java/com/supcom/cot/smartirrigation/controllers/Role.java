package com.supcom.cot.smartirrigation.controllers;

public enum Role {   //Different roles for the user which are defined by a permsision value
    Surfer(1L),   // regular user
    Administrator(2L); // administrator of the application

    private final long value;
    Role(long value){
        this.value = value;
    }

    public long getValue() {
        return value;
    }

}
