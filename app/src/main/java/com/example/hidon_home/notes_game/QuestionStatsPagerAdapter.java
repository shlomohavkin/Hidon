package com.example.hidon_home.notes_game;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.hidon_home.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionStatsPagerAdapter extends FragmentStateAdapter {
    private ArrayList<Question> questions;

    public QuestionStatsPagerAdapter(FragmentActivity fragmentActivity, ArrayList<Question> questions) {
        super(fragmentActivity);
        this.questions = questions;
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Question question = questions.get(position);
        QuestionStatsCardFragment fragment = new QuestionStatsCardFragment();
        Bundle args = new Bundle();
        args.putInt("questionIndex", HostGameActivity.currentQuestion - 1);
        args.putString("questionText", question.getQuestionContent());
        args.putString("Answer1", question.getAnswers().get(0));
        args.putString("Answer2", question.getAnswers().get(1));
        args.putString("Answer3", question.getAnswers().get(2));
        args.putString("Answer4", question.getAnswers().get(3));
        args.putString("CorrectAnswer", String.valueOf(question.getCorrectAnswer()));
        args.putInt("totalPlayers", WaitingRoom.notesGame.getPlayerCount() - 1);
        fragment.setArguments(args);
        return fragment;
    }
}
