package com.example.codeset;

import android.content.Context;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class PlatformSPOJ {

    private String username;
    private ArrayList<ProblemSPOJ> allProblems;
    private int rating;
    private int maxRating;

    PlatformSPOJ(){
        username="";
        allProblems=new ArrayList<>();
        rating=Integer.MIN_VALUE;
        maxRating =rating;
    }

    String getPlatformURL(){
        return "https://spoj.com/";
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
        return "https://spoj.com/";
    }

    ArrayList<ProblemSPOJ> getAllProblems(){
        return allProblems;
    }

    static boolean isValidUser(String username){
        return  true;
    }

    void loadProblems(){
        Log.d("CodeSetDebug","Fetching SphereOnlineJudge Problem");
        String[] levels = {"classical","challenge","partial","tutorial","riddle","basics"};
        for(String level:levels) {
            int number = 0;
            while(true) {
                String urlString = "https://www.spoj.com/problems/" + level + "/sort=0,start="+ number;
                try {
                    Document document = Jsoup.connect(urlString).get();
                    Element table = document.selectFirst("tbody");
                    Elements elements = table.select("tr");
                    for (Element problemRow : elements) {
                        String name = "";
                        String url = "";
                        Elements values = problemRow.select("td");
                        if (values.size() > 1) {
                            name = values.get(1).selectFirst("a").text();
                            url = values.get(1).selectFirst("a").absUrl("href");
                        }
                        ProblemSPOJ problem = new ProblemSPOJ(name, level, url);
                        allProblems.add(problem);
                    }
                    Element nextPage = document.selectFirst("ul.pagination");
                    if(nextPage==null)
                        break;
                    Element lastLink = nextPage.select("li").last();
                    if(lastLink!=null){
                        if(lastLink.selectFirst("a")==null)
                            break;
                    }
                    number+=50;
                } catch (Exception ex) {
                    Log.d("CodeSetDebug", "SphereOnlineJudge loadProblemsSPOJ", ex);
                }
            }
        }
        Log.d("CodeSetDebug","Fetched SphereOnlineJudge Problem");
    }

    void loadSubmissions(){
        Log.d("CodeSetDebug","Fetching SphereOnlineJudge Submission");
        /*if(username==null||username.isEmpty())
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
            Log.d("CodeSetDebug", "SphereOnlineJudge loadSubmissionsSPOJ", ex);
        }
        if(!submissions.isEmpty()) {
            for (ProblemAC p : allProblems) {
                String index = p.getIndex();
                int state = 0;
                if (submissions.containsKey(index))
                    state = submissions.get(index);
                p.setSolveState(state);
            }
        }*/
        Log.d("CodeSetDebug","Fetched SphereOnlineJudge Submission");
    }

    void loadRating(){
        Log.d("CodeSetDebug","Fetching SphereOnlineJudge Rating");
        /*if(username==null||username.isEmpty())
            return;
        String urlString="https://atcoder.jp/users/"+username;
        rating = Integer.MIN_VALUE;
        try {
            Document document = Jsoup.connect(urlString).get();
            Elements trs = document.select("tr");
            for(Element tr:trs){
                Element th = tr.selectFirst("th");
                if(th.text().equalsIgnoreCase("Rating")) {
                    rating = Integer.valueOf(tr.selectFirst("td").text());
                    break;
                }
            }
        } catch(Exception ex) {
            Log.d("CodeSetDebug", "SphereOnlineJudge loadRatingSPOJ", ex);
        }*/
        Log.d("CodeSetDebug","Fetched SphereOnlineJudge Rating");
    }

    private void clearSubmissions(){
        for (ProblemSPOJ p : allProblems)
            p.setSolveState(0);
    }

    static  int getRatingColor(Context context, int rating){
        return Integer.MIN_VALUE;
    }

    int getNumberOfProblems(){
        return allProblems.size();
    }

    private ArrayList<ProblemSPOJ> searchByName(ArrayList<ProblemSPOJ> current, String query){
        ArrayList<ProblemSPOJ> filtered = new ArrayList<>();
        query = query.toLowerCase();
        for(ProblemSPOJ p:current){
            if(p.getName().toLowerCase().contains(query))
                filtered.add(p);
        }
        return filtered;
    }

    private ArrayList<ProblemSPOJ> searchByLevel(ArrayList<ProblemSPOJ> current, String level){
        ArrayList<ProblemSPOJ> filtered = new ArrayList<>();
        for(ProblemSPOJ p:current){
            if(p.getLevel().equalsIgnoreCase(level))
                filtered.add(p);
        }
        return filtered;
    }

    ArrayList<ProblemSPOJ> search(String query, String level){
        ArrayList<ProblemSPOJ> current = allProblems;
        if(query.length()>0)
            current = searchByName(current,query);
        if(!level.equalsIgnoreCase("Any Difficulty"))
            current = searchByLevel(current,level);
        return  current;
    }
}
