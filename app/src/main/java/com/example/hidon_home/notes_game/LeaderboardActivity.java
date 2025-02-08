package com.example.hidon_home.notes_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hidon_home.R;
import com.example.hidon_home.hidon.AmericanQuestionActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {
    List<Map.Entry<String, Integer>> leaderboard;

    FirebaseDatabase database;
    DatabaseReference kahootGamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        database = FirebaseDatabase.getInstance();
        kahootGamesRef = database.getReference("kahoot_games");
        leaderboard = new ArrayList<>();

        // Get leaderboard from database
        kahootGamesRef.child(String.valueOf(JoinScreen.roomCode)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                ArrayList<Integer> scores = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().child("game").child("playersScore").getChildren()) {
                    scores.add(snapshot.getValue(Integer.class));
                }
                ArrayList<String> names = new ArrayList<>();
                names.add("Leader");
                for (DataSnapshot snapshot : task.getResult().child("names").getChildren()) {
                    names.add(snapshot.getValue(String.class));
                }

                for (int i = 0; i < scores.size(); i++) {
                    leaderboard.add(new AbstractMap.SimpleEntry<>(names.get(i), scores.get(i)));
                }
                leaderboard.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                populateLeaderboard(leaderboard);

                new Handler().postDelayed(() -> {
                    leaderboard.clear();
                    startActivity(new Intent(this, NotesGameControlActivity.class));
                }, 3000);
            } else {
                // Handle error
            }
        });


    }

    private void populateLeaderboard(List<Map.Entry<String, Integer>> leaderboard) {
        // Get the container LinearLayout from your XML (the one with id "leaderboard_container")
        LinearLayout leaderboardContainer = findViewById(R.id.leaderboard_container);

        // Clear any existing views
        leaderboardContainer.removeAllViews();

        // Loop through each entry in the sorted list
        for (Map.Entry<String, Integer> entry : leaderboard) {
            // Create a new horizontal LinearLayout for this row
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);

            // Create layout parameters for the row with margins
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 8, 0, 8); // 8dp top and bottom margin
            row.setLayoutParams(rowParams);

            // Create a TextView for the player's name
            TextView nameTextView = new TextView(this);
            nameTextView.setText(entry.getKey());
            nameTextView.setTextSize(18);
            nameTextView.setTextColor(getResources().getColor(android.R.color.black));
            // Use layout weight so the name takes available space
            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            nameTextView.setLayoutParams(nameParams);

            // Create a TextView for the player's score
            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(String.valueOf(entry.getValue()));
            scoreTextView.setTextSize(18);
            scoreTextView.setTextColor(getResources().getColor(android.R.color.black));
            // Use wrap_content for the score so it only takes as much space as needed
            LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            scoreTextView.setLayoutParams(scoreParams);

            // Add the name and score TextViews to the row
            row.addView(nameTextView);
            row.addView(scoreTextView);

            // Add the row to the leaderboard container
            leaderboardContainer.addView(row);
        }
    }
}