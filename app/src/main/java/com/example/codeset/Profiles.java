package com.example.codeset;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Profiles extends AppCompatActivity {

    protected void setUsernameCF(String username, int rating, TextView usernameText){
        int ratingColor = PlatformCF.getRatingColor(this,rating);
        if(ratingColor==getColor(R.color.legendary)){
            String text = "<font color="+ratingColor+">"+username.charAt(0)+"</font><font color="+getColor(R.color.grandMaster)+">"+username.substring(1)+"</font>";
            usernameText.setText(Html.fromHtml(text));
        }
        else {
            if(ratingColor!=Integer.MIN_VALUE)
                usernameText.setTextColor(ratingColor);
            usernameText.setText(username);
        }
    }

    protected void setUsernameCC(String username, int rating, TextView usernameText){
        int ratingColor = PlatformCC.getRatingColor(this,rating);
        if(ratingColor!=Integer.MIN_VALUE)
            usernameText.setTextColor(ratingColor);
        usernameText.setText(username);
    }

    protected void setUsernameAC(String username, int rating, TextView usernameText){
        int ratingColor = PlatformAC.getRatingColor(this,rating);
        usernameText.setTextColor(ratingColor);
        usernameText.setText(username);
    }

    protected void setRatingCF(int rating, TextView ratingText){
        int ratingColor = PlatformCF.getRatingColor(this,rating);
        if(ratingColor==getColor(R.color.legendary))
            ratingColor = getColor(R.color.grandMaster);
        if(ratingColor!=Integer.MIN_VALUE)
            ratingText.setTextColor(ratingColor);
        ratingText.setText(String.valueOf(rating));
    }

    protected void setRatingCC(int rating, TextView ratingText){
        int ratingColor = PlatformCC.getRatingColor(this,rating);
        if(ratingColor!=Integer.MIN_VALUE)
            ratingText.setTextColor(ratingColor);
        ratingText.setText(String.valueOf(rating));
    }

    protected void setRatingAC(int rating,TextView ratingText){
        int ratingColor = PlatformAC.getRatingColor(this,rating);
        ratingText.setTextColor(ratingColor);
        ratingText.setText(String.valueOf(rating));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Gson gson = new Gson();
        String response;

        TextView result = findViewById(R.id.result);
        boolean user = false;

        response=sharedPreferences.getString("platformCF" , "");
        final PlatformCF platformCF = gson.fromJson(response, new TypeToken<PlatformCF>() {}.getType());
        response=sharedPreferences.getString("platformCC" , "");
        final PlatformCC platformCC = gson.fromJson(response, new TypeToken<PlatformCC>() {}.getType());
        response=sharedPreferences.getString("platformAC" , "");
        final PlatformAC platformAC = gson.fromJson(response, new TypeToken<PlatformAC>() {}.getType());

        ConstraintLayout cf = findViewById(R.id.cfProfile);
        if(platformCF!=null) {
            String username = platformCF.getUsername();
            if(username.length()>0){
                user = true;
                cf.setVisibility(View.VISIBLE);
                int rating = platformCF.getRating();
                TextView name = findViewById(R.id.cfName);
                setUsernameCF(username, rating, name);
                if(rating!=Integer.MIN_VALUE) {
                    TextView current = findViewById(R.id.cfCurr);
                    setRatingCF(rating,current);
                    TextView maximum = findViewById(R.id.cfMax);
                    setRatingCF(platformCF.getMaxRating(),maximum);
                }
            }
            cf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webString = platformCF.getUserURL();
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl( Profiles.this, Uri.parse(webString));
                }
            });
        }

        ConstraintLayout cc = findViewById(R.id.ccProfile);
        if(platformCC!=null) {
            String username = platformCC.getUsername();
            if(username.length()>0){
                user = true;
                cc.setVisibility(View.VISIBLE);
                int rating = platformCC.getRating();
                TextView name = findViewById(R.id.ccName);
                setUsernameCC(username,rating, name);
                if(rating!=Integer.MIN_VALUE) {
                    TextView current = findViewById(R.id.ccCurr);
                    setRatingCC(rating,current);
                    TextView maximum = findViewById(R.id.ccMax);
                    setRatingCC(platformCC.getMaxRating(),maximum);
                }
            }
            cc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webString = platformCC.getUserURL();
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl( Profiles.this, Uri.parse(webString));
                }
            });
        }

        ConstraintLayout ac = findViewById(R.id.acProfile);
        if(platformAC!=null) {
            String username = platformAC.getUsername();
            if(username.length()>0){
                user = true;
                ac.setVisibility(View.VISIBLE);
                int rating = platformAC.getRating();
                TextView name = findViewById(R.id.acName);
                setUsernameAC(username,rating, name);
                if(rating!=Integer.MIN_VALUE) {
                    TextView current = findViewById(R.id.acCurr);
                    setRatingAC(rating,current);
                    TextView maximum = findViewById(R.id.acMax);
                    setRatingAC(platformAC.getMaxRating(),maximum);
                }
            }
            ac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webString = platformAC.getUserURL();
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl( Profiles.this, Uri.parse(webString));
                }
            });
        }

        if(!user)
            result.setVisibility(View.VISIBLE);
    }
}
