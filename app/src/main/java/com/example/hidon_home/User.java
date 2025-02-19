package com.example.hidon_home;

import com.example.hidon_home.notes_game.Questioneer;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String password;
    private ArrayList<Questioneer> questioners;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public User(String name, String email, String password, ArrayList<Questioneer> questioners) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.questioners = questioners;
    }

    public User(String name, String email, String password, Questioneer questioner) {
        this.name = name;
        this.email = email;
        this.password = password;
        if (this.questioners == null)
            this.questioners = new ArrayList<>();
        this.questioners.add(questioner);
    }

    public User() {}

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Questioneer> getQuestioners() {
        return this.questioners;
    }

    public void setQuestioners(ArrayList<Questioneer> questioners) {
        this.questioners = questioners;
    }

    public void addQuestion(Questioneer questioneer) {
        if (this.questioners == null) {
            this.questioners = new ArrayList<>();
        }
        this.questioners.add(questioneer);
    }
}
