package com.example.hidon_home;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Question implements Parcelable {

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

    protected Question(Parcel in) {
        this.questionContent = in.readString();

        // Read answers
        this.answers = new ArrayList<>();
        in.readStringList(this.answers);

        this.correctAnswer = in.readInt();
    }

    // Parcelable Creator
    public static final Parcelable.Creator<Question> CREATOR = new Parcelable.Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    // Parcelable method to describe contents
    @Override
    public int describeContents() {
        return 0;
    }

    // Write object data to parcel
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.questionContent);
        dest.writeStringList(this.answers);
        dest.writeInt(this.correctAnswer);
    }


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

