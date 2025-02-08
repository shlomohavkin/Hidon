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
import com.example.hidon_home.hidon.GameControlActivity;
import com.example.hidon_home.hidon.ResultActivity;
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


        if (currentQuestion == 0) {
            if (MainActivity.isMainPlayer) {
                questionGenerator = new QuestionGenerator();
                generateQuestions(); // Generate questions
            } else {
                gamesRef.child(String.valueOf(JoinScreen.roomCode)).addValueEventListener(new ValueEventListener() {
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
                            gamesRef.child(String.valueOf(JoinScreen.roomCode)).removeEventListener(this);
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

                    for (int i = 0; i < WaitingRoom.notesGame.getPlayerCount() - 1; i++) {
                        playersState.add(new Game.PlayerState(0, false, 0));
                        playersScore.add(0);
                    }

                    game = new Game(String.valueOf(JoinScreen.roomCode), playersScore, questions, playersState);
                    gamesRef.child(String.valueOf(JoinScreen.roomCode)).child("game").setValue(game); // Store in Firebase
                    startGame();
                } else {
                    Log.e("Question Error", "Failed to generate questions");
                    currentQuestion++;
                    showErrorAndReturnToMain();
                }
            }
        });
    }

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
            startActivity(new Intent(this, AmericanQuestionActivity.class));
        } else {
            startActivity(new Intent(NotesGameControlActivity.this, ResultActivity.class));
        }
    }

    private void showErrorAndReturnToMain() {
        Log.e("GameControl", "Returning to Main Activity due to question generation failure.");
        startActivity(new Intent(NotesGameControlActivity.this, MainActivity.class));
    }
}