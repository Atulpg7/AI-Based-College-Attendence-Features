package com.example.aibasedattendencesystem.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.aibasedattendencesystem.R;
import com.example.aibasedattendencesystem.Server.Config;
import com.example.aibasedattendencesystem.Utility.Constant;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText etUsername;
    EditText etPassword;
    TextView btnLogin;
    LinearLayout mainPageLL;
    ProgressDialog dialog;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUIAndVariables();
        initializeClickActions();
        isAlreadyLoggedIn();
    }

    //Function for check if User is Already LoggedIn
    private void isAlreadyLoggedIn() {
        boolean alreadyLoggedIn = sharedPreferences.getBoolean(Constant.isLoggedIn, false);
        if (alreadyLoggedIn) {
            String role = sharedPreferences.getString(Constant.role, "ADMIN");
            if (role.equals("ADMIN")) {
                startActivity(new Intent(this, AdminArea.class));
                finish();
            } else if (role.equals("TEACHER")) {
                startActivity(new Intent(this, TeacherArea.class));
                finish();
            } else {
                startActivity(new Intent(this, StudentArea.class));
                finish();
            }
        }
    }

    //Function for initialize UI and Variables
    private void initializeUIAndVariables() {
        etUsername = findViewById(R.id.idETUsername);
        etPassword = findViewById(R.id.idETPassword);
        btnLogin = findViewById(R.id.idBtnSubmit);
        mainPageLL = findViewById(R.id.idLLMainPage);
        dialog = new ProgressDialog(this);
        sharedPreferences = getSharedPreferences(Constant.myPrefs, MODE_PRIVATE);
    }

    //Function for initialize click actions
    private void initializeClickActions() {

        btnLogin.setOnClickListener(view -> {

            String uName = etUsername.getText().toString();
            String uPass = etPassword.getText().toString();

            if (checkNull(uName)) {
                Snackbar snackbar = Snackbar
                        .make(mainPageLL, "Please enter Username", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else if (checkNull(uPass)) {
                Snackbar snackbar = Snackbar
                        .make(mainPageLL, "Please enter Password", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else {
                dialog.setMessage("Validating.....");
                dialog.show();
                authenticateCredentials(uName, uPass);
            }

        });
    }

    //Function for checking string is null or not
    public boolean checkNull(String s) {
        return s.equals("");
    }

    //Function for saving data in memory
    private void saveDataInSharedPreferences(String s, String token, String username) {
        editor = sharedPreferences.edit();
        String str = "Bearer " + s;
        editor.putString(Constant.Authorization, str);
        editor.putString(Constant.role, token);
        editor.putString(Constant.username, username);
        editor.putBoolean(Constant.isLoggedIn, true);
        editor.apply();
    }

    //Function for validating user's credentials
    private void authenticateCredentials(String uName, String uPass) {

        String urlToHit = Config.authUrl;

        Map<String, Object> params = new HashMap<>();
        params.put(Constant.username, uName);
        params.put(Constant.password, uPass);

        JSONObject paramsJSONObject = new JSONObject(params);

        JsonObjectRequest jsonObjectRequest;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, urlToHit, paramsJSONObject,
                response -> {
                    Log.e(Constant.successTag, "Response for api call: " + urlToHit + " is: " + response);
                    dialog.dismiss();
                    if (response != null) {
                        try {

                            String role = "", token = "", username = "";

                            if (response.has(Constant.role)) {
                                role = response.getString(Constant.role);
                            }

                            if (response.has(Constant.username)) {
                                username = response.getString(Constant.username);
                            }

                            if (response.has(Constant.token)) {
                                token = response.getString(Constant.token);
                            }

                            if (!checkNull(role) && !checkNull(token)) {

                                saveDataInSharedPreferences(token, role, username);

                                if (role.equals("ADMIN")) {
                                    startActivity(new Intent(this, AdminArea.class));
                                    finish();
                                } else if (role.equals("TEACHER")) {
                                    startActivity(new Intent(this, TeacherArea.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(this, StudentArea.class));
                                    finish();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(Constant.failureTag, "\n Response was null for url: " + urlToHit);
                        dialog.dismiss();
                        Snackbar snackbar = Snackbar
                                .make(mainPageLL, "Null response", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                },
                volleyError -> {
                    Log.e(Constant.failureTag, "\n No info received from the server for api call: "
                            + urlToHit + " " + volleyError.getMessage());
                    dialog.dismiss();

                    Snackbar snackbar = Snackbar
                            .make(mainPageLL, "Something went amiss. We are looking into it.", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }
}