package com.example.hidon_home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hidon_home.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    boolean isStarted;
    static boolean isPlayer1;
    static FirebaseDatabase database;
    DatabaseReference myRef;
    TextView title;
    static @Nullable UUID gameID;
    Button notesGameButton, joinGameNotes, createQuestionsNotes, startGameNotes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        System.out.println("hello world");
        isStarted = false;
        isPlayer1 = false;
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("game_status");
        DatabaseReference gamesRef = database.getReference("games");

        notesGameButton = findViewById(R.id.notes_button);
        startGameNotes = findViewById(R.id.start_notes_game_button);
        joinGameNotes = findViewById(R.id.join_notes_game_button);
        createQuestionsNotes = findViewById(R.id.create_questions_button);

        title = findViewById(R.id.textViewTitle);
    }

    public void onStartClick(View view) {
        startActivity(new Intent(this, GameControlActivity.class));
    }

    public void onStartClickOnline(View view) {
        if (!isStarted) {
            myRef.setValue("start");
            isPlayer1 = true;
            title.setText("Waiting For A Player To Join...");

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String valueFB = dataSnapshot.getValue(String.class);
                    if (isValidUUID(valueFB)) {
                        isStarted = true;
                        gameID = UUID.fromString(valueFB);
                        Log.d("start game id", gameID.toString());
                        myRef.removeValue();
                        startActivity(new Intent(MainActivity.this, GameControlActivity.class));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
    }

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
                            startActivity(new Intent(MainActivity.this, GameControlActivity.class));
                        } else if (valueFB == null || !isValidUUID(valueFB)) {
                            Toast.makeText(MainActivity.this, "Can't join Game - No one started one", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Can't join Game - you were starting the game", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(this, "Joining Game", Toast.LENGTH_LONG).show();
        }
    }

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

    public void onNotesGameButtonClick(View view) {
        if (startGameNotes.getVisibility() == View.GONE) {
            joinGameNotes.setVisibility(View.VISIBLE);
            createQuestionsNotes.setVisibility(View.VISIBLE);
            startGameNotes.setVisibility(View.VISIBLE);

            // Apply Slide Down animation
            Animation slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
            Animation slideDown2 = AnimationUtils.loadAnimation(this, R.anim.slide_down2);
            Animation slideDown3 = AnimationUtils.loadAnimation(this, R.anim.slide_down3);

            startGameNotes.startAnimation(slideDown);
            joinGameNotes.startAnimation(slideDown2);
            createQuestionsNotes.startAnimation(slideDown3);
        } else {
            // Apply Slide Down animation
            Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
            Animation slideUp2 = AnimationUtils.loadAnimation(this, R.anim.slide_up2);
            Animation slideUp3 = AnimationUtils.loadAnimation(this, R.anim.slide_up3);

            startGameNotes.startAnimation(slideUp);
            joinGameNotes.startAnimation(slideUp2);
            createQuestionsNotes.startAnimation(slideUp3);

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

    public void onNotesCreateClick(View view) {
        startActivity(new Intent(this, NotesGameQuestionsGen.class));
    }

    public void onNotesGameStart(View view) {
        startActivity(new Intent(this, WaitingRoom.class));
    }

    public void onNotesGameJoin(View view) {

    }


}