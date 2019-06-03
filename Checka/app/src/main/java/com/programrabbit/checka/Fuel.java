package com.programrabbit.checka;

import com.google.android.gms.maps.model.LatLng;

public class Fuel {
    String name;
    String address;
    LatLng latLng;
    Boolean availabe;
    String lastUpdate;

    public Fuel(String name, String address, LatLng latLng, Boolean availabe, String lu) {
        this.name = name;
        this.address = address;
        this.latLng = latLng;
        this.availabe = availabe;
        this.lastUpdate = lu;
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

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
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
