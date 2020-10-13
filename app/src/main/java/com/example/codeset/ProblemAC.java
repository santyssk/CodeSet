package com.example.codeset;


public class ProblemAC {
    private String contestId;
    private String index;
    private String name;
    private int solveState;
    private String url;

    ProblemAC(String contestId, String index, String name){
        this.contestId=contestId;
        this.index=index;
        this.name=name;
        solveState=0;
        url = "https://atcoder.jp/contests/"+contestId+"/tasks/"+index;
    }

    void setSolveState(int  solveState){
        this.solveState=solveState;
    }

    String getUrl(){
        return url;
    }

    String getContestId(){
        return contestId;
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
}
