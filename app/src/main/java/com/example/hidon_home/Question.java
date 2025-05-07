package com.example.hidon_home;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class represents a question in the game.
 */
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

    public Question(Question other) {
        this.questionContent = other.questionContent;
        this.answers = new ArrayList<>();
        this.answers.addAll(other.answers);
        this.correctAnswer = other.correctAnswer;
    }


    /**
     * Converts the question object to a JSON object.
     * This is used when I get an answer from the API
     * and I need to convert a json object to a question object.
     * @return A JSON object representing the question.
     */
    public void fromJson(JSONObject json) throws Exception {
        this.questionContent = json.getString("questionContent");
        this.answers = new ArrayList<>();
        JSONArray answersArray = json.getJSONArray("answers");
        for (int i = 0; i < answersArray.length(); i++) {
            this.answers.add(answersArray.getString(i));
        }
        this.correctAnswer = json.getInt("correctAnswer");

        // Randomize the answers
        String correctAnswerValue = this.answers.get(this.correctAnswer); // Store the correct answer
        Collections.shuffle(this.answers); // Shuffle the answers
        this.correctAnswer = this.answers.indexOf(correctAnswerValue);
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

