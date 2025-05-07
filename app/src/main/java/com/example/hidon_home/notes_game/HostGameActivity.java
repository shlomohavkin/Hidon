package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
        updateQuizInfoUI();
        startQuizTimer();
    }

    /**
     * Sets up the views on the screen.
     */
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

    /**
     * sets up the viewpager in the screen with a custom
     * adapter and the tab layout.
     */
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

    /**
     * Sets the views on the screen
     */
    private void updateQuizInfoUI() {
        tvQuizTitle.setText(WaitingRoomActivity.pickedQuestioner.getTitle());

        kahootGameRef.child(String.valueOf(JoinScreenActivity.roomCode)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isEnded) {
                    kahootGameRef.child(String.valueOf(JoinScreenActivity.roomCode)).removeEventListener(this);
                    endGame();
                    return;
                }
                currentQuestion = snapshot.child("currentQuestion").getValue(Integer.class);

                tvQuestionProgress.setText("Question: " + currentQuestion +
                "/" + WaitingRoomActivity.pickedQuestioner.getQuestioneer().size());

                startQuizTimer(); // when the question changes, start the timer again
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    /**
     * starts the quiz timer in the current screen, when the
     * question changes for the players.
     */
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
            }
        }.start();
    }

    /**
     * Ends the game, if all the question are finished. The game is finished by showing
     * a button to fo back to the main screen, and resetting the data used.
     */
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
                WaitingRoomActivity.notesGame = null;
                WaitingRoomActivity.pickedQuestioner = null;
                WaitingRoomActivity.playerNum = -1;
                HostQuestionStatsFragment.questioneer = null;
                HostGameActivity.isEnded = false;
                HostGameActivity.currentQuestion = 0;
                kahootGameRef.child(String.valueOf(JoinScreenActivity.roomCode)).removeValue();
                finish();
                startActivity(new Intent(HostGameActivity.this, MainActivity.class));
            }
        });
    }
}