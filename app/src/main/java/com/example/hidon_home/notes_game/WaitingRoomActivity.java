package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.util.Random;

public class WaitingRoomActivity extends AppCompatActivity {
    FirebaseDatabase database;
    DatabaseReference kahootGamesRef;
    Button startGameButton;
    TextView roomCodeNumber, playerCountNumber;
    public static int playerNum = -1;
    public static NotesGame notesGame;
    ListView nameList;
    ArrayAdapter<String> adapter;
    LinearLayout waitingLayout;
    ArrayList<String> playerNames;
    ValueEventListener listener;
    static Questioneer pickedQuestioner;

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
        kahootGamesRef = database.getReference("kahoot_games");

        startGameButton = findViewById(R.id.startGameButton);
        roomCodeNumber = findViewById(R.id.roomCode);
        playerCountNumber = findViewById(R.id.numberOfPlayers);
        nameList = findViewById(R.id.playersList);
        waitingLayout = findViewById(R.id.waitingLayout);

        playerNames = new ArrayList<>();
        adapter = new WaitingScreenPlayerListAdapter(this, playerNames);
        nameList.setAdapter(adapter);

        if (MainActivity.isMainPlayer) {
            createRoom();
        }
        roomCodeNumber.setText(String.valueOf(JoinScreenActivity.roomCode));


        // listener to check if the game has started by the host or if the player has joined the game
        kahootGamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).addValueEventListener(listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                notesGame = snapshot.getValue(NotesGame.class);
                if (notesGame == null) return;

                if (notesGame.getIsStarted()) {
                    kahootGamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).removeEventListener(this);
                    startActivity(new Intent(WaitingRoomActivity.this, NotesGameControlActivity.class));
                }
                notesGame.setPlayerCount(notesGame.getPlayerCount() + 1);
                if (playerNum == -1) {
                    playerNum = notesGame.getPlayerCount() - 1; // give the player a number

                    if (notesGame.getNames() == null) {
                        notesGame.setNames(new ArrayList<>());
                    }
                    notesGame.addName(JoinScreenActivity.playerName);
                    kahootGamesRef.child(String.valueOf(notesGame.getRoomNumber())).setValue(notesGame);
                }

                updatePlayerList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error: " + error.getMessage());
            }
        });
    }

    private void createRoom() {
        Random rnd = new Random();
        JoinScreenActivity.roomCode = 1000 + rnd.nextInt(9000);
        playerNum = 0;
        JoinScreenActivity.playerName = "Host";
        ArrayList<String> names = new ArrayList<>();
        if (GameQuestionsActivity.isAutoGenQuestionerChosen) {
            names.add("Host");
            notesGame = new NotesGame(JoinScreenActivity.roomCode, 1, names, false, 0);
        } else {
            notesGame = new NotesGame(JoinScreenActivity.roomCode, 0, names, false, 0);
        }

        kahootGamesRef.child(String.valueOf(notesGame.getRoomNumber())).setValue(notesGame);

        startGameButton.setVisibility(Button.VISIBLE);
        startGameButton.setOnClickListener(v -> {
            kahootGamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).child("isStarted").setValue(true);
            kahootGamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).removeEventListener(listener);
            startActivity(new Intent(this, NotesGameControlActivity.class));
        });
    }


    private void updatePlayerList(DataSnapshot snapshot1) {
        if (snapshot1.exists()) {
            NotesGame updatedGame = snapshot1.getValue(NotesGame.class);
            if (updatedGame != null && updatedGame.getNames() != null) {
                if (updatedGame.getPlayerCount() > 0 && waitingLayout.getVisibility() == LinearLayout.VISIBLE) {
                    waitingLayout.setVisibility(LinearLayout.GONE);
                }
                playerNames.clear();
                playerNames.addAll(updatedGame.getNames());
                nameList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                playerCountNumber.setText(String.valueOf(updatedGame.getPlayerCount()));
            }
        }
    }
}
