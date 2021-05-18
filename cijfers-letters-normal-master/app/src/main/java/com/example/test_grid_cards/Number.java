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

import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Number extends Fragment {
    // Add all variables (no database structure)
    public GridLayout cardGridLayout;
    View v;
    Gamestate_viewmodel gameViewModel;
    Number_viewmodel numberViewModel;
    Button btn_Check;
    int num_player1;
    int num_player2;
    int targetNum;
    EditText editText1;
    EditText editText2;
    Timer t = new Timer();
    private static final int PERIOD = 1000;
    public MutableLiveData<Integer> number = new MutableLiveData<Integer>();
    MutableLiveData<Integer> ronde;
    Random random = new Random();
    public int randomNum;
    int firstRound = 0;
    int secondRound = 1;
    int thirdRound = 2;
    int randomNumLimit = 900; //Highest possible number to be generated for Number rounds
    int DELAY = 1000;

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
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // ↓ set cardlayout variable to gridlayout and set the other UI components, as well as the viewmodels
        cardGridLayout = v.findViewById(R.id.gridlayout);
        gameViewModel = new ViewModelProvider(requireActivity()).get(Gamestate_viewmodel.class);
        numberViewModel = new ViewModelProvider(requireActivity()).get(Number_viewmodel.class);
        editText1 = v.findViewById(R.id.et_player1);
        editText2 = v.findViewById(R.id.et_player2);

        btn_Check = v.findViewById(R.id.check_button);
        btn_Check.setVisibility(View.INVISIBLE);

        // set textviews to correct layout element and set the player scores
        TextView tv_player1 = v.findViewById(R.id.score_player1);
        TextView tv_player2 = v.findViewById(R.id.score_player2);
        tv_player1.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        tv_player2.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));


        // ↓ set button onclicklisteners
        v.findViewById(R.id.btn_low_number).setOnClickListener(view -> {
            numberViewModel.pickLowNumber();
            //Log.d("TAG", "LOW");
        });

        v.findViewById(R.id.btn_high_number).setOnClickListener(view -> {
            //Log.d("TAG", "HIGH");
            numberViewModel.pickHighNumber();
        });

        btn_Check.setOnClickListener(view -> {
            // ↓ if one of the players has no number filled in, the other player automatically wins the round
            if (editText1.getText().length() == 0){
                gameViewModel.scorePlayer2++;
            }

            if (editText2.getText().length() == 0){
                gameViewModel.scorePlayer1++;
            }

            else{
                // ↓ if both players have a number filled in, check which player has the closest answer to the random number, and award that player a point
                num_player1 = Integer.parseInt(String.valueOf(editText1.getText()));
                num_player2 = Integer.parseInt(String.valueOf(editText2.getText()));
                int result = gameViewModel.calculateDifference(num_player1, num_player2, targetNum);
                if (result == 0){
                    // show win of player 1
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player1_win), Toast.LENGTH_LONG).show());
                }
                else if (result == 1){
                    // show win of player 2
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.player2_win), Toast.LENGTH_LONG).show());
                }
                else {
                    // show that there's a draw
                    new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(requireContext(), getResources().getString(R.string.draw), Toast.LENGTH_LONG).show());
                }
            }

            // ↓ if the last game is on it's way, set the ending screen after the letter-round
            ronde = gameViewModel.getRound();
            if (ronde.getValue().equals(firstRound)){
                //Log.d("TAG", "ronde: 0");
                ((MainActivity) requireActivity()).setRound(secondRound);
            }
            else if(ronde.getValue().equals(secondRound)){
                //Log.d("TAG", "ronde: 1");
                ((MainActivity) requireActivity()).setRound(thirdRound);
            }
            else {
                //Log.d("TAG", "ronde: 2");
                ((MainActivity) requireActivity()).setRound(firstRound);
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
                randomNum = random.nextInt(randomNumLimit)+100;
                targetNum = randomNum;
                tv.setText(String.format(Locale.ENGLISH, "Number to reach: %d", targetNum));
                startTimer(requireView());
            }
        });

        // update the progressbar according to the number(timer)
        ProgressBar pb = requireActivity().findViewById(R.id.progress_bar);
        //pb::setProgress == (number -> pb.setProgress(number)
        number.observe(requireActivity() , pb::setProgress);
    }

    public void startTimer(View w) {
        // ↓start a timer, and update the timer each period (1 second)
        long startTime = System.currentTimeMillis();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime <= gameViewModel.timerDuration) {
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
        editText1.setText("");
        editText2.setText("");
    }

    @Override
    public void onDestroyView() {
        // ↓ empty the 2 textfields at ondestroyview so that they're empty
        Number_viewmodel numberViewModel = new ViewModelProvider(requireActivity()).get(Number_viewmodel.class);
        super.onDestroyView();
        numberViewModel.clearNumber();
    }

}