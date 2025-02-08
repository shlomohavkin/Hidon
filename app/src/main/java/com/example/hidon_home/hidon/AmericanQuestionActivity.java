package com.example.hidon_home.hidon;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.hidon_home.Game;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.R;
import com.example.hidon_home.notes_game.JoinScreen;
import com.example.hidon_home.notes_game.NotesGameControlActivity;
import com.example.hidon_home.notes_game.WaitingRoom;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.time.chrono.MinguoChronology;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AmericanQuestionActivity extends AppCompatActivity {
    private static final int CORRECT_ANSWER_POINTS = 20;
    private static final int KAHOOT_MAX_POINTS = 100;
    private static final long TIMEOUT_MILLIS = 15000; // 15 seconds
    Question question;
    TextView questionContent, leftPlayerScore, rightPlayerScore, leftPlayerName, rightPlayerName;
    Button answer1, answer2, answer3, answer4;
    FirebaseDatabase database;
    DatabaseReference  gamesRef;
    private boolean isScreenFinished, isUpdatedScore = false;
    ProgressBar timeProgressBar;
    private ValueAnimator progressAnimator;
    String gameId;
    int numberOfPlayers;
    int currentQuestion;
    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_american_question);

        isScreenFinished = false;
        isUpdatedScore = false;

        database = FirebaseDatabase.getInstance();

        if (!MainActivity.isNotesGame) {
            gamesRef = database.getReference("games");
            numberOfPlayers = 2;
            currentQuestion = GameControlActivity.currentQuestion;
            game = GameControlActivity.game;
        }
        else {
            gamesRef = database.getReference("kahoot_games");
            numberOfPlayers = WaitingRoom.notesGame.getPlayerCount() - 1;
            currentQuestion = NotesGameControlActivity.currentQuestion;
            game = NotesGameControlActivity.game;
        }

        question = game.getQuestions().get(currentQuestion - 1);
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


        if (!MainActivity.isNotesGame) {
            leftPlayerName = findViewById(R.id.Player1);
            rightPlayerName = findViewById(R.id.Player2);

            leftPlayerName.setText("Your Score: ");
            rightPlayerName.setText("Other's Score: ");

            leftPlayerScore = findViewById(R.id.Player1_Score);
            rightPlayerScore = findViewById(R.id.Player2_Score);
            if (MainActivity.isPlayer1) {
                rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1))); // set the left score to the your score
                leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
            } else {
                leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1)));
                rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
            }

            gameId = game.getId();
        } else {
            gameId = String.valueOf(JoinScreen.roomCode);
            leftPlayerName = findViewById(R.id.Player1);
            rightPlayerName = findViewById(R.id.Player2);
            leftPlayerName.setVisibility(View.GONE);
            rightPlayerName.setVisibility(View.GONE);
            leftPlayerScore = findViewById(R.id.Player1_Score);
            rightPlayerScore = findViewById(R.id.Player2_Score);
            leftPlayerScore.setVisibility(View.GONE);
            rightPlayerScore.setVisibility(View.GONE);
        }

        gamesRef.child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (isScreenFinished) {
                    gamesRef.child(gameId).removeEventListener(this);
                    return;
                }

                // here we fill the array of the player states for the states of all the players
                ArrayList<Game.PlayerState> playersState = new ArrayList<>();
                if (MainActivity.isNotesGame) { // the database differ from the notes game to a normal game
                    for (DataSnapshot playerSnapshot : snapshot.child("game").child("playersState").getChildren()) {
                        Game.PlayerState player = playerSnapshot.getValue(Game.PlayerState.class);
                        if (player != null) {
                            playersState.add(player);
                        }
                    }
                } else {
                    for (DataSnapshot playerSnapshot : snapshot.child("playersState").getChildren()) {
                        Game.PlayerState player = playerSnapshot.getValue(Game.PlayerState.class);
                        if (player != null) {
                            playersState.add(player);
                        }
                    }
                }

                // we fill the array of the correct answers of the players and the last question answered by the players from the players state array
                boolean[] playeriCorrect = new boolean[numberOfPlayers];
                int[] lastQuestionPlayeri = new int[numberOfPlayers];
                int[] playeriHasAnswered = new int[numberOfPlayers];
                for (int i = 0; i < numberOfPlayers; i++) {
                    playeriCorrect[i] = playersState.get(i).getIsCorrectAnswerChosen();
                    lastQuestionPlayeri[i] = playersState.get(i).getLastQuestionAnswered();
                    playeriHasAnswered[i] = playersState.get(i).getLastQuestionAnswered();
                }

                // here we check if all the players have answered the current question
                boolean isEveryoneAnswered = true;
                for (int i = 0 ; i < numberOfPlayers; i++) {
                    if (playeriHasAnswered[i] != currentQuestion) {
                        isEveryoneAnswered = false;
                        if (MainActivity.isNotesGame) { // if not all the players answered then we wait for the other players to answer.
                            return;
                        }
                        break;
                    }
                }

                if (isEveryoneAnswered && MainActivity.isNotesGame) {
                    // All players have answered; determine the winner
                    long[] correctPlayeriTimestamp = new long[numberOfPlayers];
                    int numberOfCorrectAnswers = 0;
                    for (int i = 0; i < numberOfPlayers; i++) {
                        playeriCorrect[i] = playersState.get(i).getIsCorrectAnswerChosen();
                        if (playeriCorrect[i]) {
                            correctPlayeriTimestamp[numberOfCorrectAnswers++] = playersState.get(i).getTimeStamp();
                        }
                    }

                    if (MainActivity.isNotesGame) {
                        List<Map.Entry<Integer, Long>> playerTimestamps = new ArrayList<>();
                        for (int i = 0; i < numberOfCorrectAnswers; i++) {
                            playerTimestamps.add(new AbstractMap.SimpleEntry<>(i, correctPlayeriTimestamp[i]));
                        }
                        playerTimestamps.sort(Map.Entry.comparingByValue());

                        for (int i = 0; i < numberOfCorrectAnswers; i++) {
                            int n = playerTimestamps.size() == 0 ? 1 : playerTimestamps.size();
                            int points = KAHOOT_MAX_POINTS * (n - i) / n; // calculation of the points the player gets
                            game.setPlayersScoreAt(game.getPlayersScoreAt(playerTimestamps.get(i).getKey()) + points, playerTimestamps.get(i).getKey());
                        }
                    }

                    // Proceed to the next question
                    progressAnimator.cancel();
                    new Handler().postDelayed(() -> {
                        isScreenFinished = true;
                        gamesRef.child(gameId).removeEventListener(this);
                        gamesRef.child(gameId).child("game").setValue(game);
                        startActivity(new Intent(AmericanQuestionActivity.this, NotesGameControlActivity.class));
                    }, 2000);
                }

                if (!MainActivity.isNotesGame) {
                    for (int i = 0; i < numberOfPlayers; i++) {
                        if (lastQuestionPlayeri[i] == currentQuestion && playeriCorrect[i]) {
                            Log.d("move to next screen", "last question answered by player " + i + ":" + lastQuestionPlayeri[i] + " current question: " + currentQuestion);
                            disableAllAnswerButtons();

                            isScreenFinished = true;

                            game.setPlayersScoreAt(game.getPlayersScoreAt(i) + CORRECT_ANSWER_POINTS, i);

                            if (!MainActivity.isNotesGame && i == 0) {
                                leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
                                rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1)));
                            } else if (!MainActivity.isNotesGame && i == 1) {
                                if (MainActivity.isPlayer1) {
                                    rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1))); // set the left score to the your score
                                    leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
                                } else {
                                    leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1)));
                                    rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
                                }
                            }


                            // Proceed to the next question
                            progressAnimator.cancel();
                            new Handler().postDelayed(() -> {
                                gamesRef.child(gameId).removeEventListener(this);
                                gamesRef.child(gameId).setValue(game);
                                startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
                            }, 2000);
                        }
                    }
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
        disableAllAnswerButtons();

        isScreenFinished = true;

        progressAnimator.cancel();
        new Handler().postDelayed(() -> {
            if (MainActivity.isNotesGame) {
                startActivity(new Intent(AmericanQuestionActivity.this, NotesGameControlActivity.class));
            } else {
                startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
            }
        }, 2000);
    }

    public void onAnswerClick(View view) {

        // Disable all answer buttons immediately
        disableAllAnswerButtons();

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
        if (MainActivity.isNotesGame) {
            playerPath = String.valueOf(WaitingRoom.playerNum);
        }
        long answerTimestamp = System.currentTimeMillis();
        Game.PlayerState player = new Game.PlayerState(currentQuestion, isCorrect, answerTimestamp);
        if (MainActivity.isNotesGame) {
            gamesRef.child(gameId).child("game").child("playersState").child(playerPath).setValue(player);
        } else {
            gamesRef.child(gameId).child("playersState").child(playerPath).setValue(player);
        }

        // Check the status of both players
        gamesRef.child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (isScreenFinished) {
                    gamesRef.child(gameId).removeEventListener(this);
                    return;
                }
                int[] playeriHasAnswered = new int[numberOfPlayers];
                boolean isEveryoneAnswered = true;

                for (int i = 0; i < numberOfPlayers; i++) {
                    if (MainActivity.isNotesGame) {
                        playeriHasAnswered[i] = dataSnapshot.child("game").child("playersState").child(String.valueOf(i)).child("lastQuestionAnswered").getValue(int.class);
                    } else {
                        playeriHasAnswered[i] = dataSnapshot.child("playersState").child(String.valueOf(i)).child("lastQuestionAnswered").getValue(int.class);
                    }
                }

                for (int i = 0 ; i < numberOfPlayers; i++) {
                    if (playeriHasAnswered[i] != currentQuestion) {
                        isEveryoneAnswered = false;
                        break;
                    }
                }

                if (isEveryoneAnswered) {
                    // All players have answered; determine the winner
                    long[] correctPlayeriTimestamp = new long[numberOfPlayers];
                    boolean[] playeriCorrect = new boolean[numberOfPlayers];
                    int numberOfCorrectAnswers = 0;
                    for (int i = 0; i < numberOfPlayers; i++) {
                        if (MainActivity.isNotesGame) {
                            playeriCorrect[i] = dataSnapshot.child("game").child("playersState").child(String.valueOf(i)).child("isCorrectAnswerChosen").getValue(boolean.class);
                        } else {
                            playeriCorrect[i] = dataSnapshot.child("playersState").child(String.valueOf(i)).child("isCorrectAnswerChosen").getValue(boolean.class);
                        }
                        if (playeriCorrect[i]) {
                            if (MainActivity.isNotesGame) {
                                correctPlayeriTimestamp[numberOfCorrectAnswers++] = dataSnapshot.child("game").child("playersState").child(String.valueOf(i)).child("timeStamp").getValue(long.class);
                            } else {
                                correctPlayeriTimestamp[numberOfCorrectAnswers++] = dataSnapshot.child("playersState").child(String.valueOf(i)).child("timeStamp").getValue(long.class);
                            }
                        }
                    }

                    if (MainActivity.isNotesGame) {
                        List<Map.Entry<Integer, Long>> playerTimestamps = new ArrayList<>();
                        for (int i = 0; i < numberOfCorrectAnswers; i++) {
                            playerTimestamps.add(new AbstractMap.SimpleEntry<>(i, correctPlayeriTimestamp[i]));
                        }
                        playerTimestamps.sort(Map.Entry.comparingByValue());

                        for (int i = 0; i < numberOfCorrectAnswers; i++) {
                            int n = playerTimestamps.size() == 0 ? 1 : playerTimestamps.size();
                            int points = KAHOOT_MAX_POINTS * (n - i) / n; // calculation of the points the player gets
                            game.setPlayersScoreAt(game.getPlayersScoreAt(playerTimestamps.get(i).getKey()) + points, playerTimestamps.get(i).getKey());
                        }
                    } else {
                        int winner;
                        if (playeriCorrect[0] && playeriCorrect[1]) {
                            winner = (correctPlayeriTimestamp[0] < correctPlayeriTimestamp[1]) ? 0 : 1;
                        } else if (playeriCorrect[0]) {
                            winner = 0;
                        } else if (playeriCorrect[1]) {
                            winner = 1;
                        } else {
                            winner = -1; // tie
                        }

                        // Update scores and notify players
                        if (winner == 0 && !isUpdatedScore) {
                            game.setPlayersScoreAt(game.getPlayersScoreAt(0) + CORRECT_ANSWER_POINTS, 0);
                        } else if (winner == 1 && !isUpdatedScore) {
                            game.setPlayersScoreAt(game.getPlayersScoreAt(1) + CORRECT_ANSWER_POINTS, 1);
                        }
                        if (MainActivity.isPlayer1) {
                            rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1))); // set the left score to the your score
                            leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
                        } else {
                            leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1)));
                            rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
                        }
                    }

                    // Proceed to the next question
                    progressAnimator.cancel();
                    new Handler().postDelayed(() -> {
                        isScreenFinished = true;
                        gamesRef.child(gameId).removeEventListener(this);
                        if (MainActivity.isNotesGame) {
                            gamesRef.child(gameId).child("game").setValue(game);
                            startActivity(new Intent(AmericanQuestionActivity.this, NotesGameControlActivity.class));
                        } else {
                            gamesRef.child(gameId).setValue(game);
                            startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
                        }
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

    private void disableAllAnswerButtons() {
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);
        answer4.setEnabled(false);
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