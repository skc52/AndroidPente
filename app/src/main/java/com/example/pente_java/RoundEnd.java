package com.example.pente_java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RoundEnd extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_end);

        TextView humanPairsCaptured = findViewById(R.id.humanPairsCaptured);
        TextView humanFourConsecutives = findViewById(R.id.humanFourConsecutives);
        TextView gamePointsHuman = findViewById(R.id.gamePointsHuman);
        TextView computerPairsCaptured = findViewById(R.id.computerPairsCaptured);
        TextView computerFourConsecutives = findViewById(R.id.computerFourConsecutives);
        TextView gamePointsComputer = findViewById(R.id.gamePointsComputer);
        Button continueTournamentButton = findViewById(R.id.continueTournamentButton);
        Button quitGameButton = findViewById(R.id.quitGameButton);
        TextView textView2 = findViewById(R.id.textView2);
        TextView textView3 = findViewById(R.id.textView3);
        View divider = findViewById(R.id.divider);
        View divider2 = findViewById(R.id.divider2);
        TextView totalHuman = findViewById(R.id.totalHuman);
        TextView totalComputer = findViewById(R.id.totalComputer);
        TextView winnerDeclaration = findViewById(R.id.winnerDeclaration);


//        Intent intent = getIntent();
//        Round r = (Round) intent.getSerializableExtra("round");
//        Tournament t = (Tournament) intent.getSerializableExtra("tournament");
//        if (r != null) {
//            humanPairsCaptured.setText(humanPairsCaptured.getText() + Integer.toString(r.getPairsCapturedNum(t.getHuman())));
//            computerPairsCaptured.setText(computerPairsCaptured.getText() + Integer.toString(r.getPairsCapturedNum(t.getComputer())));
//            humanFourConsecutives.setText(humanFourConsecutives.getText() + Integer.toString(r.getFourConsecutivesNum(t.getHuman())));
//            computerFourConsecutives.setText(computerFourConsecutives.getText() + Integer.toString(r.getFourConsecutivesNum(t.getComputer())));
//
//            gamePointsHuman.setText(gamePointsHuman.getText() + Integer.toString(r.getGamePoints(t.getHuman())));
//            gamePointsComputer.setText(computerFourConsecutives.getText() + Integer.toString(r.getGamePoints(t.getComputer())));
//            totalHuman.setText(totalHuman.getText() + Integer.toString(r.getRoundEndScore(t.getHuman())));
//            totalComputer.setText(totalComputer.getText() + Integer.toString(r.getRoundEndScore(t.getComputer())));
//
//        }


    }
}