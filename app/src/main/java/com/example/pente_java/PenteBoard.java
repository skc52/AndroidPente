package com.example.pente_java;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;
import java.util.Scanner;

public class PenteBoard extends AppCompatActivity {
    private static final int REQUEST_CODE_SAVE_FILE = 2;
    private Board b;
    private Tournament t;
    private Round r;
    private Player h;
    private Player c;
    private char humanChoice;
    private int suggestedRow = -1;
    private int suggestedCol = -1;

    private ComputerStrategy s;



    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

//    private String filename = "SampleFile.txt";
    private String filepath = "MyFileStorage";

    File myExternalFile;
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    private void saveGameToFile(Board b, Player human, Player computer, Tournament t, String filename) {

        // on below line creating and initializing variable for context wrapper.
        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        // on below line creating a directory for file and specifying the file name.
        File directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        // on below line creating a text file.
        File txtFile = new File(directory, filename + ".txt");
        // on below line writing the text to our file.
        FileOutputStream fos = null;
        String gameData = buildGameData(b, human, computer, t); // Customize this method to build your game data

        try {
            fos = new FileOutputStream(txtFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(gameData);
            osw.flush();
            osw.close();
            fos.close();
            Toast.makeText(contextWrapper, "File write successful..", Toast.LENGTH_SHORT).show();
//            msgEdt.setText("");
        } catch (Exception e) {
            // on below line handling the exception.
            e.printStackTrace();
        }
    }


    // Define a method to build your game data as a string
    private String buildGameData(Board b, Player human, Player computer, Tournament t) {
        StringBuilder gameData = new StringBuilder();
        gameData.append("Board:\n");
        for (int i = 1; i < 20; i++) {
            for (int j = 1; j < 20; j++) {
                if (b.getPiece(i, j) == '0') {
                    gameData.append('O');
                } else {
                    gameData.append(b.getPiece(i, j));
                }
            }
            gameData.append("\n");
        }

        gameData.append("Human:\n");
        gameData.append("Captured pairs: " + r.getPairsCapturedNum(human) + "\n");
        gameData.append("Score: " + t.getTotalScores(human, true) + "\n\n");

        gameData.append("Computer:\n");
        gameData.append("Captured pairs: " + r.getPairsCapturedNum(computer) + "\n");
        gameData.append("Score: " + t.getTotalScores(computer, true) + "\n\n");

        String colorNext = r.getCurrentPlayer().getColor() == 'W' ? "White" : "Black";
        String nextPlayerCategory = r.getCurrentPlayer().getName().equals("ROBOT") ? "Computer" : "Human";
        gameData.append("Next Player: " + nextPlayerCategory + " - " + colorNext);

        return gameData.toString();
    }
    private void showTournamentEndDialog(
        int humanScore, int computerScore,
        int humanCapturedPairs, int humanConsecutiveFours, int humanGamePoints,
        int computerCapturedPairs, int computerConsecutiveFours, int computerGamePoints) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.tournament_end_dialog, null);
        builder.setView(dialogView);

        TextView humanScoreView = dialogView.findViewById(R.id.humanScore);
        TextView computerScoreView = dialogView.findViewById(R.id.computerScore);
        TextView humanCapturedPairsView = dialogView.findViewById(R.id.humanCapturedPairs);
        TextView humanConsecutiveFoursView = dialogView.findViewById(R.id.humanConsecutiveFours);
        TextView humanGamePointsView = dialogView.findViewById(R.id.humanGamePoints);
        TextView computerCapturedPairsView = dialogView.findViewById(R.id.computerCapturedPairs);
        TextView computerConsecutiveFoursView = dialogView.findViewById(R.id.computerConsecutiveFours);
        TextView computerGamePointsView = dialogView.findViewById(R.id.computerGamePoints);
        TextView winnerTextView = dialogView.findViewById(R.id.winnerText);

        Button closeButton = dialogView.findViewById(R.id.closeButton);

        humanScoreView.setText("Human Score: " + humanScore);
        computerScoreView.setText("Computer Score: " + computerScore);
        humanCapturedPairsView.setText("Human Captured Pairs: " + humanCapturedPairs);
        humanConsecutiveFoursView.setText("Human Consecutive Fours: " + humanConsecutiveFours);
        humanGamePointsView.setText("Human Game Points: " + humanGamePoints);
        computerCapturedPairsView.setText("Computer Captured Pairs: " + computerCapturedPairs);
        computerConsecutiveFoursView.setText("Computer Consecutive Fours: " + computerConsecutiveFours);
        computerGamePointsView.setText("Computer Game Points: " + computerGamePoints);

