package com.deeksha.jsonapp;


import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefsUtil {

    private static final String PREFS_NAME = "QuizPrefs";
    private static SharedPreferences prefs;

    public static void init(Context context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        }
    }

    public static void saveCurrentQuestion(int questionIndex) {
        prefs.edit().putInt("currentQuestion", questionIndex).apply();
    }

    public static int getCurrentQuestion() {
        return prefs.getInt("currentQuestion", 0);
    }

    public static void saveRemainingTime(long remainingTime) {
        prefs.edit().putLong("remainingTime", remainingTime).apply();
    }

    public static long getRemainingTime() {
        return prefs.getLong("remainingTime", 600000); // default to 10 minutes
    }

    public static void clearSavedData() {
        prefs.edit().clear().apply();
    }
}