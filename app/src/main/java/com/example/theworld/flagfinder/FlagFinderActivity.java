package com.example.theworld.flagfinder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.theworld.MainActivity;
import com.example.theworld.R;
import com.example.theworld.logic.AbstractClass;
import com.example.theworld.logic.ImageListener;
import com.example.theworld.logic.Logic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import database.DataBaseHelper;
import database.ProcessingDatabase;

/**
 * Activitiy for FlagFinder part of the application, extends AbstractClass. User is presented with
 * one flag on a daily basis which they then have six attempts to guess. Data on guesses is added
 * to shared preferences which is then displayed in the {@link com.example.theworld.ui.notifications.StatsActivity}
 */
public class FlagFinderActivity extends AbstractClass {
    ImageView imageView;
    Context context;
    ArrayList<String> countries;
    ArrayList<TextView> countryViews = new ArrayList<>();
    ArrayList<TextView> bANDdViews = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences time;
    SharedPreferences stats;
    private Logic l;
    int count = 0;
    boolean dayChange = false;
    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(setView());
        context = setContext();
        l = new Logic(new ProcessingDatabase(context));
        countries = l.getCountryNames();
        displayImage("flags/");
        displayPickList();
        addViews(countryViews);
        addViews(bANDdViews);
        sp = getSharedPreferences("user guesses", Context.MODE_PRIVATE);
        stats = getSharedPreferences("stats", Context.MODE_PRIVATE);
        time = getSharedPreferences("times", Context.MODE_PRIVATE);
        checkDays();
        useSP();
        acceptOrReject();
        Log.d("date today", l.dateToday());
    }

    @Override
    public int setView(){
        return R.layout.flag_finder;
    }
    @Override
    public Context setContext(){
        return this;
    }

    @Override
    public String setImagePath(){
        return MainActivity.path;
    }

    @Override
    public boolean setMapClass(){
        return false;
    }





    /**
     * Method that accepts or rejects the user's guess after clicking the guess button, based on
     * whether the guess in a list of countries in the database. If the guess is accepted then the
     * {@link #populate(String)} method will be used to populate the UI and determine if the guess
     * is correct. If the input is not accepted it is because the guess is not in the database.
     */
    private void acceptOrReject() {

        Button guess = (Button) findViewById(R.id.guessFlag);
        guess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AutoCompleteTextView userInput = (AutoCompleteTextView) findViewById(R.id.userGuess);
                String userGuess = userInput.getText().toString().toUpperCase();

                if (countries.contains(userGuess)) {
                    userGuess = userGuess.replaceAll("'", "''");
                    Log.d("in list", "yes");
                    addToSharedPreferences(userGuess, count);
                    populate(userGuess);
                    userInput.getText().clear();
                    Log.d("coordinates", l.distanceAndBearing(userGuess, l.correctCountry(MainActivity.path, false)));
                    Log.d("count", count + "");

                } else {
                    Toast.makeText(getApplicationContext(), "Not in country list.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Method to populate the UI based on whether the user input was accepted by {@link #acceptOrReject()}
     * Probably can be broken down into a few more methods in order to adhere to best practices.
     * If the guess is correct then two {@link TextView} will be shown. If correct they will be both
     * be green with the correct country in one and an arrow board? in the other, as well as the
     * guess button being disabled. {@link Toast} will show saying congratulation.
     * If incorrect then both will be red with the incorrect guess in one and the distance and direction
     * in the other. If there are six incorrect guesses then no more will be allowed. {@link Toast}
     * will show saying better luck next time.
     * Data on guesses for {@link com.example.theworld.ui.notifications.StatsActivity} will be added
     * here using {@link #addFlagStats(String,int)}
     * @param userGuess
     */
    private void populate(String userGuess) {
        Button guess = (Button) findViewById(R.id.guessFlag);
        if (count < 6) {
            String distance = l.distanceAndBearing(userGuess, l.correctCountry(MainActivity.path, false));
            int count = count();
            countryViews.get(count).setVisibility(View.VISIBLE);
            bANDdViews.get(count).setVisibility(View.VISIBLE);
            userGuess = userGuess.replaceAll("''", "'");
            countryViews.get(count).setText(userGuess);
            bANDdViews.get(count).setText(distance);


            if (userGuess.equals(l.correctCountry(MainActivity.path, false))) {
                countryViews.get(count).setBackgroundResource(R.color.correctGreen);
                bANDdViews.get(count).setBackgroundResource(R.color.correctGreen);
                // Toast.makeText(getApplicationContext(), "Congratulations", Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), "Congratulations", Toast.LENGTH_LONG).show();
                addFlagStats("Correct guesses", -1);
                addFlagStats("Number of guesses", (count + 1));
                addFlagStats("day", Integer.parseInt(l.dateToday()));
                Button share = (Button) findViewById(R.id.share);
                share.setVisibility(View.VISIBLE);
                sharing(count,false);
                guess.setEnabled(false);
                guess.setText((count + 1) + "/" + 6);
                guess.setBackgroundTintList(context.getResources().getColorStateList(R.color.steelBlack));
            } else {
                countryViews.get(count).setBackgroundResource(R.color.wrongRed);
                bANDdViews.get(count).setBackgroundResource(R.color.wrongRed);
                if (count == 5) {
                    Toast.makeText(getApplicationContext(), l.correctCountry(MainActivity.path,false), Toast.LENGTH_LONG).show();
                    addFlagStats("Fails", -1);
                    addFlagStats("day", Integer.parseInt(l.dateToday()));
                    Button share = (Button) findViewById(R.id.share);
                    share.setVisibility(View.VISIBLE);
                    sharing(-1,false);
                    guess.setEnabled(false);
                    guess.setText("FAIL");
                    guess.setBackgroundTintList(context.getResources().getColorStateList(R.color.steelBlack));
                }
            }
        }
    }

    // shouldn't have done this
    private int count() {
        return this.count++;
    }

    // Adding all the TextViews to an arraylist so that they can be populated incrementally
    private void addViews(ArrayList<TextView> countryOrDirection) {
        if (countryOrDirection == countryViews) {
            TextView view0 = findViewById(R.id.output0);
            TextView view1 = findViewById(R.id.output1);
            TextView view2 = findViewById(R.id.output2);
            TextView view3 = findViewById(R.id.output3);
            TextView view4 = findViewById(R.id.output4);
            TextView view5 = findViewById(R.id.output5);
            countryOrDirection.add(view0);
            countryOrDirection.add(view1);
            countryOrDirection.add(view2);
            countryOrDirection.add(view3);
            countryOrDirection.add(view4);
            countryOrDirection.add(view5);
        } else {
            TextView view0 = findViewById(R.id.bd0);
            TextView view1 = findViewById(R.id.bd1);
            TextView view2 = findViewById(R.id.bd2);
            TextView view3 = findViewById(R.id.bd3);
            TextView view4 = findViewById(R.id.bd4);
            TextView view5 = findViewById(R.id.bd5);
            bANDdViews.add(view0);
            bANDdViews.add(view1);
            bANDdViews.add(view2);
            bANDdViews.add(view3);
            bANDdViews.add(view4);
            bANDdViews.add(view5);
        }
    }

    /**
     * Adding the user's guesses that they have made on a specific day to {@link SharedPreferences}
     * so that that they can be reloaded when the user leaves this Activity.
     * Resets every day.
     * @param userInput
     * @param count
     */
    // https://www.youtube.com/watch?v=jiD2fxn8iKA
    private void addToSharedPreferences(String userInput, int count) {
        SharedPreferences.Editor editor = sp.edit();
        SharedPreferences.Editor timer = time.edit();
        if (this.dayChange) {
            editor.clear();
            Log.d("test", "true");
        }
        String key = count + "";
        editor.putString(key, userInput);
        editor.commit();
        timer.commit();
    }

    /**
     * After the user is done with their guesses for the day i.e they got the right answer or reached
     * the limit this method will be called adding the statistics to {@link SharedPreferences}
     * to be used in the {@link com.example.theworld.ui.notifications.StatsActivity}
     * Example key = "Correct Guesses" with a default key of "-1"
     * @param key
     * @param value
     */
    private void addFlagStats(String key, int value) {
        SharedPreferences get = getSharedPreferences("stats", Context.MODE_PRIVATE);
        String day = get.getString("day", "");
        String dateToday = Integer.parseInt(l.dateToday()) + "";
        Log.d("sp data", day);
        Log.d("string int", dateToday);
        SharedPreferences.Editor editor = stats.edit();
        if (key.equals("Number of guesses")) {
            key += " " + value;
        }
        if (!day.equals(dateToday)) {
            if (key.equals("day")) {
                editor.putString(key, value + "");
                editor.commit();
            } else {
                int oldValue = stats.getInt(key, 0);
                int newValue = oldValue + 1;
                editor.putInt(key, newValue);
                editor.commit();
            }
        }
    }



    // https://stackoverflow.com/questions/22089411/how-to-get-all-keys-of-sharedpreferences-programmatically-in-android

    /**
     * Repopulating the ui with their previous guesses for the day if the leave the activity using
     * {@link SharedPreferences}
     */
    private void useSP() {
        SharedPreferences get = getSharedPreferences("user guesses", Context.MODE_PRIVATE);
        Map<String, ?> info = get.getAll();
        for (Map.Entry<String, ?> entry : info.entrySet()) {
            if (Integer.parseInt(entry.getKey()) < 6) {
                Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                this.count = Integer.parseInt(entry.getKey());
                populate(entry.getValue().toString());
            }

        }
    }

    /**
     * Determining if the day has changed. If the day has changed then clear the shared preferences
     */
    private void checkDays() {
        SharedPreferences.Editor editor = sp.edit();
        SharedPreferences.Editor timer = time.edit();
        timer.putString(l.dateToday(), l.dateToday());
        SharedPreferences get = getSharedPreferences("times", Context.MODE_PRIVATE);
        Map<String, ?> info = get.getAll();
        for (Map.Entry<String, ?> entry : info.entrySet()) {
            Log.d("date saved", entry.getKey());
            if (!entry.getKey().equals(l.dateToday()) && Integer.parseInt(entry.getKey()) != 0) {
                editor.clear();
                timer.remove(entry.getKey());
                this.count = 0;
            }
        }
        editor.commit();
        timer.commit();
    }





}