        // Determine and display the winner
        String winner;
        if (humanScore > computerScore) {
            winner = "Human";
        } else if (humanScore < computerScore) {
            winner = "Computer";
        } else {
            winner = "Tie";
        }
        winnerTextView.setText("Winner: " + winner);

        final AlertDialog dialog = builder.create();
        dialog.show();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(PenteBoard.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showCoinTossDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.coin_toss, null);
        builder.setView(dialogView);

        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);


        Button headsButton = dialogView.findViewById(R.id.headsBtn);
        Button tailsButton = dialogView.findViewById(R.id.tailsBtn);
        Button closeBtn = dialogView.findViewById(R.id.closeBtn);
        TextView coinTossResult = dialogView.findViewById(R.id.coinTossResult);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        humanChoice = 'X';
        headsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headsButton.setVisibility(View.GONE);
                tailsButton.setVisibility(View.GONE);
                humanChoice = 'H';
                closeBtn.setVisibility(View.VISIBLE);

                Random rand = new Random();
                char toss = (rand.nextInt(2) == 0) ? 'H' : 'T';
                coinTossResult.setVisibility(View.VISIBLE);
                if (humanChoice == toss) {
                    coinTossResult.setText("Human won the toss! Human is starting the round!");
                    r.startRound(t, b, 'H',PenteBoard.this);
                    h.setBackground(R.drawable.white_piece);
                    c.setBackground(R.drawable.black_piece);

                    humanColor.setText("Human: WHITE");
                    computerColor.setText("Computer: BLACK");

                } else {
                    coinTossResult.setText("Computer won the toss! Computer is starting the round!");
                    r.startRound(t, b, 'C',PenteBoard.this);
                    //set Human:Black
                    //set Computer: White
                    h.setBackground(R.drawable.black_piece);
                    c.setBackground(R.drawable.white_piece);
                    humanColor.setText("Human: BLACK");
                    computerColor.setText("Computer: WHITE");


                }
                initBoard();;

//                dialog.dismiss();
            }
        });

        tailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headsButton.setVisibility(View.GONE);
                tailsButton.setVisibility(View.GONE);

                humanChoice = 'T';
