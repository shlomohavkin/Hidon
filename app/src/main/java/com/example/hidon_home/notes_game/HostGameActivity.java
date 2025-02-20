package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;
import com.example.hidon_home.notes_game.HostPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class HostGameActivity extends AppCompatActivity {

    private TextView tvQuizTitle, tvQuestionProgress, tvTimer;
    private Button btnPause, btnSkip, btnEnd;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private HostPageAdapter pagerAdapter;

    // Data models
//    private QuizSession currentQuiz;
//    private List<Player> players = new ArrayList<>();
//    private QuestionStats currentQuestionStats;
    private CountDownTimer questionTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_game);

        initViews();
        setupViewPager();
        setupListeners();
//        loadQuizData();
        startQuizTimer();
    }

    private void initViews() {
        tvQuizTitle = findViewById(R.id.tvQuizTitle);
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvTimer = findViewById(R.id.tvTimer);

        btnPause = findViewById(R.id.btnPause);
        btnSkip = findViewById(R.id.btnSkip);
        btnEnd = findViewById(R.id.btnEnd);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
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

    private void setupListeners() {
//        btnPause.setOnClickListener(v -> {
//            // Toggle pause state
//            boolean isPaused = currentQuiz.togglePauseState();
//            btnPause.setText(isPaused ? "RESUME" : "PAUSE");
//            // Update UI and notify players of pause/resume
//            if (isPaused) {
//                if (questionTimer != null) questionTimer.cancel();
//            } else {
//                startQuizTimer();
//            }
//            notifyPlayersOfPauseState(isPaused);
//        });
//
//        btnSkip.setOnClickListener(v -> {
//            // Confirm with dialog
//            new AlertDialog.Builder(this)
//                    .setTitle("Skip Question")
//                    .setMessage("Are you sure you want to skip to the next question?")
//                    .setPositiveButton("Skip", (dialog, which) -> skipToNextQuestion())
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        });
//
//        btnEnd.setOnClickListener(v -> {
//            // Confirm with dialog
//            new AlertDialog.Builder(this)
//                    .setTitle("End Quiz")
//                    .setMessage("Are you sure you want to end the quiz? This will show final results to all players.")
//                    .setPositiveButton("End Quiz", (dialog, which) -> endQuiz())
//                    .setNegativeButton("Cancel", null)
//                    .show();
//        });
    }

    private void loadQuizData() {
//        // Update UI with quiz data
//        updateQuizInfoUI();

        // Update fragments with data
        updateFragmentsWithData();
    }

//    private void updateQuizInfoUI() {
//        tvQuizTitle.setText(currentQuiz.getTitle());
//        tvQuestionProgress.setText("Question: " + currentQuiz.getCurrentQuestionNumber() +
//                "/" + currentQuiz.getTotalQuestions());
//    }

    private void updateFragmentsWithData() {
        // This is how we update our fragments with new data
        // We need to find the current fragments that are shown

        try {
            HostLeaderboard leaderboardFragment =
                    (HostLeaderboard) getSupportFragmentManager().findFragmentByTag("f0");
            if (leaderboardFragment != null) {
//                leaderboardFragment.updatePlayerList(players);
            }

            HostQuestionStats statsFragment =
                    (HostQuestionStats) getSupportFragmentManager().findFragmentByTag("f1");
            if (statsFragment != null) {
//                statsFragment.updateStats(currentQuestionStats);
            }

        } catch (Exception e) {
            // Sometimes fragments might not be initialized yet
            // We'll update them when they're created
        }
    }

    private void startQuizTimer() {
        // Cancel any existing timer
        if (questionTimer != null) {
            questionTimer.cancel();
        }

        // Start a new timer (example: 30 seconds per question)
        questionTimer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvTimer.setText("Time: 00:" + (seconds < 10 ? "0" : "") + seconds);
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
        startActivity(new Intent(this, MainActivity.class));
    }

//    private void skipToNextQuestion() {
//        // Logic to move to next question
//        currentQuiz.moveToNextQuestion();
//        updateQuizInfoUI();
//
//        // Reset timer
//        startQuizTimer();
//
//        // Get new question stats (would come from server in real app)
//        currentQuestionStats = new QuestionStats(currentQuiz.getCurrentQuestionNumber(), "New Category");
//
//        // Update fragments
//        updateFragmentsWithData();
//
//        // Notify all players about question change
//        notifyPlayersOfQuestionChange();
//    }

//    private void endQuiz() {
//        // Logic to end quiz and show results
//        if (questionTimer != null) {
//            questionTimer.cancel();
//        }
//
//        // Navigate to results screen
//        Intent intent = new Intent(this, QuizResultsActivity.class);
//        intent.putExtra("QUIZ_ID", currentQuiz.getQuizId());
//        startActivity(intent);
//        finish();
//    }

    private void notifyPlayersOfPauseState(boolean isPaused) {
        // In a real app, this would send a message to your backend/server
        // which would then notify all connected players
        System.out.println("Notifying players: Quiz is " + (isPaused ? "paused" : "resumed"));
    }

//    private void notifyPlayersOfQuestionChange() {
//        // In a real app, this would notify all players that we've moved to a new question
//        System.out.println("Notifying players: Moving to question " + currentQuiz.getCurrentQuestionNumber());
//    }
}