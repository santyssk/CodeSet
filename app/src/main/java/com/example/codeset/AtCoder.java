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

public class AtCoder extends AppCompatActivity {

    PlatformAC platformAC = new PlatformAC();
    RecyclerView recyclerViewAC;
    SharedPreferences sharedPreferences;
    TextView result;

    int numOfProblems = 0;
    String contestID = "";
    String index = "";
    String query = "";
    int solveState = -1;

   protected void setUsername(TextView usernameText){
        String username = platformAC.getUsername();
        if(username==null||username.length()==0)
            return;
        int rating = platformAC.getRating();
        int ratingColor = PlatformAC.getRatingColor(this,rating);
        usernameText.setTextColor(ratingColor);
        usernameText.setText(username);
    }

    protected  void showProblems(ArrayList<ProblemAC> current){
        String s = "AtCoder There are " + numOfProblems + " problems.";
        Log.d("CodeSetDebug",s);
        AdapterAC myAdapter = new AdapterAC(this, current);
        recyclerViewAC.setAdapter(myAdapter);
        recyclerViewAC.setLayoutManager(new LinearLayoutManager(this));
        if(numOfProblems>0)
            result.setVisibility(View.GONE);
        else {
            result.setVisibility(View.VISIBLE);
            result.setText(getString(R.string.zeroResultProblem));
        }
    }

    protected void search(){
        ArrayList<ProblemAC> current = platformAC.search(contestID,index,query,solveState);
        numOfProblems = current.size();
        showProblems(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_at_coder);
        recyclerViewAC = findViewById(R.id.recyclerViewAC);
        result=findViewById(R.id.result);

        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String response=sharedPreferences.getString("platformAC" , "");
        Gson gson = new Gson();
        if(response!=null&&response.length()>0)
            platformAC = gson.fromJson(response, new TypeToken<PlatformAC>(){}.getType());

        ImageView imageView = findViewById(R.id.platformImage);
        final Context context = imageView.getContext();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webString = platformAC.getPlatformURL();
                Helper.openUrl(context,webString);
            }
        });

        final TextView usernameText = findViewById(R.id.platformUser);
        ImageView clearSearch = findViewById(R.id.clearAC);
        final EditText searchId = findViewById(R.id.searchIdAC);
        final EditText searchIndex = findViewById(R.id.searchIndexAC);
        final EditText searchQuery = findViewById(R.id.searchQueryAC);
        final Spinner searchSolveStatus = findViewById(R.id.searchStatusAC);
        final ImageView searchStatusArrow = findViewById(R.id.searchStatusArrowAC);
        result = findViewById(R.id.result);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        final String[] searchStatusValues = getResources().getStringArray(R.array.solveStatus);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,searchStatusValues);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSolveStatus.setAdapter(statusAdapter);

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numOfProblems!=platformAC.getNumberOfProblems()) {
                    View view = AtCoder.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    contestID = "";
                    index = "";
                    query = "";
                    solveState = -1;
                    searchId.setText("");
                    searchIndex.setText("");
                    searchQuery.setText("");
                    searchSolveStatus.setSelection(0);
                }
            }
        });

        searchId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                contestID = searchId.getText().toString();
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchIndex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                index = searchIndex.getText().toString();
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        String username = platformAC.getUsername();
        if(username!=null&&username.length()>0) {
            usernameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webString = platformAC.getUserURL();
                    Helper.openUrl(context,webString);
                }
            });

            searchSolveStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position - 1 != solveState) {
                        ((TextView) view).setText(searchStatusValues[position]);
                        solveState = position - 1;
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
        numOfProblems=platformAC.getNumberOfProblems();
        if(numOfProblems==0)
            new CountDownTimer(90000, 3000) {

            public void onTick(long millisUntilFinished) {
                String response=sharedPreferences.getString("platformAC" , "");
                if(response!=null&&response.length()>0) {
                    Gson gson = new Gson();
                    platformAC = gson.fromJson(response, new TypeToken<PlatformAC>() {}.getType());
                    numOfProblems = platformAC.getNumberOfProblems();
                    progressBar.setVisibility(View.GONE);
                    showProblems(platformAC.getAllProblems());
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
            showProblems(platformAC.getAllProblems());
        }
    }
}
