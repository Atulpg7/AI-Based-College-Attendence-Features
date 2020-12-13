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

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getPresentStudents() {
        return presentStudents;
    }

    public void setPresentStudents(List<String> presentStudents) {
        this.presentStudents = presentStudents;
    }
}
