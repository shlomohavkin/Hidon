package com.example.hidon_home.notes_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.hidon_home.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinScreenActivity extends AppCompatActivity {
    EditText roomCodeText, playerNameText;
    FirebaseDatabase database;
    DatabaseReference kahootGamesRef;
    public static int roomCode;
    public static String playerName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_screen);

        database = FirebaseDatabase.getInstance();
        kahootGamesRef = database.getReference("kahoot_games");

        roomCodeText = findViewById(R.id.roomCodeInput);
        playerNameText = findViewById(R.id.playerNameInput);

    }

    public void onJoinClick(View view) {
        if (!playerNameText.getText().toString().trim().isEmpty() && !roomCodeText.getText().toString().trim().isEmpty()) {
            kahootGamesRef.child(roomCodeText.getText().toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    kahootGamesRef.child(roomCodeText.getText().toString()).child("players").child(playerNameText.getText().toString());
                    roomCode = Integer.parseInt(roomCodeText.getText().toString());
                    playerName = playerNameText.getText().toString();
                    startActivity(new Intent(this, WaitingRoomActivity.class));
                } else {
                    roomCodeText.setError("Room code does not exist");
                }
            });
        } else {
            if (playerNameText.getText().toString().trim().isEmpty()) {
                playerNameText.setError("Please enter a name");
            }
            if (roomCodeText.getText().toString().trim().isEmpty()) {
                roomCodeText.setError("Please enter a room code");
            }
        }
    }
}