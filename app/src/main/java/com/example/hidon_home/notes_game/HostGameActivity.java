package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.R;
import com.example.hidon_home.hidon.AmericanQuestionActivity;
import com.example.hidon_home.notes_game.HostPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HostGameActivity extends AppCompatActivity {

    private TextView tvQuizTitle, tvQuestionProgress, tvTimer;
    private Button goBackButton;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HostPageAdapter pagerAdapter;
    FirebaseDatabase database;
    DatabaseReference kahootGameRef;
    private CountDownTimer questionTimer;
    public static int currentQuestion;
    public static boolean isEnded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        initViews();
        setupViewPager();
        loadQuizData();
        startQuizTimer();
    }

    private void initViews() {
        database = FirebaseDatabase.getInstance();
        kahootGameRef = database.getReference("kahoot_games");

        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvTimer = findViewById(R.id.tvTimer);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        goBackButton = findViewById(R.id.goBackButton);
        goBackButton.setVisibility(View.GONE);
    }

    private void setupViewPager() {
        pagerAdapter = new HostPageAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Connect TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Leaderboard");
                    break;
                case 1:
                    tab.setText("Question Stats");
                    break;
            }
        }).attach();
    }

    private void loadQuizData() {
        //Update UI with quiz data
        updateQuizInfoUI();
    }

    private void updateQuizInfoUI() {
        tvQuizTitle.setText(WaitingRoom.pickedQuestioner.getTitle());

        kahootGameRef.child(String.valueOf(JoinScreen.roomCode)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isEnded) {
                    kahootGameRef.child(String.valueOf(JoinScreen.roomCode)).removeEventListener(this);
                    endGame();
                    return;
                }
                currentQuestion = snapshot.child("currentQuestion").getValue(Integer.class);

                tvQuestionProgress.setText("Question: " + currentQuestion +
                "/" + WaitingRoom.pickedQuestioner.getQuestioneer().size());

                startQuizTimer(); // when the question changes, start the timer again
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void startQuizTimer() {
        if (questionTimer != null) {
            questionTimer.cancel();
        }

        // Start a new timer (example: 30 seconds per question)
        questionTimer = new CountDownTimer(20000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText("Time: " + (seconds < 10 ? "0" : "") + seconds);
            }

            @Override
            public void onFinish() {
                tvTimer.setText("Time: 00:00");
                // Auto-move to next question or handle time expiration
                handleTimeExpired();
            }
        }.start();
    }

    private void handleTimeExpired() {
//        startActivity(new Intent(this, MainActivity.class));
    }


    private void notifyPlayersOfPauseState(boolean isPaused) {
        // In a real app, this would send a message to your backend/server
        // which would then notify all connected players
        System.out.println("Notifying players: Quiz is " + (isPaused ? "paused" : "resumed"));
    }

//    private void notifyPlayersOfQuestionChange() {
//        // In a real app, this would notify all players that we've moved to a new question
//        System.out.println("Notifying players: Moving to question " + currentQuiz.getCurrentQuestionNumber());
//    }

    private void endGame() {
        goBackButton.setVisibility(View.VISIBLE);
        goBackButton.setTranslationY(goBackButton.getHeight()); // Move it out of view
        goBackButton.setAlpha(0f); // Make it transparent

        goBackButton.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(500)
                .setInterpolator(new DecelerateInterpolator())
                .start();

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isStarted = false;
                MainActivity.isPlayer1 = false;
                NotesGameControlActivity.game = null;
                NotesGameControlActivity.currentQuestion = 0;
                WaitingRoom.notesGame = null;
                WaitingRoom.pickedQuestioner = null;
                WaitingRoom.playerNum = -1;
                HostQuestionStats.questioneer = null;
                HostGameActivity.isEnded = false;
                HostGameActivity.currentQuestion = 0;
                kahootGameRef.child(String.valueOf(JoinScreen.roomCode)).removeValue();
                finish();
                startActivity(new Intent(HostGameActivity.this, MainActivity.class));
            }
        });
    }
}