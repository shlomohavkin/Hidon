package com.example.hidon_home.notes_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hidon_home.Game;
import com.example.hidon_home.R;
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
    private TextView countdownText;
    private static final long START_TIME_IN_MILLIS = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        database = FirebaseDatabase.getInstance();
        kahootGamesRef = database.getReference("kahoot_games");
        leaderboard = new ArrayList<>();
        countdownText = findViewById(R.id.countdownText);


        // Get leaderboard from database
        kahootGamesRef.child(String.valueOf(JoinScreen.roomCode)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                ArrayList<Integer> scores = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().child("game").child("playersScore").getChildren()) {
                    scores.add(snapshot.getValue(Integer.class));
                }
                ArrayList<String> names = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().child("names").getChildren()) {
                    names.add(snapshot.getValue(String.class));
                }

                for (int i = 0; i < scores.size(); i++) {
                    leaderboard.add(new AbstractMap.SimpleEntry<>(names.get(i), scores.get(i)));
                }
                leaderboard.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

                populateLeaderboard(leaderboard);

                NotesGame notesGame = task.getResult().getValue(NotesGame.class);
                ArrayList<Game.PlayerState> playerStates = new ArrayList<>();
                for (int i = 0; i < notesGame.getPlayerCount(); i++) {
                    Game.PlayerState playerState = new Game.PlayerState(0, false, 0);
                    playerState.setAnswerChosen(4);
                    playerStates.add(playerState);
                }
                kahootGamesRef.child(String.valueOf(JoinScreen.roomCode)).child("game").child("playersState").setValue(playerStates);

                startCountdown();
            } else {
                // Handle error
            }
        });


    }

    private void populateLeaderboard(List<Map.Entry<String, Integer>> leaderboard) {
        LinearLayout leaderboardContainer = findViewById(R.id.leaderboard_container);

        leaderboardContainer.removeAllViews();

        for (Map.Entry<String, Integer> entry : leaderboard) {
            LinearLayout row = new LinearLayout(this);
            if (entry.getKey().equals(JoinScreen.playerName))
                row.setBackground(getDrawable(R.drawable.rounded_corners_background_highlighted));
            else
                row.setBackground(getDrawable(R.drawable.rounded_corners_background_not_highlighted));
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setPadding(32, 32, 32, 32);


            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rowParams.setMargins(0, 8, 0, 8); // 8dp top and bottom margin
            row.setLayoutParams(rowParams);

            TextView nameTextView = new TextView(this);
            nameTextView.setText(entry.getKey());
            nameTextView.setTextSize(20);
            nameTextView.setTextColor(getResources().getColor(android.R.color.black));

            LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            nameTextView.setLayoutParams(nameParams);

            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(String.valueOf(entry.getValue()));
            scoreTextView.setTextSize(20);
            scoreTextView.setTextColor(getResources().getColor(android.R.color.black));

            LinearLayout.LayoutParams scoreParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            scoreTextView.setLayoutParams(scoreParams);

            row.addView(nameTextView);
            row.addView(scoreTextView);

            leaderboardContainer.addView(row);
        }
    }

    private void startCountdown() {
        // Update the TextView with the remaining seconds
        // Set countdown to 0 when finished
        new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining seconds
                long secondsLeft = millisUntilFinished / 1000;
                countdownText.setText(String.valueOf(secondsLeft));
            }

            @Override
            public void onFinish() {
                // Set countdown to 0 when finished
                countdownText.setText("0");
                leaderboard.clear();
                startActivity(new Intent(LeaderboardActivity.this, NotesGameControlActivity.class));
            }
        }.start();
    }
}