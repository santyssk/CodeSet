package com.example.codeset;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class SphereOnlineJudge extends AppCompatActivity {

    PlatformSPOJ platformSPOJ = new PlatformSPOJ();
    RecyclerView recyclerViewSPOJ;
    SharedPreferences sharedPreferences;
    TextView result;

    int numOfProblems = 0;
    String query = "";
    String level = "Any Difficulty";

    protected  void showProblems(ArrayList<ProblemSPOJ> current){
        String s = "Spoj There are " + numOfProblems + " problems.";
        Log.d("CodeSetDebug",s);
        AdapterSPOJ myAdapter = new AdapterSPOJ(this, current);
        recyclerViewSPOJ.setAdapter(myAdapter);
        recyclerViewSPOJ.setLayoutManager(new LinearLayoutManager(this));
        if(numOfProblems>0)
            result.setVisibility(View.GONE);
        else {
            result.setVisibility(View.VISIBLE);
            result.setText(getString(R.string.zeroResultProblem));
        }
    }

    protected void search(){
        ArrayList<ProblemSPOJ> current = platformSPOJ.search(query, level);
        numOfProblems = current.size();
        showProblems(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sphere_online_judge);
        recyclerViewSPOJ = findViewById(R.id.recyclerViewSPOJ);
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String response=sharedPreferences.getString("platformSPOJ" , "");
        Gson gson = new Gson();
        if(response!=null&&response.length()>0)
            platformSPOJ = gson.fromJson(response, new TypeToken<PlatformSPOJ>(){}.getType());

        ImageView imageView = findViewById(R.id.platformImage);
        final Context context = imageView.getContext();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webString = platformSPOJ.getPlatformURL();
                Helper.openUrl(context,webString);
            }
        });

        ImageView clearSearch = findViewById(R.id.clearSPOJ);
        final EditText searchQuery = findViewById(R.id.searchQuerySPOJ);
        final Spinner searchLevel = findViewById(R.id.searchLevelSPOJ);
        final ImageView searchLevelArrow = findViewById(R.id.searchLevelArrowSPOJ);
        result = findViewById(R.id.result);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        final String[] searchLevelValues = getResources().getStringArray(R.array.levelsSPOJ);
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,searchLevelValues);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchLevel.setAdapter(levelAdapter);

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numOfProblems!=platformSPOJ.getNumberOfProblems()) {
                    View view = SphereOnlineJudge.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    query = "";
                    level="Any Difficulty";
                    searchLevel.setSelection(0);
                    searchQuery.setText("");
                    searchLevel.setSelection(0);                }
            }
        });

        searchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                query = searchQuery.getText().toString();
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!searchLevelValues[position].equalsIgnoreCase(level)){
                    level = searchLevelValues[position];
                    ((TextView) view).setText(level);
                    search();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        searchLevelArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchLevel.performClick();
            }
        });

        searchLevel.setSelection(0);

        numOfProblems=platformSPOJ.getNumberOfProblems();
        if(numOfProblems==0)
            new CountDownTimer(300000, 15000) {

            public void onTick(long millisUntilFinished) {
                String response=sharedPreferences.getString("platformSPOJ" , "");
                if(response!=null&&response.length()>0) {
                    Gson gson = new Gson();
                    platformSPOJ = gson.fromJson(response, new TypeToken<PlatformSPOJ>() {}.getType());
                    numOfProblems = platformSPOJ.getNumberOfProblems();
                    progressBar.setVisibility(View.GONE);
                    showProblems(platformSPOJ.getAllProblems());
                    this.cancel();
                }
                else
                    result.setText(getString(R.string.loadingProblem));
            }
            public void onFinish() {
                if(numOfProblems==0)
                    result.setText(getString(R.string.error));
            }
        }.start();
        else{
            progressBar.setVisibility(View.GONE);
            showProblems(platformSPOJ.getAllProblems());
        }
    }
}
