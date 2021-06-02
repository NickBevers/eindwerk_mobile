package com.example.test_grid_cards;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import be.bluebanana.zakisolver.NumberSolver;

import static android.content.ContentValues.TAG;

public class Number extends Fragment {
    // ↓ Viewmodel variables
    View v;
    Gamestate_viewmodel gameViewModel;
    Number_viewmodel numberViewModel;

    // ↓ Variables for the layout elements
    Button btn_Check;
    Button btn_High;
    Button btn_Low;
    TextView tv_results;
    TextView namePlayer1;
    TextView namePlayer2;
    TextView tv_player1;
    TextView tv_player2;
    EditText editText1;
    EditText editText2;
    ProgressBar pb;

    // ↓ Variables for local
    public GridLayout cardGridLayout;
    public MutableLiveData<Integer> number = new MutableLiveData<>();
    int num_player1;
    int num_player2;
    int targetNum;
    int checkActionToDo;
    int randomNum;
    int firstRound = 0;
    int secondRound = 1;
    int thirdRound = 2;
    int overview = 3;
    int endingScreen = 4;
    int randomNumLimit = 900; //Highest possible number to be generated for Number rounds
    int DELAY = 1000;
    int PERIOD = 1000;
    String resultString;
    Timer t = new Timer();
    MutableLiveData<Integer> ronde;
    Random random = new Random();
    NumberSolver numSolver = new NumberSolver();
    ArrayList<String> solutions = new ArrayList<>();

