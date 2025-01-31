package com.example.hidon_home.notes_game;

import android.util.Log;

import java.util.ArrayList;

public class NotesGame {
    private int roomNumber;
    private int playerCount;
    private ArrayList<String> names;


    public NotesGame(int roomNumber, int playerCount, ArrayList<String> names) {
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
    }

    public NotesGame() {}

    public int getPlayerCount() {
        return this.playerCount;
    }
    public ArrayList<String> getNames() {
        return names;
    }
    public int getRoomNumber() {
        return this.roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }
    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }
    public void setNames(ArrayList<String> names) {
        this.names = new ArrayList<>(names);
    }
    public void addName(String name) {
        this.names.add(name);
    }

}
