package com.programrabbit.checka;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class Price implements Serializable {
    String name, address, lastUpdate;
    double lat;
    double lng;
    Double price;
    String uid;
    String uid_updated;
    int voteCount;

    ArrayList<String> comments = new ArrayList<>();


    ArrayList<String> positiveVoteUsers = new ArrayList<>();
    ArrayList<String> negativeVoteUsers = new ArrayList<>();

    public Price(String name, String address, double lat, double lng, Double price, String lu, String uid, int voteCount) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.price = price;
        this.lastUpdate = lu;
        this.uid = uid;
        this.voteCount = voteCount;

        positiveVoteUsers = new ArrayList<>();
        negativeVoteUsers = new ArrayList<>();
        comments = new ArrayList<>();
    }

    public Price(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid_updated() {
        return uid_updated;
    }

    public void setUid_updated(String uid_updated) {
        this.uid_updated = uid_updated;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public ArrayList<String> getPositiveVoteUsers() {
        return positiveVoteUsers;
    }

    public void setPositiveVoteUsers(ArrayList<String> positiveVoteUsers) {
        this.positiveVoteUsers = positiveVoteUsers;
    }

    public ArrayList<String> getNegativeVoteUsers() {
        return negativeVoteUsers;
    }

    public void setNegativeVoteUsers(ArrayList<String> negativeVoteUsers) {
        this.negativeVoteUsers = negativeVoteUsers;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
