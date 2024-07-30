package database;



import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.example.theworld.MainActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Class to process sqlite interactions
 */
public class ProcessingDatabase {

    Context context;


    DataBaseHelper dbh;

    // https://stackoverflow.com/questions/2810615/how-to-retrieve-data-from-cursor-class
    public ProcessingDatabase(Context context){
        this.context = context;
        dbh = new DataBaseHelper(this.context);
    }


    /**
     *
     * @return list of countries from sqlite database
     */
    public ArrayList<String> processCountries(){
        ArrayList<String> countriesList = new ArrayList<>();
        Cursor cursor = dbh.getCountryTable();
        while (cursor.moveToNext()) {
            String country = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_COUNTRY_NAME)).toUpperCase();
            countriesList.add(country);
        }
        cursor.close();

        return countriesList;
    }

    /**
     * @return image location from sqlite database which matches location in firestore
     */
    public String processImageLocation(){
        ArrayList<String> imageLocations = new ArrayList<>();
        Cursor cursor = dbh.getCountryTable();
        while (cursor.moveToNext()) {
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_IMAGE_LOCATION));
            imageLocations.add(image);
        }
        int random = (int) (Math.random() * (imageLocations.size() -1) +1);
        cursor.close();
        String location = imageLocations.get(random);
        return location;
    }

    /**
     *
     * @param imagePath
     * @return relevant sqlite row as an arraylist
     */
    public ArrayList<String> flagRow(String imagePath){
        ArrayList<String> rowItems = new ArrayList<>();
        Cursor cursor = dbh.getFlagRow(imagePath);
        if (cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getColumnCount(); i++){
                rowItems.add((cursor.getString(i)) + "");
            }
        }

        cursor.close();

        return rowItems;
    }

    /**
     * @return image location from sqlite database which matches location in firestore
     */
    public String processMapImageLocation(){
        ArrayList<String> imageLocations = new ArrayList<>();
        Cursor cursor = dbh.getCountryTable();
        while (cursor.moveToNext()) {
            String image = cursor.getString(cursor.getColumnIndexOrThrow(DataBaseHelper.COLUMN_MAP_LOCATION));
            if(!image.equals("no")) {
                imageLocations.add(image);
            }
        }
        int random = (int) (Math.random() * (imageLocations.size() -1) +1);
        cursor.close();
        String hi = imageLocations.get(random);
        return hi;
    }

    /**
     *
     * @param imagePath
     * @return relevant sqlite row as an arraylist
     */
    public ArrayList<String> mapRow(String imagePath){
        ArrayList<String> rowItems = new ArrayList<>();
        Cursor cursor = dbh.getMapRow(imagePath);
        if (cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getColumnCount(); i++){
                rowItems.add((cursor.getString(i)) + "");
            }
        }

        cursor.close();

        return rowItems;
    }

    /**
     *
     * @param country
     * @return returns coordinates of a countries capital
     */
    public String countryRowCoordinates(String country){
        ArrayList<String> rowItems = new ArrayList<>();
        Cursor cursor = dbh.getCountryRow(country);
        if (cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getColumnCount(); i++){
                rowItems.add((cursor.getString(i)).toUpperCase() + "");
            }
        }
        cursor.close();
        String coordinates = rowItems.get(3) + " " + rowItems.get(4);
        return coordinates;
    }





}
