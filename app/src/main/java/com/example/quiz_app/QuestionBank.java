package com.example.quiz_app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class QuestionBank {
    private List<Question> questions;
    private int currentIndex;

    public QuestionBank() {
        questions = new ArrayList<>();
        currentIndex = 0;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void shuffleQuestions() {
        Collections.shuffle(questions);
    }

    public Question getCurrentQuestion() {
        if(currentIndex >= 0 && currentIndex < questions.size()) {
            return questions.get(currentIndex);
        }
        return null;
    }

    public int getCurrentQuestionIndex() {
        return currentIndex;
    }

    public void moveToNextQuestion() {
        currentIndex++;
    }

    public boolean isLastQuestion() {
        return currentIndex == questions.size() - 1;
    }

    public void reset() {
        currentIndex = 0;
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public void removeQuestions(int userInputNumber) {
        Collections.shuffle(questions);
        int numberOfQuestionsToBeRemoved = questions.size() - userInputNumber;
        for (int i = 0; i < numberOfQuestionsToBeRemoved; i++) {
            questions.remove(i);
        }
    }

    public void setCurrentIndex(int index) {
        currentIndex = index;
    }

}
