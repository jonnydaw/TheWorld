package com.example.theworld.ui.notifications;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.theworld.R;

public class InfoActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
       // String string = ;
        populate();
    }

    private void populate(){
        TextView tx = findViewById(R.id.paragraph);
        tx.setText(getString(R.string.text_for_info));
    }
}
