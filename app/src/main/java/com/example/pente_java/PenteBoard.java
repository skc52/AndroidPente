package com.example.pente_java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Scanner;

public class PenteBoard extends AppCompatActivity {
    private Board b;
    private Tournament t;
    private Round r;
    private Player h;
    private Player c;

    private ComputerStrategy s;
    public void scoreDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.score_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        // Find TextViews and Button within the dialogView
        TextView humanScoreTextView = dialogView.findViewById(R.id.humanScore);
        TextView computerScoreTextView = dialogView.findViewById(R.id.computerScore);
        TextView humanCaptureScoreTextView = dialogView.findViewById(R.id.humanCaptureScore);
        TextView computerCaptureScoreTextView = dialogView.findViewById(R.id.computerCaptureScore);
        TextView humanFiveScoreTextView = dialogView.findViewById(R.id.humanFiveScore);
        TextView computerFiveScoreTextView = dialogView.findViewById(R.id.computerFiveScore);
        TextView humanFourScoreTextView = dialogView.findViewById(R.id.humanFourScore);
        TextView computerFourScoreTextView = dialogView.findViewById(R.id.computerFourScore);
        Button closeButton = dialogView.findViewById(R.id.closeButton);
        // Find the Close Button


        // Set the score values
        humanScoreTextView.setText("Human Score: " + Integer.toString(r.getRoundEndScore(h)));
        computerScoreTextView.setText("Computer Score: " + Integer.toString(r.getRoundEndScore(c)));
        humanCaptureScoreTextView.setText("Human Capture Score: " + r.getPairsCapturedNum(h));
        computerCaptureScoreTextView.setText("Computer Capture Score: " + r.getPairsCapturedNum(c));
        humanFiveScoreTextView.setText("Human Game Score: " + r.getGamePoints(h));
        computerFiveScoreTextView.setText("Computer Ga,e Score: " + r.getGamePoints(c));
        humanFourScoreTextView.setText("Human Four Consecutive Score: " + r.getFourConsecutivesNum(h));
        computerFourScoreTextView.setText("Computer Four Consecutive Score: " + r.getFourConsecutivesNum(c));


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog

            }
        });
        // Show the dialog
        dialog.show();

    }
    public void initBoard(){
        //  now show the board
        //  now show the board


        GridLayout penteBoard = findViewById(R.id.penteBoard);
        penteBoard.removeAllViews();

        for (int row = 0; row < b.getBoardDimension()-1; row++) {
            for (int col = 0; col < b.getBoardDimension()-1; col++) {
                Button button = new Button(PenteBoard.this);

                // Set button properties
                button.setLayoutParams(new GridLayout.LayoutParams());
                int margin = 10; // Set your desired margin value
                button.setPadding(margin, margin, margin, margin);
                if (b.getPiece(row+1, col+1) == '0'){
                    button.setBackgroundResource(R.drawable.border_drawable); // Custom background drawable
                }
                else if (b.getPiece(row+1, col+1) == 'W'){
                    button.setBackgroundResource(R.drawable.white_piece); // Custom background drawable
                }
                else{
                    button.setBackgroundResource(R.drawable.black_piece); // Custom background drawable
                }

                // Define the layout parameters for positioning in the GridLayout
                GridLayout.Spec rowSpec = GridLayout.spec(row, 1f); // 1f means equal distribution
                GridLayout.Spec colSpec = GridLayout.spec(col, 1f); // 1f means equal distribution
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.width = 0;
                params.height = 0;

                button.setLayoutParams(params);

                // Add the button to the GridLayout
                penteBoard.addView(button);

                // Set a click listener for the button
                final int finalRow = row;
                final int finalCol = col;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //change turn
                        if (b.getPiece(finalRow+1, finalCol+1)!='0'){
                            Toast.makeText(getApplicationContext(), "Cannot place on Clicked: Row " + finalRow + ", Column " + finalCol, Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(getApplicationContext(), r.getCurrentPlayer().getName(), Toast.LENGTH_SHORT).show();

                            button.setBackgroundResource(r.getCurrentPlayer().getBackground());
                            if (!r.getCurrentPlayer().makeMove(finalRow+1, finalCol+1, r, b, s, t, PenteBoard.this)){
                                //TODO go to a new intent
//                                display round end scores
//                                show option to play a new game or quit the game
//                                if new game is clicked, re intent back to this page
//                                else show the overall tournament scores and end the game
                               scoreDialog();
                            }

                            Toast.makeText(getApplicationContext(), "Placed on Clicked: Row " + finalRow + ", Column " + finalCol, Toast.LENGTH_SHORT).show();

                            initBoard();


                        }

                    }
                });

                //we want the computer to play on its own without having us to click on the cells
                if (r.getCurrentPlayer() == c) {
                    if (!r.getCurrentPlayer().makeMove(-10, -10, r, b, s, t, PenteBoard.this)){
                        //TODO go to a new intent
//                                display round end scores
//                                show option to play a new game or quit the game
//                                if new game is clicked, re intent back to this page
//                                else show the overall tournament scores and end the game
//                        Intent intent = new Intent(PenteBoard.this, RoundEnd.class);
////                        intent.putExtra("round", r);
////                        intent.putExtra("tournament", t);
//
//                        startActivity(intent);
                        scoreDialog();

                    }
                    initBoard();
                }


            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pente_board);

        //setup board
        b = new Board();
        t = new Tournament();
        r = t.createANewRound();
        h = new Human("Human");
        c = new Computer("Robot");
        s = new ComputerStrategy(t);
        t.setUpPlayers(h, c);

        //toss coin and show who won
        Button headsBtn = findViewById(R.id.headsBtn);
        Button tailsBtn = findViewById(R.id.tailsBtn);
        TextView coinTossText = findViewById(R.id.coinTossText);
        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);


        // Define a Handler and a Runnable to hide the widgets
        Handler handler = new Handler();
        Runnable hideWidgetsRunnable = new Runnable() {
            @Override
            public void run() {
                headsBtn.setVisibility(View.GONE);
                tailsBtn.setVisibility(View.GONE);
                coinTossText.setVisibility(View.GONE);

                //init boardview
                initBoard();
            }
        };

        View.OnClickListener coinTossListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonLabel = (String) v.getTag(); // Assuming you set a tag on your buttons

                // Create an Intent to open the new activity
                // Toss the coin
