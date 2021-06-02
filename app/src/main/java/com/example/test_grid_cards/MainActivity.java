package com.example.test_grid_cards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    // set the variables for the mainactivity
    private final Letter letter_frag = new Letter();
    private final Number number_frag = new Number();
    private final EndingScreen ending_frag = new EndingScreen();
    private final Startscreen start_frag = new Startscreen();
    private final NewRound round_frag = new NewRound();
    Gamestate_viewmodel gameViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the correct layout for mainactivity and set the viewmodel to the gamestate_viewmodel class
        setContentView(R.layout.activity_main);
        gameViewModel = new ViewModelProvider(this).get(Gamestate_viewmodel.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_player, start_frag)
                .commit();

        gameViewModel.gameStarted.observe(this, started -> {
            if (started.equals(true)) {
                // observe the getRound function to see which round we are playing
                gameViewModel.getRound().observe(this, round -> {

                    if (round.equals(0)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, number_frag)
                                .commit();
                    } else if (round.equals(1)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, letter_frag)
                                .commit();
                    } else if (round.equals(2)) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, number_frag)
                                .commit();
                    } else if (round.equals(3)){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, round_frag)
                                .commit();
                    } else if(round.equals(4)){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, ending_frag)
                                .commit();
                    }
                });
            }
        });
    }

    public void setRound(int num){
        gameViewModel.setRound(num);
    }
}