package com.example.theworld.logic;

import android.graphics.Bitmap;

/**
 * Interface which outlines the firestore interactions to get the relevant images for the activity
 */
public interface ImageListener {
    //https://stackoverflow.com/questions/48501384/how-can-i-return-a-value-from-firebase-in-android
    void onImageReceived(Bitmap bitmap);
    void onError(Exception e);
}
