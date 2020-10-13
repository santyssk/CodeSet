package com.example.codeset;

public class ProblemCC {
    private String code;
    private String name;
    private String level;
    private int solveState;
    private String url;

    ProblemCC(String name, String code, String level){
        if(level.equalsIgnoreCase("school"))
            level="Beginner";
        else if(level.equalsIgnoreCase("extcontest"))
            level="Unknown";
        else
            level = level.substring(0, 1).toUpperCase() + level.substring(1);
        this.code=code;
        this.name=name;
        this.level=level;
        this.solveState=0;
        this.url="https://www.codechef.com/problems/"+code;
    }

    String getCode(){
        return code;
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
