package com.example.hidon_home;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ResultActivity extends AppCompatActivity {
    TextView score, result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        boolean isPlayer1Won = false;
        score = findViewById(R.id.score_text);
        result = findViewById(R.id.explanation_text);

        score.setText("You scored: " +
                (MainActivity.isPlayer1 ? GameControlActivity.game.getPlayersScoreAt(0) : GameControlActivity.game.getPlayersScoreAt(1))
                + "/100");

        if (GameControlActivity.game.getPlayersScoreAt(0) > GameControlActivity.game.getPlayersScoreAt(1))
            isPlayer1Won = true;

        if ((MainActivity.isPlayer1 && isPlayer1Won) || (!MainActivity.isPlayer1 && !isPlayer1Won)) {
            result.setText("You won! Congratulations!");
        } else {
            result.setText("You lost! Better luck next time!");
        }


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference gamesRef = database.getReference("games");
        gamesRef.child(MainActivity.gameID.toString()).removeValue();

    }

    public void onContinueClick(View view) {
        MainActivity.isStarted = false;
        MainActivity.isPlayer1 = false;
        GameControlActivity.game = null;
        GameControlActivity.currentQuestion = 0;
        startActivity(new Intent(this, MainActivity.class));
    }
}