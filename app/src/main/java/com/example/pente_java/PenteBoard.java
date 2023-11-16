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
import android.util.TypedValue;
import android.view.Gravity;
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
    private Board b;
    private Tournament t;
    private Round r;
    private Player h;
    private Player c;
    private char humanChoice;
    private int suggestedRow = -1;
    private int suggestedCol = -1;
    private ComputerStrategy s;
    private String messages = "";



    /**
     * Saves the current game state to a file.
     *
     * @param b The game board.
     * @param human The human player.
     * @param computer The computer player.
     * @param t The tournament instance.
     * @param filename The name of the file to save.
     * Assistance Received - https://www.tutorialspoint.com/how-to-save-files-on-external-storage-in-android
     */
    private void saveGameToFile(Board b, Player human, Player computer, Tournament t, String filename) {

        ContextWrapper contextWrapper = new ContextWrapper(getApplicationContext());
        File directory = contextWrapper.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        File txtFile = new File(directory, filename + ".txt");
        FileOutputStream fos = null;
        String gameData = buildGameData(b, human, computer, t);
        try {
            fos = new FileOutputStream(txtFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(gameData);
            osw.flush();
            osw.close();
            fos.close();
            new AlertDialog.Builder(PenteBoard.this)
                    .setTitle("Success")
                    .setMessage("File write successful..")
                    .setPositiveButton("OK", null)
                    .show();
            messages += "File write successful\n\n";
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    /**
     * Builds a string representing the game data for saving.
     * @param b The game board.
     * @param human The human player.
     * @param computer The computer player.
     * @param t The tournament instance.
     * @return A string containing the formatted game data.
     */
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

    /**
     * Displays an AlertDialog showing the results of the tournament, including scores,
     * captured pairs, consecutive fours, game points, and the winner.
     * @param humanScore The score of the human player.
     * @param computerScore The score of the computer player.
     * @param humanCapturedPairs The number of pairs captured by the human player.
     * @param humanConsecutiveFours The number of consecutive fours achieved by the human player.
     * @param humanGamePoints The game points scored by the human player.
     * @param computerCapturedPairs The number of pairs captured by the computer player.
     * @param computerConsecutiveFours The number of consecutive fours achieved by the computer player.
     * @param computerGamePoints The game points scored by the computer player.
     */
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


    /**
     * Displays an AlertDialog for the coin toss, allowing the user to choose heads or tails.
     * The result of the toss determines which player (human or computer) starts the round.
     * The chosen options and result are shown in the dialog.
     */
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
                    r.startRound(t, b, 'H');
                    h.setBackground(R.drawable.white_piece);
                    c.setBackground(R.drawable.black_piece);

                    humanColor.setText("Human: WHITE");
                    computerColor.setText("Computer: BLACK");

                } else {
                    coinTossResult.setText("Computer won the toss! Computer is starting the round!");
                    r.startRound(t, b, 'C');
                    //set Human:Black
                    //set Computer: White
                    h.setBackground(R.drawable.black_piece);
                    c.setBackground(R.drawable.white_piece);
                    humanColor.setText("Human: BLACK");
                    computerColor.setText("Computer: WHITE");


                }
                initBoard();;

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
                    r.startRound(t, b, 'H');
                    h.setBackground(R.drawable.white_piece);
                    c.setBackground(R.drawable.black_piece);
                    humanColor.setText("Human: WHITE");
                    computerColor.setText("Computer: BLACK");
                } else {
                    coinTossResult.setText("Computer won the toss! Computer is starting the round!");
                    r.startRound(t, b, 'C');
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

    /**
     * Displays an AlertDialog showing the scores and winner at the end of a round.
     * The dialog includes scores for human and computer players, captured pairs, game points,
     * consecutive fours, and declares the winner. It provides options to quit the tournament or
     * continue to the next round.
     */
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

                    if (t.getTotalScores(h, false) < t.getTotalScores(c, false)){
                        r.startRound(t, b, 'C');
                        humanColor.setText("Human: BLACK");
                        computerColor.setText("Computer: WHITE");
                        h.setBackground(R.drawable.black_piece);
                        c.setBackground(R.drawable.white_piece);
                    }
                    else{
                        r.startRound(t, b, 'H');
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

    /**
     * Displays a dialog containing scrollable messages/logs.
     * Messages are set dynamically through the 'messages' variable.
     * Allows the user to close the dialog.
     */
    private void showLogs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.scrollable_messages, null);
        builder.setView(dialogView);
        TextView messagesTextView = dialogView.findViewById(R.id.messageTextView);
        messagesTextView.setText(messages);
        Button cancelSaveBtn = dialogView.findViewById(R.id.closeButton);


        final AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);


        cancelSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    /**
     * Displays a dialog for asking the user to enter a filename for saving the game.
     * Enables the save button when the filename is not empty.
     * Saves the game data to a file when the user clicks the save button.
     * Navigates back to the main activity after saving.
     */

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
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
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

    /**
     * Initializes the game board with buttons representing each cell.
     * Displays relevant game information such as captured pairs, total scores, and reasons.
     * Allows the human player to make moves by clicking on empty cells.
     * Initiates the computer's move automatically after the human player makes a move.
     * Displays appropriate error messages if invalid moves are attempted.
     * Handles the end of the round by determining the winner and showing round end scores.
     * Offers options to start a new game or quit the current game.
     */
    public void initBoard(){
        TextView humanCapturedPairsTextView = findViewById(R.id.humanCapturedPairs);
        TextView computerCapturedPairsTextView = findViewById(R.id.computerCapturedPairs);
        TextView humanTotalScoreTextView = findViewById(R.id.humanTotalScore);
        TextView computerTotalScoreTextView = findViewById(R.id.computerTotalScore);
        TextView reasonTextView = findViewById(R.id.reason);
        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);

        Button helpBtn = findViewById(R.id.helpBtn);
        Button saveGameBtn = findViewById(R.id.saveGame);
        Button logBtn = findViewById(R.id.logBtn);




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
                messages+="Suggested position is " + s.getFinalReason()+"\n\n";

                initBoard();
            }
        });
        saveGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askFileNameDialog();
            }
        });

        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogs();
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


        final int numCP = r.getPairsCapturedNum(r.getCurrentPlayer());


        for (int row = 0; row < b.getBoardDimension(); row++) {
            for (int col = 0; col < b.getBoardDimension(); col++) {
                if (row == 0 && col == 0){
                    continue;
                }

                if (row == 0)
                {
                    TextView colLabel = new TextView(PenteBoard.this);
                    colLabel.setText(String.valueOf((char)('A' + col-1)));
                    colLabel.setTextColor(getResources().getColor(R.color.white));
                    float currentSize = colLabel.getTextSize();
                    float newSize = currentSize * 1.2f; // You can adjust the scale factor as needed
                    colLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
                    colLabel.setGravity(Gravity.CENTER);

                    GridLayout.Spec rowSpec = GridLayout.spec(row, 1f); // 1f means equal distribution
                    GridLayout.Spec colSpec = GridLayout.spec(col, 1f); // 1f means equal distribution
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                    params.width = 0;
                    params.height = 0;
                    colLabel.setLayoutParams(params);

                    penteBoard.addView(colLabel);
                    continue;
                }
                if (col == 0)
                {
                    TextView rowlabel = new TextView(PenteBoard.this);
                    rowlabel.setText(String.valueOf(19-row+1));
                    rowlabel.setTextColor(getResources().getColor(R.color.white));
                    float currentSize = rowlabel.getTextSize();
                    float newSize = currentSize * 1.2f; // You can adjust the scale factor as needed
                    rowlabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
                    rowlabel.setGravity(Gravity.CENTER);


                    GridLayout.Spec rowSpec = GridLayout.spec(row, 1f); // 1f means equal distribution
                    GridLayout.Spec colSpec = GridLayout.spec(col, 1f); // 1f means equal distribution
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                    params.width = 0;
                    params.height = 0;
                    rowlabel.setLayoutParams(params);

                    penteBoard.addView(rowlabel);
                    continue;
                }

                Button button = new Button(PenteBoard.this);
                // Set button properties
                button.setLayoutParams(new GridLayout.LayoutParams());
                int margin = 100; // Set your desired margin value
                button.setPadding(margin, margin, margin, margin);


                if (b.getPiece(row, col) == '0'){
                    button.setBackgroundResource(R.drawable.border_drawable); // Custom background drawable
                }
                else if (b.getPiece(row, col) == 'W'){
                    button.setBackgroundResource(R.drawable.white_piece); // Custom background drawable
                }
                else{
                    button.setBackgroundResource(R.drawable.black_piece); // Custom background drawable
                }
                if (row == suggestedRow && col == suggestedCol){
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
                        suggestedCol = -1;
                        suggestedRow = -1;
                        //change turn
                        if (b.getPiece(finalRow, finalCol)!='0'){
                            new AlertDialog.Builder(PenteBoard.this)
                                    .setTitle("Error")
                                    .setMessage("Cannot place on Clicked: Row " + s.convertPosToString(finalRow, finalCol))
                                    .setPositiveButton("OK", null)
                                    .show();
//                            Toast.makeText(getApplicationContext(), "Cannot place on Clicked: Row " + s.convertPosToString(finalRow, finalCol), Toast.LENGTH_SHORT).show();
                            messages += "Cannot place on " + s.convertPosToString(finalRow, finalCol) + "\n\n";
                        }
                        else if (r.getTurnNum() == 2 && r.getCurrentPlayer().getColor() == 'W' && (Math.abs(finalRow - 10) <= 3 && Math.abs(finalCol - 10) <= 3))  {
                            // If a human inputs within 3 steps from the center, re-ask for input.
                            new AlertDialog.Builder(PenteBoard.this)
                                    .setTitle("Error")
                                    .setMessage("Cannot put within 3 steps from J10 ")
                                    .setPositiveButton("OK", null)
                                    .show();
//                                Toast.makeText(getApplicationContext(), "Cannot put within 3 steps from J10 ", Toast.LENGTH_SHORT).show();
                                messages += "Cannot put within 3 steps from J10\n\n";
                        }
                        else{
//                            Toast.makeText(getApplicationContext(), r.getCurrentPlayer().getName(), Toast.LENGTH_SHORT).show();
                            button.setBackgroundResource(r.getCurrentPlayer().getBackground());
                            if (!r.getCurrentPlayer().makeMove(finalRow, finalCol, r, b, s, t)) {
//                                display round end scores
//                                show option to play a new game or quit the game
//                                if new game is clicked, re intent back to this page
//                                else show the overall tournament scores and end the game
                                r.determineWinnerOfTheRound();
                                messages+= r.getWinner().getName() + " won the round number " + String.valueOf(t.getRoundsCount()) + "\n\n";
                                scoreDialog();
                            }
                            messages += r.getGameLog();
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

                    if (!r.getCurrentPlayer().makeMove(-10, -10, r, b, s, t)){
                        r.determineWinnerOfTheRound();
                        messages+= r.getWinner().getName() + " won the round number " + String.valueOf(t.getRoundsCount()) + "\n\n";
                        scoreDialog();
                    }

                    messages += r.getGameLog();
                    reasonTextView.setText("Computer placed on " + s.getFinalReason());
                    messages += "Computer placed on " + s.getFinalReason() + "\n\n";
                    initBoard();
                }


            }
        }
    }

    /**
     * Loads the game state from the provided game data, updating the game board and round information.
     * Displays the current player's color, the next player's color, and the reason for the suggested move.
     * @param gameData The serialized game data containing information about the round and board state.
     * Initializes the game board with buttons representing each cell based on the loaded state.
     */
    private void loadRound(String gameData){
        TextView humanColor = findViewById(R.id.humanColor);
        TextView computerColor = findViewById(R.id.computerColor);
        TextView reasonTextView = findViewById(R.id.reason);

        t.loadGame(gameData, b, r);
        r.loadRound(gameData, h, c, t);

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

    /**
     * Initializes the PenteBoard activity when created.
     * Sets up the game board, tournament, and players.
     * If the activity is created with game data from a saved file, loads the game state and displays relevant messages.
     * If the activity is created for a new game, initiates a coin toss to determine the starting player.
     */
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
            messages += "Loading game from saved file "+ intent.getStringExtra("filename") + "\n\n";
            String receivedData = intent.getStringExtra("gamedata");
            loadRound(receivedData);
        }
        else{
            //if new game
            messages += "Starting a new game. Tossing a coin.\n\n";
            showCoinTossDialog();
        }
    }
}