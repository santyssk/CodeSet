package com.example.codeset;

public class ProblemSPOJ {
    private String name;
    private String level;
    private int solveState;
    private String url;

    ProblemSPOJ(String name, String level, String url){
        level = level.substring(0, 1).toUpperCase() + level.substring(1);
        this.name=name;
        this.level=level;
        this.solveState=0;
        this.url=url;
    }

    String getName(){
        return name;
    }

    String getLevel(){
        return level;
    }

    String getUrl(){
        return url;
    }

    int getSolveState(){
        return solveState;
    }

    void setSolveState(int solveState){
        this.solveState=solveState;
    }
}
