package com.deeksha.jsonapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private List<Question> questions;
    private int currentQuestion = 0;
    private CountDownTimer timer;
    private static final long QUIZ_DURATION_MILLIS = 600000; // 10 minutes

    private TextView questionTextView;
    private Button option1Button, option2Button, option3Button, option4Button, startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load questions from JSON
        questions = parseJson();

        // Initialize UI elements
        questionTextView = findViewById(R.id.questionTextView);
        option1Button = findViewById(R.id.option1Button);
        option2Button = findViewById(R.id.option2Button);
        option3Button = findViewById(R.id.option3Button);
        option4Button = findViewById(R.id.option4Button);
        startButton = findViewById(R.id.startButton);

        // Restore question index from SharedPreferences (if available)
        currentQuestion = SharedPrefsUtil.getCurrentQuestion();

        // Restore timer state if activity was recreated
        if (savedInstanceState != null) {
            long remainingTime = savedInstanceState.getLong("remainingTime", 0);
            if (remainingTime > 0) {
                startTimer(remainingTime);
            }
        } else {
            // Get remaining time from SharedPreferences
            long remainingTime = SharedPrefsUtil.getRemainingTime();
            if (remainingTime < QUIZ_DURATION_MILLIS) {
                startTimer(remainingTime);
            }
        }

        // UI setup (text views, buttons, etc.)
        updateQuestion();

        // Start button click listener
        startButton.setOnClickListener(v -> startQuiz());

        // Option button click listeners
        option1Button.setOnClickListener(v -> handleOptionClick(0));
        option2Button.setOnClickListener(v -> handleOptionClick(1));
        option3Button.setOnClickListener(v -> handleOptionClick(2));
        option4Button.setOnClickListener(v -> handleOptionClick(3));
    }

    private void startQuiz() {
        // Reset quiz state
        currentQuestion = 0;
        updateQuestion();
        startTimer(QUIZ_DURATION_MILLIS); // Set timer duration
        SharedPrefsUtil.clearSavedData(); // Clear any previous saved data
    }

    private void startTimer(long duration) {
        if (timer != null) {
            timer.cancel(); // Cancel any existing timer
        }
        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long remainingTime = millisUntilFinished / 1000;
                // Update UI with remaining time
                // e.g., timerTextView.setText("Time left: " + remainingTime + "s");
                SharedPrefsUtil.saveRemainingTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // Handle quiz end
                // e.g., show results, disable options, etc.
            }
        };
        timer.start();
    }

    private void updateQuestion() {
        if (currentQuestion < questions.size()) {
            Question question = questions.get(currentQuestion);
            questionTextView.setText(question.getQuestion());
            List<String> options = question.getOptions();
            option1Button.setText(options.get(0));
            option2Button.setText(options.get(1));
            option3Button.setText(options.get(2));
            option4Button.setText(options.get(3));
        }
    }

    private void handleOptionClick(int optionIndex) {
        Question question = questions.get(currentQuestion);
        if (optionIndex == question.getAnswerIndex()) {
            // Handle correct answer
        } else {
            // Handle incorrect answer
        }
        currentQuestion++;
        if (currentQuestion < questions.size()) {
            updateQuestion();
            SharedPrefsUtil.saveCurrentQuestion(currentQuestion);
        } else {
            // Quiz is finished
            timer.cancel();
            // Show results or perform any other end-of-quiz actions
        }
    }

    private List<Question> parseJson() {
        List<Question> questionList = new ArrayList<>();
        try {
            InputStream inputStream = getAssets().open("questions.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            String json = jsonBuilder.toString();
            Question[] questionsArray = new Gson().fromJson(json, Question[].class);
            for (Question question : questionsArray) {
                questionList.add(question);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return questionList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (timer != null) {
            outState.putLong("remainingTime", SharedPrefsUtil.getRemainingTime());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPrefsUtil.saveCurrentQuestion(currentQuestion);
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        long remainingTime = SharedPrefsUtil.getRemainingTime();
        if (remainingTime < QUIZ_DURATION_MILLIS) {
            startTimer(remainingTime);
        }
        updateQuestion();
    }
}