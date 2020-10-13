package com.example.codeset;

public class Contest{
    private String duration;
    private String url;
    private long startTime;
    private long endTime;
    private String name;
    private String platform;
    Contest(String name, String url, long startTime, long endTime, String duration){
        String[] platforms = {"codeforces","codechef","topcoder","atcoder","google","leetcode"};
        this.name=name;
        this.url=url;
        this.startTime=startTime;
        this.endTime=endTime;
        this.duration=duration;
        this.platform = "other";
        for(String p:platforms)
            if(url.contains(p)){
                this.platform=p;
                break;
            }
    }

    String getDuration(){
        return duration;
    }

    String getName(){
        return name;
    }

    String getUrl(){
        return url;
    }

    String getPlatform(){
        return platform;
    }

    long getStartTime(){
        return startTime;
    }

    long getEndTime(){return endTime;}
}
