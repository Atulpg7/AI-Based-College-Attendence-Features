package com.example.aibasedattendencesystem.Utility;

import android.widget.TextView;

public class WelcomeSetter {

    public static void setWelcomeMsg(TextView textView,String username){
        String usName = "Welcome, "+username;
        textView.setText(usName);
    }
}
