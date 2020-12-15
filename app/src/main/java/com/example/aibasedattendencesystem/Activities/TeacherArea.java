package com.example.aibasedattendencesystem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.tv.TvContract;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aibasedattendencesystem.Model.AttendenceAdapter;
import com.example.aibasedattendencesystem.R;
import com.example.aibasedattendencesystem.Server.Config;
import com.example.aibasedattendencesystem.Utility.Constant;
import com.example.aibasedattendencesystem.Utility.Logout;
import com.example.aibasedattendencesystem.Utility.Student;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherArea extends AppCompatActivity implements AttendenceAdapter.CheckBoxStateChange {

    RecyclerView rvStudents;
    RelativeLayout mainLL;
    ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    List<Student> studentsList;
    Button btn;
    AttendenceAdapter attendenceAdapter;
    TextView logoutTV;
    Activity activity;
    List<String> attendingStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_area);

        mainLL = findViewById(R.id.idLLMain);
        rvStudents = findViewById(R.id.idRVStudents);
        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(Constant.myPrefs, MODE_PRIVATE);
        btn = findViewById(R.id.idBTNSubmit);
        studentsList = new ArrayList<>();
        logoutTV = findViewById(R.id.TVLogout);
        activity = this;

        dialog.setMessage("Getting Students....");
        dialog.show();
        fetchStudents();

        btn.setOnClickListener(view -> {
            getAttendingStudents();
            dialog.setMessage("Saving Attendence....");
            dialog.show();
            sendDetailsToServer();
        });

        logoutTV.setOnClickListener(view -> Logout.logout(activity));
    }

    @Override
    public void stateChange(int pos, boolean b) {
        studentsList.get(pos).setPresent(b);
    }

    private void getAttendingStudents() {
        attendingStudents = new ArrayList<>();
        for (Student student : studentsList)
            if (student.isPresent()) attendingStudents.add(student.getName());
    }

    private void setRV() {
        attendenceAdapter = new AttendenceAdapter(studentsList);
        rvStudents.setHasFixedSize(true);
        rvStudents.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvStudents.setAdapter(attendenceAdapter);
        attendenceAdapter.setCheckBoxStateChange(this);
        attendenceAdapter.notifyDataSetChanged();
    }

    private void fetchStudents() {
        String token = sharedPreferences.getString(Constant.Authorization, "null");
        String urlToHit = Config.allStudentsInCourse;

        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlToHit, null,
                response -> {
                    Log.i(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
                    dialog.dismiss();
                    if (response != null) {
                        try {
                            JSONArray jsonArray = response.getJSONArray(Constant.usernames);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String name = (String) jsonArray.get(i);
                                Student student = new Student(name, true);
                                studentsList.add(student);
                            }
                            setRV();
                            attendenceAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(Constant.failureTag, "\n Response was null for url: " + urlToHit);
                        dialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(mainLL, "Null response", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                },
                volleyError -> {
                    Log.e(Constant.failureTag, "\n No info received from the server for api call: "
                            + urlToHit + " " + volleyError.getMessage());
                    dialog.dismiss();

                    Snackbar snackbar = Snackbar
                            .make(mainLL, "Something went amiss. We are looking into it.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }) {
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

    private void sendDetailsToServer() {
        String token = sharedPreferences.getString(Constant.Authorization, "null");
        String urlToHit = Config.attendenceUrl;

        Map<String, Object> params = new HashMap<>();
        params.put(Constant.usernames, attendingStudents.toArray());

        JSONObject paramsJSONObject = new JSONObject(params);
        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlToHit, paramsJSONObject,
                response -> {
                    Log.e(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
                    dialog.dismiss();
                    if (response != null) {
                        try {
                            Snackbar snackbar = Snackbar.make(mainLL, "Attendence Uploaded", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(Constant.failureTag, "\n Response was null for url: " + urlToHit);
                        dialog.dismiss();
                        Snackbar snackbar = Snackbar.make(mainLL, "Attendence Uploaded", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                },
                volleyError -> {
                    Log.e(Constant.failureTag, "\n No info received from the server for api call: "
                            + urlToHit + " " + volleyError.getMessage());
                    dialog.dismiss();

                    Snackbar snackbar = Snackbar.make(mainLL, "Attendence Uploaded", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }) {
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
}