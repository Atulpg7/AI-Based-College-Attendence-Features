package com.example.aibasedattendencesystem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aibasedattendencesystem.R;
import com.example.aibasedattendencesystem.Server.Config;
import com.example.aibasedattendencesystem.Utility.Constant;
import com.example.aibasedattendencesystem.Utility.Logout;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AdminArea extends AppCompatActivity {

    TextView tvLogout, tvSubmit;
    EditText etSUserName, etSName, etSPassword, etSParentMobNo;
    LinearLayout adminLL;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog dialog;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_area);

        tvLogout = findViewById(R.id.TVLogout);
        tvSubmit = findViewById(R.id.idBtnSubmit);
        etSUserName = findViewById(R.id.idETSUsername);
        etSName = findViewById(R.id.idETSName);
        etSPassword = findViewById(R.id.idETSPassword);
        etSParentMobNo = findViewById(R.id.idETSPMobNO);
        adminLL = findViewById(R.id.idLLAdminArea);
        sharedPreferences = getSharedPreferences(Constant.myPrefs, MODE_PRIVATE);
        dialog = new ProgressDialog(this);
        activity = this;


        tvLogout.setOnClickListener(view -> {
            Logout.logout(activity);
        });


        tvSubmit.setOnClickListener(view -> {

            String username = etSUserName.getText().toString();
            String name = etSName.getText().toString();
            String password = etSPassword.getText().toString();
            String parentMob = etSParentMobNo.getText().toString();

            if (checkNull(username)) {
                Snackbar snackbar = Snackbar
                        .make(adminLL, "Please enter Username", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            if (checkNull(name)) {
                Snackbar snackbar = Snackbar
                        .make(adminLL, "Please enter Name", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            if (checkNull(password)) {
                Snackbar snackbar = Snackbar
                        .make(adminLL, "Please enter Password", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            if (checkNull(parentMob)) {
                Snackbar snackbar = Snackbar
                        .make(adminLL, "Please enter Mobile No", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }
            if (parentMob.length() < 10) {
                Snackbar snackbar = Snackbar
                        .make(adminLL, "Please enter valid Mobile No", Snackbar.LENGTH_LONG);
                snackbar.show();
                return;
            }

            dialog.setMessage("Adding Student...");
            dialog.show();

            sendStudentData(username, name, password, parentMob);
        });
    }

    private boolean checkNull(String str) {
        return str.equals("");
    }

    private void sendStudentData(String username, String name, String password, String parentMob) {
        String token = sharedPreferences.getString(Constant.Authorization, "null");
        String urlToHit = Config.addStudentUrl;

        Map<String, Object> params = new HashMap<>();
        params.put(Constant.username, username);
        params.put(Constant.name, name);
        params.put(Constant.password, password);
        params.put(Constant.parentPhone, parentMob);

        JSONObject paramsJSONObject = new JSONObject(params);
        Log.e("Sending Params==>", paramsJSONObject.toString());
        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlToHit, paramsJSONObject,
                response -> {
                    Log.e(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
                    dialog.dismiss();
                    if (response != null) {
                        try {


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(Constant.failureTag, "\n Response was null for url: " + urlToHit);
                        dialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(adminLL, "Null response", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                },
                volleyError -> {

                    String message = "";
                    if (volleyError.networkResponse != null) {
                        try {

                            JSONObject object = new JSONObject(new String(volleyError.networkResponse.data));
                            Log.e(Constant.failureTag, ""
                                    + urlToHit + " " + object);
                            message = object.getString("message");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    dialog.dismiss();
                    Snackbar snackbar = Snackbar.make(adminLL, "" + message, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put(Constant.Authorization, token);
                params.put("Content-Type", "application/json");
                Log.e("SendingParams=>", params.toString());
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