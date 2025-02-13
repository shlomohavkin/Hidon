package com.example.hidon_home.notes_game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hidon_home.R;

public class SelectQuestioneerScreen extends AppCompatActivity {
    LinearLayout questionnairesContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_questioneer_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        questionnairesContainer = findViewById(R.id.questionnaireContainer);

        populateQuestionnaireList();
    }

    private void populateQuestionnaireList() {
        // Clear previous views (if any)
        questionnairesContainer.removeAllViews();

        // Dynamically create buttons for each questionnaire
        for (Questioneer questionnaire : NotesGameQuestionsGen.questioners) {
            Button button = new Button(this);
            button.setText(questionnaire.getTitle());
            button.setTextSize(18);
            button.setPadding(16, 8, 16, 8);
            button.setBackgroundResource(R.drawable.button_background); // Custom background
            button.setTextColor(getResources().getColor(android.R.color.white));

            // Set click listener
            button.setOnClickListener(v -> {
                WaitingRoom.pickedQuestioner = questionnaire;
                startActivity(new Intent(this, WaitingRoom.class));
            });

            // Add button to the container
            questionnairesContainer.addView(button);
        }
    }
}