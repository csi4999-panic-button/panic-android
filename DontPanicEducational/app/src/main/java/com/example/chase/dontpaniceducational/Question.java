package com.example.chase.dontpaniceducational;

import java.io.Serializable;

public class Question implements Serializable {
    private String question, user, id;
    private Answer answerObject;
    private int resolution, votes;
    private boolean voted;

    public Question() {}

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public Answer getAnswerObject() {
        return answerObject;
    }

    public void setAnswerObject(Answer answerObject) {
        this.answerObject = answerObject;
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
}