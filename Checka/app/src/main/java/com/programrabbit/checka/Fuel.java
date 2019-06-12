package com.programrabbit.checka;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

public class Fuel implements Serializable {
    String name;
    String address;
    double lat;
    double lng;
    Boolean availabe;
    String lastUpdate;
    String uid;
    String uid_updated;
    int voteCount;

    ArrayList<String> positiveVoteUsers = new ArrayList<>();
    ArrayList<String> negativeVoteUsers = new ArrayList<>();
    ArrayList<String> comments = new ArrayList<>();

    public Fuel(String name, String address, double lat, double lng, Boolean availabe, String lu, String uid, int voteCount) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.availabe = availabe;
        this.lastUpdate = lu;
        this.uid = uid;
        this.voteCount = voteCount;

        positiveVoteUsers = new ArrayList<>();
        negativeVoteUsers = new ArrayList<>();
        comments = new ArrayList<>();
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

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
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

    public Fuel(){}

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

    public Boolean getAvailabe() {
        return availabe;
    }

    public void setAvailabe(Boolean availabe) {
        this.availabe = availabe;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
