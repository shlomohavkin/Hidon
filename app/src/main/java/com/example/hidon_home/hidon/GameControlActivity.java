package com.example.hidon_home.hidon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.hidon_home.Game;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.Question;
import com.example.hidon_home.question_gen.QuestionCallBack;
import com.example.hidon_home.question_gen.QuestionGenerator;
import com.example.hidon_home.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class GameControlActivity extends AppCompatActivity {
    ArrayList<Question> questions = new ArrayList<>();
    static Game game;
    static int currentQuestion = 0;
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
        gamesRef = database.getReference("games");


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
            if (MainActivity.isPlayer1) { // if the main player, create the questions
                questionGenerator = new QuestionGenerator();
                generateQuestions(); // Generate questions
            } else { // else, get the questions the main player created from the FireBase
                gamesRef.child(com.example.hidon_home.MainActivity.gameID.toString()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        game = snapshot.getValue(Game.class);
                        if (game == null) {
                            Log.e("DataSnapshotError", "Game is null.");
                            return;
                        } else {
                            Log.d("DataSnapshotError", "Game is not null.");
                            Log.d("Game", "Game found: " + game.getId());
                            Log.d("Game", "Game questions: " + game.getQuestions().toString());
                            gamesRef.child(MainActivity.gameID.toString()).removeEventListener(this);
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
            public void onQuestionGenerated(ArrayList<Question> gen_questions) {
                if (gen_questions != null) {
                    for (int i = 0; i < gen_questions.size(); i++) {
                        ArrayList<String> genAnswers = new ArrayList<>();
                        genAnswers.addAll(gen_questions.get(i).getAnswers());
                        questions.add(new Question(gen_questions.get(i).getQuestionContent(),
                                                    genAnswers, gen_questions.get(i).getCorrectAnswer()));
                        Log.d("GameControl", "Question " + (i + 1) + " generated.");
                        loading_screen_text.setText("Generated " + (i + 1) + " questions of " + gen_questions.size() + "...");
                    }
                    Log.d("GameControl", "All questions generated. Starting game...");

                    ArrayList<Game.PlayerState> playersState = new ArrayList<>();
                    ArrayList<Integer> playersScore = new ArrayList<>();

                    Game.PlayerState playerState = new Game.PlayerState(0, false, 0);
                    playerState.setAnswerChosen(4);
                    playersState.add(playerState);

                    playerState = new Game.PlayerState(0, false, 0);
                    playerState.setAnswerChosen(4);
                    playersState.add(playerState);

                    playersScore.add(0);
                    playersScore.add(0);

                    game = new Game(MainActivity.gameID.toString(), playersScore, questions, playersState);
                    gamesRef.child(MainActivity.gameID.toString()).setValue(game); // Store in Firebase
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

        // Start the first question activity
        if (currentQuestion != game.getQuestions().size()) {
            currentQuestion++;
            Intent intent = new Intent(GameControlActivity.this, AmericanQuestionActivity.class);
            intent.putExtra("currentQuestion", currentQuestion);
            startActivity(intent);
        } else {
            startActivity(new Intent(GameControlActivity.this, ResultActivity.class));
        }
    }

    /**
     * If the questions aren't properly initialized then
     * it shows an error and goes back to the main activity.
     */
    private void showErrorAndReturnToMain() {
        Log.e("GameControl", "Returning to Main Activity due to question generation failure.");
        startActivity(new Intent(GameControlActivity.this, MainActivity.class));
    }
}

