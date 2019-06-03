package com.programrabbit.checka;

import com.google.android.gms.maps.model.LatLng;

public class Price {
    String name, address, lastUpdate;
    LatLng latLng;
    Double price;

    public Price(String name, String address, String lastUpdate, LatLng latLng, Double price) {
        this.name = name;
        this.address = address;
        this.lastUpdate = lastUpdate;
        this.latLng = latLng;
        this.price = price;
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

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