//                Round r = new Round();
                Scanner scanner = new Scanner(System.in);
                humanColor.setVisibility(View.VISIBLE);
                computerColor.setVisibility(View.VISIBLE);
                // Coin toss returns true if the user won

                if (r.startRound(t,b, scanner, buttonLabel.charAt(0), PenteBoard.this)) {
                    //set Human:White
                    //set Computer: Black
                    humanColor.setText("Human:White");
                    computerColor.setText("Computer:Black");
                    coinTossText.setText("Winner: Human (" + buttonLabel + ")");
                    h.setBackground(R.drawable.white_piece);
                    c.setBackground(R.drawable.black_piece);
                } else {
                    //set Human:Black
                    //set Computer: White
                    humanColor.setText("Human:Black");
                    computerColor.setText("Computer:White");
                    coinTossText.setText("Winner: Computer (" + buttonLabel + ")");
                    h.setBackground(R.drawable.black_piece);
                    c.setBackground(R.drawable.white_piece);
                }

                // Use the Handler to post the Runnable with a delay of 2000 milliseconds (2 seconds)
                handler.postDelayed(hideWidgetsRunnable, 2000);
            }
        };

        headsBtn.setTag("Heads"); // Set a tag to identify the button
        tailsBtn.setTag("Tails"); // Set a tag to identify the button

        headsBtn.setOnClickListener(coinTossListener);
        tailsBtn.setOnClickListener(coinTossListener);





    }
}