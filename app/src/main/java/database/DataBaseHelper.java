package database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import com.example.theworld.MainActivity;

import androidx.annotation.Nullable;

import org.checkerframework.checker.index.qual.PolyUpperBound;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
    private Context context;
    public static final String COUNTRY_DATA_TABLE = "COUNTRY_DATA_TABLE";
    public static final String COLUMN_COUNTRY_NAME = "COLUMN_COUNTRY_NAME";
    public static final String COLUMN_CAPITAL_CITY = "COLUMN_CAPITAL_CITY";
    public static final String COLUMN_LATITUDE = "COLUMN_LATITUDE";
    public static final String COLUMN_LONGITUDE = "COLUMN_LONGITUDE";
    public static final String COLUMN_POPULATION = "COLUMN_POPULATION";
    public static final String COLUMN_IMAGE_LOCATION = "COLUMN_IMAGE_LOCATION";
    public static final String COLUMN_MAP_LOCATION = "COLUMN_MAP_LOCATION";
    private static DataBaseHelper sInstance;
    public DataBaseHelper(@Nullable Context context) {
        super(context, "countrymapflagDB.db", null, 1);
        this.context = context;
    }
    //Country 	Capital City 	Latitude 	Longitude 	Population 	Capital Type
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE "  + COUNTRY_DATA_TABLE + "(ID INTEGER PRIMARY KEY AUTOINCREMENT ," + COLUMN_COUNTRY_NAME + " TEXT," +
                COLUMN_CAPITAL_CITY + " TEXT," + COLUMN_LATITUDE + " REAL," + COLUMN_LONGITUDE + " REAL, " + COLUMN_POPULATION + " INT," + COLUMN_IMAGE_LOCATION + " TEXT,"+
                COLUMN_MAP_LOCATION + " TEXT)";
        db.execSQL(createTableStatement);

    }

    //https://stackoverflow.com/questions/43055661/reading-csv-file-in-android-app#43055945
    // https://www.youtube.com/watch?v=YxsW1u5FChk
    // https://stackoverflow.com/questions/29539797/sqlite-file-copied-from-assets-folder-by-android-sqliteopenhelper-corrupted
    // https://stackoverflow.com/questions/19131120/reading-csv-then-insert-data-to-sqlite
    // https://stackoverflow.com/questions/54732295/when-i-call-an-item-from-sqlite-database-by-its-id-sqlitedatabase-db-this-ge
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Loading CSV file containing all information needed for app to function and adding it to
     * sqlite database
     */
    public void addCSV(){
        AssetManager am = this.context.getAssets();
        SQLiteDatabase db = getWritableDatabase();
        try {
            InputStream inputStream = am.open("country-capital-lat-long-population.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                String countryName = nextLine[0];
                String capitalCity = nextLine[1];
                double latitude = Double.parseDouble(nextLine[2]);
                double longitude = Double.parseDouble(nextLine[3]);
                int population = Integer.parseInt(nextLine[4]);
                String imageLocation = nextLine[5];
                String mapLocation = nextLine[6];

                ContentValues cv = new ContentValues();
                cv.put(COLUMN_COUNTRY_NAME, countryName.toUpperCase());
                cv.put(COLUMN_CAPITAL_CITY, capitalCity);
                cv.put(COLUMN_LATITUDE, latitude);
                cv.put(COLUMN_LONGITUDE, longitude);
                cv.put(COLUMN_POPULATION, population);
                cv.put(COLUMN_IMAGE_LOCATION, imageLocation);
                cv.put(COLUMN_MAP_LOCATION, mapLocation);
              // db.execSQL("DROP TABLE " + COUNTRY_TABLE);
                db.insert(COUNTRY_DATA_TABLE, null, cv);
              // db.execSQL("DELETE FROM " + COUNTRY_TABLE);


            }
            reader.close();
        } catch (IOException | NumberFormatException e) {
            Log.e("DBHelper", "Error reading CSV file from assets: " + e.getMessage());
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * runs sql query to get the entire table
     * @return entire database
     */
    public Cursor getCountryTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + COUNTRY_DATA_TABLE, null);
    }

    /**
     * runs sql query to get the row from which the flag image path originated
     * @param imagePath
     * @return
     */
    public Cursor getFlagRow(String imagePath){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + COUNTRY_DATA_TABLE +
                " WHERE " + COLUMN_IMAGE_LOCATION + "='" + imagePath + "'", null);
    }

    /**
     * runs sql query to get the row from which the map image path originated
     * @param mapPath
     * @return
     */
    public Cursor getMapRow(String mapPath){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + COUNTRY_DATA_TABLE +
                " WHERE " + COLUMN_MAP_LOCATION + "='" + mapPath + "'", null);
    }

    /**
     * runs sql query to get the row from which the map image path originated
     * @param country
     * @return
     */
    public Cursor getCountryRow(String country){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + COUNTRY_DATA_TABLE +
                " WHERE " + COLUMN_COUNTRY_NAME + "='" + country + "'", null);
    }







}
