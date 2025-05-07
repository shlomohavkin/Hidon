package com.example.hidon_home.notes_game;

import com.example.hidon_home.Question;
import java.util.ArrayList;

/**
 * This class represents a questioneer object which contains a list of questions and a title.
 * It is used to create a custom questioneer for the game, and saved in the firebase database
 * in the data of the user.
 */
public class Questioneer {
    private ArrayList<Question> questioneer = new ArrayList<>();
    private String title;

    public Questioneer(ArrayList<Question> questioneer, String title) {
        this.questioneer = questioneer;
        this.title = title;
    }

    public Questioneer(Questioneer other) {
        this.questioneer = new ArrayList<>();
        for (Question question : other.questioneer) {
            this.questioneer.add(new Question(question));
        }
        this.title = other.title;
    }

    public Questioneer() {}

    public ArrayList<Question> getQuestioneer() {
        return questioneer;
    }

    public void setQuestioneer(ArrayList<Question> questioneer) {
        this.questioneer = questioneer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toString() {
        String res = this.title;

        for (int i = 0; i < this.questioneer.size(); i++) {
            res += "\n";
            res += "Question Content: " + this.questioneer.get(i).getQuestionContent() + "\n";
            res += "Answer 1: " + this.questioneer.get(i).getAnswers().get(0) + "\n";
            res += "Answer 2: " + this.questioneer.get(i).getAnswers().get(1) + "\n";
            res += "Answer 3: " + this.questioneer.get(i).getAnswers().get(2) + "\n";
            res += "Answer 4: " + this.questioneer.get(i).getAnswers().get(3) + "\n";
            res += "Correct Answer Index: " + this.questioneer.get(i).getCorrectAnswer() + "\n\n";
        }
        return res;
    }
}
