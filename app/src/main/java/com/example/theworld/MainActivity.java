package com.example.theworld;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.theworld.databinding.ActivityMainBinding;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//import database.Daily;
import database.DataBaseHelper;
import database.ProcessingDatabase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private DataBaseHelper db;
    FirebaseFirestore firestore;

    public static Context appContext;

    private String pathToday;

    public static String path;
    private String mapPathToday;
    public static String mapPath;

    private ProcessingDatabase pd;

//https://www.youtube.com/watch?v=aiX8bMPX_t8

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firestore = FirebaseFirestore.getInstance();
        appContext = getApplicationContext();
        pd = new ProcessingDatabase(appContext);
        Map<String, Object> user = new HashMap<>();
        user.put("firstname", "hi");
        user.put("lastname", "hi");
        user.put("description", "student");

        // early stage of development to check if attempts to connect to firestore are valid
        firestore.collection("users").add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(getApplicationContext(), "Working", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Please connect your Wi-Fi connection", Toast.LENGTH_SHORT).show();
            }
        });
        // populating sqlite database with country-capital-lat-long-population.csv
        db = new DataBaseHelper(this);
        //    db.addCSV();
        Cursor cursor = db.getCountryTable();
        if (!cursor.moveToNext()) {
            db.addCSV();
        }
        Log.d("cursor status", String.valueOf(cursor.moveToNext()));
        while (cursor.moveToNext()) {
            String country = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COUNTRY_NAME));
            Log.d("country", country);
            //      db.addCSV();
        }
        cursor.close();

        // Getting the daily images
        dailyFlagFirestore();
        dailyMapFirestore();

        // auto generated
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_gamemode, R.id.navigation_stats)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        //https://stackoverflow.com/questions/24742732/android-studio-action-bar-remove
        getSupportActionBar().hide();
    }

    private void grabber() {
        Log.d("grabber", pathToday + "");
        if (pathToday == null) {
            dailyFlagFirestore();
        }
        path = pathToday;
    }

    private void mapGrabber() {
        Log.d("mapgrabber", mapPathToday + "");
        if (mapPathToday == null) {
            dailyMapFirestore();
        }
        mapPath = mapPathToday;

    }

    /**
     * Getting and setting the flag image from firestore. All the users of the application will share
     * the same image everyday.
     * Example:
     * Step 1: User opens app.
     * Step 2: Method from processImageLocation() from {@link ProcessingDatabase} gets a
     * random imagelocation from the sqlitedatabase.
     * Step 3: The current date e.g. 2023-10-14 and this random image location e.g. bangladesh.png
     * are added to a map. The date relates to a document in the firestore database which at this
     * stage may or may not exist.
     * Step 4: Reference is made firestore database and checks if this document exists
     * Step 5: If it exists already then the Main.path variable is set to the image path. If it
     * doesn't exist then the document is created and the method is called again.
     */
    private void dailyFlagFirestore() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateSimple = dateFormat.format(date);
        String imagePath = pd.processImageLocation();
       // String imageMapPath = pd.processMapImageLocation();
        Log.d("path", imagePath);
        Map<String, Object> flagToday = new HashMap<>();
        if (!flagToday.containsKey(dateSimple)) {
            flagToday.put(dateSimple, imagePath);
        }


        //https://stackoverflow.com/questions/53332471/checking-if-a-document-exists-in-a-firestore-collection
        DocumentReference docIdRef = firestore.collection("whichFlag").document(dateSimple);
        docIdRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("doc check", "Document exists!");
                        pathToday = document.getString(dateSimple);
                        Log.d("doc check", pathToday);
                        grabber();

                    } else {
                        Log.d("doc check", "Document does not exist!");
                        firestore.collection("whichFlag").document(dateSimple).set(flagToday).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Toast.makeText(getApplicationContext(), "flag ready", Toast.LENGTH_SHORT).show();
                                grabber();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "no flag", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.d("doc check", "Failed with: ", task.getException());
                }
            }
        });
        Log.d("flag boo", pathToday + "");
    }

    /**
     * Same as {@link #dailyFlagFirestore()} but for map image
     */
    private void dailyMapFirestore() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateSimple = dateFormat.format(date);
        String imageMapPath = pd.processMapImageLocation();
        Log.d("mapPath", imageMapPath);
        Map<String, Object> mapToday = new HashMap<>();
        if (!mapToday.containsKey(dateSimple)) {
            mapToday.put(dateSimple, imageMapPath);
        }


        DocumentReference docIdRefMap = firestore.collection("whichMap").document(dateSimple);
        docIdRefMap.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("doc check", "Document exists!");
                        mapPathToday = document.getString(dateSimple);
                        Log.d("doc check", mapPathToday);
                        mapGrabber();

                    } else {
                        Log.d("doc check", "Document does not exist!");

                        firestore.collection("whichMap").document(dateSimple).set(mapToday).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void v) {
                                Toast.makeText(getApplicationContext(), "map ready", Toast.LENGTH_SHORT).show();
                                mapGrabber();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "no map", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    Log.e("map check", "Failed with: ", task.getException());
                }
            }
        });
        Log.d("map boo", mapPathToday + "");
    }
}