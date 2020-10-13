package com.example.codeset;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PlatformCF {

    private String username;
    private ArrayList<ProblemCF> allProblems;
    private int rating;
    private int maxRating;

    PlatformCF(){
        username="";
        allProblems=new ArrayList<>();
        rating=Integer.MIN_VALUE;
        maxRating = rating;
    }

    String getPlatformURL(){
        return "https://codeforces.com/";
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
        return "https://codeforces.com/profile/"+username;
    }

    ArrayList<ProblemCF> getAllProblems(){
        return allProblems;
    }

    static boolean isValidUser(String username){
        String urlString="https://codeforces.com/profile/"+username;
        String currUrl = "";
        try {
            Document document = Jsoup.connect(urlString).get();
            currUrl = document.location();
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "CodeChef isValidUserCC", ex);
        }
        return urlString.equalsIgnoreCase(currUrl);
    }

    void loadProblems(){
        Log.d("CodeSetDebug","Fetching CodeForces Problem");
        String urlString="https://codeforces.com/api/problemset.problems";
        try {
            URL url = new URL(urlString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            JSONObject response = new JSONObject(responseString.toString());
            if(response.has("status")) {
                String status = response.getString("status");
                if (status.equalsIgnoreCase("OK")) {
                    response = response.getJSONObject("result");
                    if(response.has("problems")) {
                        JSONArray problems = response.getJSONArray("problems");
                        for (int i = 0; i < problems.length(); i++) {
                            JSONObject data = problems.getJSONObject(i);
                            int contestId = 0;
                            if (data.has("contestId"))
                                contestId = data.getInt("contestId");
                            String index = "X";
                            if (data.has("index"))
                                index = data.getString("index");
                            String name = "XX";
                            if (data.has("name"))
                                name = data.getString("name");
                            int rating = 0;
                            if (data.has("rating"))
                                rating = data.getInt("rating");
                            String tags = "X";
                            if(data.has("tags")) {
                                tags = data.getString("tags");
                                tags = tags.substring(1,tags.length()-1);
                            }
                            ProblemCF problem = new ProblemCF(contestId, index, name, rating, tags);
                            allProblems.add(problem);
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "CodeForces loadProblemsCF", ex);
        }
        Log.d("CodeSetDebug","Fetched CodeForces Problem");
    }

    void loadSubmissions(){
        Log.d("CodeSetDebug","Fetching CodeForces Submission");
        if(username==null||username.isEmpty())
            return;
        HashMap<String,Integer> submissions = new HashMap<>();
        String urlString="https://codeforces.com/api/user.status?handle="+username;
        try {
            URL url = new URL(urlString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            JSONObject response = new JSONObject(responseString.toString());
            if(response.has("status")) {
                String status = response.getString("status");
                if (status.equalsIgnoreCase("OK")) {
                    JSONArray jsonArraySubmissions = response.getJSONArray("result");
                    for(int i=0;i<jsonArraySubmissions.length();i++){
                        JSONObject submission = jsonArraySubmissions.getJSONObject(i);
                        String verdict = "X";
                        if(submission.has("verdict"))
                            verdict = submission.getString("verdict");
                        JSONObject problem = null;
                        if(submission.has("problem"))
                            problem = submission.getJSONObject("problem");
                        String id="";
                        if(problem!=null&&problem.has("contestId")&&problem.has("index"))
                            id = problem.getInt("contestId")+problem.getString("index");
                        if(id.length()>0) {
                            if (verdict.equalsIgnoreCase("OK"))
                                submissions.put(id, 2);
                            else if (!submissions.containsKey(id))
                                submissions.put(id, 1);
                        }
                    }
                }
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "CodeForces loadSubmissionsCF", ex);
        }
        if(!submissions.isEmpty()) {
            for (ProblemCF p : allProblems) {
                String id = p.getContestId() + p.getIndex();
                int state = 0;
                if (submissions.containsKey(id))
                    state = submissions.get(id);
                p.setSolveState(state);
            }
        }
        Log.d("CodeSetDebug","Fetched CodeForces Submission");
    }

    void loadRating(){
        Log.d("CodeSetDebug","Fetching CodeForces Rating");
        if(username==null||username.isEmpty())
            return;
        String urlString="https://codeforces.com/api/user.info?handles="+username;
        try {
            URL url = new URL(urlString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            JSONObject response = new JSONObject(responseString.toString());
            if(response.has("status")) {
                String status = response.getString("status");
                if (status.equalsIgnoreCase("OK")) {
                    JSONArray users = response.getJSONArray("result");
                    JSONObject user = users.getJSONObject(0);
                    if(user.has("rank")&&user.has("rating"))
                        rating = user.getInt("rating");
                    if(user.has("rank")&&user.has("maxRating"))
                        maxRating = user.getInt("maxRating");
                }
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "CodeForces loadRatingCF", ex);
        }
        Log.d("CodeSetDebug","Fetched CodeForces Rating");
    }

    private void clearSubmissions(){
        for (ProblemCF p : allProblems)
            p.setSolveState(0);
    }

    static int getRatingColor(Context context, int rating){
        int ratingColor = Integer.MIN_VALUE;
        if(rating>=3000)
            ratingColor = context.getColor(R.color.legendary);
        else if(rating>=2400)
            ratingColor = context.getColor(R.color.grandMaster);
        else if(rating>=2100)
            ratingColor = context.getColor(R.color.master);
        else if(rating>=1900)
            ratingColor = context.getColor(R.color.candidateMaster);
        else if(rating>=1600)
            ratingColor = context.getColor(R.color.expert);
        else if(rating>=1400)
            ratingColor = context.getColor(R.color.specialist);
        else if(rating>=1200)
            ratingColor = context.getColor(R.color.pupil);
        else if(rating>Integer.MIN_VALUE)
            ratingColor = context.getColor(R.color.newbie);
        return ratingColor;
    }
    
    int getNumberOfProblems(){
        return allProblems.size();
    }
    
    private ArrayList<ProblemCF> searchById(ArrayList<ProblemCF> current, int number){
        ArrayList<ProblemCF> filtered = new ArrayList<>();
        for(ProblemCF p:current){
            if(p.getContestId()==number)
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemCF> searchByIndex(ArrayList<ProblemCF> current, String index){
        ArrayList<ProblemCF> filtered = new ArrayList<>();
        index = index.toUpperCase();
        for(ProblemCF p:current){
            if(p.getIndex().contains(index))
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemCF> searchByRating(ArrayList<ProblemCF> current, int minRating, int maxRating){
        ArrayList<ProblemCF> filtered = new ArrayList<>();
        for(ProblemCF p:current){
            int rating = p.getRating();
            if(rating>=minRating&&rating<=maxRating)
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemCF> searchByNameOrTag(ArrayList<ProblemCF> current, String query){
        ArrayList<ProblemCF> filtered = new ArrayList<>();
        query = query.toLowerCase();
        for(ProblemCF p:current){
            if(p.getName().toLowerCase().contains(query)||p.getTags().toLowerCase().contains(query))
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemCF> searchByState(ArrayList<ProblemCF> current, int state){
        ArrayList<ProblemCF> filtered = new ArrayList<>();
        for(ProblemCF p:current){
            if(p.getSolveState()==state)
                filtered.add(p);
        }
        return filtered;
    }

    ArrayList<ProblemCF> search(int number, String index, int minRating, int maxRating, String query, int state){
        ArrayList<ProblemCF> current = allProblems;
        if(number>0)
            current = searchById(current,number);
        if(index.length()>0)
            current = searchByIndex(current,index);
        if(minRating>0||maxRating<Integer.MAX_VALUE)
            current = searchByRating(current,minRating,maxRating);
        if(query.length()>0)
            current = searchByNameOrTag(current,query);
        if(state!=-1)
            current = searchByState(current,state);
        return  current;
    }
}
