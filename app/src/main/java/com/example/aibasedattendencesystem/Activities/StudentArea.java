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
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.aibasedattendencesystem.R;
import com.example.aibasedattendencesystem.Server.Config;
import com.example.aibasedattendencesystem.Utility.Attendence;
import com.example.aibasedattendencesystem.Utility.AttendenceHistory;
import com.example.aibasedattendencesystem.Utility.Constant;
import com.example.aibasedattendencesystem.Utility.Logout;
import com.example.aibasedattendencesystem.Utility.SMS;
import com.example.aibasedattendencesystem.Utility.WelcomeSetter;

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
    List<AttendenceHistory> oldAttendence;
    TextView logout;
    String studentName;
    Handler handler;
    Thread thread;
    boolean isFirstTimeAttendence, isFirstTimeOldAttendence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_area);
        initializeUIAndVariables();
        initializeClickActions();
    }

    //Function for perform task while app running in background
    @Override
    protected void onPause() {
        super.onPause();

        if (isFirstTimeOldAttendence || isFirstTimeAttendence)
            startHandlerForBackground();
    }

    //Function for perform task when app is just opened
    @Override
    protected void onResume() {
        super.onResume();
        handler.removeCallbacks(thread);
    }

    //Function for perform task on app close
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(thread);
    }

    //Function for initialize UI and Variables
    private void initializeUIAndVariables() {
        sharedPreferences = getSharedPreferences(Constant.myPrefs, MODE_PRIVATE);
        studentName = sharedPreferences.getString(Constant.username, "null");
        mainLL = findViewById(R.id.idLLMain);
        logout = findViewById(R.id.idTVLogout);
        handler = new Handler();
        isFirstTimeAttendence = true;
        isFirstTimeOldAttendence = true;

        String userName = sharedPreferences.getString(Constant.username, "Null");
        WelcomeSetter.setWelcomeMsg(findViewById(R.id.idTVWelcome), userName);
    }

    //Function for initialize click actions
    private void initializeClickActions() {
        logout.setOnClickListener(view -> Logout.logout(this));
    }

    //Function for fetch Attendence Data for Notification
    private void fetchAttendenceData() {
        String token = sharedPreferences.getString(Constant.Authorization, "null");
        String urlToHit = Config.attendenceUrl;
        attendenceListMain = new ArrayList<>();

        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, urlToHit, null,
                response -> {
                    Log.e(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
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

                            notifyAttendenceStatus();
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

    //Function for send Notification
    private void notifyAttendenceStatus() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = simpleDateFormat.format(new Date());

        List<String> fetchedDates = new ArrayList<>();
        for (Attendence attendence : attendenceListMain)
            fetchedDates.add(attendence.getDate());

        if (fetchedDates.contains(date) && isFirstTimeAttendence) {
            int index = fetchedDates.indexOf(date);
            Attendence attendence = attendenceListMain.get(index);
            List<String> students = attendence.getPresentStudents();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(Constant.CHANNEL_ID, Constant.CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, Constant.CHANNEL_ID);

            if (students.contains(studentName)) {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.present);
                mBuilder.setLargeIcon(largeIcon);
                mBuilder.setSmallIcon(R.drawable.present);
                mBuilder.setContentText("Hurray! Present in Spring Boot class:)");
                mBuilder.setAutoCancel(false);
                mBuilder.setContentTitle("Attendence Marked");
            } else {
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.absent);
                mBuilder.setLargeIcon(largeIcon);
                mBuilder.setSmallIcon(R.drawable.present);
                mBuilder.setContentTitle("Attendence Marked");
                mBuilder.setAutoCancel(false);
                mBuilder.setContentText("Oops! Absent in Spring Boot class?");
            }

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(999, mBuilder.build());
            isFirstTimeAttendence = false;
        }
    }

    //Function for fetching Attendence History
    private void fetchAttendenceHistory() {
        String token = sharedPreferences.getString(Constant.Authorization, "null");
        String userName = sharedPreferences.getString(Constant.username, "null");
        String urlToHit = Config.attendenceHistoryUrl + userName + Config.limit;
        oldAttendence = new ArrayList<>();

        JsonArrayRequest jsonObjectRequest;
        jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, urlToHit, null,
                response -> {
                    Log.e(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
                    if (response != null) {

                        try {

                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String date = jsonObject.getString(Constant.date);
                                boolean present = jsonObject.getBoolean(Constant.present);
                                AttendenceHistory attendence = new AttendenceHistory(date, present);
                                oldAttendence.add(attendence);
                            }

                            checkForRegularAbsents();
                        } catch (Exception e) {
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

    //Function for check regular absents to send sms to parents
    private void checkForRegularAbsents() {
        if (oldAttendence != null) {
            int count = 0;
            for (AttendenceHistory attendenceHistory : oldAttendence)
                if (!attendenceHistory.isPresent()) count++;

            if (count == 5 && isFirstTimeOldAttendence) {
                new sendSMS().execute();
                isFirstTimeOldAttendence = false;
            }

            Toast.makeText(this, ""+count, Toast.LENGTH_SHORT).show();
        }
    }

    //Async Class for perform sms send functionality in background
    private static class sendSMS extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SMS.setUpServer("8193800247", "Dear Parent,\nYour ward has missed 5 classes of Course: \"Spring Boot\". Please have a look on that");
            return null;
        }
    }

    //Function for start handler for hit API after 3 seconds
    private void startHandlerForBackground() {
        thread = new Thread() {
            public void run() {
                fetchAttendenceData();
                fetchAttendenceHistory();
                handler.postDelayed(this, 3000);
            }
        };
        thread.start();
    }
}