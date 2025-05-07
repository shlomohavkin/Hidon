package com.example.hidon_home.notes_game;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.hidon_home.R;
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
     * @param dataSet containing the data to populate views to be used
     * by RecyclerView, with the players names, and their scores
     */
    public LeaderboardAdapter(List<Map.Entry<String, Integer>> dataSet) {
        this.localDataSet = dataSet;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_leaderboard_adapter, viewGroup, false);

        return new ViewHolder(view);
    }

    /**
     * Replace the contents of a view
     *
     * @param viewHolder The ViewHolder which should be updated to
     * display the contents of the item at the given position
     * in the data set
     * @param position The position of the item within the adapter's
     * data set
     */
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String rank = String.valueOf(position + 1);
        String username = localDataSet.get(position).getKey();
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

    // Return the size of your dataset
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}