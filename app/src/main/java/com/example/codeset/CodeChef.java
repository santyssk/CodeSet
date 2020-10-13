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


public class CodeChef extends AppCompatActivity {

    PlatformCC platformCC = new PlatformCC();
    RecyclerView recyclerViewCC;
    SharedPreferences sharedPreferences;
    TextView result;

    int numOfProblems = 0;
    String query = "";
    String level = "Any Difficulty";
    int solveState = -1;

    protected void setUsername( TextView usernameText){
        String username = platformCC.getUsername();
        if(username==null||username.length()==0)
            return;
        int rating = platformCC.getRating();
        int ratingColor = PlatformCC.getRatingColor(this,rating);
        if(ratingColor!=Integer.MIN_VALUE)
            usernameText.setTextColor(ratingColor);
        usernameText.setText(username);
    }

    protected void showProblems(ArrayList<ProblemCC> current){
        String s = "CodeChef There are " + numOfProblems + " problems.";
        Log.d("CodeSetDebug",s);
        AdapterCC myAdapter = new AdapterCC(this, current);
        recyclerViewCC.setAdapter(myAdapter);
        recyclerViewCC.setLayoutManager(new LinearLayoutManager(this));
        if(numOfProblems>0)
            result.setVisibility(View.GONE);
        else {
            result.setVisibility(View.VISIBLE);
            result.setText(getString(R.string.zeroResultProblem));
        }
    }

    protected void search(){
        ArrayList<ProblemCC> current = platformCC.search(query, level, solveState);
        numOfProblems = current.size();
        showProblems(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codechef);
        recyclerViewCC = findViewById(R.id.recyclerViewCC);
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String response=sharedPreferences.getString("platformCC" , "");
        Gson gson = new Gson();
        if(response!=null&&response.length()>0)
            platformCC = gson.fromJson(response, new TypeToken<PlatformCC>(){}.getType());

        ImageView imageView = findViewById(R.id.platformImage);
        final Context context = imageView.getContext();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webString = platformCC.getPlatformURL();
                Helper.openUrl(context,webString);
            }
        });

        final TextView usernameText = findViewById(R.id.platformUser);
        ImageView clearSearch = findViewById(R.id.clearCC);
        final EditText searchQuery = findViewById(R.id.searchQueryCC);
        final Spinner searchSolveStatus = findViewById(R.id.searchStatusCC);
        final Spinner searchLevel = findViewById(R.id.searchLevelCC);
        final ImageView searchStatusArrow = findViewById(R.id.searchStatusArrowCC);
        final ImageView searchLevelArrow = findViewById(R.id.searchLevelArrowCC);
        result = findViewById(R.id.result);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        final String[] searchStatusValues = getResources().getStringArray(R.array.solveStatus);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,searchStatusValues);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSolveStatus.setAdapter(statusAdapter);
        final String[] searchLevelValues = getResources().getStringArray(R.array.levelsCC);
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,searchLevelValues);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchLevel.setAdapter(levelAdapter);


        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numOfProblems!=platformCC.getNumberOfProblems()) {
                    View view = CodeChef.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    query = "";
                    solveState = -1;
                    level="Any Difficulty";
                    searchLevel.setSelection(0);
                    searchQuery.setText("");
                    searchSolveStatus.setSelection(0);
                    searchLevel.setSelection(0);
                }
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

        String username = platformCC.getUsername();
        if(username!=null&&username.length()>0) {
            usernameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webString = platformCC.getPlatformURL();
                    Helper.openUrl(context,webString);
                }
            });

            searchSolveStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position-1!=solveState){
                        ((TextView) view).setText(searchStatusValues[position]);
                        solveState = position-1;
                        search();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            searchStatusArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchSolveStatus.performClick();
                }
            });

            searchSolveStatus.setSelection(0);
        }
        else
            searchSolveStatus.setEnabled(false);

        setUsername(usernameText);
        numOfProblems=platformCC.getNumberOfProblems();
        if(numOfProblems==0)
            new CountDownTimer(90000, 3000) {

            public void onTick(long millisUntilFinished) {
                String response=sharedPreferences.getString("platformCC" , "");
                if(response!=null&&response.length()>0) {
                    Gson gson = new Gson();
                    platformCC = gson.fromJson(response, new TypeToken<PlatformCC>() {}.getType());
                    numOfProblems = platformCC.getNumberOfProblems();
                    progressBar.setVisibility(View.GONE);
                    showProblems(platformCC.getAllProblems());
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
        else {
            progressBar.setVisibility(View.GONE);
            showProblems(platformCC.getAllProblems());
        }
    }
}
