package com.example.hidon_home.notes_game.adapters;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.hidon_home.Question;
import com.example.hidon_home.notes_game.HostGameActivity;
import com.example.hidon_home.notes_game.QuestionStatsCardFragment;
import com.example.hidon_home.notes_game.WaitingRoomActivity;

import java.util.ArrayList;

// This class is used to create a pager adapter for the question stats screen.
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


    /**
     * This method is used to create a new fragment for each question in the list.
     * It passes the question data to the fragment using the arguments.
     *
     * @param position The position of the question in the list.
     * @return A new instance of QuestionStatsCardFragment with the question data.
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Question question = questions.get(position);
        QuestionStatsCardFragment fragment = new QuestionStatsCardFragment();
        Bundle args = new Bundle();
        args.putInt("questionIndex", HostGameActivity.currentQuestion - 1);
        args.putInt("questionNumber", position + 1);
        args.putString("questionText", question.getQuestionContent());
        args.putString("Answer1", question.getAnswers().get(0));
        args.putString("Answer2", question.getAnswers().get(1));
        args.putString("Answer3", question.getAnswers().get(2));
        args.putString("Answer4", question.getAnswers().get(3));
        args.putString("CorrectAnswer", String.valueOf(question.getCorrectAnswer()));
        args.putInt("totalPlayers", WaitingRoomActivity.notesGame.getPlayerCount() - 1);
        fragment.setArguments(args);
        return fragment;
    }
}
