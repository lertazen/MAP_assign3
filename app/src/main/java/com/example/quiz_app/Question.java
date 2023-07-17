package com.example.quiz_app;

public class Question {
    private String text;
    private boolean ans;

    public Question(String text, boolean ans) {
        this.text = text;
        this.ans = ans;
    }

    public String getText() {
        return text;
    }

    public boolean isAns() {
        return ans;
    }
}
