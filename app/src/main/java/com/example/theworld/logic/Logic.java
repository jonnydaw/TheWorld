package com.example.theworld.logic;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.theworld.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import database.ProcessingDatabase;

/**
 * Class that outlines the base lgoic of the Application
 */
public class Logic {

    private ProcessingDatabase pd;
    //private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageReference;

    /**
     * Logic constructor
     * @param pd
     */
    public Logic(ProcessingDatabase pd) {
        this.pd = pd;
    }

    /**
     * Gets all the countries from the {@link ProcessingDatabase} which provides all the countries
     * from the sqlite database
     * @return pd.ProcessCountries()
     */
    public ArrayList<String> getCountryNames() {
        return pd.processCountries();
    }

    /**
     * Gets the relevant image from the firestore storage. Said image is then decoded and set as
     * a bitmap which is then resized as on some devices (google pixel 3a). this unfortunately means
     * that on some devices the image is quite large.
     * @param folder
     * @param listener
     */
    public void getImage(String folder,final ImageListener listener) {
        File image;
        String path = folder;
        if(path.equals("flags/")) {
            if (MainActivity.path.length() > 0) {
                path += MainActivity.path;
                Log.e("yesssssssss", path);
            } else {
                path += pd.processImageLocation();
                Log.e("why", path);
            }
        } else if (path.equals("outlines/")) {
            if (MainActivity.mapPath.length() > 0) {
                path += MainActivity.mapPath;
                Log.e("yesssssssss", path);
            } else {
                path += pd.processMapImageLocation();
                Log.e("why", path);
            }
        }
        storageReference = FirebaseStorage.getInstance().getReference(path);

        {
            try {
                image = File.createTempFile("tempFile", ".jpg");
                storageReference.getFile(image).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        //Toast.makeText(FlagFinderActivity.this, "success", Toast.LENGTH_SHORT).show();

                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                        // https://stackoverflow.com/questions/45115924/how-to-resize-a-bitmap-image-in-android
                       Bitmap resized = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth()*1.1), (int)(bitmap.getHeight()*1.1), true);
                        //  imageView.setImageBitmap(bitmap);
                        listener.onImageReceived(resized);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailure(e);
                        // Toast.makeText(FlagFinderActivity.this, "fail", Toast.LENGTH_SHORT).show();

                    }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Gets the sqlite row of the correct country of the day and returns the country name
     * @param path
     * @param forMap
     * @return rowData.get(1)
     */
    public String correctCountry(String path, boolean forMap) {
        ArrayList<String> rowData = forMap ? pd.mapRow(path) : pd.flagRow(path);
        return rowData.get(1);
    }

    /**
     * Equation that calculates the distance between two coordinates. Takes the coordinates of the
     * country that the user guessed and the coordinates of the correct country for the day.
     * Coordinates are of the countries capital.
     * @param guessCoordinates
     * @param correctCoordinates
     * @return
     */
    // https://www.geeksforgeeks.org/program-distance-two-points-earth/
    public double calculateDistance(String guessCoordinates, String correctCoordinates) {
        String[] coords1 = guessCoordinates.split(" ");
        String[] coords2 = correctCoordinates.split(" ");
        double lat1 = Double.parseDouble(coords1[0].trim());
        double lon1 = Double.parseDouble(coords1[1].trim());
        double lat2 = Double.parseDouble(coords2[0].trim());
        double lon2 = Double.parseDouble(coords2[1].trim());;
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    /**
     * Gets the coordinates of the countries capital from the sqlite database
     * @param guess
     * @return
     */
    public String getCountryCoordinates(String guess) {
        return pd.countryRowCoordinates(guess);
    }

    // https://www.movable-type.co.uk/scripts/latlong.html
    /**
     * Calculates the bearing which the user would take to arrive at the correct country from their
     * guess
     * @param guessCoordinates
     * @param correctCoordinates
     * @return
     */
    public String calculateBearing(String guessCoordinates, String correctCoordinates) {
        String[] coords1 = guessCoordinates.split(" ");
        String[] coords2 = correctCoordinates.split(" ");
        double lat1 = Double.parseDouble(coords1[0].trim());
        double lon1 = Double.parseDouble(coords1[1].trim());
        double lat2 = Double.parseDouble(coords2[0].trim());
        double lon2 = Double.parseDouble(coords2[1].trim());;
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double y = Math.sin(lon2 - lon1) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
        double theta = Math.atan2(y,x);
        double bearing = ((theta * 180)/Math.PI + 360) % 360;
        int bearingDown = (int) bearing;
        if(bearing <= 30){
            return "⬆️";
        } else if (bearing <= 60) {
            return "↗️";
        } else if (bearing <= 120) {
            return "➡️";
        } else if (bearing <= 150) {
            return "↘️";
        } else if (bearing <= 210) {
            return "⬇️";
        } else if (bearing <= 240) {
            return "↙️";
        }else if (bearing <= 300) {
            return "⬅️";
        }else if (bearing <= 330) {
            return "↖️";
        }else if (bearing <= 360) {
            return "⬆️️️";
        }

        return "";

    }

    /**
     * From the {@link #calculateBearing(String, String)} and {@link #calculateDistance(String, String)}
     * a string is returned to be displayed for the user which allows them to see how far they were
     * and what direction they need to go if they guessed the incorrect answer. If correct guess
     * then a target  emoji is returned
     * @param userGuess
     * @param correctCountry
     * @return String
     */
    public String distanceAndBearing(String userGuess, String correctCountry) {
        double distance = calculateDistance(getCountryCoordinates(userGuess), getCountryCoordinates(correctCountry));
        String bearing = calculateBearing(getCountryCoordinates(userGuess), getCountryCoordinates(correctCountry));
        int distanceDown = (int) distance;
        if (distanceDown == 0) {
            return "           \uD83C\uDFAF";
        }
        return "  " + bearing + " " + distanceDown + "km";
    }

    /**
     * returns the current day number e.g. 17 as a string. Used for sharepreferences
     * @return dateSimple:String
     */
    public String dateToday() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        String dateSimple = dateFormat.format(date);
        return dateSimple;
    }
}

