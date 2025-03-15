package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;

import java.util.List;

public class SelectQuestioneerScreen extends AppCompatActivity {
    RecyclerView questionnaireRecyclerView;
    Button backButton;
    List<Questioneer> questionList;
    QuestionnaireAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_questioneer_screen);

        // Initialize Views
        questionnaireRecyclerView = findViewById(R.id.questionnaireRecyclerView);
        backButton = findViewById(R.id.backButton);

        // Setup RecyclerView
        questionList = MainActivity.user.getQuestioners();
        adapter = new QuestionnaireAdapter(questionList, this);
        questionnaireRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionnaireRecyclerView.setAdapter(adapter);

        // Back Button Click Listener
        backButton.setOnClickListener(view -> {
            startActivity(new Intent(this, GameQuestionsActivity.class));
        });
    }
}
