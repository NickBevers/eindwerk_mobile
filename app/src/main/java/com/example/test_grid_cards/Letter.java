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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import be.bluebanana.zakisolver.LetterSolver;

import static android.content.ContentValues.TAG;

public class Letter extends Fragment {
    // Add all variables (no database structure)
    public GridLayout cardGridLayout;
    private static final int PERIOD = 1000;
    private static final int DELAY = 1000;
    public MutableLiveData<Integer> letter = new MutableLiveData<Integer>();
    public boolean result1;
    public boolean result2;
    private final int maxLetters = 9;
    TextView tv_results;
    Button btn_Check;
    final LetterSolver letSolver = new LetterSolver();
    ArrayList<String> solutions = new ArrayList<>();
    int checkActionToDo = 0;
    int firstRound = 0;
    int secondRound = 1;
    int thirdRound = 2;
    String resultString = "Possible solutions were: \n";
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
        letSolver.loadDictionary(requireActivity(), R.raw.dictionary);
        tv_results = v.findViewById(R.id.tv_results);
        tv_results.setText("");
        btn_Check = v.findViewById(R.id.check_button);
        btn_Check.setVisibility(View.INVISIBLE);

        TextView namePlayer1 = v.findViewById(R.id.tv_player1);
        TextView namePlayer2 = v.findViewById(R.id.tv_player2);
        namePlayer1.setText(gameViewModel.name_Player_1);
        namePlayer2.setText(gameViewModel.name_Player_2);

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

        letterViewModel.results.observe(getViewLifecycleOwner(), strings -> {
            strings.forEach(string->{
                resultString += tv_results.getText() + ", " + string;
            });
        });


        btn_Check.setOnClickListener(view -> {
            if (checkActionToDo == 0){
                String text1 = String.valueOf(editText1.getText());
                String text2 = String.valueOf(editText2.getText());

                // check the given word to see if it exists and contains only letters from the cards
                boolean resultPlayer1 = letterViewModel.checkText(text1, result1);
                boolean resultPlayer2 = letterViewModel.checkText(text2, result2);

                // set a toast for the winner depending on who won the round
                // continue only if both players have given a valid solution
                if (resultPlayer1 && resultPlayer2){
                    if (text1.length() == text2.length()){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.draw), Toast.LENGTH_SHORT).show());
                        gameViewModel.winPlayer1();
                        gameViewModel.winPlayer2();
                    }

                    else if (text1.length() > text2.length()){
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
                checkActionToDo++;
                btn_Check.setText("Show possible solutions");
            }

            else if(checkActionToDo == 1){
                tv_results.setText(resultString);
                checkActionToDo++;
                btn_Check.setText("Next Round");
            }

            else if(checkActionToDo == 2){
                gameViewModel.setGame(gameViewModel.gameType++);
                ronde = gameViewModel.getRound();
                //Log.d(TAG, "ROUND: " + ronde.getValue());
                if (ronde.getValue().equals(firstRound)){
                    Log.d("TAG", "ronde: 0");
                    //gameViewModel.round.postValue(secondRound);
                    ((MainActivity) requireActivity()).setRound(secondRound);
                }
                else if(ronde.getValue().equals(secondRound)){
                    Log.d("TAG", "ronde: 1");
                    ((MainActivity) requireActivity()).setRound(thirdRound);
                    //gameViewModel.setRound(thirdRound);
                }
                else {
                    Log.d("TAG", "ronde: 2");
                    ((MainActivity) requireActivity()).setRound(firstRound);
                    //gameViewModel.setRound(firstRound);
                }
            }
        });

        // ↓ check if the letterArray (cards in cardview) contains 9 letters
        // if not so, draw a new card, if so, start a timer
        letterViewModel.getLetters().observe(getViewLifecycleOwner(), letterArray -> {
            if (letterArray.size() > 0 && letterArray.size() <= maxLetters){
                View cardView = getLayoutInflater().inflate(R.layout.cardlayout, cardGridLayout, false);
                TextView tv = cardView.findViewById(R.id.number_card_text);
                tv.setText(String.valueOf(letterArray.get(letterArray.size()-1)));
                cardGridLayout.addView(cardView);
            }

            if (letterArray.size() == maxLetters){
                startTimer(requireView());
                solve(letterArray);
            }
        });

        // ↓ set progressbar to correct UI element and set the value of the pb
        ProgressBar pb = requireActivity().findViewById(R.id.progress_bar);
        //pb::setProgress == (number -> pb.setProgress(number);
        pb.setMax((gameViewModel.timerDuration/1000)-1);
        letter.observe(requireActivity() , pb::setProgress);
        return v;
    }


    public void startTimer(View w) {
        // ↓ start the timer and set it's value to the current value of letter + 1
        long startTime = System.currentTimeMillis();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime <= gameViewModel.timerDuration) {
                    letter.postValue(letter.getValue() + 1);
                } else {

                    btn_Check.getHandler().post(new Runnable() {
                        public void run() {
                            btn_Check.setVisibility(View.VISIBLE);
                        }
                    });
                    cancel();
                    // ↓ if time is up, compare the start the check of who is the winner

                }
            }
        }, DELAY, PERIOD);
    }


    @Override
    public void onStart() {
        // ↓ empty the 2 textfields at onstart so that they're emptied after a round switch
        super.onStart();
        editText1.setText("");
        editText2.setText("");
        tv_results.setText("");
        checkActionToDo = 0;
        resultString = "Possible solutions were: ";
    }

    @Override
    public void onDestroyView() {
        // ↓ empty the 2 textfields at ondestroyview so that they're empty
        super.onDestroyView();
        letterViewModel.clearLetter();
    }

    public void solve (ArrayList<Character> letters) {
        // set up the solver
        letSolver.setInput(letters, results -> {
            Log.d("ZAKI", String.format("Found %d matches.", results.size()));

            if (results.size() == 0) {
                Log.d(TAG, "solve: No solutions found.");
                return;
            }
            results.stream()
                    .limit(3)
                    .forEach(result -> {
                        solutions.add(result);
                        Log.d(TAG, "solve: " + solutions);
                    });
            letterViewModel.results.postValue(solutions);
        });

        // Start the solver
        new Thread(letSolver).start();
    }
}