package com.example.codeset;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
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

public class CodeForces extends AppCompatActivity {

    PlatformCF platformCF = new PlatformCF();
    RecyclerView recyclerViewCF;
    TextView result;
    SharedPreferences sharedPreferences;

    int numOfProblems = 0;
    int iD = 0;
    String index = "";
    int minRating = 0;
    int maxRating = Integer.MAX_VALUE;
    String query = "";
    int solveState = -1;

    protected void setUsername(TextView usernameText){
        String username = platformCF.getUsername();
        if(username==null||username.length()==0)
            return;
        int rating = platformCF.getRating();
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

    protected  void showProblems(ArrayList<ProblemCF> current){
        String s = "CodeForces There are " + numOfProblems + " problems.";
        Log.d("CodeSetDebug",s);
        AdapterCF myAdapter = new AdapterCF(this, current);
        recyclerViewCF.setAdapter(myAdapter);
        recyclerViewCF.setLayoutManager(new LinearLayoutManager(this));
        if(numOfProblems>0)
            result.setVisibility(View.GONE);
        else {
            result.setVisibility(View.VISIBLE);
            result.setText(getString(R.string.zeroResultProblem));
        }
    }

    protected void search(){
        ArrayList<ProblemCF> current = platformCF.search(iD,index,minRating,maxRating,query,solveState);
        numOfProblems = current.size();
        showProblems(current);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_codeforces);
        recyclerViewCF = findViewById(R.id.recyclerViewCF);
        sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        String response=sharedPreferences.getString("platformCF" , "");
        Gson gson = new Gson();
        if(response!=null&&response.length()>0)
            platformCF = gson.fromJson(response, new TypeToken<PlatformCF>(){}.getType());

        ImageView imageView = findViewById(R.id.platformImage);
        final Context context = imageView.getContext();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String webString = platformCF.getPlatformURL();
                Helper.openUrl(context,webString);
            }
        });

        final TextView usernameText = findViewById(R.id.platformUser);
        ImageView clearSearch = findViewById(R.id.clearCF);
        final EditText searchId = findViewById(R.id.searchIdCF);
        final EditText searchIndex = findViewById(R.id.searchIndexCF);
        final EditText searchMinRating = findViewById(R.id.searchMinRatingCF);
        final EditText searchMaxRating = findViewById(R.id.searchMaxRatingCF);
        final EditText searchQuery = findViewById(R.id.searchQueryCF);
        final Spinner searchSolveStatus = findViewById(R.id.searchStatusCF);
        final ImageView searchStatusArrow = findViewById(R.id.searchStatusArrowCF);
        result = findViewById(R.id.result);
        final ProgressBar progressBar = findViewById(R.id.progressBar);

        final String[] searchStatusValues = getResources().getStringArray(R.array.solveStatus);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,searchStatusValues);
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSolveStatus.setAdapter(statusAdapter);

        String username = platformCF.getUsername();
        if(username!=null&&username.length()>0) {
            usernameText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String webString = platformCF.getUserURL();
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

        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(numOfProblems!=platformCF.getNumberOfProblems()) {
                    View view = CodeForces.this.getCurrentFocus();
                    if(view!=null){
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    iD = 0;
                    index = "";
                    minRating = 0;
                    maxRating = Integer.MAX_VALUE;
                    query = "";
                    solveState = -1;
                    searchId.setText("");
                    searchIndex.setText("");
                    searchMinRating.setText("");
                    searchMaxRating.setText("");
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
                if(searchId.getText().toString().length()!=0)
                    iD = Integer.valueOf(searchId.getText().toString());
                else
                    iD=0;
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

        searchMaxRating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchMinRating.getText().toString().length()!=0)
                    minRating = Integer.valueOf(searchMinRating.getText().toString());
                else
                    minRating = 0;
                search();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchMaxRating.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(searchMaxRating.getText().toString().length()!=0)
                    maxRating = Integer.valueOf(searchMaxRating.getText().toString());
                else
                    maxRating = Integer.MAX_VALUE;
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

        numOfProblems = platformCF.getNumberOfProblems();
        setUsername(usernameText);
        if(numOfProblems==0)
            new CountDownTimer(90000, 3000) {

                public void onTick(long millisUntilFinished) {
                    String response=sharedPreferences.getString("platformCF" , "");
                    if(response!=null&&response.length()>0) {
                        Gson gson = new Gson();
                        platformCF = gson.fromJson(response, new TypeToken<PlatformCF>() {}.getType());
                        numOfProblems = platformCF.getNumberOfProblems();
                        progressBar.setVisibility(View.GONE);
                        showProblems(platformCF.getAllProblems());
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
            showProblems(platformCF.getAllProblems());
        }
    }
}
