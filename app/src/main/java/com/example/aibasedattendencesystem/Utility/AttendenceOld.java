package com.example.aibasedattendencesystem.Utility;

import java.util.List;

public class AttendenceOld {
    String date;
    boolean isPresent;

    public AttendenceOld(String date, boolean isPresent) {
        this.date = date;
        this.isPresent = isPresent;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
