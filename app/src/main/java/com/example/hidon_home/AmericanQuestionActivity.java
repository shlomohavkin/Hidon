package com.example.hidon_home;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AmericanQuestionActivity extends AppCompatActivity {
    Question question;
    TextView questionContent, leftPlayerScore, rightPlayerScore, leftPlayerName, rightPlayerName;
    Button answer1, answer2, answer3, answer4;
    FirebaseDatabase database;
    DatabaseReference  gamesRef, playerRef;
    private static final long TIMEOUT_MILLIS = 15000; // 15 seconds
    private boolean isScreenFinished, isUpdatedScore = false;
    ProgressBar timeProgressBar;
    private ValueAnimator progressAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_american_question);

        isScreenFinished = false;
        isUpdatedScore = false;

        leftPlayerName = findViewById(R.id.Player1);
        rightPlayerName = findViewById(R.id.Player2);

        leftPlayerName.setText("Your Score: ");
        rightPlayerName.setText("Other's Score: ");

        database = FirebaseDatabase.getInstance();
        gamesRef = database.getReference("games");

        question = GameControlActivity.game.getQuestions().get(GameControlActivity.currentQuestion - 1);
        timeProgressBar = findViewById(R.id.timeProgressBar);

        questionContent = findViewById(R.id.QuestionContent);
        answer1 = findViewById(R.id.Answer1);
        answer2 = findViewById(R.id.Answer2);
        answer3 = findViewById(R.id.Answer3);
        answer4 = findViewById(R.id.Answer4);

        questionContent.setText(question.getQuestionContent());
        answer1.setText(question.getAnswers().get(0));
        answer2.setText(question.getAnswers().get(1));
        answer3.setText(question.getAnswers().get(2));
        answer4.setText(question.getAnswers().get(3));

        leftPlayerScore = findViewById(R.id.Player1_Score);
        rightPlayerScore = findViewById(R.id.Player2_Score);

        if (MainActivity.isPlayer1) {
            Log.d("players score", "player 1 score: " + GameControlActivity.game.getPlayersScoreAt(0) + " player 2 score: " + GameControlActivity.game.getPlayersScoreAt(1));
            rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1))); // set the left score to the your score
            leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
        } else {
            Log.d("players score", "player 1 score: " + GameControlActivity.game.getPlayersScoreAt(0) + " player 2 score: " + GameControlActivity.game.getPlayersScoreAt(1));
            leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1)));
            rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
        }

        gamesRef.child(GameControlActivity.game.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (isScreenFinished) {
                    gamesRef.child(GameControlActivity.game.getId()).removeEventListener(this);
                    return;
                }
                boolean player1Correct = snapshot.child("playersState").child("0").child("isCorrectAnswerChosen").getValue(boolean.class);
                boolean player2Correct = snapshot.child("playersState").child("1").child("isCorrectAnswerChosen").getValue(boolean.class);
                int lastQuestionPlayer1 = snapshot.child("playersState").child("0").child("lastQuestionAnswered").getValue(int.class);
                int lastQuestionPlayer2 = snapshot.child("playersState").child("1").child("lastQuestionAnswered").getValue(int.class);

                if (lastQuestionPlayer1 == GameControlActivity.currentQuestion && player1Correct) {
                    Log.d("move to next screen", "last question answered by player 1: " + lastQuestionPlayer1 + " current question: " + GameControlActivity.currentQuestion);
                    answer1.setEnabled(false);
                    answer2.setEnabled(false);
                    answer3.setEnabled(false);
                    answer4.setEnabled(false);

                    isScreenFinished = true;

                    GameControlActivity.game.setPlayersScoreAt(GameControlActivity.game.getPlayersScoreAt(0) + 20, 0);
                    leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
                    rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1)));


                    // Proceed to the next question
                    progressAnimator.cancel();
                    new Handler().postDelayed(() -> {
                        gamesRef.child(GameControlActivity.game.getId()).removeEventListener(this);
                        startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
                    }, 2000);
                } else if (lastQuestionPlayer2 == GameControlActivity.currentQuestion && player2Correct) {
                    Log.d("move to next screen", "last question answered by player 2: " + lastQuestionPlayer2 + " current question: " + GameControlActivity.currentQuestion);
                    answer1.setEnabled(false);
                    answer2.setEnabled(false);
                    answer3.setEnabled(false);
                    answer4.setEnabled(false);

                    isScreenFinished = true;

                    GameControlActivity.game.setPlayersScoreAt(GameControlActivity.game.getPlayersScoreAt(1) + 20, 1);
                    if (MainActivity.isPlayer1) {
                        rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1))); // set the left score to the your score
                        leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
                    } else {
                        leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1)));
                        rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
                    }

                    // Proceed to the next question
                    progressAnimator.cancel();
                    new Handler().postDelayed(() -> {
                        gamesRef.child(GameControlActivity.game.getId()).removeEventListener(this);
                        startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
                    }, 2000);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("Firebase Error", "Error: " + error.getMessage());
            }
        });
        startProgressBarAnimation();
    }

    private void startProgressBarAnimation() {
        int startProgress = 100; // Fully filled
        int endProgress = 0; // Empty

        // Create a ValueAnimator to animate the progress change
        progressAnimator = ValueAnimator.ofInt(startProgress, endProgress);
        progressAnimator.setDuration(TIMEOUT_MILLIS); // 15-second animation duration
        progressAnimator.setInterpolator(new LinearInterpolator()); // Smooth linear interpolation

        // Update the ProgressBar’s progress during the animation
        progressAnimator.addUpdateListener(animation -> {
            int progress = (int) animation.getAnimatedValue();
            timeProgressBar.setProgress(progress);
        });

        progressAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Call handleTimeout when the progress bar finishes
                handleTimeout();
            }
        });
        // Start the animation
        progressAnimator.start();
    }

    private void handleTimeout() {
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);
        answer4.setEnabled(false);

        isScreenFinished = true;

        progressAnimator.cancel();
        new Handler().postDelayed(() -> {
            startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
        }, 2000);
    }

    public void onAnswerClick(View view) {

        // Disable all answer buttons immediately
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);
        answer4.setEnabled(false);

        int viewID = view.getId() == R.id.Answer1 ? 0 : view.getId() == R.id.Answer2 ? 1 : view.getId() == R.id.Answer3 ? 2 : 3;

        // Determine if the selected answer is correct
        boolean isCorrect = (viewID == 0 && answer1.getText().equals(question.getAnswers().get(question.getCorrectAnswer())))
                || (viewID == 1 && answer2.getText().equals(question.getAnswers().get(question.getCorrectAnswer())))
                || (viewID == 2 && answer3.getText().equals(question.getAnswers().get(question.getCorrectAnswer())))
                || (viewID == 3 && answer4.getText().equals(question.getAnswers().get(question.getCorrectAnswer())));

        // Provide immediate feedback
        if (isCorrect) {
            new Handler().postDelayed(() -> {
                view.setBackgroundColor(getResources().getColor(R.color.correct_answer_green));
            }, 50);
        } else {
            new Handler().postDelayed(() -> {
                view.setBackgroundColor(getResources().getColor(R.color.wrong_answer_red));
                highlightCorrectAnswer();
            }, 50);
        }

        // Record the player's response in Firebase
        String playerPath = MainActivity.isPlayer1 ? "0" : "1";
        long answerTimestamp = System.currentTimeMillis();
        Game.PlayerState player = new Game.PlayerState(GameControlActivity.currentQuestion, isCorrect, answerTimestamp);
        gamesRef.child(GameControlActivity.game.getId()).child("playersState").child(playerPath).setValue(player);

        // Check the status of both players
        gamesRef.child(GameControlActivity.game.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isScreenFinished) {
                    gamesRef.child(GameControlActivity.game.getId()).removeEventListener(this);
                    return;
                }
                int player1HasAnswered = dataSnapshot.child("playersState").child("0").child("lastQuestionAnswered").getValue(int.class);
                int player2HasAnswered = dataSnapshot.child("playersState").child("1").child("lastQuestionAnswered").getValue(int.class);

                if (player1HasAnswered == GameControlActivity.currentQuestion && player2HasAnswered == GameControlActivity.currentQuestion) {
                    // Both players have answered; determine the winner
                    long player1Timestamp = dataSnapshot.child("playersState").child("0").child("timeStamp").getValue(long.class);
                    long player2Timestamp = dataSnapshot.child("playersState").child("1").child("timeStamp").getValue(long.class);
                    boolean player1Correct = dataSnapshot.child("playersState").child("0").child("isCorrectAnswerChosen").getValue(boolean.class);
                    boolean player2Correct = dataSnapshot.child("playersState").child("1").child("isCorrectAnswerChosen").getValue(boolean.class);
                    Log.d("Firebase", "Player 1: " + player1Correct + " at " + player1Timestamp);
                    Log.d("Firebase", "Player 2: " + player2Correct + " at " + player2Timestamp);

                    String winner;
                    if (player1Correct && player2Correct) {
                        winner = (player1Timestamp < player2Timestamp) ? "Player 1" : "Player 2";
                    } else if (player1Correct) {
                        winner = "Player 1";
                    } else if (player2Correct) {
                        winner = "Player 2";
                    } else {
                        winner = "No one";
                    }

                    // Update scores and notify players
                    if (winner.equals("Player 1") && !isUpdatedScore) {
                        Log.d("players score", "player 1 score: " + GameControlActivity.game.getPlayersScoreAt(0) + " player 2 score: " + GameControlActivity.game.getPlayersScoreAt(1));
                        GameControlActivity.game.setPlayersScoreAt(GameControlActivity.game.getPlayersScoreAt(0) + 20, 0);
                    } else if (winner.equals("Player 2") && !isUpdatedScore) {
                        Log.d("players score", "player 1 score: " + GameControlActivity.game.getPlayersScoreAt(0) + " player 2 score: " + GameControlActivity.game.getPlayersScoreAt(1));
                        GameControlActivity.game.setPlayersScoreAt(GameControlActivity.game.getPlayersScoreAt(1) + 20, 1);
                    }
                    if (MainActivity.isPlayer1) {
                        Log.d("players Scores", "player 1 score: " + GameControlActivity.game.getPlayersScoreAt(0) + " player 2 score: " + GameControlActivity.game.getPlayersScoreAt(1));
                        rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1))); // set the left score to the your score
                        leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
                    } else {
                        Log.d("players Scores", "player 1 score: " + GameControlActivity.game.getPlayersScoreAt(0) + " player 2 score: " + GameControlActivity.game.getPlayersScoreAt(1));
                        leftPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(1)));
                        rightPlayerScore.setText(String.valueOf(GameControlActivity.game.getPlayersScoreAt(0)));
                    }

                    // Proceed to the next question
                    progressAnimator.cancel();
                    new Handler().postDelayed(() -> {
                        isScreenFinished = true;
                        gamesRef.child(GameControlActivity.game.getId()).removeEventListener(this);
                        startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
                    }, 2000);
                } else {
                    // Wait for the other player to answer
                    Toast.makeText(AmericanQuestionActivity.this, "Waiting for the other player...", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase Error", "Error: " + databaseError.getMessage());
            }
        });
    }


    // Helper method to highlight the correct answer
    private void highlightCorrectAnswer() {
        switch (question.getCorrectAnswer()) {
            case 0:
                answer1.setBackgroundColor(getResources().getColor(R.color.correct_answer_green));
                break;
            case 1:
                answer2.setBackgroundColor(getResources().getColor(R.color.correct_answer_green));
                break;
            case 2:
                answer3.setBackgroundColor(getResources().getColor(R.color.correct_answer_green));
                break;
            case 3:
                answer4.setBackgroundColor(getResources().getColor(R.color.correct_answer_green));
                break;
            default:
                break;
        }
    }

}