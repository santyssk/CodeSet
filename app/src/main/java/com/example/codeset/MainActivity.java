package com.example.codeset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    protected class GetDataThread implements Runnable{
        @Override
        public void run() {
            Helper.loadContests(MainActivity.this);
            Helper.loadProblems(MainActivity.this);
        }
    }

    protected class GetSPOJThread implements Runnable{
        @Override
        public void run() {
            Helper.loadSpoj(MainActivity.this);
        }
    }

    protected class GetDataEmptyThread implements Runnable{
        @Override
        public void run() {
            Helper.loadContestsIfEmpty(MainActivity.this);
            Helper.loadProblemsIfEmpty(MainActivity.this);
        }
    }

    protected class GetSPOJEmptyThread implements Runnable{
        @Override
        public void run() {
            Helper.loadSpojIfEmpty(MainActivity.this);
        }
    }

    ScrollView mainPage;
    LinearLayout aboutPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ImageView cf = findViewById(R.id.cf);
        ImageView cc = findViewById(R.id.cc);
        ImageView spoj = findViewById(R.id.spoj);
        ImageView ac = findViewById(R.id.ac);

        Button contests = findViewById(R.id.contests);
        Button settings = findViewById(R.id.settings);
        Button profiles = findViewById(R.id.profiles);
        Button about = findViewById(R.id.about);

        mainPage = findViewById(R.id.mainPage);
        aboutPage = findViewById(R.id.aboutPage);
        Button aboutClose = findViewById(R.id.aboutClose);

        cf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CodeForces.class);
                startActivity(intent);
            }
        });

        cc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CodeChef.class);
                startActivity(intent);
            }
        });

        spoj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SphereOnlineJudge.class);
                startActivity(intent);
            }
        });

        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AtCoder.class);
                startActivity(intent);
            }
        });

        contests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Contests.class);
                startActivity(intent);
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
            }
        });

        profiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profiles.class);
                startActivity(intent);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPage.setVisibility(View.INVISIBLE);
                aboutPage.setVisibility(View.VISIBLE);
            }
        });

        aboutClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aboutPage.setVisibility(View.INVISIBLE);
                mainPage.setVisibility(View.VISIBLE);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        boolean firstTime = sharedPreferences.getBoolean("firstTime",true);
        boolean internet = Helper.isInternetConnected();
        //firstTime = true;
        if(firstTime) {
            sharedPreferences.edit().clear().apply();
            mainPage.setVisibility(View.INVISIBLE);
            aboutPage.setVisibility(View.VISIBLE);
            if(!internet){
                String title = "No Internet";
                String message = "Please connect to internet to fetch data.";
                Helper.errorMessage(this,title,message);
            }
            else {
                GetDataThread getDataThread = new GetDataThread();
                new Thread(getDataThread).start();
                GetSPOJThread getSPOJThread = new GetSPOJThread();
                new Thread(getSPOJThread).start();
            }
            Helper.setScheduleProblem(this);
            Helper.setScheduleSubmission(this);
            Helper.setScheduleRating(this);
            Helper.setScheduleContest(this);
            sharedPreferences.edit().putBoolean("firstTime",false).apply();
        }
        else  if(internet) {
            GetDataEmptyThread getDataEmptyThread = new GetDataEmptyThread();
            new Thread((getDataEmptyThread)).start();
            GetSPOJEmptyThread getSPOJEmptyThread = new GetSPOJEmptyThread();
            new Thread(getSPOJEmptyThread).start();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(aboutPage.getVisibility()==View.VISIBLE){
            aboutPage.setVisibility(View.INVISIBLE);
            mainPage.setVisibility(View.VISIBLE);
        }
        else
            finish();
    }
}