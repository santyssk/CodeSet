package com.example.codeset;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Settings extends AppCompatActivity {

    PlatformCC platformCC = new PlatformCC();
    PlatformCF platformCF = new PlatformCF();
    PlatformAC platformAC = new PlatformAC();
    SharedPreferences sharedPreferences;
    Gson gson = new Gson();

    void wrongUser(String username){
        String title = "Invalid username";
        String message = username+ " is not a valid username. Please check username and try again";
        Helper.errorMessage(this,title,message);
    }

    void noInternet(){
        String title = "No Internet";
        String message = "Please connect to internet to fetch data.";
        Helper.errorMessage(this,title,message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final EditText cfUserInput = findViewById(R.id.cfUserInput);
        final EditText ccUserInput = findViewById(R.id.ccUserInput);
        final EditText acUserInput = findViewById(R.id.acUserInput);

        final Button cfButton = findViewById(R.id.cfButton);
        final Button ccButton = findViewById(R.id.ccButton);
        final Button acButton = findViewById(R.id.acButton);
        Button done = findViewById(R.id.userDone);

        String response;
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        response=sharedPreferences.getString("platformCF" , "");
        platformCF = gson.fromJson(response, new TypeToken<PlatformCF>() {}.getType());
        response=sharedPreferences.getString("platformCC" , "");
        platformCC = gson.fromJson(response, new TypeToken<PlatformCC>() {}.getType());
        response=sharedPreferences.getString("platformAC" , "");
        platformAC = gson.fromJson(response, new TypeToken<PlatformAC>() {}.getType());

        if(platformCF!=null&&platformCF.getUsername().length()>0){
            cfUserInput.setText(platformCF.getUsername());
            cfButton.setText(getString(R.string.change));
            cfUserInput.setFocusable(false);
        }
        if(platformCC!=null&&platformCC.getUsername().length()>0){
            ccUserInput.setText(platformCC.getUsername());
            ccButton.setText(getString(R.string.change));
            ccUserInput.setFocusable(false);
        }
        if(platformAC!=null&&platformAC.getUsername().length()>0){
            acUserInput.setText(platformAC.getUsername());
            acButton.setText(getString(R.string.change));
            acUserInput.setFocusable(false);
        }

        cfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = cfButton.getText().toString();
                if(text.equalsIgnoreCase(getString(R.string.change))) {
                    cfUserInput.setFocusableInTouchMode(true);
                    cfButton.setText(getString(R.string.set));
                }
                else{
                    View view = Settings.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    boolean internet = Helper.isInternetConnected();
                    if(!internet)
                        noInternet();
                    else {
                        String username = cfUserInput.getText().toString();
                        if (platformCF != null) {
                            String prevUser = platformCF.getUsername();
                            if (username.length() > 0 || prevUser.length() > 0) {
                                if (username.isEmpty() || PlatformCF.isValidUser(username)) {
                                    cfUserInput.setFocusable(false);
                                    cfButton.setText(getString(R.string.change));
                                    if (prevUser == null || !prevUser.equals(username)) {
                                        Log.d("CodeSetDebug", "CF " + username);
                                        platformCF.setUsername(username);
                                        String jsonString = gson.toJson(platformCF);
                                        sharedPreferences.edit().putString("platformCF", jsonString).apply();
                                    }
                                } else
                                    wrongUser(username);
                            }
                        }
                    }
                }
            }
        });

        ccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ccButton.getText().toString();
                if(text.equalsIgnoreCase(getString(R.string.change))) {
                    ccUserInput.setFocusableInTouchMode(true);
                    ccButton.setText(getString(R.string.set));
                }
                else{
                    View view = Settings.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    boolean internet = Helper.isInternetConnected();
                    if(!internet)
                        noInternet();
                    else {
                        String username = ccUserInput.getText().toString();
                        if (platformCC != null) {
                            String prevUser = platformCC.getUsername();
                            if (username.length() > 0 || prevUser.length() > 0) {
                                if (username.isEmpty() || PlatformCC.isValidUser(username)) {
                                    ccUserInput.setFocusable(false);
                                    ccButton.setText(getString(R.string.change));
                                    if (prevUser == null || !prevUser.equals(username)) {
                                        Log.d("CodeSetDebug", "CC " + username);
                                        platformCC.setUsername(username);
                                        String jsonString = gson.toJson(platformCC);
                                        sharedPreferences.edit().putString("platformCC", jsonString).apply();
                                    }
                                } else
                                    wrongUser(username);
                            }
                        }
                    }


                }
            }
        });

        acButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = acButton.getText().toString();
                if(text.equalsIgnoreCase(getString(R.string.change))) {
                    acUserInput.setFocusableInTouchMode(true);
                    acButton.setText(getString(R.string.set));
                }
                else{
                    View view = Settings.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    boolean internet = Helper.isInternetConnected();
                    if(!internet)
                        noInternet();
                    else {
                        String username = acUserInput.getText().toString();
                        if (platformAC != null) {
                            String prevUser = platformAC.getUsername();
                            if (username.length() > 0 || prevUser.length() > 0) {
                                if (username.isEmpty() || PlatformAC.isValidUser(username)) {
                                    acUserInput.setFocusable(false);
                                    acButton.setText(getString(R.string.change));
                                    if (prevUser == null || !prevUser.equals(username)) {
                                        Log.d("CodeSetDebug", "AC " + username);
                                        platformAC.setUsername(username);
                                        String jsonString = gson.toJson(platformAC);
                                        sharedPreferences.edit().putString("platformAC", jsonString).apply();
                                    }
                                } else
                                    wrongUser(username);
                            }
                        }
                    }
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
