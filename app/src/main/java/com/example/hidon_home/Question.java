package com.example.hidon_home;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class Question {

    private String questionContent;
    private ArrayList<String> answers = new ArrayList<>();
    private int correctAnswer;

    public Question() {}
    public Question(String questionContent, ArrayList<String> answersArr, int correctAnswer) {
        this.questionContent = questionContent;
        this.answers = new ArrayList<>();
        this.answers.addAll(answersArr);
        this.correctAnswer = correctAnswer;
    }


    public void fromJson(JSONObject json) throws Exception {
        this.questionContent = json.getString("questionContent");
        this.answers = new ArrayList<>();
        JSONArray answersArray = json.getJSONArray("answers");
        for (int i = 0; i < answersArray.length(); i++) {
            this.answers.add(answersArray.getString(i));
        }
        this.correctAnswer = json.getInt("correctAnswer");
    }

    public int getCorrectAnswer() {
        return this.correctAnswer;
    }


    public String getQuestionContent() {
        return this.questionContent;
    }

    public ArrayList<String> getAnswers() {
        ArrayList<String> res = new ArrayList<>();
        res.addAll(this.answers);
        return res;
    }

    public void setQuestionContent(String content) {
        this.questionContent = content;
    }

    public void setAnswers(ArrayList<String> answers) {
        if (this.answers == null) {
            this.answers = new ArrayList<>();
            this.answers.addAll(answers);
            return;
        }
        this.answers.clear();
        this.answers.addAll(answers);
    }

    public void setCorrectAnswer(int answer) {
        this.correctAnswer = answer;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionContent='" + questionContent + '\'' +
                ", answers=" + answers +
                ", correctAnswer=" + correctAnswer +
                '}';
    }
}

