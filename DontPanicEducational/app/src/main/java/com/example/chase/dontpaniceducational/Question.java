package com.example.chase.dontpaniceducational;

import java.io.Serializable;
import java.util.ArrayList;

public class Question implements Serializable {
    private String question, id;
    private ArrayList<Answer> answerList = new ArrayList<>();
    private int resolution, votes;
    private boolean voted, mine;

    public Question() {}

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionId() {
        return id;
    }

    public void setQuestionId(String id) {
        this.id = id;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public ArrayList<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(ArrayList<Answer> answerList) {
        this.answerList = answerList;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public boolean getVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }
}