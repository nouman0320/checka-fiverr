package com.programrabbit.checka;

public class User {
    String name, address, email, mobile_number;

    public User(String name, String address, String email, String mobile_number) {
        this.name = name;
        this.address = address;
        this.email = email;
        this.mobile_number = mobile_number;
    }

    public User() { }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_number() {
        return mobile_number;
    }

    public void setMobile_number(String mobile_number) {
        this.mobile_number = mobile_number;
    }
}
