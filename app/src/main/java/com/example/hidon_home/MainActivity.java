package com.example.hidon_home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hidon_home.hidon.GameControlActivity;
import com.example.hidon_home.notes_game.GameQuestionsActivity;
import com.example.hidon_home.notes_game.JoinScreenActivity;
import com.example.hidon_home.notes_game.NotesGameQuestionsGenActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static boolean isStarted;
    public static boolean isPlayer1;
    public static boolean isMainPlayer;
    public static boolean isNotesGame;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public static @Nullable UUID gameID;
    public static User user;
    Button notesGameButton, joinGameNotes, createQuestionsNotes, startGameNotes;
    LinearLayout waitingPopup;
    AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("hello world");
        isStarted = false;
        isPlayer1 = false;
        user = new User(LoginActivity.user);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("game_status");

        notesGameButton = findViewById(R.id.multiplayer_button);
        startGameNotes = findViewById(R.id.start_notes_game_button);
        joinGameNotes = findViewById(R.id.join_notes_game_button);
        createQuestionsNotes = findViewById(R.id.create_questions_button);

        waitingPopup = findViewById(R.id.waiting_popup);

        builder = new AlertDialog.Builder(this);
    }


    /**
     * This method is called when the user clicks the "Start" button for a 1v1 online game.
     * @param view The view that was clicked.
     */
    public void onStartClickOnline(View view) {
        if (!isStarted) {
            myRef.setValue("start");
            isPlayer1 = true;
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            waitingPopup.startAnimation(slideDown);
            waitingPopup.setVisibility(View.VISIBLE);

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String valueFB = dataSnapshot.getValue(String.class);
                    if (isValidUUID(valueFB)) {
                        isStarted = true;
                        gameID = UUID.fromString(valueFB);
                        Log.d("start game id", gameID.toString());
                        myRef.removeValue();
                        isNotesGame = false;
                        startActivity(new Intent(MainActivity.this, GameControlActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

    /**
     * This method is called when the user clicks the "Join" button for a 1v1 online game.
     * @param view The view that was clicked.
     */
    public void onJoinCLickOnline(View view) {
        if (!isStarted) {
            if (!isPlayer1) {
                myRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String valueFB = dataSnapshot.getValue(String.class);
                        if (valueFB != null && valueFB.equals("start")) {
                            gameID = UUID.randomUUID();
                            myRef.setValue(gameID.toString());
                            isStarted = true;
                            Log.d("join game id", gameID.toString());
                            myRef.removeEventListener(this);
                            isNotesGame = false;
                            startActivity(new Intent(MainActivity.this, GameControlActivity.class));
                        } else if (valueFB == null || !isValidUUID(valueFB)) {
                            builder.setTitle("Can't Join Game - Game not started yet");
                            builder.setMessage("Wait for something to start a game or start a game yourself");
                            builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            } else {
                builder.setTitle("Can't Join Game - You were starting the game");
                builder.setMessage("You can't join a game that you started");
                builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }

    /**
     * This method checks if the input string is a valid UUID.
     * @param input The string to check.
     * @return true if the string is a valid UUID, false otherwise.
     */
    public static boolean isValidUUID(String input) {
        if (input == null) {
            return false;
        }
        try {
            // Try to convert the string to a UUID
            UUID uuid = UUID.fromString(input);
            return true; // No exception, it's a valid UUID
        } catch (IllegalArgumentException e) {
            // The string is not a valid UUID
            return false;
        }
    }

    /**
     * This method is called when the user clicks the "Multiplayer Game" button.
     * It toggles the visibility of the notes game options, and does an animation.
     * @param view The view that was clicked.
     */
    public void onMultiplayerGameButtonClick(View view) {
        if (startGameNotes.getVisibility() == View.GONE) {
            joinGameNotes.setVisibility(View.VISIBLE);
            createQuestionsNotes.setVisibility(View.VISIBLE);
            startGameNotes.setVisibility(View.VISIBLE);

            // Apply Slide Down animation
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);

            startGameNotes.startAnimation(slideDown);
            joinGameNotes.startAnimation(slideDown);
            createQuestionsNotes.startAnimation(slideDown);
        } else {
            // Apply Slide Down animation
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

            startGameNotes.startAnimation(slideUp);
            joinGameNotes.startAnimation(slideUp);
            createQuestionsNotes.startAnimation(slideUp);

            // Set the visibility to GONE after animation completes
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    joinGameNotes.setVisibility(View.GONE);
                    createQuestionsNotes.setVisibility(View.GONE);
                    startGameNotes.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }

    }

    /**
     * This method is called when the user clicks the "Create Questions" button.
     * It starts the NotesGameQuestionsGenActivity.
     * @param view The view that was clicked.
     */
    public void onNotesCreateClick(View view) {
        startActivity(new Intent(this, NotesGameQuestionsGenActivity.class));
    }

    /**
     * This method is called when the user clicks the "Start Notes Game" button.
     * @param view The view that was clicked.
     */
    public void onNotesGameStart(View view) {
        isMainPlayer = true;
        isNotesGame = true;
        startActivity(new Intent(this, GameQuestionsActivity.class));
    }

    /**
     * This method is called when the user clicks the "Join Notes Game" button.
     * @param view The view that was clicked.
     */
    public void onNotesGameJoin(View view) {
        isMainPlayer = false;
        isNotesGame = true;
        startActivity(new Intent(this, JoinScreenActivity.class));
    }


}