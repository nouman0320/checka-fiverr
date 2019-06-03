package com.programrabbit.checka;

import com.google.android.gms.maps.model.LatLng;

public class Service {
    String name, address;
    int problemLevel;
    int serviceType;
    LatLng latLng;
    String lastUpdate;


    public Service(String name, String address, int problemLevel, int serviceType, LatLng latLng, String lastUpdate) {
        this.name = name;
        this.address = address;
        this.problemLevel = problemLevel;
        this.serviceType = serviceType;
        this.latLng = latLng;
        this.lastUpdate = lastUpdate;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
