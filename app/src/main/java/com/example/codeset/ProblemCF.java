package com.example.codeset;


public class ProblemCF {
    private int contestId;
    private String index;
    private String name;
    private int rating;
    private String tags;
    private int solveState;
    private String url;

    ProblemCF(int contestId, String index, String name, int rating, String tags){
        this.contestId=contestId;
        this.index=index;
        this.name=name;
        this.rating=rating;
        this.tags=tags;
        solveState=0;
        url = "https://codeforces.com/problemset/problem/"+contestId+"/"+index;
    }

    void setSolveState(int  solveState){
        this.solveState=solveState;
    }

    String getUrl(){
        return url;
    }

    int getContestId(){
        return contestId;
    }

    int getRating(){
        return rating;
    }

    int getSolveState(){
        return solveState;
    }

    String getIndex(){
        return index;
    }

    String getName(){
        return name;
    }

    String getTags(){
        return tags;
    }
}
