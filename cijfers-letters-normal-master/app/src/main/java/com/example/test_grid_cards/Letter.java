package com.example.test_grid_cards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Letter extends Fragment {
    // Add all variables (no database structure)
    public GridLayout cardGridLayout;
    private static final int PERIOD = 1000;
    public MutableLiveData<Integer> letter = new MutableLiveData<Integer>();
    public boolean result1;
    public boolean result2;
    private

    View v;
    Gamestate_viewmodel gameViewModel;
    Timer t = new Timer();
    MutableLiveData<Integer> ronde;
    EditText editText1;
    EditText editText2;
    Letter_viewmodel letterViewModel;

    public Letter() {
        // Required empty public constructor
        super(R.layout.letter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // set view with correct inflater, set number to 0 when view is created
        // set correct UI elements to the edittext fields
        v = inflater.inflate(R.layout.letter, container, false);
        gameViewModel = new ViewModelProvider(requireActivity()).get(Gamestate_viewmodel.class);
        letterViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(Letter_viewmodel.class);
        //letterViewModel = new ViewModelProvider(requireActivity()).get(Letter_viewmodel.class);
        letter.setValue(0);
        editText1 = v.findViewById(R.id.et_player1);
        editText2 = v.findViewById(R.id.et_player2);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ↓ set the layout for the 2 textfields that contain the score of both players, and set their scores in the textfields
        cardGridLayout = v.findViewById(R.id.gridlayout);
        TextView tv_player1 = v.findViewById(R.id.score_player1);
        TextView tv_player2 = v.findViewById(R.id.score_player2);
        tv_player1.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        tv_player2.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));

        // ↓ set onclicklisteners for the buttons
        v.findViewById(R.id.btn_vowel).setOnClickListener(view -> {
            letterViewModel.pickVowel();
        });

        v.findViewById(R.id.btn_consonant).setOnClickListener(view -> {
            letterViewModel.pickConsonant();
        });

        // ↓ check if the letterArray (cards in cardview) contains 6 letters
        // if not so, draw a new card, if so, start a timer
        letterViewModel.getLetters().observe(getViewLifecycleOwner(), letterArray -> {
            if (letterArray.size() > 0 && letterArray.size() <= 6){
                View cardView = getLayoutInflater().inflate(R.layout.cardlayout, cardGridLayout, false);
                TextView tv = cardView.findViewById(R.id.number_card_text);
                tv.setText(String.valueOf(letterArray.get(letterArray.size()-1)));
                cardGridLayout.addView(cardView);
            }

            if (letterArray.size() == 6){
                startTimer(requireView());
            }
        });

        // ↓ set progressbar to correct UI element and set the value of the pb
        ProgressBar pb = requireActivity().findViewById(R.id.progress_bar);
        //pb::setProgress == (number -> pb.setProgress(number)
        letter.observe(requireActivity() , pb::setProgress);
    }

    public void startTimer(View w) {
        // ↓ start the timer and set it's value to the current value of letter + 1
        long startTime = System.currentTimeMillis();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                int game = gameViewModel.gameType;
                if (System.currentTimeMillis() - startTime <= gameViewModel.timerDuration) {
                    letter.postValue(letter.getValue() + 1);
                } else {
                    // ↓ if time is up, compare the start the check of who is the winner

                    //editText1.setFocusable(false);
                    //editText2.setFocusable(false);
                    String text1 = String.valueOf(editText1.getText());
                    String text2 = String.valueOf(editText2.getText());

                    // check the given word to see if it exists and contains only letters from the cards
                    boolean resultPlayer1 = letterViewModel.checkText(text1, result1);
                    boolean resultPlayer2 = letterViewModel.checkText(text2, result2);

                    // set a toast for the winner depending on who won the round
                    if (game != gameViewModel.numberOfGames){
                        // continue only if both players have given a valid solution
                        if (resultPlayer1 && resultPlayer2){
                            if (text1.length() == text2.length()){
                                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.draw), Toast.LENGTH_SHORT).show());
                                gameViewModel.winPlayer1();
                                gameViewModel.winPlayer2();
                            }

                            if (text1.length() > text2.length()){
                                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_SHORT).show());
                                gameViewModel.winPlayer1();
                            }
                            else{
                                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_SHORT).show());
                                gameViewModel.winPlayer2();
                            }
                        }
                        //if only 1 player has given a valid solution, the other player wins
                        else if (resultPlayer1 && !resultPlayer2){
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_SHORT).show());
                            gameViewModel.winPlayer1();
                        }

                        else if (!resultPlayer1 && resultPlayer2){
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_SHORT).show());
                            gameViewModel.winPlayer2();
                        }

                        // if neither player has given a valid solution, there will be no points
                        else{
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.no_winner), Toast.LENGTH_SHORT).show());
                        }
                    }


                    game += 1;
                    gameViewModel.setGame(game);
                    Log.d("TAG", "GAME: " + game);
                    if (game < gameViewModel.numberOfGames){
                        ((MainActivity) requireActivity()).setRound(0);
                    }
                    else {
                        ((MainActivity) requireActivity()).setRound(3);
                    }

                    cancel();
                }

            }
        }, 1000, PERIOD);
    }

    @Override
    public void onStart() {
        // ↓ empty the 2 textfields at onstart so that they're emptied after a round switch
        super.onStart();
        editText1.setText("");
        editText2.setText("");
    }

    @Override
    public void onDestroyView() {
        // ↓ empty the 2 textfields at ondestroyview so that they're empty
        super.onDestroyView();
        letterViewModel.clearLetter();
    }

}