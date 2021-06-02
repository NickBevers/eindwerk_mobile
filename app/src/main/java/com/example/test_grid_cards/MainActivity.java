/*
Needed:

Main activity -> The screen

Fragment Player 1 -> bottom half of the screen
Fragment Player 2 -> top half of the screen

Viewmodel Letters -> All content for the letters
Viewmodel Numbers -> All content for the numbers
Viewmodel Gamestate -> All info about the game (playing/stopped, round,
 */


package com.example.test_grid_cards;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    // set the variables for the mainactivity
    Gamestate_viewmodel viewModel;
    private final Letter letter_frag = new Letter();
    private final Number number_frag = new Number();
    private final EndingScreen ending_frag = new EndingScreen();
    private final Startscreen start_frag = new Startscreen();
    private final NewRound round_frag = new NewRound();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set the correct layout for mainactivity and set the viewmodel to the gamestate_viewmodel class
        setContentView(R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(Gamestate_viewmodel.class);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frag_player, start_frag)
                .commit();

        viewModel.gameStarted.observe(this, started -> {
            if (started.equals(true)) {
                Log.d(TAG, "GAMESTARTED: " + viewModel.gameStarted.getValue() + "  #GAMES: " + viewModel.numberOfGames.getValue());
                // observe the getround functino to see which round we are playing
                viewModel.getRound().observe(this, round -> {

                    if (round.equals(0)) {
                        Log.d(TAG, "onCreate: SWITCH TO ROUND 1");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, number_frag)
                                .commit();
                    } else if (round.equals(1)) {
                        Log.d(TAG, "onCreate: SWITCH TO ROUND 2");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, letter_frag)
                                .commit();
                    } else if (round.equals(2)) {
                        Log.d(TAG, "onCreate: SWITCH TO ROUND 3");
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.frag_player, number_frag)
                                .commit();
                    } else if (round.equals(3)){
                        Log.d(TAG, "onCreate: SWITCH TO OVERZICHT");
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
        //Log.d("TAG", "setRound: RONDE " + num);
        viewModel.setRound(num);
    }
}