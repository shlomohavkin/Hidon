package com.example.hidon_home;

import java.util.ArrayList;

/**
 * This class represents a game in the application.
 * It contains information about the game ID, players' scores, questions, and players' states.
 */
public class Game {
    private String id;
    private ArrayList<Integer> playersScore = new ArrayList<>();
    private ArrayList<Question> questions = new ArrayList<>();
    private ArrayList<PlayerState> playersState = new ArrayList<>(); // Players' states in the game


    public Game(String id, ArrayList<Integer> playersScore1, ArrayList<Question> questions, ArrayList<PlayerState> playersState) {
        this.id = id;
        this.questions = new ArrayList<>(questions); // copy of questions
        this.playersState = new ArrayList<>(playersState); // copy of players state
        this.playersScore = new ArrayList<>(playersScore1); // copy of players score
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
        return new ArrayList<>(this.questions);
    }

    public void setQuestions(ArrayList<Question> questions) {
        this.questions = new ArrayList<>(questions); // Deep copy
    }

    public ArrayList<Integer> getPlayersScore() {
        return new ArrayList<>(this.playersScore);
    }
    public int getPlayersScoreAt(int i) {
        if (i < 0 || i >= this.playersScore.size()) {
            throw new IndexOutOfBoundsException("Invalid player score index: " + i);
        }
        return this.playersScore.get(i);
    }
    public void setPlayersScore(ArrayList<Integer> playersScore) {
        this.playersScore = new ArrayList<>(playersScore); // Deep copy
    }

    public void setPlayersScoreAt(int playerScore, int i) {
        if (i < 0 || i >= this.playersScore.size()) {
            throw new IndexOutOfBoundsException("Invalid player score index: " + i);
        }
        this.playersScore.set(i, playerScore);
    }


    public ArrayList<PlayerState> getPlayersState() {
        return new ArrayList<>(this.playersState);
    }

    public PlayerState getPlayersStateAt(int i) {
        if (i < 0 || i >= this.playersState.size()) {
            throw new IndexOutOfBoundsException("Invalid player score index: " + i);
        }
        return new PlayerState(this.playersState.get(i));
    }

    public void setPlayersStateAt(PlayerState playerState, int i) {
        if (i < 0 || i >= this.playersState.size()) {
            throw new IndexOutOfBoundsException("Invalid player score index: " + i);
        }
        this.playersState.set(i, new PlayerState(playerState));
    }



    // Nested class for Player State, which represents the state of a player in the game
    public static class PlayerState {
        private int lastQuestionAnswered;
        private boolean isCorrectAnswerChosen;
        private long timeStamp;
        private int answerChosen;

        public PlayerState(int lastQuestionAnswered, boolean isCorrectAnswerChosen, long timeStamp) {
            this.lastQuestionAnswered = lastQuestionAnswered;
            this.isCorrectAnswerChosen = isCorrectAnswerChosen;
            this.timeStamp = timeStamp;
        }

        public PlayerState(PlayerState other) {
            this.lastQuestionAnswered = other.lastQuestionAnswered;
            this.isCorrectAnswerChosen = other.isCorrectAnswerChosen;
            this.timeStamp = other.timeStamp;
            this.answerChosen = other.answerChosen;
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
        public int getAnswerChosen() {
            return this.answerChosen;
        }
        public void setAnswerChosen(int answerChosen) {
            this.answerChosen = answerChosen;
        }
    }
}