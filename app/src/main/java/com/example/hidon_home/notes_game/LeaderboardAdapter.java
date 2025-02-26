package com.example.hidon_home.notes_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hidon_home.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<Map.Entry<String, Integer>> localDataSet;
    int currentScore;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView rankTextView;
        private TextView usernameTextView;
        private TextView pointsTextView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            rankTextView = (TextView) view.findViewById(R.id.tvRank);
            usernameTextView = (TextView) view.findViewById(R.id.tvUsername);
            pointsTextView = (TextView) view.findViewById(R.id.tvPoints);
        }

        public TextView getRankTextView() {
            return this.rankTextView;
        }
        public TextView getUsernameTextView() {
            return this.usernameTextView;
        }
        public TextView getPointsTextView() {
            return this.pointsTextView;
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public LeaderboardAdapter(List<Map.Entry<String, Integer>> dataSet) {
        this.localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_leaderboard_adapter, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String rank = String.valueOf(position + 1);
        String username = localDataSet.get(position).getKey();
        String points = String.valueOf(localDataSet.get(position).getValue());
        int newScore = localDataSet.get(position).getValue();

        viewHolder.getRankTextView().setText(rank);
        viewHolder.getUsernameTextView().setText(username);

        ValueAnimator animator = ValueAnimator.ofInt(currentScore, newScore);
        animator.setDuration(500);
        animator.addUpdateListener(animation ->
                viewHolder.getPointsTextView().setText(animation.getAnimatedValue().toString()));
        animator.start();
        currentScore = newScore;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}