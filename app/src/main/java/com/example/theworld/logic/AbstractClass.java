package com.example.theworld.logic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.theworld.MainActivity;
import com.example.theworld.R;

import java.util.AbstractCollection;
import java.util.ArrayList;

import database.ProcessingDatabase;

/**
 * AbstractClass in order to provide an outline for the {@link com.example.theworld.mapMaster.MapMasterActivity}
 * and {@link com.example.theworld.flagfinder.FlagFinderActivity} in order to reduce duplication.
 * Not implemented entirely as I don't want to mess up the sharedpreferences for saving user guesses
 * and stats.
 */
public  abstract class AbstractClass extends Activity {
    ArrayList<String> countryList = new ArrayList<>();
    Context context;
    ImageView imageView;

    String imagePath;
    boolean map;
    private Logic l;

    SharedPreferences sp;
    SharedPreferences time;
    SharedPreferences stats;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = setContext();
        setContentView(setView());
        l = new Logic(new ProcessingDatabase(context));
        this.imagePath = setImagePath();
        this.map = setMapClass();
//        sp = getSharedPreferences("user guesses", Context.MODE_PRIVATE);
//        stats = getSharedPreferences("stats", Context.MODE_PRIVATE);
//        time = getSharedPreferences("times", Context.MODE_PRIVATE);
//        checkDays();
//        useSP();
    }


    /**
     * Abstract method to set the view of the relevant activity when overriden in an activity
     * class
     * @return
     */
    public abstract int setView();

    /**
     * Abstract method to set the context of the relevant activity when overriden in an activity
     * class
     * @return
     */
    public abstract Context setContext();

    /**
     * Abstract method to set the image path of the relevant activity when overriden in an activity
     * class
     * @return
     */
    public abstract String setImagePath();

    /**
     * Abstract method which allows the type of activity to be determined
     * class
     * @return
     */
    public abstract boolean setMapClass();



    /**
     * Populates the UI with a drop down list of countries to guess from when the user is making a guess.
     * Also helps the user to see what countries are available
     */
    public void displayPickList() {
        countryList = l.getCountryNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.select_dialog_singlechoice, countryList);
        AutoCompleteTextView acTextView = (AutoCompleteTextView) findViewById(R.id.userGuess);
        acTextView.setThreshold(1);
        acTextView.setAdapter(adapter);
        //  Log.e("row", pd.flagRow(MainActivity.path).toString());
        l.correctCountry(imagePath, this.map);
    }

    /**
     * Displays the relevant flag or map outline which the user has to guess.
     * Interacts with the {@link ImageListener} interface.
     * @param folder
     */
    public void displayImage(String folder) {
        imageView = (ImageView) findViewById(R.id.imageView);
        l.getImage(folder, new ImageListener() {
            @Override
            public void onImageReceived(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
            @Override
            public void onError(Exception e) {
                Log.e("fail",e.toString());
            }
        });

    }

    // https://stackoverflow.com/questions/12952865/how-to-share-text-to-whatsapp-from-my-app
    private void shareToWhatsapp(int count,boolean isMap){
        String activity = isMap ? "Map Master" : "Flag Finder";
        String toShare = "";
        if(count == -1){
            toShare = "Didn't get the " + activity + " today";
        }else{
            toShare = "I got " +activity+ " in " + (count+1) + " today";
        }

        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, toShare);
        try {
            context.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "Whatsapp may not be installed", Toast.LENGTH_LONG).show();
        }

    }

    public void sharing(int guesses, boolean isMap){
        Button share = (Button) findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shareToWhatsapp(guesses,isMap);
            }
        });

    }





}
