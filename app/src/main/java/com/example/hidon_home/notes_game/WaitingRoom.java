package com.example.hidon_home.notes_game;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class WaitingRoom extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("kahoot_games");

        startGameButton = findViewById(R.id.startGameButton);


        Random rnd = new Random();
        int roomNumber = 10000000 + rnd.nextInt(90000000);

        myRef.child(String.valueOf(roomNumber)).child("numPlayers").setValue(0);

        if (MainActivity.isMainPlayer == true) {
            startGameButton.setVisibility(Button.VISIBLE);
        }

    }
}