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

import com.example.hidon_home.MainActivity;
import com.example.hidon_home.R;

public class SelectQuestioneerScreen extends AppCompatActivity {
    LinearLayout questionnairesContainer;
    Button backButton;

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
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(view -> {
            startActivity(new Intent(this, GameQuestionsActivity.class));
        });

        populateQuestionnaireList();
    }

    private void populateQuestionnaireList() {
        // Clear previous views
        questionnairesContainer.removeAllViews();

        // create buttons for each questionnaire
        for (Questioneer questionnaire : MainActivity.user.getQuestioners()) {
            Button button = new Button(this);
            button.setText(questionnaire.getTitle());
            button.setTextSize(18);
            button.setPadding(16, 8, 16, 8);
            button.setBackgroundResource(R.drawable.button_background);
            button.setTextColor(getResources().getColor(android.R.color.white));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 24, 0, 0); // 24dp top margin for spacing
            button.setLayoutParams(params);

            button.setOnClickListener(v -> {
                WaitingRoom.pickedQuestioner = questionnaire;
                startActivity(new Intent(this, WaitingRoom.class));
            });

            // Add button to the container
            questionnairesContainer.addView(button);
        }
    }
}