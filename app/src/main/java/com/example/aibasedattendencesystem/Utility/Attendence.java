package com.example.aibasedattendencesystem.Utility;

import java.util.List;

public class Attendence {
    String date;
    List<String> presentStudents;


    public Attendence(String date, List<String> presentStudents) {
        this.date = date;
        this.presentStudents = presentStudents;
    }

    public String getDate() {
        return date;
    }

    public List<String> getPresentStudents() {
        return presentStudents;
    }
}
