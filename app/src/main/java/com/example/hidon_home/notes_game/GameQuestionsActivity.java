package com.example.hidon_home.notes_game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.hidon_home.R;


public class GameQuestionsActivity extends AppCompatActivity {
    public static boolean isAutoGenQuestionerChosen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_questions);
    }

    public void onCustomQuestionerClick(View view) {
        isAutoGenQuestionerChosen = false;
        startActivity(new Intent(this, SelectQuestioneerActivity.class));
    }

    public void onAutoGenQuestionerClick(View view) {
        isAutoGenQuestionerChosen = true;
        startActivity(new Intent(this, WaitingRoomActivity.class));
    }
}