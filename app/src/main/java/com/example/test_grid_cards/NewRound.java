package com.example.test_grid_cards;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class NewRound extends Fragment {
    Button btn_next;
    TextView tv_name_player1;
    TextView tv_name_player2;
    TextView tv_score_player1;
    TextView tv_score_player2;
    int firstround = 0;
    Gamestate_viewmodel gameViewModel;


    public NewRound() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_round, container, false);
        btn_next = v.findViewById(R.id.next_round_button);
        tv_name_player1 = v.findViewById(R.id.tv_name_player1);
        tv_name_player2 = v.findViewById(R.id.tv_name_player2);
        tv_score_player1 = v.findViewById(R.id.tv_points_player1);
        tv_score_player2 = v.findViewById(R.id.tv_points_player2);
        gameViewModel = new ViewModelProvider(requireActivity()).get(Gamestate_viewmodel.class);

        tv_name_player1.setText(gameViewModel.name_Player_1);
        tv_name_player2.setText(gameViewModel.name_Player_2);
        tv_score_player1.setText(String.valueOf(gameViewModel.scorePlayer1));
        tv_score_player2.setText(String.valueOf(gameViewModel.scorePlayer2));


        btn_next.setOnClickListener(view ->{
            ((MainActivity) requireActivity()).setRound(firstround);
        });


        return v;
    }
}