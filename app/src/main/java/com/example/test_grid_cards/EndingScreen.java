package com.example.test_grid_cards;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;

public class EndingScreen extends Fragment {
    View v;
    TextView player;
    TextView score;
    Gamestate_viewmodel gameViewModel;
    Button btn_play_again;
    int startScreen = 5;

    public EndingScreen() {
        // Required empty public constructor
        super(R.layout.number);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // ↓ set the view, the UI elements and set the name of the winner
        v = inflater.inflate(R.layout.activity_ending_screen, container, false);
        gameViewModel = new ViewModelProvider(requireActivity()).get(Gamestate_viewmodel.class);

        btn_play_again = v.findViewById(R.id.btn_play_again);
        player = v.findViewById(R.id.tv_playerWinner);
        score = v.findViewById(R.id.tv_score);

        if (gameViewModel.scorePlayer1 > gameViewModel.scorePlayer2){
            player.setText(gameViewModel.name_Player_1);
            score.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer1));
        }
        else{
            player.setText(gameViewModel.name_Player_2);
            score.setText(String.format(Locale.ENGLISH,"Score: %d", gameViewModel.scorePlayer2));
        }

        btn_play_again.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setRound(startScreen);
        });
        return v;
    }
}