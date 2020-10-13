package com.example.codeset;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Helper {

    static boolean isInternetConnected() {
        try {
            String command = "ping -c 1 google.com";
            return (Runtime.getRuntime().exec(command).waitFor() == 0);
        } catch (Exception e) {
            return false;
        }
    }

    static void setScheduleProblem(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulerProblemLoader.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        long interval = AlarmManager.INTERVAL_HOUR*6;
        if(alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
            long startTime = System.currentTimeMillis()+interval;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        }
    }

    static void setScheduleSubmission(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulerSubmissionLoader.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, 0);
        long interval = AlarmManager.INTERVAL_HOUR;
        if(alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
            long startTime = System.currentTimeMillis()+interval;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        }
    }

    static void setScheduleRating(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulerRatingLoader.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 2, intent, 0);
        long interval = AlarmManager.INTERVAL_HOUR*4;
        if(alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
            long startTime = System.currentTimeMillis()+AlarmManager.INTERVAL_HOUR*2;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        }
    }

    static void setScheduleContest(Context context){
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SchedulerContestLoader.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 3, intent, 0);
        long interval = AlarmManager.INTERVAL_HOUR*4;
        if(alarmManager!=null) {
            alarmManager.cancel(pendingIntent);
            long startTime = System.currentTimeMillis()+interval;
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime, interval, pendingIntent);
        }
    }

    static private void cancelCurrentNotifications(Context context, int lastRequestCode){
        for(int i=5;i<lastRequestCode;i++) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, Remind.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,i, intent, 0);
            if(alarmManager!=null)
                alarmManager.cancel(pendingIntent);
        }
    }

    static private int setNotifications(Context context, ArrayList<Contest> contests){
        int lastRequestCode = 5;
        for(int i=0;i<contests.size();i++) {
            Contest contest = contests.get(i);
            long currTime = System.currentTimeMillis()/1000;
            if(contest.getStartTime()<currTime)
                continue;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, Remind.class);
            intent.putExtra("requestCode", lastRequestCode);
            intent.putExtra("platform", contest.getPlatform());
            intent.putExtra("title",contest.getName());
            intent.putExtra("url",contest.getUrl());
            intent.putExtra("startTime",contest.getStartTime());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,lastRequestCode, intent, 0);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                long startTime = contest.getStartTime()-AlarmManager.INTERVAL_HALF_HOUR/1000;
                //Log.d("CodeSetDebug",currTime+" "+startTime);
                if(currTime>startTime)
                    startTime=currTime;
                alarmManager.set(AlarmManager.RTC_WAKEUP,startTime*1000,pendingIntent);
                lastRequestCode++;
            }
        }
        return lastRequestCode;
    }

    static void loadSpojIfEmpty(Context context){
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadProblemsIfEmpty No internet");
            return;
        }SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        PlatformSPOJ platformSPOJ = new PlatformSPOJ();
        String jsonString = sharedPreferences.getString("platformSPOJ","");
        if(jsonString!=null&&jsonString.length()==0) {
            platformSPOJ.loadProblems();
            jsonString = gson.toJson(platformSPOJ);
            sharedPreferences.edit().putString("platformSPOJ", jsonString).apply();
        }
    }

    static void loadSpoj(Context context) {
        boolean internet = isInternetConnected();
        if (!internet) {
            Log.d("CodeSerDebug", "loadSpoj No internet");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", context.MODE_PRIVATE);
        Gson gson = new Gson();
        PlatformSPOJ platformSPOJ = new PlatformSPOJ();
        String jsonString = sharedPreferences.getString("platformSPOJ", "");
        if (jsonString != null && jsonString.length() > 0)
            platformSPOJ = gson.fromJson(jsonString, new TypeToken<PlatformCF>() {
            }.getType());
        platformSPOJ.loadProblems();
        jsonString = gson.toJson(platformSPOJ);
        sharedPreferences.edit().putString("platformSPOJ", jsonString).apply();
    }

    static void loadProblemsIfEmpty(Context context){
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadProblemsIfEmpty No internet");
            return;
        }SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString;

        PlatformCF platformCF = new PlatformCF();
        jsonString = sharedPreferences.getString("platformCF","");
        if(jsonString!=null&&jsonString.length()==0) {
            platformCF.loadProblems();
            jsonString = gson.toJson(platformCF);
            sharedPreferences.edit().putString("platformCF", jsonString).apply();
        }

        PlatformCC platformCC = new PlatformCC();
        jsonString = sharedPreferences.getString("platformCC","");
        if(jsonString!=null&&jsonString.length()==0) {
            platformCC.loadProblems();
            jsonString = gson.toJson(platformCC);
            sharedPreferences.edit().putString("platformCC", jsonString).apply();
        }

        PlatformAC platformAC = new PlatformAC();
        jsonString = sharedPreferences.getString("platformAC","");
        if(jsonString!=null&&jsonString.length()==0) {
            platformAC.loadProblems();
            jsonString = gson.toJson(platformAC);
            sharedPreferences.edit().putString("platformAC", jsonString).apply();
        }
    }

    static void loadProblems(Context context){
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadProblems No internet");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString;

        PlatformCF platformCF = new PlatformCF();
        jsonString = sharedPreferences.getString("platformCF","");
        if(jsonString!=null&&jsonString.length()>0)
            platformCF = gson.fromJson(jsonString, new TypeToken<PlatformCF>(){}.getType());
        platformCF.loadProblems();
        jsonString = gson.toJson(platformCF);
        sharedPreferences.edit().putString("platformCF", jsonString).apply();

        PlatformCC platformCC = new PlatformCC();
        jsonString = sharedPreferences.getString("platformCC","");
        if(jsonString!=null&&jsonString.length()>0)
            platformCC = gson.fromJson(jsonString, new TypeToken<PlatformCC>(){}.getType());
        platformCC.loadProblems();
        jsonString = gson.toJson(platformCC);
        sharedPreferences.edit().putString("platformCC", jsonString).apply();

        PlatformAC platformAC = new PlatformAC();
        jsonString = sharedPreferences.getString("platformAC","");
        if(jsonString!=null&&jsonString.length()>0)
            platformAC = gson.fromJson(jsonString, new TypeToken<PlatformAC>(){}.getType());
        platformAC.loadProblems();
        jsonString = gson.toJson(platformAC);
        sharedPreferences.edit().putString("platformAC", jsonString).apply();
    }

    static void loadContestsIfEmpty(Context context){
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadContestsIfEmpty No internet");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        String response=sharedPreferences.getString("allContests" , "");
        if(response!=null&&response.length()==0)
            loadContests(context);
    }

    static void loadContests(Context context){
        //old
        //https://code-carrot.herokuapp.com/contests
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadContests No internet");
            return;
        }
        Log.d("CodeSetDebug","Fetching Contests");
        String urlString="https://code-carrot.herokuapp.com/contests";
        ArrayList<Contest> allContests = new ArrayList<>();
        try {
            URL url = new URL(urlString);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder responseString = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                responseString.append(line);
            }
            JSONObject response = new JSONObject(responseString.toString());
            if(response.has("result")){
                JSONArray result = response.getJSONArray("result");
                for(int i=0;i<result.length();i++){
                    JSONObject data = result.getJSONObject(i);
                    String name = "";
                    String duration = "";
                    String link ="";
                    long startTime=0;
                    long endTime=0;
                    if(data.has("event"))
                        name = data.getString("event");
                    if(data.has("href"))
                        link = data.getString("href");
                    if(data.has("duration"))
                        duration = data.getString("duration");
                    if(data.has("stamp"))
                        startTime = (long) data.getDouble("stamp");
                    if(data.has("endstamp"))
                        endTime = (long) data.getDouble("endstamp");
                    long diff = endTime-startTime;
                    if(diff<=1036800){
                        Contest contest = new Contest(name,link,startTime,endTime,duration);
                        allContests.add(contest);
                    }
                }
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "loadContests", ex);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        long currentTime = System.currentTimeMillis()/1000;
        String response=sharedPreferences.getString("allContests" , "");
        if(response!=null&&response.length()>0){
            ArrayList<Contest> old = gson.fromJson(response, new TypeToken<List<Contest>>() {}.getType());
            for(Contest o:old)
                if(o.getEndTime()>currentTime) {
                    boolean included = false;
                    for(Contest n:allContests)
                        if(o.getName().equalsIgnoreCase(n.getName())){
                            included=true;
                            break;
                        }
                    if(!included)
                        allContests.add(o);
                }
        }
        Collections.sort(allContests, new Comparator<Contest>() {
            @Override
            public int compare(Contest c1, Contest c2) {
                return (int) (c1.getStartTime()-c2.getStartTime());
            }
        });

        int lastRequestCode = sharedPreferences.getInt("lastRequestCode",5);
        cancelCurrentNotifications(context,lastRequestCode);
        lastRequestCode = setNotifications(context,allContests);
        sharedPreferences.edit().putInt("lastRequestCode",lastRequestCode).apply();
        String jsonString = gson.toJson(allContests);
        sharedPreferences.edit().putString("allContests", jsonString).apply();
        Log.d("CodeSetDebug","Fetched Contests");
    }

    static void loadSubmissions(Context context){
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadSubmissions No internet");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString;

        PlatformCF platformCF;
        jsonString = sharedPreferences.getString("platformCF","");
        if(jsonString!=null&&jsonString.length()>0) {
            platformCF = gson.fromJson(jsonString, new TypeToken<PlatformCF>() {}.getType());
            platformCF.loadSubmissions();
            jsonString = gson.toJson(platformCF);
            sharedPreferences.edit().putString("platformCF", jsonString).apply();
        }

        PlatformCC platformCC;
        jsonString = sharedPreferences.getString("platformCC","");
        if(jsonString!=null&&jsonString.length()>0) {
            platformCC = gson.fromJson(jsonString, new TypeToken<PlatformCC>() {}.getType());
            platformCC.loadSubmissions();
            jsonString = gson.toJson(platformCC);
            sharedPreferences.edit().putString("platformCC", jsonString).apply();
        }

        PlatformAC platformAC;
        jsonString = sharedPreferences.getString("platformAC","");
        if(jsonString!=null&&jsonString.length()>0) {
            platformAC = gson.fromJson(jsonString, new TypeToken<PlatformAC>() {}.getType());
            platformAC.loadSubmissions();
            jsonString = gson.toJson(platformAC);
            sharedPreferences.edit().putString("platformAC", jsonString).apply();
        }
    }

    static void loadRatings(Context context){
        boolean internet = isInternetConnected();
        if(!internet){
            Log.d("CodeSerDebug","loadRatings No internet");
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref",context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonString;

        PlatformCF platformCF;
        jsonString = sharedPreferences.getString("platformCF","");
        if(jsonString!=null&&jsonString.length()>0) {
            platformCF = gson.fromJson(jsonString, new TypeToken<PlatformCF>() {}.getType());
            platformCF.loadRating();
            jsonString = gson.toJson(platformCF);
            sharedPreferences.edit().putString("platformCF", jsonString).apply();
        }

        PlatformCC platformCC;
        jsonString = sharedPreferences.getString("platformCC","");
        if(jsonString!=null&&jsonString.length()>0) {
            platformCC = gson.fromJson(jsonString, new TypeToken<PlatformCC>() {}.getType());
            platformCC.loadRating();
            jsonString = gson.toJson(platformCC);
            sharedPreferences.edit().putString("platformCC", jsonString).apply();
        }

        PlatformAC platformAC;
        jsonString = sharedPreferences.getString("platformAC","");
        if(jsonString!=null&&jsonString.length()>0) {
            platformAC = gson.fromJson(jsonString, new TypeToken<PlatformAC>() {}.getType());
            platformAC.loadRating();
            jsonString = gson.toJson(platformAC);
            sharedPreferences.edit().putString("platformAC", jsonString).apply();
        }
    }

    static void errorMessage(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which){
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    
    static void openUrl(Context context, String url){
        boolean internet = Helper.isInternetConnected();
        if(!internet){
            String title = "No Internet";
            String message = "Could not connect to "+url+" Please connect to internet to fetch data.";
            errorMessage(context,title,message);
        }
        else{
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        }
    }

}
