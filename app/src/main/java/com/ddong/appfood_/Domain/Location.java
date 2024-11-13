package com.ddong.appfood_.Domain;

public class Location {
    private int Id;
    private String LOC;

    public Location() {
    }

    @Override
    public String toString() {
        return  LOC ;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getLOC() {
        return LOC;
    }

    public void setLOC(String LOC) {
        this.LOC = LOC;
    }
}
