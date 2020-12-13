package com.example.aibasedattendencesystem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aibasedattendencesystem.R;
import com.example.aibasedattendencesystem.Server.Config;
import com.example.aibasedattendencesystem.Utility.Attendence;
import com.example.aibasedattendencesystem.Utility.Constant;
import com.example.aibasedattendencesystem.Utility.Logout;
import com.example.aibasedattendencesystem.Utility.SMS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentArea extends AppCompatActivity {

    RelativeLayout mainLL;
    SharedPreferences sharedPreferences;
    List<Attendence> attendenceListMain;
    TextView logout;
    String studentName;
    String CHANNEL_ID = "AI Attendence";
    String CHANNEL_NAME = "ANDROID_NOTIFICATION";
    Handler handler;
    Thread r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_area);

        sharedPreferences = getSharedPreferences(Constant.myPrefs, MODE_PRIVATE);
        studentName = sharedPreferences.getString(Constant.username, "null");
        mainLL = findViewById(R.id.idLLMain);
        logout = findViewById(R.id.idTVLogout);
        handler = new Handler();
        logout.setOnClickListener(view -> Logout.logout(this));

        startTimer();

        //new setSMSFunctionality().execute();
    }

    private static class setSMSFunctionality extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SMS.setUpServer("8193800247", "Your child is bunking classes regularly please have a look on that");
            return null;
        }
    }

    private void checkForAttendence() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date());

        List<String> fetchedDates = new ArrayList<>();
        for (Attendence attendence : attendenceListMain)
            fetchedDates.add(attendence.getDate());

        if (fetchedDates.contains(date)) {
            int index = fetchedDates.indexOf(date);
            Attendence attendence = attendenceListMain.get(index);
            List<String> students = attendence.getPresentStudents();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

            if (students.contains(studentName)) {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.present);
                mBuilder.setLargeIcon(largeIcon);
                mBuilder.setSmallIcon(R.drawable.present);
                mBuilder.setContentText("Hurray! Present in Spring Boot class!");
                mBuilder.setAutoCancel(false);
                mBuilder.setContentTitle("Attendence Report");
            } else {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.absent);
                mBuilder.setLargeIcon(largeIcon);
                mBuilder.setSmallIcon(R.drawable.present);
                mBuilder.setContentTitle("Attendence Report");
                mBuilder.setAutoCancel(false);
                mBuilder.setContentText("Opss! Absent in Spring Boot class?");
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(999, mBuilder.build());
            handler.removeCallbacks(r);
        }
    }

    private void startTimer() {
        r = new Thread() {
            public void run() {
                fetchData();
                handler.postDelayed(this, 3000);
            }
        };
        r.start();
    }

    private void fetchData() {
        String token = sharedPreferences.getString(Constant.Authorization, "null");
        String urlToHit = Config.attendenceUrl;
        attendenceListMain = new ArrayList<>();

        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, urlToHit, null,
                response -> {
                    Log.i(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
                    if (response != null) {

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String date = jsonObject.getString(Constant.date);
                                List<String> studentsList = new ArrayList<>();

                                JSONArray jsonArray = (JSONArray) jsonObject.get(Constant.attendanceList);
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    String name = (String) jsonArray.get(j);
                                    studentsList.add(name);
                                }

                                Attendence attendence = new Attendence(date, studentsList);
                                attendenceListMain.add(attendence);
                            }

                            checkForAttendence();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(Constant.failureTag, "\n Response was null for url: " + urlToHit);
                    }
                },
                volleyError -> Log.e(Constant.failureTag, "\n No info received from the server for api call: "
                        + urlToHit + " " + volleyError.getMessage())) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.Authorization, token);
                params.put("Content-Type", "application/json");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return super.getBodyContentType();
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(r);
    }
}