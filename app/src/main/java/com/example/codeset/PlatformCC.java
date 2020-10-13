package com.example.codeset;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;

public class PlatformCC {

    private String username;
    private ArrayList<ProblemCC> allProblems;
    private int rating;
    private int maxRating;

    PlatformCC(){
        username="";
        allProblems=new ArrayList<>();
        rating=Integer.MIN_VALUE;
        maxRating=rating;
    }

    String getPlatformURL(){
        return "https://codechef.com/";
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
        return "https://www.codechef.com/users/"+username;
    }

    ArrayList<ProblemCC> getAllProblems(){
        return allProblems;
    }

    static boolean isValidUser(String username){
        String urlString="https://www.codechef.com/users/"+username;
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
        Log.d("CodeSetDebug","Fetching CodeChef Problem");
        String[] levels = {"school","easy","medium","hard","challenge","extcontest"};
        for(String level:levels) {
            String urlString = "https://www.codechef.com/problems/"+level+"/";
            try {
                Document document = Jsoup.connect(urlString).get();
                Elements elements = document.select("tr.problemrow");
                for(Element problemRow :elements) {
                    String name = "";
                    String code = "";
                    Elements values = problemRow.select("td");
                    if(values.size()>0)
                        name = values.first().text();
                    if(values.size()>1)
                        code = values.get(1).text();
                    ProblemCC problem = new ProblemCC(name,code,level);
                    allProblems.add(problem);
                }
            } catch(Exception ex) {
                Log.d("CodeSetDebug", "CodeChef loadProblemsCC", ex);
            }
        }
        Log.d("CodeSetDebug","Fetched CodeChef Problem");
    }

    void loadSubmissions(){
        Log.d("CodeSetDebug","Fetching CodeChef Submission");
        if(username==null||username.isEmpty())
            return;
        HashMap<String,Integer> submissions = new HashMap<>();
        String urlString="https://www.codechef.com/users/"+username;
        try {
            Document document = Jsoup.connect(urlString).get();
            Element element= document.selectFirst("section.rating-data-section.problems-solved");
            if(element!=null){
                Elements values = element.select("article");
                if(values.size()>0){
                    Elements problems = values.get(0).select("a");
                    for(Element problem:problems)
                        submissions.put(problem.text(),2);
                }
                if(values.size()>1){
                    Elements problems = values.get(1).select("a");
                    for(Element problem:problems)
                        submissions.put(problem.text(),1);
                }
            }

        } catch(Exception ex) {
            Log.d("CodeSetDebug", "CodeForces loadSubmissionsCC", ex);
        }
        if(!submissions.isEmpty()) {
            for (ProblemCC p : allProblems) {
                String code = p.getCode();
                int state = 0;
                if (submissions.containsKey(code))
                    state = submissions.get(code);
                p.setSolveState(state);
            }
        }
        Log.d("CodeSetDebug","Fetched CodeChef Submission");
    }

    void loadRating(){
        Log.d("CodeSetDebug","Fetching CodeChef Rating");
        if(username==null||username.isEmpty())
            return;
        String urlString="https://www.codechef.com/users/"+username;
        try {
            Document document = Jsoup.connect(urlString).get();
            Element ratingCenter = document.selectFirst("div.rating-header.text-center");
            rating = Integer.valueOf(ratingCenter.selectFirst("div.rating-number").text());
            String value = ratingCenter.selectFirst("small").text();
            value = value.replaceAll("\\D+","");
            maxRating = Integer.valueOf(value);
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "CodeChef loadRatingCC", ex);
        }
        Log.d("CodeSetDebug","Fetched CodeChef Rating");
    }

    private void clearSubmissions(){
        for (ProblemCC p : allProblems)
            p.setSolveState(0);
    }

    static int getRatingColor(Context context, int rating){
        int ratingColor = Integer.MIN_VALUE;
        if(rating>=2500)
            ratingColor = context.getColor(R.color.ccRed);
        else if(rating>=2200)
            ratingColor = context.getColor(R.color.ccOrange);
        else if(rating>=2000)
            ratingColor = context.getColor(R.color.ccYellow);
        else if(rating>=1800)
            ratingColor = context.getColor(R.color.ccViolet);
        else if(rating>=1600)
            ratingColor = context.getColor(R.color.ccBlue);
        else if(rating>=1400)
            ratingColor = context.getColor(R.color.ccGreen);
        else if(rating>Integer.MIN_VALUE)
            ratingColor = context.getColor(R.color.ccGray);
        return  ratingColor;
    }

    int getNumberOfProblems(){
        return allProblems.size();
    }

    private ArrayList<ProblemCC> searchByName(ArrayList<ProblemCC> current, String query){
        ArrayList<ProblemCC> filtered = new ArrayList<>();
        query = query.toLowerCase();
        for(ProblemCC p:current){
            if(p.getName().toLowerCase().contains(query))
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemCC> searchByState(ArrayList<ProblemCC> current, int state){
        ArrayList<ProblemCC> filtered = new ArrayList<>();
        for(ProblemCC p:current){
            if(p.getSolveState()==state)
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemCC> searchByLevel(ArrayList<ProblemCC> current, String level){
        ArrayList<ProblemCC> filtered = new ArrayList<>();
        for(ProblemCC p:current){
            if(p.getLevel().equalsIgnoreCase(level))
                filtered.add(p);
        }
        return filtered;
    }

    ArrayList<ProblemCC> search(String query, String level, int state){
        ArrayList<ProblemCC> current = allProblems;
        if(query.length()>0)
            current = searchByName(current,query);
        if(state!=-1)
            current = searchByState(current,state);
        if(!level.equalsIgnoreCase("Any Difficulty"))
            current = searchByLevel(current,level);
        return  current;
    }

}
