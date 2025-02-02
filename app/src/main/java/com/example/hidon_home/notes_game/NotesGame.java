package com.example.hidon_home.notes_game;

import android.util.Log;

import java.util.ArrayList;

public class NotesGame {
    private int roomNumber;
    private int playerCount;
    private ArrayList<String> names = new ArrayList<>();
    private boolean isStarted = false;


    public NotesGame(int roomNumber, int playerCount, ArrayList<String> names, boolean isStarted) {
        this.roomNumber = roomNumber;
        this.playerCount = playerCount;
        this.names = names != null ? names : new ArrayList<>();
    }

    public NotesGame(NotesGame other) {
        this.roomNumber = other.roomNumber;
        this.playerCount = other.playerCount;
        if (other.names == null) {
            this.names = new ArrayList<>();
            return;
        }
        this.names = new ArrayList<>(other.names);
        this.isStarted = other.isStarted;
    }

    public NotesGame() {}

    public int getPlayerCount() {
        return this.playerCount;
    }
    public ArrayList<String> getNames() {
        return new ArrayList<>(this.names);
    }
    public int getRoomNumber() {
        return this.roomNumber;
    }
    public boolean getIsStarted() {
        return this.isStarted;
    }


    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
    public void setNames(ArrayList<String> names) {
        if (names != null)
            this.names = new ArrayList<>(names);
    }
    public void addName(String name) {
        this.names.add(name);
    }
    public void setIsStarted(boolean isStarted) {
        this.isStarted = isStarted;
    }



}
