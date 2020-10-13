package com.example.codeset;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PlatformAC {

    private String username;
    private ArrayList<ProblemAC> allProblems;
    private int rating;
    private int maxRating;

    PlatformAC(){
        username="";
        allProblems=new ArrayList<>();
        rating=Integer.MIN_VALUE;
        maxRating=rating;
    }

    String getPlatformURL(){
        return "https://atcoder.jp/";
    }

    String getUsername(){
        return username;
    }

    void setUsername(String username){
        this.username=username;
        if(username.length()>0) {
            loadSubmissions();
            loadRating();
        }
        else{
            rating=Integer.MIN_VALUE;
            clearSubmissions();
        }
    }

    int getRating(){
        return rating;
    }

    int getMaxRating(){return maxRating;}

    String getUserURL(){
        return "https://atcoder.jp/users/"+username;
    }

    ArrayList<ProblemAC> getAllProblems(){
        return allProblems;
    }

    static boolean isValidUser(String username){
        String urlString="https://atcoder.jp/users/"+username;
        try {
            Document document = Jsoup.connect(urlString).get();
            String pageText = document.toString();
            if(!pageText.contains("404 Page Not Found"))
                return true;
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "AtCoder isValidUserAC", ex);
        }
        return false;
    }

    void loadProblems(){
        Log.d("CodeSetDebug","Fetching AtCoder Problem");
        String urlString = "https://kenkoooo.com/atcoder/resources/problems.json";
        try {
            URL url = new URL(urlString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            JSONArray response = new JSONArray(responseString.toString());
            for(int i=0;i<response.length();i++){
                JSONObject data = response.getJSONObject(i);
                String contestId="";
                String index="";
                String name="";
                if(data.has("contest_id"))
                    contestId = data.getString("contest_id");
                if(data.has("id"))
                    index = data.getString("id");
                if(data.has("title"))
                    name = data.getString("title");
                ProblemAC problem = new ProblemAC(contestId,index,name);
                allProblems.add(problem);
            }

        } catch(Exception ex) {
            Log.d("CodeSetDebug", "AtCoder loadProblemsAC", ex);
        }
        Log.d("CodeSetDebug","Fetched AtCoder Problem");
    }

    void loadSubmissions(){
        Log.d("CodeSetDebug","Fetching AtCoder Submission");
        if(username==null||username.isEmpty())
            return;
        HashMap<String,Integer> submissions = new HashMap<>();
        String urlString="https://kenkoooo.com/atcoder/atcoder-api/results?user="+username;
        try {
            URL url = new URL(urlString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            JSONArray response = new JSONArray(responseString.toString());
            for(int i=0;i<response.length();i++){
                JSONObject data = response.getJSONObject(i);
                String index = "";
                String result ="X";
                if(data.has("problem_id"))
                    index=data.getString("problem_id");
                if(data.has("result"))
                    result=data.getString("result");
                if(index.length()>0) {
                    if (result.equalsIgnoreCase("AC"))
                        submissions.put(index, 2);
                    else if (!submissions.containsKey(index))
                        submissions.put(index, 1);
                }
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "AtCoder loadSubmissionsAC", ex);
        }
        if(!submissions.isEmpty()) {
            for (ProblemAC p : allProblems) {
                String index = p.getIndex();
                int state = 0;
                if (submissions.containsKey(index))
                    state = submissions.get(index);
                p.setSolveState(state);
            }
        }
        Log.d("CodeSetDebug","Fetched AtCoder Submission");
    }

    void loadRating(){
        Log.d("CodeSetDebug","Fetching AtCoder Rating");
        if(username==null||username.isEmpty())
            return;
        String urlString="https://atcoder.jp/users/"+username;
        rating = Integer.MIN_VALUE;
        try {
            Document document = Jsoup.connect(urlString).get();
            Elements trs = document.select("tr");
            for(Element tr:trs){
                Element th = tr.selectFirst("th");
                if(th.text().equalsIgnoreCase("Rating"))
                    rating = Integer.valueOf(tr.selectFirst("td").text());
                else if(th.text().equalsIgnoreCase("Highest Rating"))
                    maxRating = Integer.valueOf(th.selectFirst("span").text());
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "AtCoder loadRatingAC", ex);
        }
        Log.d("CodeSetDebug","Fetched AtCoder Rating");
    }

    private void clearSubmissions(){
        for (ProblemAC p : allProblems)
            p.setSolveState(0);
    }

    static int getRatingColor(Context context, int rating){
        int ratingColor = context.getColor(R.color.acUnrated);
        if(rating>=2800)
            ratingColor = context.getColor(R.color.acRed);
        else if(rating>=2400)
            ratingColor = context.getColor(R.color.acOrange);
        else if(rating>=2000)
            ratingColor = context.getColor(R.color.acYellow);
        else if(rating>=1600)
            ratingColor = context.getColor(R.color.acBlue);
        else if(rating>=1200)
            ratingColor = context.getColor(R.color.acCyan);
        else if(rating>=800)
            ratingColor = context.getColor(R.color.acGreen);
        else if(rating>=400)
            ratingColor = context.getColor(R.color.acBrown);
        else if(rating>Integer.MIN_VALUE)
            ratingColor = context.getColor(R.color.acGray);
        return ratingColor;
    }

    int getNumberOfProblems(){
        return allProblems.size();
    }

    private ArrayList<ProblemAC> searchByName(ArrayList<ProblemAC> current, String query){
        ArrayList<ProblemAC> filtered = new ArrayList<>();
        query = query.toLowerCase();
        for(ProblemAC p:current){
            if(p.getName().toLowerCase().contains(query))
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemAC> searchByState(ArrayList<ProblemAC> current, int state){
        ArrayList<ProblemAC> filtered = new ArrayList<>();
        for(ProblemAC p:current){
            if(p.getSolveState()==state)
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemAC> searchById(ArrayList<ProblemAC> current, String contestId){
        ArrayList<ProblemAC> filtered = new ArrayList<>();
        for(ProblemAC p:current){
            if(p.getContestId().contains(contestId))
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemAC> searchByIndex(ArrayList<ProblemAC> current, String index){
        ArrayList<ProblemAC> filtered = new ArrayList<>();
        for(ProblemAC p:current){
            if(p.getIndex().contains(index))
                filtered.add(p);
        }
        return filtered;
    }

    ArrayList<ProblemAC> search(String contestId, String index, String query, int state){
         ArrayList<ProblemAC> current = allProblems;
         if(contestId.length()>0)
            current = searchById(current,contestId);
        if(index.length()>0)
            current = searchByIndex(current,index);
        if(query.length()>0)
            current = searchByName(current,query);
        if(state!=-1)
            current = searchByState(current,state);
        return  current;
    }
}
