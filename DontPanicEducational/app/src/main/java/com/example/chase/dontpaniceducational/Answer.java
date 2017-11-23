package com.example.chase.dontpaniceducational;

import java.io.Serializable;
import java.util.ArrayList;

public class Answer implements Serializable {
    private ArrayList<Answer> answers = new ArrayList<>();
    private String user, id;
    private ArrayList<String> votes = new ArrayList<>();
    private boolean resolution;

    public ArrayList getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList answer) {
        this.answers = answers;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getVotes() {
        return votes;
    }

    public void setVotes(ArrayList<String> votes) {
        this.votes = votes;
    }

    public boolean isResolution() {
        return resolution;
    }

    public void setResolution(boolean resolution) {
        this.resolution = resolution;
    }
}
