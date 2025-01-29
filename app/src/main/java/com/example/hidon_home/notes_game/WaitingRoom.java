package com.example.hidon_home.notes_game;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WaitingRoom extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference myRef;
    Button startGameButton;
    TextView roomCodeNumber, playerCountNumber;
    public static int playerNum = -1;
    public static NotesGame notesGame;
    ListView nameList;
    ArrayAdapter<String> adapter;

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
        roomCodeNumber = findViewById(R.id.roomCode);
        playerCountNumber = findViewById(R.id.numberOfPlayers);
        nameList = findViewById(R.id.playersList);
        if (notesGame != null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesGame.getNames());
            nameList.setAdapter(adapter);
        }

        if (MainActivity.isMainPlayer) {
            Random rnd = new Random();
            int roomNumberGen = 10000000 + rnd.nextInt(90000000);
            roomCodeNumber.setText(String.valueOf(roomNumberGen));

            notesGame = new NotesGame(roomNumberGen, 0, new ArrayList<>());
            myRef.child(String.valueOf(notesGame.getRoomNumber())).setValue(notesGame);
            if (nameList.getAdapter() == null) {
                nameList.setAdapter(adapter);
            }
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notesGame.getNames());

            myRef.child(String.valueOf(notesGame.getRoomNumber())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Integer numberOfPlayersSnapshot = snapshot.child("playerCount").getValue(Integer.class);
                    if (numberOfPlayersSnapshot == null) return;

                    notesGame.setPlayerCount(numberOfPlayersSnapshot);
                    if (nameList.getAdapter() != null)
                        adapter.notifyDataSetChanged();
                    playerCountNumber.setText(String.valueOf(notesGame.getPlayerCount()));
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error: " + error.getMessage());
                }
            });

            startGameButton.setVisibility(Button.VISIBLE);
            startGameButton.setOnClickListener(v -> {
            });
        } else {
            roomCodeNumber.setText(String.valueOf(JoinScreen.roomCode));
            myRef.child(String.valueOf(JoinScreen.roomCode)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.getValue(NotesGame.class) == null)
                        return;
                    notesGame = new NotesGame(snapshot.getValue(NotesGame.class));

                    notesGame.setPlayerCount(notesGame.getPlayerCount() + 1);
                    // playerNum will be -1 if he just joined and doesn't have a number yet
                    if (playerNum == -1) {
                        playerNum = notesGame.getPlayerCount();
                        notesGame.addName(JoinScreen.playerName);
                        myRef.child(String.valueOf(notesGame.getRoomNumber())).setValue(notesGame);
                    }
                    if (adapter == null) {
                        adapter = new ArrayAdapter<>(WaitingRoom.this, android.R.layout.simple_list_item_1, notesGame.getNames());
                        nameList.setAdapter(adapter);
                    }
                    adapter.notifyDataSetChanged();
                    playerCountNumber.setText(String.valueOf(notesGame.getPlayerCount()));

                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("Firebase", "Error: " + error.getMessage());
                }
            });
        }


    }
}