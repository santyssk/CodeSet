package com.example.codeset;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterAC extends RecyclerView.Adapter<AdapterAC.MyViewHolder> {

    private ArrayList<ProblemAC> problems;
    private Context context;

    public AdapterAC(Context context, ArrayList<ProblemAC> problems){
        this.context=context;
        this.problems=problems;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.problem_card_ac,parent,false);
        return new MyViewHolder(view);
    }

    private void openProblem(String urlString){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(urlString));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ProblemAC problem = problems.get(position);
        String header = problem.getContestId()+problem.getName();
        holder.title.setText(header);
        int solve = problem.getSolveState();
        if(solve==0) {
            holder.oneProblem.setBackground(context.getDrawable(R.drawable.unsolved_problem));
            holder.title.setBackground(context.getDrawable(R.drawable.unsolved_header));
        }
        else if(solve==1) {
            holder.oneProblem.setBackground(context.getDrawable(R.drawable.tried_problem));
            holder.title.setBackground(context.getDrawable(R.drawable.tried_header));
        }
        else {
            holder.oneProblem.setBackground(context.getDrawable(R.drawable.solved_problem));
            holder.title.setBackground(context.getDrawable(R.drawable.solved_header));
        }
        String index = problem.getIndex();
        holder.index.setText(index);
        final String urlString = problem.getUrl();
        holder.oneProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProblem(urlString);
            }
        });
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openProblem(urlString);
            }
        });
    }

    @Override
    public int getItemCount() {
        return problems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        Button title;
        TextView index;
        ConstraintLayout oneProblem;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            oneProblem = itemView.findViewById(R.id.oneProblem);
            title = itemView.findViewById(R.id.title);
            index = itemView.findViewById(R.id.index);
        }
    }
}