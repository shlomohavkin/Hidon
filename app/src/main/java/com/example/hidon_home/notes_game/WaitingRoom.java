package com.example.hidon_home.notes_game;

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
    List<String> playerNames;

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
        playerNames = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playerNames);
        nameList.setAdapter(adapter);

        if (MainActivity.isMainPlayer) {
            createRoom();
        } else {
            joinRoom();
        }
    }

    private void createRoom() {
        Random rnd = new Random();
        int roomNumberGen = 10000000 + rnd.nextInt(90000000);
        roomCodeNumber.setText(String.valueOf(roomNumberGen));

        notesGame = new NotesGame(roomNumberGen, 0, new ArrayList<>());
        myRef.child(String.valueOf(notesGame.getRoomNumber())).setValue(notesGame);

        myRef.child(String.valueOf(notesGame.getRoomNumber())).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updatePlayerList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });

        startGameButton.setVisibility(Button.VISIBLE);
        startGameButton.setOnClickListener(v -> {
            // Handle game start logic here
        });
    }

    private void joinRoom() {
        roomCodeNumber.setText(String.valueOf(JoinScreen.roomCode));

        myRef.child(String.valueOf(JoinScreen.roomCode)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                notesGame = snapshot.getValue(NotesGame.class);
                if (notesGame == null) return;

                if (playerNum == -1) {
                    notesGame.setPlayerCount(notesGame.getPlayerCount() + 1);
                    playerNum = notesGame.getPlayerCount();

                    if (notesGame.getNames() == null) {
                        notesGame.setNames(new ArrayList<>());
                    }
                    notesGame.addName(JoinScreen.playerName);
                    myRef.child(String.valueOf(notesGame.getRoomNumber())).setValue(notesGame);
                }

                updatePlayerList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void updatePlayerList(DataSnapshot snapshot1) {
        runOnUiThread(() -> {
            if (snapshot1.exists()) {
                NotesGame updatedGame = snapshot1.getValue(NotesGame.class);
                if (updatedGame != null && updatedGame.getNames() != null) {
                    playerNames.clear();
                    playerNames.addAll(updatedGame.getNames());
                    adapter.notifyDataSetChanged();
                    playerCountNumber.setText(String.valueOf(updatedGame.getPlayerCount()));
                }
            }
        });
    }
}
