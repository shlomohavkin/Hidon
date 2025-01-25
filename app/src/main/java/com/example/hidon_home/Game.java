package com.example.hidon_home;

import java.util.ArrayList;

public class Game {
    private String id;
    private int player1Score;
    private int player2Score;
    private ArrayList<Question> questions = new ArrayList<>();
    private PlayerState player1; // Player 1 state
    private PlayerState player2; // Player 2 state


    public Game(String id, int player1Score, int player2Score, ArrayList<Question> questions, PlayerState player1, PlayerState player2) {
        this.id = id;
        this.questions = new ArrayList<>(questions); // Deep copy of questions
        this.player1 = player1;
        this.player2 = player2;
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public Game() {
        // Default constructor required for Firebase
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = new ArrayList<>(questions); // Deep copy
    }

    public int getPlayer1Score() {
        return player1Score;
    }
    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }
    public int getPlayer2Score() {
        return player2Score;
    }
    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public PlayerState getPlayer1() {
        return player1;
    }

    public void setPlayer1(PlayerState player1) {
        this.player1 = player1;
    }

    public PlayerState getPlayer2() {
        return player2;
    }

    public void setPlayer2(PlayerState player2) {
        this.player2 = player2;
    }

    // Nested class for Player State
    public static class PlayerState {
        private int lastQuestionAnswered;
        private boolean isCorrectAnswerChosen;
        private long timeStamp;

        public PlayerState(int lastQuestionAnswered, boolean isCorrectAnswerChosen, long timeStamp) {
            this.lastQuestionAnswered = lastQuestionAnswered;
            this.isCorrectAnswerChosen = isCorrectAnswerChosen;
            this.timeStamp = timeStamp;
        }

        public PlayerState() {
            // Default constructor required for Firebase
        }

        public int getLastQuestionAnswered() {
            return lastQuestionAnswered;
        }

        public void setLastQuestionAnswered(int lastQuestionAnswered) {
            this.lastQuestionAnswered = lastQuestionAnswered;
        }

        public boolean getIsCorrectAnswerChosen() {
            return isCorrectAnswerChosen;
        }

        public void setIsCorrectAnswerChosen(boolean correctAnswerChosen) {
            isCorrectAnswerChosen = correctAnswerChosen;
        }

        public long getTimeStamp() {
            return timeStamp;
        }
        public void setTimeStamp(long timeStamp) {
            this.timeStamp = timeStamp;
        }
    }
}