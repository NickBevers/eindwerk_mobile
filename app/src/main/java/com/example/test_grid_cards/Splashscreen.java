package com.example.test_grid_cards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splashscreen extends AppCompatActivity {
    int DELAY = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // set a timer to switch the splashscreen to the mainactivity after a period (1 second)
                Intent i = new Intent(Splashscreen.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, DELAY);
    }
}