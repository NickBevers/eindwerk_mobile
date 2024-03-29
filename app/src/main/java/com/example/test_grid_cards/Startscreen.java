package com.example.test_grid_cards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.jetbrains.annotations.NotNull;

public class Startscreen extends Fragment {
    // ↓ variables for layout elements
    NumberPicker np;
    Gamestate_viewmodel gamestate_viewmodel;
    Button btn_ready;
    EditText et_player1;
    EditText et_player2;

    // ↓ variables for locally used functions
    int gamesToPlay;

    public Startscreen() {
        // Required empty public constructor
        super(R.layout.activity_startscreen);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.activity_startscreen, container, false);
        gamestate_viewmodel = new ViewModelProvider(requireActivity()).get(Gamestate_viewmodel.class);
        btn_ready = v.findViewById(R.id.btn_ready);
        et_player1 = v.findViewById(R.id.et_name_player1);
        et_player2 = v.findViewById(R.id.et_name_player2);

        np = v.findViewById(R.id.np_round);
        np.setEnabled(true);
        np.setMaxValue(10);
        np.setMinValue(1);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        np.setOnValueChangedListener((picker, oldVal, newVal) -> {
            gamesToPlay =  picker.getValue();
            gamestate_viewmodel.numberOfGames.postValue(gamesToPlay);
        });

        btn_ready.setOnClickListener(v -> {
            gamestate_viewmodel.name_Player_1 = et_player1.getText().toString();
            gamestate_viewmodel.name_Player_2 = et_player2.getText().toString();

            if(et_player1.getText().toString().equals("")){
                gamestate_viewmodel.name_Player_1 = "John Doe";
            }
            if(et_player2.getText().toString().equals("")){
                gamestate_viewmodel.name_Player_2 = "Jane Doe";
            }

            gamestate_viewmodel.gameStarted.setValue(true);
        });

    }
}