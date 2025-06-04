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
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.hidon_home.Game;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.R;
import com.example.hidon_home.notes_game.JoinScreenActivity;
import com.example.hidon_home.notes_game.LeaderboardActivity;
import com.example.hidon_home.notes_game.NotesGameControlActivity;
import com.example.hidon_home.notes_game.WaitingRoomActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AmericanQuestionActivity extends AppCompatActivity {
    private final int CORRECT_ANSWER_POINTS = 20;
    private final int KAHOOT_MAX_POINTS = 100;
    private final long TIMEOUT_MILLIS = 15000; // 15 seconds
    Question question;
    TextView questionContent, leftPlayerScore, rightPlayerScore, leftPlayerName, rightPlayerName, numberOfPlayersText, numberAnsweredText, answeredTextView;
    Button answer1, answer2, answer3, answer4;
    FirebaseDatabase database;
    DatabaseReference  gamesRef;
    boolean isScreenFinished, isUpdatedScore = false;
    ProgressBar timeProgressBar;
    ValueAnimator progressAnimator;
    String gameId;
    public static int numberOfPlayers;
    int currentQuestion;
    Game game;
    long questionCreatedTimestamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_american_question);

        isScreenFinished = false;
        isUpdatedScore = false;

        questionCreatedTimestamp = System.currentTimeMillis(); // get the time the question was created
        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();
        currentQuestion = intent.getIntExtra("currentQuestion", 0);

        setUpUI();
        setUpDatabaseListener();
        startProgressBarAnimation();
    }

    /**
     * Initialize the views in the current screen, depending on the
     * game, whether its 1vs1 or a kahoot game.
     */
    private void setUpUI() {
        if (!MainActivity.isNotesGame) {
            gamesRef = database.getReference("games");
            numberOfPlayers = 2;
            game = GameControlActivity.game;
        }
        else {
            gamesRef = database.getReference("kahoot_games");
            numberOfPlayers = WaitingRoomActivity.notesGame.getPlayerCount() - 1;
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

        numberAnsweredText = findViewById(R.id.numberAnswered);
        numberOfPlayersText = findViewById(R.id.numberOfPlayers);

        leftPlayerName = findViewById(R.id.Player1);
        rightPlayerName = findViewById(R.id.Player2);
        leftPlayerScore = findViewById(R.id.Player1_Score);
        rightPlayerScore = findViewById(R.id.Player2_Score);
        answeredTextView = findViewById(R.id.answeredText);

        if (!MainActivity.isNotesGame) {
            leftPlayerName.setText("Your Score: ");
            rightPlayerName.setText("Other's Score: ");

            numberAnsweredText.setVisibility(View.GONE);
            numberOfPlayersText.setVisibility(View.GONE);
            answeredTextView.setVisibility(View.GONE);


            if (MainActivity.isPlayer1) {
                rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1))); // set the left score to the your score
                leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
            } else {
                leftPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(1)));
                rightPlayerScore.setText(String.valueOf(game.getPlayersScoreAt(0)));
            }

            gameId = game.getId();
        } else {
            gameId = String.valueOf(JoinScreenActivity.roomCode);
            leftPlayerName.setVisibility(View.GONE);
            rightPlayerName.setVisibility(View.GONE);
            leftPlayerScore.setVisibility(View.GONE);
            rightPlayerScore.setVisibility(View.GONE);
            numberOfPlayersText.setText("/" + numberOfPlayers);
            numberAnsweredText.setText("0");

            if (WaitingRoomActivity.playerNum == 0) {
                gamesRef.child(gameId).child("currentQuestion").setValue(currentQuestion);
            }
        }
    }

    /**
     * The function sets up a listener in the firebase database, in order to get the
     * needed information from the changes happening on the other devices,
     * and act according to it.
     */
    private void setUpDatabaseListener() {
        gamesRef.child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isScreenFinished) {
                    gamesRef.child(gameId).removeEventListener(this);
                    return;
                }

                int actualAnsweredCount = 0;

                // here we fill the array of the player states for the states of all the players
                ArrayList<Game.PlayerState> playersState = new ArrayList<>();
                if (MainActivity.isNotesGame) { // the database differ from the notes game to a normal game
                    for (DataSnapshot playerSnapshot : snapshot.child("game").child("playersState").getChildren()) {
                        Game.PlayerState player = playerSnapshot.getValue(Game.PlayerState.class);
                        if (player != null) {
                            playersState.add(player);
                            if (player.getLastQuestionAnswered() == currentQuestion) {
                                actualAnsweredCount++;
                            }
                        }
                    }
                } else {
                    for (DataSnapshot playerSnapshot : snapshot.child("playersState").getChildren()) {
                        Game.PlayerState player = playerSnapshot.getValue(Game.PlayerState.class);
                        if (player != null) {
                            playersState.add(player);
                            if (player.getLastQuestionAnswered() == currentQuestion) {
                                actualAnsweredCount++;
                            }
                        }
                    }
                }

                numberAnsweredText.setText(String.valueOf(actualAnsweredCount));

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
                    // All players have answered, determine the winner
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
                        startActivity(new Intent(AmericanQuestionActivity.this, LeaderboardActivity.class));
                    }, 2000);
                }

                if (!MainActivity.isNotesGame) {
                    for (int i = 0; i < numberOfPlayers; i++) {
                        if (lastQuestionPlayeri[i] == currentQuestion && playeriCorrect[i]) {
                            Log.d("move to next screen", "last question answered by player " + i + ":" + lastQuestionPlayeri[i] + " current question: " + currentQuestion);
                            disableAllAnswerButtons();

                            isScreenFinished = true;

                            game.setPlayersScoreAt(game.getPlayersScoreAt(i) + CORRECT_ANSWER_POINTS, i);

                            if (!MainActivity.isNotesGame && i == 0 || i == 1) {
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
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase Error", "Error: " + error.getMessage());
            }
        });
    }


    /**
     * The function starts the progress bar in the bottom of the screen
     * for the current player.
     */
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

    /**
     * The function handles timeout of the progress bar, which was
     * initialized in the previous function. If the progress bar times out,
     * then the user moves to the next activity, according to the current mode
     * of play.
     */
    private void handleTimeout() {
        disableAllAnswerButtons();

        isScreenFinished = true;

        progressAnimator.cancel();
        new Handler().postDelayed(() -> {
            if (MainActivity.isNotesGame) {
                startActivity(new Intent(AmericanQuestionActivity.this, LeaderboardActivity.class));
            } else {
                startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
            }
        }, 2000);
    }

    /**
     * This is the main function of the current screen, which has the most logic in it.
     * The function handles a click on one of the possible answers.
     * Checks the Firebase whether the other players already answered
     * and if correctly, and know to distribute the points to the correct people
     * according to the mode of play, and knows whether to wait, or to move on.
     * @param view the button which was clicked.
     */
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
            new Handler().postDelayed(() -> view.setBackgroundColor(getResources().getColor(R.color.correct_answer_green)), 50);
        } else {
            new Handler().postDelayed(() -> {
                view.setBackgroundColor(getResources().getColor(R.color.wrong_answer_red));
                highlightCorrectAnswer();
            }, 50);
        }

        // Record the player's response in Firebase
        String playerPath = MainActivity.isPlayer1 ? "0" : "1";
        if (MainActivity.isNotesGame) {
            playerPath = String.valueOf(WaitingRoomActivity.playerNum);
        }
        long answerTimestamp = System.currentTimeMillis() - questionCreatedTimestamp; // get the time the player answered the question
        Game.PlayerState player = new Game.PlayerState(currentQuestion, isCorrect, answerTimestamp);
        player.setAnswerChosen(viewID);
        if (MainActivity.isNotesGame) {
            gamesRef.child(gameId).child("game").child("playersState").child(playerPath).setValue(player);
        } else {
            gamesRef.child(gameId).child("playersState").child(playerPath).setValue(player);
        }

        // Check the status of both players, and decide what to do
        gamesRef.child(gameId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
                    // All players have answered. determine the winner
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
                            startActivity(new Intent(AmericanQuestionActivity.this, LeaderboardActivity.class));
                        } else {
                            gamesRef.child(gameId).setValue(game);
                            startActivity(new Intent(AmericanQuestionActivity.this, GameControlActivity.class));
                        }
                    }, 2000);
                } else {
                    // Wait for the other player to answer
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase Error", "Error: " + databaseError.getMessage());
            }
        });
    }


    /**
     * This function is a helper function to another one that disables the answers button views on the screen,
     * in a couple of situation: if someone answered or if someone else answered correctly
     * in the 1vs1 mode.
     */
    private void disableAllAnswerButtons() {
        answer1.setEnabled(false);
        answer2.setEnabled(false);
        answer3.setEnabled(false);
        answer4.setEnabled(false);
    }


    /**
     * This function is a helper function to another function, that highlights the
     * correct answer to the green color.
     */
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