    public Number() {
        // Required empty public constructor
        super(R.layout.number);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //set view with correct inflater, set number to 0 when view is created
        v = inflater.inflate(R.layout.number, container, false);
        number.setValue(0);

        // ↓ set cardlayout variable to gridlayout and set the other UI components, as well as the viewmodels
        cardGridLayout = v.findViewById(R.id.gridlayout);
        gameViewModel = new ViewModelProvider(requireActivity()).get(Gamestate_viewmodel.class);
        numberViewModel = new ViewModelProvider(requireActivity()).get(Number_viewmodel.class);
        editText1 = v.findViewById(R.id.et_player1);
        editText2 = v.findViewById(R.id.et_player2);
        pb = requireActivity().findViewById(R.id.progress_bar);
        tv_results = v.findViewById(R.id.tv_results);


        namePlayer1 = v.findViewById(R.id.tv_player1);
        namePlayer2 = v.findViewById(R.id.tv_player2);
        namePlayer1.setText(gameViewModel.name_Player_1);
        namePlayer2.setText(gameViewModel.name_Player_2);

        btn_Check = v.findViewById(R.id.check_button);
        btn_High = v.findViewById(R.id.btn_high_number);
        btn_Low = v.findViewById(R.id.btn_low_number);

        // set textviews to correct layout element and set the player scores
        tv_player1 = v.findViewById(R.id.score_player1);
        tv_player2 = v.findViewById(R.id.score_player2);
        tv_player1.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        tv_player2.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));


        // ↓ set button onclicklisteners
        btn_Low.setOnClickListener(view -> numberViewModel.pickLowNumber());
        btn_High.setOnClickListener(view -> numberViewModel.pickHighNumber());
        numberViewModel.results.observe(getViewLifecycleOwner(), strings -> strings.forEach(string-> resultString += tv_results.getText() + "\n" + string));

        btn_Check.setOnClickListener(view -> {
            if(checkActionToDo == 0){
                // ↓ if one of the players has no number filled in, the other player automatically wins the round
                if (editText1.getText().length() == 0 && editText2.getText().length() > 0){
                    gameViewModel.scorePlayer2++;
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_LONG).show());
                }

                else if (editText1.getText().length() > 0 && editText2.getText().length() == 0){
                    gameViewModel.scorePlayer1++;
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_LONG).show());
                }

                else if (editText1.getText().length() == 0 && editText2.getText().length() == 0){
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.no_winner), Toast.LENGTH_LONG).show());
                }

                else{
                    num_player1 = Integer.parseInt(String.valueOf(editText1.getText()));
                    num_player2 = Integer.parseInt(String.valueOf(editText2.getText()));
                    int result = gameViewModel.calculateDifference(num_player1, num_player2, targetNum);

                    // ↓ if both players have a number filled in, check which player has the closest answer to the random number, and award that player a point
                    if (result == 0){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_LONG).show());
                    }
                    else if (result == 1){
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_LONG).show());
                    }
                    else {
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.draw), Toast.LENGTH_LONG).show());
                    }
                }
                checkActionToDo++;
                btn_Check.setText(R.string.possible_solutions);
            }

            else if(checkActionToDo == 1){
                tv_results.setText(resultString);
                checkActionToDo++;
                btn_Check.setText(R.string.next_round);
            }
            else if(checkActionToDo == 2){
                // ↓ if the last game is on it's way, set the ending screen after the letter-round
                ronde = gameViewModel.getRound();
                //Log.d(TAG, "ROUND: " + ronde.getValue());
                if (Objects.equals(ronde.getValue(), firstRound)){
                    ((MainActivity) requireActivity()).setRound(secondRound);
                }
                else if(ronde.getValue().equals(secondRound)){
                    ((MainActivity) requireActivity()).setRound(thirdRound);
                }
                else if(ronde.getValue().equals(thirdRound)) {
                    assert gameViewModel.numberOfGames.getValue() != null;
                    if(gameViewModel.amountOfRounds < gameViewModel.numberOfGames.getValue()){
                        ((MainActivity) requireActivity()).setRound(overview);
                        gameViewModel.amountOfRounds++;
                    }
                    else if(gameViewModel.amountOfRounds.equals(gameViewModel.numberOfGames.getValue())){
                        ((MainActivity) requireActivity()).setRound(endingScreen);
                    }
                }
            }
        });


        // ↓ observe the amount of cards in the cardview via the getNumbers function
        // if the number is not 6, draw an extra card
        // if the number is equal to 6, draw a random number and start the timer
        numberViewModel.getNumbers().observe(getViewLifecycleOwner(), numberArray -> {
            if (numberArray.size() > 0 && numberArray.size() <= 6){
                View cardView = getLayoutInflater().inflate(R.layout.cardlayout, cardGridLayout, false);
                TextView tv = cardView.findViewById(R.id.number_card_text);
                tv.setText(String.valueOf(numberArray.get(numberArray.size()-1)));
                cardGridLayout.addView(cardView);
            }

            if (numberArray.size() == 6){
                TextView tv = v.findViewById(R.id.tv_random);
                //generate random number
                randomNum = random.nextInt(randomNumLimit)+100;
                targetNum = randomNum;
                tv.setText(String.format(Locale.ENGLISH, "Number to reach: %d", targetNum));
                startTimer(requireView());
                solve(numberArray, randomNum);
                btn_High.setEnabled(false);
                btn_High.setVisibility(View.INVISIBLE);
                btn_Low.setEnabled(false);
                btn_Low.setVisibility(View.INVISIBLE);
            }
        });

        // update the progressbar according to the number(timer)
        pb.setMax((gameViewModel.timerDuration / 1000)-1);
        number.observe(requireActivity() , pb::setProgress);
        return v;
    }

    public void startTimer(View w) {
        // ↓start a timer, and update the timer each period (1 second)
        long startTime = System.currentTimeMillis();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime <= gameViewModel.timerDuration) {
                    assert number.getValue() != null;
                    number.postValue(number.getValue() + 1);
                }
                else {
                    btn_Check.getHandler().post(new Runnable() {
                        public void run() {
                            btn_Check.setVisibility(View.VISIBLE);
                        }
                    });
                    cancel();
                }
            }
        }, DELAY, PERIOD);
    }

    @Override
    public void onStart() {
        // ↓ empty the 2 textfields at onstart so that they're emptied after a round switch
        super.onStart();
        btn_Check.setText("Check");
        btn_Check.setVisibility(View.INVISIBLE);
        btn_High.setEnabled(true);
        btn_High.setVisibility(View.VISIBLE);
        btn_Low.setEnabled(true);
        btn_Low.setVisibility(View.VISIBLE);
        editText1.setText("");
        editText2.setText("");
        tv_results.setText("");
        checkActionToDo = 0;
        resultString = "Possible solutions were: ";
    }

    @Override
    public void onDestroyView() {
        // ↓ empty the 2 textfields at ondestroyview so that they're empty
        Number_viewmodel numberViewModel = new ViewModelProvider(requireActivity()).get(Number_viewmodel.class);
        super.onDestroyView();
        numberViewModel.clearNumber();
        solutions.clear();
    }

    public void solve (ArrayList<Integer> numbers, int target) {
        // set up the solver
        numSolver.setInput(numbers, target, results -> {
            Log.d("ZAKI", String.format("Found %d matches.", results.size()));

            if (results.size() == 0) {
                Log.d(TAG, "solver: No solutions found.");
                return;
            }
            results.stream()
                    .limit(3)
                    .forEach(result -> {
                        solutions.add(result);
                        Log.d(TAG, "solve: " + solutions);
                    });
            numberViewModel.results.postValue(solutions);
            //Log.d(TAG, "solverResult: " + numberViewModel.results);
        });

        // Start the solver
        new Thread(numSolver).start();
    }

}