//                dialog.dismiss();
                closeBtn.setVisibility(View.VISIBLE);
                Random rand = new Random();
                char toss = (rand.nextInt(2) == 0) ? 'H' : 'T';
                coinTossResult.setVisibility(View.VISIBLE);
                if (humanChoice == toss) {
                    coinTossResult.setText("Human won the toss! Human is starting the round!");
                    r.startRound(t, b, 'H',PenteBoard.this);
                    h.setBackground(R.drawable.white_piece);
                    c.setBackground(R.drawable.black_piece);
                    humanColor.setText("Human: WHITE");
                    computerColor.setText("Computer: BLACK");
                } else {
                    coinTossResult.setText("Computer won the toss! Computer is starting the round!");
                    r.startRound(t, b, 'C',PenteBoard.this);
                    //set Human:Black
                    //set Computer: White
                    h.setBackground(R.drawable.black_piece);
                    c.setBackground(R.drawable.white_piece);
                    humanColor.setText("Human: BLACK");
                    computerColor.setText("Computer: WHITE");
                }
                initBoard();
            }
        });





        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });
        dialog.show();
    }



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
        TextView winnerDeclarationTextView = dialogView.findViewById(R.id.winnerDeclaration);

        Button quitBtn = dialogView.findViewById(R.id.quitTournament);
        Button continueBtn = dialogView.findViewById(R.id.continueTournament);

        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);

        // Find the Close Button


        // Set the score values
        humanScoreTextView.setText("Human Total Score: " + Integer.toString(r.getRoundEndScore(h)));
        computerScoreTextView.setText("Computer Total Score: " + Integer.toString(r.getRoundEndScore(c)));
        humanCaptureScoreTextView.setText("Human Capture Score: " + r.getPairsCapturedNum(h));
        computerCaptureScoreTextView.setText("Computer Capture Score: " + r.getPairsCapturedNum(c));
        humanFiveScoreTextView.setText("Human Game Score: " + r.getGamePoints(h));
        computerFiveScoreTextView.setText("Computer Game Score: " + r.getGamePoints(c));
        humanFourScoreTextView.setText("Human Four Consecutive Score: " + r.getFourConsecutivesNum(h));
        computerFourScoreTextView.setText("Computer Four Consecutive Score: " + r.getFourConsecutivesNum(c));
        winnerDeclarationTextView.setText("Winner: " + r.getWinner().getName());

        quitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog
                // TODO show tournament end scores in a dialog box
                int[] hFinalScores = t.getFinalScores(h, false);
                int[] cFinalScores = t.getFinalScores(c, false);
                showTournamentEndDialog(hFinalScores[0], cFinalScores[0], hFinalScores[1],
                        hFinalScores[2], hFinalScores[3], cFinalScores[1], cFinalScores[2], cFinalScores[3]);

            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog
                // reset the board and start a new round
                b.resetBoard();
                //TODO coin toss dialog
                r = t.createANewRound();
                if (t.getTotalScores(h, false) == t.getTotalScores(c, false)){
                    showCoinTossDialog();
                }
                else{
                    //no coin toss needed
//                    showCoinTossDialog();
                    if (t.getTotalScores(h, false) < t.getTotalScores(c, false)){
                        r.startRound(t, b, 'C',PenteBoard.this);
                        humanColor.setText("Human: BLACK");
                        computerColor.setText("Computer: WHITE");
                        h.setBackground(R.drawable.black_piece);
                        c.setBackground(R.drawable.white_piece);
                    }
                    else{
                        r.startRound(t, b, 'H',PenteBoard.this);
                        humanColor.setText("Human: WHITE");
                        computerColor.setText("Computer: BLACK");
                        h.setBackground(R.drawable.white_piece);
                        c.setBackground(R.drawable.black_piece);
                    }
                }
                initBoard();

            }
        });
        // Show the dialog
        dialog.show();

    }

    private void askFileNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.ask_filename_save, null);
        builder.setView(dialogView);


        EditText filenameEditText = dialogView.findViewById(R.id.filenameEditText);
        Button saveGameBtn = dialogView.findViewById(R.id.saveGameBtn);
        Button cancelSaveBtn = dialogView.findViewById(R.id.cancelSavingBtn);

        saveGameBtn.setEnabled(false);
        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);

        filenameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for this example, but you must implement it.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check the text in filenameEditText and enable/disable loadGameButton accordingly
                if (charSequence.toString().trim().isEmpty()) {
                    saveGameBtn.setEnabled(false);
                } else {
                    saveGameBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        saveGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveGameToFile(b, h, c, t, filenameEditText.getText().toString());
//                dialog.dismiss();
                Intent intent = new Intent(PenteBoard.this, MainActivity.class);
                startActivity(intent);
            }
        });
        cancelSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }
    public void initBoard(){
        //  now show the board
        //  now show the board


        TextView humanCapturedPairsTextView = findViewById(R.id.humanCapturedPairs);
        TextView computerCapturedPairsTextView = findViewById(R.id.computerCapturedPairs);
        TextView humanTotalScoreTextView = findViewById(R.id.humanTotalScore);
        TextView computerTotalScoreTextView = findViewById(R.id.computerTotalScore);
        TextView reasonTextView = findViewById(R.id.reason);
        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);

        Button helpBtn = findViewById(R.id.helpBtn);
        Button saveGameBtn = findViewById(R.id.saveGame);

        if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
            saveGameBtn.setEnabled(false);
        }


        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = s.determineBestPosition(b, r.getCurrentPlayer(), r.getNextPlayer(), r);
                //parse into row and col
                char colChar = Character.toUpperCase(inputString.charAt(0));
                int numericEquivalent = colChar - 'A' + 1;
                suggestedCol = numericEquivalent;

                String rowString = inputString.substring(1);
                suggestedRow = b.getBoardDimension() - Integer.parseInt(rowString);
                reasonTextView.setText("Suggested position is " + s.getFinalReason());

                initBoard();
            }
        });
        saveGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askFileNameDialog();
            }
        });
        humanColor.setVisibility(View.VISIBLE);
        computerColor.setVisibility(View.VISIBLE);

        humanCapturedPairsTextView.setVisibility(View.VISIBLE);
        computerCapturedPairsTextView.setVisibility(View.VISIBLE);
        humanTotalScoreTextView.setVisibility(View.VISIBLE);
        computerTotalScoreTextView.setVisibility(View.VISIBLE);
        reasonTextView.setVisibility(View.VISIBLE);

        humanCapturedPairsTextView.setText("Human Captured Pairs: " + Integer.toString(r.getPairsCapturedNum(h)));
        computerCapturedPairsTextView.setText("Computer Captured Pairs: " + Integer.toString(r.getPairsCapturedNum(c)));
        humanTotalScoreTextView.setText("Human Total Score: " + Integer.toString(t.getTotalScores(h, false)) + "  ");
        computerTotalScoreTextView.setText("Computer Total Score: "  + Integer.toString(t.getTotalScores(c, false)) + "  ");

        GridLayout penteBoard = findViewById(R.id.penteBoard);
        penteBoard.removeAllViews();

        for (int row = 0; row < b.getBoardDimension()-1; row++) {
            for (int col = 0; col < b.getBoardDimension()-1; col++) {
                Button button = new Button(PenteBoard.this);
                // Set button properties
                button.setLayoutParams(new GridLayout.LayoutParams());
                int margin = 100; // Set your desired margin value
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
                if (row+1 == suggestedRow && col+1 == suggestedCol){
                    button.setBackgroundResource(R.drawable.yellow_piece); // Custom background drawable
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
                        //check if second turn of white and withing 3 steps from j10

                        //reset suggested pos, so that we are not stuck with yellow piece for that pos even after clicling on it
                        suggestedCol = -1;
                        suggestedRow = -1;
                        //change turn
                        if (b.getPiece(finalRow+1, finalCol+1)!='0'){
                            Toast.makeText(getApplicationContext(), "Cannot place on Clicked: Row " + finalRow + ", Column " + finalCol, Toast.LENGTH_SHORT).show();
                        }
                        else if (r.getTurnNum() == 2 && r.getCurrentPlayer().getColor() == 'W' && (Math.abs(finalRow+1 - 10) <= 3 && Math.abs(finalCol+1 - 10) <= 3))  {
                            // If a human inputs within 3 steps from the center, re-ask for input.

                                Toast.makeText(getApplicationContext(), "Cannot put within 3 steps from J10 ", Toast.LENGTH_SHORT).show();
                        }
                        else{
//                            Toast.makeText(getApplicationContext(), r.getCurrentPlayer().getName(), Toast.LENGTH_SHORT).show();
                            button.setBackgroundResource(r.getCurrentPlayer().getBackground());
                            if (!r.getCurrentPlayer().makeMove(finalRow+1, finalCol+1, r, b, s, t, PenteBoard.this)) {

//                                display round end scores
//                                show option to play a new game or quit the game
//                                if new game is clicked, re intent back to this page
//                                else show the overall tournament scores and end the game
                                r.determineWinnerOfTheRound();
                                scoreDialog();
                            }
                            reasonTextView.setText("Computer placed on " + s.getFinalReason());

//                            Toast.makeText(getApplicationContext(), "Placed on Clicked: Row " + finalRow + ", Column " + finalCol, Toast.LENGTH_SHORT).show();

                            //TODO wait for 1 second
                            // Define a Handler and a Runnable to delay computer move
                            Handler handler = new Handler();
                            Runnable delayComputerMove = new Runnable() {
                                @Override
                                public void run() {
                                    initBoard();
                                }
                            };
                            handler.postDelayed(delayComputerMove, 200);

                        }

                    }
                });

                //we want the computer to play on its own without having us to click on the cells
                if (r.getCurrentPlayer() == c) {
                    if (!r.getCurrentPlayer().makeMove(-10, -10, r, b, s, t, PenteBoard.this)){
                        r.determineWinnerOfTheRound();
                        scoreDialog();

                    }
                    initBoard();
                }
            }
        }
    }
    private void loadRound(String gameData){
        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);
        t.loadGame(gameData, b, r);
        r.loadRound(gameData, h, c, t);
