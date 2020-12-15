package com.example.aibasedattendencesystem.Utility;

public class AttendenceHistory {
    String date;
    boolean isPresent;

    public AttendenceHistory(String date, boolean isPresent) {
        this.date = date;
        this.isPresent = isPresent;
    }

    public boolean isPresent() {
        return isPresent;
    }
}
