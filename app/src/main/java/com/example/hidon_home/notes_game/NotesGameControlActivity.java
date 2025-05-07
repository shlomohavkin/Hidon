package com.example.hidon_home.notes_game;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.hidon_home.Game;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.R;
import com.example.hidon_home.hidon.AmericanQuestionActivity;
import com.example.hidon_home.question_gen.QuestionCallBack;
import com.example.hidon_home.question_gen.QuestionGenerator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class NotesGameControlActivity extends AppCompatActivity {

    ArrayList<Question> questions = new ArrayList<>();
    public static Game game;
    public static int currentQuestion = 0;
    QuestionGenerator questionGenerator;
    TextView loading_screen_text;
    DatabaseReference gamesRef;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_control);

        database = FirebaseDatabase.getInstance();
        loading_screen_text = findViewById(R.id.loading_message);
        gamesRef = database.getReference("kahoot_games");

        initializeGameObjectAndStartGame();
    }

    /**
     * Initializes the question in the quiz. If you are the main player, then it
     * class the function to generate the question from ChatGPT.
     * else, it gets the question the main player created from the
     * Firebase. And if the game already started, then the function starts the game,
     * which creates the game loop.
     */
    private void initializeGameObjectAndStartGame() {
        if (currentQuestion == 0) {
            if (MainActivity.isMainPlayer && GameQuestionsActivity.isAutoGenQuestionerChosen) {
                questionGenerator = new QuestionGenerator();
                generateQuestions(); // Generate questions
            } else if (MainActivity.isMainPlayer) {
                ArrayList<Game.PlayerState> playersState = new ArrayList<>();
                ArrayList<Integer> playersScore = new ArrayList<>();

                for (int i = 0; i < WaitingRoomActivity.notesGame.getPlayerCount() - 1; i++) {
                    Game.PlayerState playerState = new Game.PlayerState(0, false, 0);
                    playerState.setAnswerChosen(4);
                    playersState.add(playerState);

                    playersScore.add(0);
                }

                game = new Game(String.valueOf(JoinScreenActivity.roomCode), playersScore, WaitingRoomActivity.pickedQuestioner.getQuestioneer(), playersState);
                gamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).child("game").setValue(game);
                startActivity(new Intent(this, HostGameActivity.class));
            } else {
                gamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        game = snapshot.child("game").getValue(Game.class);
                        if (game == null) {
                            Log.e("DataSnapshotError", "Game is null.");
                            return;
                        } else {
                            Log.d("DataSnapshotError", "Game is not null.");
                            Log.d("Game", "Game found: " + game.getId());
                            Log.d("Game", "Game questions: " + game.getQuestions().toString());
                            gamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).removeEventListener(this);
                            startGame();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("GameControl", "Error retrieving game from Firebase. Returning to Main.");
                        showErrorAndReturnToMain();
                    }
                });
            }
        } else {
            startGame();
        }
    }


    /**
     * The function that generates the questions for the quiz, initializes
     * the needed objects, and does a small UI change in the text.
     */
    private void generateQuestions() {
        // Generate all the questions
        questionGenerator.generateQuestion(new QuestionCallBack() {
            @Override
            public void onQuestionGenerated(ArrayList<Question> Gen_questions) {
                if (Gen_questions != null) {
                    for (int i = 0; i < Gen_questions.size(); i++) {
                        ArrayList<String> genAnswers = new ArrayList<>();
                        genAnswers.addAll(Gen_questions.get(i).getAnswers());
                        questions.add(new Question(Gen_questions.get(i).getQuestionContent(),
                                genAnswers, Gen_questions.get(i).getCorrectAnswer()));
                        Log.d("GameControl", "Question " + (i + 1) + " generated.");
                        loading_screen_text.setText("Generated " + (i + 1) + " questions of 5...");
                    }
                    Log.d("GameControl", "All questions generated. Starting game...");

                    ArrayList<Game.PlayerState> playersState = new ArrayList<>();
                    ArrayList<Integer> playersScore = new ArrayList<>();

                    for (int i = 0; i < WaitingRoomActivity.notesGame.getPlayerCount() - 1; i++) {
                        Game.PlayerState playerState = new Game.PlayerState(0, false, 0);
                        playerState.setAnswerChosen(4);
                        playersState.add(playerState);

                        playersScore.add(0);
                    }

                    game = new Game(String.valueOf(JoinScreenActivity.roomCode), playersScore, questions, playersState);
                    gamesRef.child(String.valueOf(JoinScreenActivity.roomCode)).child("game").setValue(game); // Store in Firebase
                    startGame();
                } else {
                    Log.e("Question Error", "Failed to generate questions");
                    currentQuestion++;
                    showErrorAndReturnToMain();
                }
            }
        });
    }


    /**
     * The function checks that the questions are not null,
     * and goes to the next question, or to the result screen if the game is finished.
     */
    private void startGame() {
        for (Question q : game.getQuestions()) {
            if (q == null) {
                Log.e("GameControl", "Questions not fully populated. Returning to Main.");
                showErrorAndReturnToMain();
                return;
            }
        }

        if (currentQuestion != game.getQuestions().size()) {
            currentQuestion++;
            Intent intent = new Intent(NotesGameControlActivity.this, AmericanQuestionActivity.class);
            intent.putExtra("currentQuestion", currentQuestion);
            startActivity(intent);
        } else {
            startActivity(new Intent(NotesGameControlActivity.this, NotesResultActivity.class));
        }
    }

    /**
     * If the questions aren't properly initialized then
     * it shows an error and goes back to the main activity.
     */
    private void showErrorAndReturnToMain() {
        Log.e("GameControl", "Returning to Main Activity due to question generation failure.");
        startActivity(new Intent(NotesGameControlActivity.this, MainActivity.class));
    }
}