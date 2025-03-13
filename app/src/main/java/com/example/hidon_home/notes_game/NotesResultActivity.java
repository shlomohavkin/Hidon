package com.example.hidon_home.notes_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NotesResultActivity extends AppCompatActivity {
    List<Map.Entry<String, Integer>> leaderboard;
    TextView playerPlace, playerScore;
    FirebaseDatabase database;
    DatabaseReference kahootGamesRef;
    Button backMenuButton;
    int score;
    int ranking;
    private static final long START_TIME_IN_MILLIS = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_result);

        database = FirebaseDatabase.getInstance();
        kahootGamesRef = database.getReference("kahoot_games");
        leaderboard = new ArrayList<>();
        backMenuButton = findViewById(R.id.back_to_menu_button);
        playerPlace = findViewById(R.id.player_place);
        playerScore = findViewById(R.id.player_score);

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
            } else {
                // Handle error
            }
        });

        backMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isStarted = false;
                MainActivity.isPlayer1 = false;
                MainActivity.isMainPlayer = false;
                MainActivity.isNotesGame = false;
                NotesGameControlActivity.game = null;
                NotesGameControlActivity.currentQuestion = 0;
                WaitingRoom.notesGame = null;
                WaitingRoom.pickedQuestioner = null;
                WaitingRoom.playerNum = -1;
                if (GameQuestionsActivity.isAutoGenQuestionerChosen) {
                    kahootGamesRef.child(String.valueOf(JoinScreen.roomCode)).removeValue();
                    // if the questions are generated automatically then remove the game from the database from the user and not the host
                }
                startActivity(new Intent(NotesResultActivity.this, MainActivity.class));
            }
        });
    }

    private void populateLeaderboard(List<Map.Entry<String, Integer>> leaderboard) {
        LinearLayout leaderboardContainer = findViewById(R.id.leaderboard_container);

        leaderboardContainer.removeAllViews();

        for (Map.Entry<String, Integer> entry : leaderboard) {
            if (entry.getKey().equals(JoinScreen.playerName)) {
                score = entry.getValue();
                ranking = leaderboard.indexOf(entry) + 1;
                String rankingString = ranking == 1 ? "1st" : ranking == 2 ? "2nd" : ranking == 3 ? "3rd" : (String.valueOf(ranking) + "th");
                playerPlace.setText("You Placed: " + rankingString);
                playerScore.setText("Score: " + String.valueOf(score) + " points");
            }

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
            scoreTextView.setText(String.valueOf(entry.getValue()) + " points");
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
}