package com.example.theworld.mapMaster;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.theworld.MainActivity;
import com.example.theworld.R;
import com.example.theworld.logic.AbstractClass;
import com.example.theworld.logic.ImageListener;
import com.example.theworld.logic.Logic;

//import com.example.theworld.flagfinder.FlagListener;
//import com.example.theworld.flagfinder.FlagLogic;

import java.util.ArrayList;
import java.util.Map;

import database.ProcessingDatabase;


/**
 * Activitiy for MapMaster part of the application, extends AbstractClass. User is presented with
 * one map on a daily basis which they then have six attempts to guess the country. Data on guesses is added
 * to shared preferences which is then displayed in the {@link com.example.theworld.ui.notifications.StatsActivity}
 */
public class MapMasterActivity extends AbstractClass {

    ImageView imageView;
    Context context;
    ArrayList<String> countries;
    ArrayList<TextView> countryViews = new ArrayList<>();
    ArrayList<TextView> bANDdViews = new ArrayList<>();
    SharedPreferences sp;
    SharedPreferences time;
    SharedPreferences mapStats;
    private Logic l;
    int count = 0;
    boolean dayChange = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setView());
        context = setContext();
        l = new Logic(new ProcessingDatabase(context));
        countries = l.getCountryNames();
        displayImage("outlines/");
        displayPickList();
        addViews(countryViews);
        addViews(bANDdViews);
        sp = getSharedPreferences("user guesses map", Context.MODE_PRIVATE);
        time = getSharedPreferences("times map", Context.MODE_PRIVATE);
        mapStats = getSharedPreferences("map stats", Context.MODE_PRIVATE);
        checkDays();
        useSP();
        acceptOrReject();
        //Log.e("date today", dateToday());
    }

    @Override
    public int setView(){
        return R.layout.map_master;
    }
    @Override
    public Context setContext(){
        return this;
    }

    @Override
    public String setImagePath(){
        return MainActivity.mapPath;
    }

    @Override
    public boolean setMapClass(){
        return true;
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
                    Log.d("coordinates", l.distanceAndBearing(userGuess, l.correctCountry(MainActivity.mapPath,true)));
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
     * here using {@link #addMapStats(String,int)}
     * @param userGuess
     */
    private void populate(String userGuess) {
        Button guess = (Button) findViewById(R.id.guessFlag);
        if (count < 6) {
            String distance = l.distanceAndBearing(userGuess, l.correctCountry(MainActivity.mapPath, true));
            int count = count();
            //++this.count;
            //TextView textView = views.get(count);
            countryViews.get(count).setVisibility(View.VISIBLE);
            bANDdViews.get(count).setVisibility(View.VISIBLE);
            userGuess = userGuess.replaceAll("''", "'");
            countryViews.get(count).setText(userGuess);
            bANDdViews.get(count).setText(distance);


            if (userGuess.equals(l.correctCountry(MainActivity.mapPath, true))) {
                countryViews.get(count).setBackgroundResource(R.color.correctGreen);
                bANDdViews.get(count).setBackgroundResource(R.color.correctGreen);
                Toast.makeText(getApplicationContext(), "Congratulations", Toast.LENGTH_LONG).show();
                addMapStats("Correct guesses", -1);
                addMapStats("Number of guesses", (count + 1));
                addMapStats("day", Integer.parseInt(l.dateToday()));
                Button share = (Button) findViewById(R.id.share);
                share.setVisibility(View.VISIBLE);
                sharing(count,true);
                guess.setEnabled(false);
                //https://stackoverflow.com/questions/29801031/how-to-add-button-tint-programmatically
                guess.setBackgroundTintList(context.getResources().getColorStateList(R.color.steelBlack));
                guess.setText((count + 1) + "/" + 6);


            } else {
                countryViews.get(count).setBackgroundResource(R.color.wrongRed);
                bANDdViews.get(count).setBackgroundResource(R.color.wrongRed);
                if (count == 5) {
                    Toast.makeText(getApplicationContext(), l.correctCountry(MainActivity.mapPath,true), Toast.LENGTH_LONG).show();
                    //guess.setBackgroundResource(R.color.wrongRed);
                    Button share = (Button) findViewById(R.id.share);
                    share.setVisibility(View.VISIBLE);
                    sharing(-1,true);
                    addMapStats("Fails", -1);
                    addMapStats("day", Integer.parseInt(l.dateToday()));
                    guess.setEnabled(false);
                    guess.setText("FAIL");
                    guess.setBackgroundTintList(context.getResources().getColorStateList(R.color.steelBlack));                }
            }
        }
    }

    private int count() {
        return this.count++;
    }

    // Adding TextViews to an arraylist so they can be populated when required
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

    // https://www.youtube.com/watch?v=jiD2fxn8iKA
    /**
     * Adding the user's guesses that they have made on a specific day to {@link SharedPreferences}
     * so that that they can be reloaded when the user leaves this Activity.
     * Resets every day.
     * @param userInput
     * @param count
     */
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
    private void addMapStats(String key, int value) {
        SharedPreferences get = getSharedPreferences("map stats", Context.MODE_PRIVATE);
        String day = get.getString("day", "");
        String dateToday = Integer.parseInt(l.dateToday()) + "";
        Log.d("sp data", day);
        Log.d("string int", dateToday);
        SharedPreferences.Editor editor = mapStats.edit();
        if (key.equals("Number of guesses")) {
            key += " " + value;
        }
        if (!day.equals(dateToday)) {
            if (key.equals("day")) {
                editor.putString(key, value + "");
                editor.commit();
            } else {
                int oldValue = mapStats.getInt(key, 0);
                int newValue = oldValue + 1;
                editor.putInt(key, newValue);
                editor.commit();
            }
        }
    }

    /**
     * Repopulating the ui with their previous guesses for the day if the leave the activity using
     * {@link SharedPreferences}
     */
    // https://stackoverflow.com/questions/22089411/how-to-get-all-keys-of-sharedpreferences-programmatically-in-android
    private void useSP() {
        SharedPreferences get = getSharedPreferences("user guesses map", Context.MODE_PRIVATE);
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
        SharedPreferences get = getSharedPreferences("times map", Context.MODE_PRIVATE);
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
