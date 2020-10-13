package com.example.codeset;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Contests extends AppCompatActivity {

    RecyclerView recyclerViewContests;
    TextView result;
    String platform = "any";
    ArrayList<Contest> allContests = new ArrayList<>();
    SharedPreferences sharedPreferences;

    protected void loadContests(){
        Gson gson = new Gson();
        String response=sharedPreferences.getString("allContests" , "");
        if(response!=null&&response.length()>0) {
            long currTime = System.currentTimeMillis()/1000;
            ArrayList<Contest> current = gson.fromJson(response, new TypeToken<List<Contest>>() {}.getType());
            for(Contest contest:current)
                if(contest.getEndTime()>currTime)
                    allContests.add(contest);
        }
    }

    protected void showContests(ArrayList<Contest> current){
        String s = "There are " + current.size() + " contests.";
        Log.d("CodeSetDebug",s);
        AdapterContest myAdapter = new AdapterContest(this, current);
        recyclerViewContests.setAdapter(myAdapter);
        recyclerViewContests.setLayoutManager(new LinearLayoutManager(this));
        if(current.size()>0)
            result.setVisibility(View.GONE);
        else {
            result.setVisibility(View.VISIBLE);
            result.setText(getString(R.string.zeroResultContest));
        }
    }

    protected void search(String platform){
        if(platform.equalsIgnoreCase("any")) {
            showContests(allContests);
            return;
        }
        ArrayList<Contest> current = new ArrayList<>();
        for(Contest contest:allContests)
            if(contest.getPlatform().equalsIgnoreCase(platform))
                current.add(contest);
        showContests(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contests);
        recyclerViewContests = findViewById(R.id.recyclerViewContest);
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        result = findViewById(R.id.result);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Spinner searchPlatform = findViewById(R.id.searchPlatform);
        final ImageView searchPlatformArrow = findViewById(R.id.searchPlatformArrow);

        final String[] searchPlatformValues = getResources().getStringArray(R.array.contestPlatforms);
        ArrayAdapter<String> platformAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,searchPlatformValues);
        platformAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchPlatform.setAdapter(platformAdapter);

        searchPlatform.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!platform.equalsIgnoreCase(searchPlatformValues[position])) {
                    platform = searchPlatformValues[position];
                    ((TextView) view).setText(platform);
                    search(platform);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchPlatformArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchPlatform.performClick();
            }
        });

        searchPlatform.setSelection(0);

        loadContests();
        if(allContests.size()==0)
            new CountDownTimer(90000, 3000) {
                public void onTick(long millisUntilFinished) {
                    loadContests();
                    if(allContests.size()>0) {
                        progressBar.setVisibility(View.GONE);
                        showContests(allContests);
                    }
                    else
                        result.setText(getString(R.string.loadingContest));
                }
                public void onFinish() {
                    if(allContests.size()==0)
                        result.setText(getString(R.string.error));
                }
            }.start();
        else {
            progressBar.setVisibility(View.GONE);
            showContests(allContests);
        }
    }
}
