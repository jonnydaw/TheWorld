package com.example.theworld;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;


import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.theworld.flagfinder.FlagFinderActivity;
import com.example.theworld.logic.Logic;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import database.DataBaseHelper;
import database.ProcessingDatabase;


@RunWith(MockitoJUnitRunner.class)
public class LogicTest {

    @Test
    public void calculateZeroDistanceTest() {
        Logic logic = new Logic(null);
        double distance = logic.calculateDistance("51.5085 -0.1257", "51.5085 -0.1257");
        assertEquals(distance,0, 0.01);
    }

    @Test
    public void calculateEstimateAntipodeDistance(){

        Logic logic = new Logic(null);
        // assuming earth is perfect sphere
        double halfCircumference = Math.PI * 6371;
        double distance = logic.calculateDistance("25.0470 121.5457", "-25.3007 -57.6359");
        assertEquals(halfCircumference,distance,100);


    }

    //  from google distance between london and caracas is 7,496.79 km
    @Test
    public void calculateDifferentDistanceTest() {
        Logic logic = new Logic(null);
        double distance = logic.calculateDistance("51.5085 0.1257", "10.4880 66.8792");
        assertEquals(distance,7496.79, 0.5);
    }


    @Test
    public void countryBearingNorthTest(){
        Logic logic = new Logic(null);
        String coords = logic.calculateBearing("6.1375 1.2123","51.5085 0.1257");
        assertEquals(coords,"⬆️️️");
    }

    @Test
    public void countryBearingSouthTest(){
        Logic logic = new Logic(null);
        String coords = logic.calculateBearing("51.5085 0.1257","6.1375 1.2123");
        assertEquals(coords,"⬇️");
    }

    @Test
    public void countryBearingEastTest(){
        Logic logic = new Logic(null);
        String coords = logic.calculateBearing("51.5085 0.1257","55.7550 37.6218");
        assertEquals(coords,"➡️");
    }

    @Test
    public void countryBearingWestTest(){
        Logic logic = new Logic(null);
        String coords = logic.calculateBearing("55.7550 37.6218","51.5085 0.1257");
        assertEquals(coords,"⬅️");
    }




//    @Test
//    public void testOutput(){
//        Context context = mock(Context.class);
//        ProcessingDatabase pd = new ProcessingDatabase(context);
//        DataBaseHelper dbh = mock(DataBaseHelper.class);
//        Logic logic = new Logic(pd);
//        String output = logic.distanceAndBearing("YEMEN","YEMEN");
//        assertEquals(output,"           \uD83C\uDFAF");
//
//    }

}
