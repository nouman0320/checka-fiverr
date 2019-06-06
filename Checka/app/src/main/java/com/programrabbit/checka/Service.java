package com.programrabbit.checka;

import com.google.android.gms.maps.model.LatLng;

public class Service {
    String name, address;
    int problemLevel;
    int serviceType;
    double lat;
    double lng;
    String lastUpdate;
    String uid;
    int voteCount;


    public Service(String name, String address, int problemLevel, int serviceType, LatLng latLng, String lastUpdate, String uid, int voteCount) {
        this.name = name;
        this.address = address;
        this.problemLevel = problemLevel;
        this.serviceType = serviceType;
        this.lat = latLng.latitude;
        this.lng = latLng.longitude;
        this.lastUpdate = lastUpdate;
        this.uid = uid;
        this.voteCount = voteCount;
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

    public Service(){}

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(int voteCount) {
        this.voteCount = voteCount;
    }

    public int getServiceType() {
        return serviceType;
    }

    public void setServiceType(int serviceType) {
        this.serviceType = serviceType;
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

    public int getProblemLevel() {
        return problemLevel;
    }

    public void setProblemLevel(int problemLevel) {
        this.problemLevel = problemLevel;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
