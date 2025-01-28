package com.example.hidon_home.question_gen;

import com.example.hidon_home.Question;

import java.util.ArrayList;

public interface QuestionCallBack {
    void onQuestionGenerated(ArrayList<Question> q);
}