//    Toast.makeText(getApplicationContext(), "TEST", Toast.LENGTH_SHORT).show();

        if (r.getCurrentPlayer().getColor() == 'W'){
            r.getCurrentPlayer().setBackground(R.drawable.white_piece);
            r.getNextPlayer().setBackground(R.drawable.black_piece);
            if (r.getCurrentPlayer() == h){

                humanColor.setText("Human: WHITE");
                computerColor.setText("Computer: BLACK");
            }
            else{

                humanColor.setText("Human: BLACK");
                computerColor.setText("Computer: WHITE");
            }
        }
        else{
            r.getCurrentPlayer().setBackground(R.drawable.black_piece);
            r.getNextPlayer().setBackground(R.drawable.white_piece);
            if (r.getCurrentPlayer() == h){
                humanColor.setText("Human: BLACK");
                computerColor.setText("Computer: WHITE");
            }
            else{
                humanColor.setText("Human: WHITE");
                computerColor.setText("Computer: BLACK");

            }
        }



        initBoard();
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


        Intent intent = getIntent();


        if (intent.hasExtra("gamedata")) {

            String receivedData = intent.getStringExtra("gamedata");
            // Now you have the received string ("Hello, this is the string to pass")
            // You can use it as needed in your target activity.
            loadRound(receivedData);

        }
        else{
            //if new game
            showCoinTossDialog();
        }






    }
}