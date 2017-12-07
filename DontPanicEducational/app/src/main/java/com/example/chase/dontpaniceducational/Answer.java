package com.example.chase.dontpaniceducational;

import java.io.Serializable;
import java.util.ArrayList;

public class Answer implements Serializable {
    private String id, answer;
    private ArrayList<String> votes = new ArrayList<>();
    private boolean resolution, mine;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
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

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
}
