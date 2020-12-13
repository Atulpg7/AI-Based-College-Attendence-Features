package com.example.aibasedattendencesystem.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.aibasedattendencesystem.Activities.MainActivity;

public class Logout {

    public static void logout(Activity activity) {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Constant.myPrefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(Constant.isLoggedIn, false);
        editor.apply();
        activity.startActivity(new Intent(activity, MainActivity.class));
        activity.finish();
    }
}
