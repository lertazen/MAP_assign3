package com.example.quiz_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private Button trueBtn;
    private Button falseBtn;

    private QuestionBank questionBank;
    private int questionCount;
    private int correctAnsCount;
    private int prevColor;
    private int currentColor;
    private int currentQuestionIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize variables
        progressBar = findViewById(R.id.progressBar);
        trueBtn = findViewById(R.id.true_button);
        falseBtn = findViewById(R.id.false_button);



        // Set up action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            currentColor = savedInstanceState.getInt("currentColor");
            correctAnsCount = savedInstanceState.getInt("correctAnsCount");
            questionCount = savedInstanceState.getInt("questionCount");
            questionBank = createQuestionBank();
            currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex");
            questionBank.setCurrentIndex(currentQuestionIndex);
        } else {
            // Initialize variables when savedInstanceState is null
            questionBank = createQuestionBank();
            questionCount = questionBank.getQuestionCount();
            currentColor = getRandomColor();
            questionCount = questionBank.getQuestionCount();
            correctAnsCount = 0;
        }
        prevColor = -1;
        updateProgressBar();

        // Pass the initial value to the fragment
        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();

            Question currentQuestion = questionBank.getCurrentQuestion();
            String questionText = currentQuestion.getText();
            bundle.putInt("current_color", currentColor);
            bundle.putString("new_question", questionText);

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentContainerView, QuestionFragment.class, bundle)
                    .commit();
        }

        // Set up onClick listener for two buttons
        trueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColor = getRandomColor();
                checkAnswer(true);
                newFragment();
                updateProgressBar();
            }
        });

        falseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColor = getRandomColor();
                checkAnswer(false);
                newFragment();
                updateProgressBar();
            }
        });
    }

    // Question bank
    private QuestionBank createQuestionBank() {
        QuestionBank newQuestionBank = new QuestionBank();

        newQuestionBank.addQuestion(new Question(getString(R.string.question_1), false));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_2), true));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_3), false));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_4), true));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_5), false));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_6), true));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_7), false));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_8), true));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_9), false));
        newQuestionBank.addQuestion(new Question(getString(R.string.question_10), true));

        newQuestionBank.shuffleQuestions();

        return newQuestionBank;
    }

    // Set up save instance state bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentColor", currentColor);
        outState.putInt("correctAnsCount", correctAnsCount);
        outState.putInt("questionCount", questionCount);
        currentQuestionIndex = questionBank.getCurrentQuestionIndex();
        outState.putInt("currentQuestionIndex", currentQuestionIndex);
    }

    // Generate random color
    private int getRandomColor() {
        Random random = new Random();
        int[] colors = {R.color.orange, R.color.purple_500, R.color.purple_700, R.color.teal_700, R.color.red,
        R.color.teal, R.color.green, R.color.black, R.color.teal_200, R.color.purple_200};
        int colorIndex = random.nextInt(colors.length);

        while (colors[colorIndex] == prevColor) {
            colorIndex = random.nextInt(colors.length);
        }
        prevColor = colors[colorIndex];
        return colors[colorIndex];
    }

    // Update progress bar
    private void updateProgressBar() {
        progressBar.setMax(questionCount);
        progressBar.setProgress(questionBank.getCurrentQuestionIndex() + 1);
    }

    // Check answer
    private void checkAnswer(boolean answer) {
        Question currentQuestion = questionBank.getCurrentQuestion();

        if (currentQuestion != null) {
            if (currentQuestion.isAns() == answer) {
                Toast.makeText(MainActivity.this, getString(R.string.button_true), Toast.LENGTH_SHORT).show();
                correctAnsCount++;
            }
            else {
                Toast.makeText(MainActivity.this, getString(R.string.button_false), Toast.LENGTH_SHORT).show();
            }

            if (questionBank.isLastQuestion()) {
                showResultsDialog();
            }
            else {
                questionBank.moveToNextQuestion();
            }
        }
    }

    // Show the results when the quiz finishes
    private void showResultsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.result_dialog_title))
                .setMessage(getString(R.string.dialog_message) + correctAnsCount + " / " + questionCount)
                .setPositiveButton(getString(R.string.button_save), (dialog, which) -> {
                    saveQuizResults();
                    resetQuiz();
                    updateProgressBar();
                })
                .setNegativeButton(getString(R.string.button_ignore), (dialog, which) -> {
                    resetQuiz();
                    updateProgressBar();
                })
                .setCancelable(false)
                .show();
    }

    private void resetQuiz() {
        questionBank.shuffleQuestions();
        questionBank.reset();
        correctAnsCount = 0;
    }

    // Save the results to the storage
    private void saveQuizResults() {
        String filename = "quiz_results.txt";
        String data = correctAnsCount + "/" + questionCount;

        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_APPEND);
            fos.write((data + "\n").getBytes());
            fos.close();
            Toast.makeText(MainActivity.this, getString(R.string.save_success), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.save_fail), Toast.LENGTH_SHORT).show();
        }
    }

    // Generate new fragment and pass the arguments
    private void newFragment() {
        QuestionFragment questionFragment = new QuestionFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        Bundle data = new Bundle();

        Question currentQuestion = questionBank.getCurrentQuestion();
        String questionText = currentQuestion.getText();
        data.putInt("current_color", currentColor);
        data.putString("new_question", questionText);

        questionFragment.setArguments(data);
        fragmentTransaction.replace(R.id.fragmentContainerView, questionFragment).commit();
    }

    // Inflate the overflow menu with menu resources
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    // Set up click events for menu items (get average for example)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        int itemId = item.getItemId();
        if (itemId == R.id.average) {
            getAverage();
            return true;
        } else if (itemId == R.id.change_q_number) {
            showChangeQuestionsCountDialog();
            return true;
        } else if (itemId == R.id.reset_results) {
            resetQuizResults();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void resetQuizResults() {
        String filename = "quiz_results.txt";

        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.close();
            Toast.makeText(MainActivity.this, getString(R.string.reset_success), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.reset_fail), Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangeQuestionsCountDialog() {
        int maxQuestionCount = questionBank.getQuestionCount();
        DialogFragment dialogFragment = QuestionsNumberFragment.newInstance(maxQuestionCount);
        dialogFragment.show(getSupportFragmentManager(), QuestionsNumberFragment.TAG);
    }

    private void getAverage() {
        // Read the quiz results form the file
        String filename = "quiz_results.txt";
        String data;
        try {
            FileInputStream fis = openFileInput(filename);
            InputStreamReader inputStreamReader = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            fis.close();
            data = stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, getString(R.string.read_fail), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the data is empty or null
        if (data == null || data.isEmpty()) {
            Toast.makeText(MainActivity.this, getString(R.string.read_empty), Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the total correct and questions answered from the string
        int totalQuestions = 0;
        int totalCorrect = 0;

        String[] results = data.split("\n");
        for (String result : results) {
            String[] values = result.split("/");
            int correct = Integer.parseInt(values[0]);
            int numQuestions = Integer.parseInt(values[1]);

            totalCorrect += correct;
            totalQuestions += numQuestions;
        }

        // Display the average to the user
        String averageMessage = getString(R.string.history_message) + totalCorrect + "/" + totalQuestions;
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.history_title))
                .setMessage(averageMessage)
                .setPositiveButton(getString(R.string.button_ok), null)
                .show();
    }

    public void changeQuestionsCount(int userInputNumber) {
        questionBank.removeQuestions(userInputNumber);
        questionCount = questionBank.getQuestionCount();
        resetQuiz();
        newFragment();
        updateProgressBar();
    }

}