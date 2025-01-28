package com.example.hidon_home.notes_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.hidon_home.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class JoinScreen extends AppCompatActivity {
    EditText roomCode, playerName;
    FirebaseDatabase database;
    DatabaseReference kahootGamesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_screen);

        database = FirebaseDatabase.getInstance();
        kahootGamesRef = database.getReference("kahoot_games");

        roomCode = findViewById(R.id.roomCodeInput);
        playerName = findViewById(R.id.playerNameInput);

    }

    public void onJoinClick(View view) {
        if (!playerName.getText().toString().trim().isEmpty() && !roomCode.getText().toString().trim().isEmpty()) {
            kahootGamesRef.child(roomCode.getText().toString()).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    kahootGamesRef.child(roomCode.getText().toString()).child("players").child(playerName.getText().toString());
                    WaitingRoom.roomNumber = Integer.parseInt(roomCode.getText().toString());
                    startActivity(new Intent(this, WaitingRoom.class));
                } else {
                    roomCode.setError("Room code does not exist");
                }
            });
        } else {
            if (playerName.getText().toString().trim().isEmpty()) {
                playerName.setError("Please enter a name");
            }
            if (roomCode.getText().toString().trim().isEmpty()) {
                roomCode.setError("Please enter a room code");
            }
        }
    }
}