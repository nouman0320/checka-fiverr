package com.programrabbit.checka;

import java.io.Serializable;

public class Rate implements Serializable {


    String rtgs, bond, date;

    public Rate(String rtgs, String bond, String date) {
        this.rtgs = rtgs;
        this.bond = bond;
        this.date = date;
    }

    public Rate(){

    }

    public String getRtgs() {
        return rtgs;
    }

    public void setRtgs(String rtgs) {
        this.rtgs = rtgs;
    }

    public String getBond() {
        return bond;
    }

    public void setBond(String bond) {
        this.bond = bond;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
