package com.example.codeset;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdapterContest extends RecyclerView.Adapter<AdapterContest.MyViewHolder> {

    private ArrayList<Contest> contests;
    private Context context;

    public AdapterContest(Context context, ArrayList<Contest> contests){
        this.context=context;
        this.contests=contests;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.contest_card,parent,false);
        return new MyViewHolder(view);
    }

    private void openProblem(String urlString){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(urlString));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Contest contest = contests.get(position);
        String header = contest.getName();
        holder.contestName.setText(header);
        String platform  = contest.getPlatform();
        if(platform.equalsIgnoreCase("codeforces"))
            holder.platformImage.setImageDrawable(context.getDrawable(R.drawable.code_forces_logo));
        else if(platform.equalsIgnoreCase("codechef"))
            holder.platformImage.setImageDrawable(context.getDrawable(R.drawable.code_chef_logo));
        else if(platform.equalsIgnoreCase("atcoder"))
            holder.platformImage.setImageDrawable(context.getDrawable(R.drawable.at_coder_logo));
        else if(platform.equalsIgnoreCase("google"))
            holder.platformImage.setImageDrawable(context.getDrawable(R.drawable.google_logo));
        else if(platform.equalsIgnoreCase("topcoder"))
            holder.platformImage.setImageDrawable(context.getDrawable(R.drawable.top_coder_logo));
        else
            holder.platformImage.setImageDrawable(null);
        String time = contest.getDuration();
        holder.duration.setText(time);
        long startTime = contest.getStartTime();
        long endTime = contest.getEndTime();
        long currTime = System.currentTimeMillis()/1000;
        if(currTime>=startTime&&currTime<=endTime){
            holder.oneContest.setBackground(context.getDrawable(R.drawable.tried_problem));
            holder.contestName.setBackground(context.getDrawable(R.drawable.tried_header));
        }
        else{
            holder.oneContest.setBackground(context.getDrawable(R.drawable.solved_problem));
            holder.contestName.setBackground(context.getDrawable(R.drawable.solved_header));
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yy hh:mm a");
        String start = simpleDateFormat.format(new Date(startTime*1000));
        holder.startTime.setText(start);
        String end = simpleDateFormat.format(new Date(endTime*1000));
        holder.endTime.setText(end);
        final String urlString = contest.getUrl();
        holder.contestCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProblem(urlString);
            }
        });
        holder.contestName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProblem(urlString);
            }
        });
    }

    @Override
    public int getItemCount() {
        return contests.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView platformImage;
        Button contestName;
        TextView startTime,endTime,duration;
        ConstraintLayout oneContest;
        ConstraintLayout contestCard;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            oneContest = itemView.findViewById(R.id.oneContest);
            contestName = itemView.findViewById(R.id.contestName);
            platformImage = itemView.findViewById(R.id.platformImage);
            startTime = itemView.findViewById(R.id.startTime);
            endTime = itemView.findViewById(R.id.endTime);
            duration = itemView.findViewById(R.id.duration);
            contestCard = itemView.findViewById(R.id.contestCard);
        }
    }
}