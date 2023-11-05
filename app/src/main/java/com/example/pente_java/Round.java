package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

import java.io.Serializable;

public class Round implements Serializable {
    private Player currentPlayer;
    private Player nextPlayer;
    private Player winnerOfTheRound;
    private Player loserOfTheRound;
    private Map<Player, Integer> pairsCaptured;
    private Map<Player, Integer> fourConsecutive;
    private Map<Player, Integer> gamePoints;
    private int currentTurnNum;
//
    public Round() {
        pairsCaptured = new HashMap<>();
        fourConsecutive = new HashMap<>();
        gamePoints = new HashMap<>();
        currentTurnNum = 0;
    }
//
    public boolean coinToss( char humanChoice) {

        Random rand = new Random();
        char toss = (rand.nextInt(2) == 0) ? 'H' : 'T';
        System.out.println("It was : " + toss);

        if (humanChoice == toss) {
            System.out.println("Human won the toss!");

            return true;
        } else {
            System.out.println("Computer won the toss.");
            // scanner.close();
            return false;
        }
    }
//
//    returns true if human started the round
    public Boolean startRound(Tournament t, Board b, Scanner scanner, char humanChoice, Activity a) {
        System.out.println("Starting round " + t.getRoundsCount() + ".");
        b.resetBoard();
        Boolean toss = false;
        // Initialize white and black players
        if (t.getRoundsCount() == 1
                || t.getTotalScores(t.getHuman(), false) == t.getTotalScores(t.getComputer(), false)) {
            // If it's the first round or scores are the same, toss a coin to determine the
            // starter
            toss = coinToss(humanChoice);
            if (toss) { // On winning the coin toss, white will be the human
                currentPlayer = t.getHuman();
                nextPlayer = t.getComputer();
            } else {
                currentPlayer = t.getComputer();
                nextPlayer = t.getHuman();
            }
        } else {
            if (t.getTotalScores(t.getHuman(), false) > t.getTotalScores(t.getComputer(), false)) {
                // If scores are different, the higher scorer is the starter
                currentPlayer = t.getHuman();
                nextPlayer = t.getComputer();
            } else {
                nextPlayer = t.getHuman();
                currentPlayer = t.getComputer();
            }
        }

        currentPlayer.setColor('W');
        nextPlayer.setColor('B');
        System.out.println(currentPlayer.getName() + " is starting the game");
        currentPlayer.makeMove(10, 10, this, b, null, t, a);
//        changeTurn();
        return toss;
    }

    public void changeTurn() {
        incrementTurnNum();

        // Swap current and next players
        Player temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;
    }
//
//    public boolean askPositionInput(Board b, Player human, Player computer, Tournament t, ComputerStrategy c, char saveState, char helpState) {
//        // Scanner scanner = new Scanner(System.in);
//
//        // Ask if the user wants to continue or save and quit
//        ;
//
////        do {
////            System.out.println("Enter s/S to save and quit your game. Enter c/C to continue with the game.");
////            saveState = scanner.next().toLowerCase().charAt(0);
////        } while (saveState != 's' && saveState != 'c');
//
//        if (saveState == 's') {
////            saveGameToFile(b, human, computer, t, scanner);
//            return false;
//        }
//
//        // If the current player is the computer, return true to continue the game
//        if (currentPlayer == computer) {
//            // scanner.close();
//            return true;
//        }
//
//        // Ask the human player if they want help or to continue with the game
////        char helpState;
//
//        do {
//            System.out.println(
//                    "Enter h/H to get recommendations on where to place your piece, c/C to continue with the game.");
//            helpState = scanner.next().toLowerCase().charAt(0);
//        } while (helpState != 'h' && helpState != 'c');
//
//        if (helpState == 'h') {
//            // Provide a suggestion on where to put the next piece
//            String bestPosition, reason;
//            // You can call the equivalent Java method to determine the best position using
//            // the computer strategy
//            // Replace the following line with the appropriate Java code:
////            bestPosition = c.determineBestPosition(b, getCurrentPlayer(), getNextPlayer(), this);
////            reason = c.getFinalReason();
////            System.out.println("Recommended position would be " + bestPosition + " to " + reason);
//        }
//
//        // Ask the human player to enter their desired position until it is valid
//        boolean isValidPos = false;
//        String position = "";
//        while (!isValidPos) {
//            System.out.println("Enter the intersection you want to put your piece in. Follow the format A10, B2, K16.");
//            position = scanner.next();
//
//            if (!b.parsePosition(position)) {
//                continue;
//            }
//
//            isValidPos = b.checkValidity(this);
//        }
//
//        System.out.println("Human entered " + position);
//        // scanner.close();
//        return true;
//    }
////
//    public void displayClosingStats() {
//        System.out.println("Showing Round End SCORES:");
//
//        int currentPlayerScore = getPairsCapturedNum(currentPlayer) + getFourConsecutivesNum(currentPlayer)
//                + getGamePoints(currentPlayer);
//        int currentPlayerPairsCaptured = getPairsCapturedNum(currentPlayer);
//        int currentPlayerFours = getFourConsecutivesNum(currentPlayer);
//        int currentPlayerGamePoints = getGamePoints(currentPlayer);
//
//        int nextPlayerScore = getPairsCapturedNum(nextPlayer) + getFourConsecutivesNum(nextPlayer)
//                + getGamePoints(nextPlayer);
//        int nextPlayerPairsCaptured = getPairsCapturedNum(nextPlayer);
//        int nextPlayerFours = getFourConsecutivesNum(nextPlayer);
//        int nextPlayerGamePoints = getGamePoints(nextPlayer);
//
//        System.out.println(currentPlayer.getName() + " scored " + (currentPlayerScore)
//                + " points. Pairs captured = " + currentPlayerPairsCaptured
//                + " Four consecutives = " + currentPlayerFours
//                + " Game Points " + currentPlayerGamePoints);
//
//        System.out.println(nextPlayer.getName() + " scored " + (nextPlayerScore)
//                + " points. Pairs captured = " + nextPlayerPairsCaptured
//                + " Four consecutives = " + nextPlayerFours
//                + " Game Points " + nextPlayerGamePoints);
//
//        determineWinnerOfTheRound();
//        System.out.println(winnerOfTheRound.getName() + " won this round");
//    }
//
//    public boolean askToContinueGame(Scanner scanner) {
//        // Scanner scanner = new Scanner(System.in);
//        char response;
//
//        // Ask the player if they want to continue the game
//        do {
//            System.out.print("Do you want to continue the tournament? (y/n): ");
//            response = scanner.next().charAt(0);
//        } while (Character.toLowerCase(response) != 'y' && Character.toLowerCase(response) != 'n');
//
//        // Check the player's response
//        if (Character.toLowerCase(response) == 'y') {
//            // Player wants to continue
//            // scanner.close();
//            return true;
//        } else {
//            // Player does not want to continue
//            // scanner.close();
//            return false;
//        }
//    }
//
//    public void loadRound(String filename, Player human, Player computer, Tournament t) {
//        int hCapturedPairs = 0;
//        int hScore = 0;
//        int cCapturedPairs = 0;
//        int cScore = 0;
//        String currPlayer = "";
//        String currColor = "";
//
//        try (BufferedReader inputFile = new BufferedReader(new FileReader(filename))) {
//            String line;
//            while ((line = inputFile.readLine()) != null) {
//                if (line.contains("Human:")) {
//                    // Parse lines to extract captured pairs and score for the human player
//                    line = inputFile.readLine();
//                    int pos = line.indexOf("Captured pairs:");
//                    if (pos != -1) {
//                        hCapturedPairs = Integer.parseInt(line.substring(pos + 15).trim());
//                    }
//                    line = inputFile.readLine();
//                    pos = line.indexOf("Score:");
//                    if (pos != -1) {
//                        hScore = Integer.parseInt(line.substring(pos + 6).trim());
//                    }
//                } else if (line.contains("Computer:")) {
//                    // Parse lines to extract captured pairs and score for the computer player
//                    line = inputFile.readLine();
//                    int pos = line.indexOf("Captured pairs:");
//                    if (pos != -1) {
//                        cCapturedPairs = Integer.parseInt(line.substring(pos + 15).trim());
//                    }
//                    line = inputFile.readLine();
//                    pos = line.indexOf("Score:");
//                    if (pos != -1) {
//                        cScore = Integer.parseInt(line.substring(pos + 6).trim());
//                    }
//                } else if (line.contains("Next Player:")) {
//                    // Parse lines to extract the current player and color
//                    int colonPos = line.indexOf(':');
//                    if (colonPos != -1) {
//                        String playerInfo = line.substring(colonPos + 2);
//                        int hyphenPos = playerInfo.indexOf('-');
//                        if (hyphenPos != -1) {
//                            currPlayer = playerInfo.substring(0, hyphenPos - 1);
//                            currColor = playerInfo.substring(hyphenPos + 2);
//                        }
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Error opening the file.");
//            return;
//        }
//
//        setPairsCapturedNum(human, hCapturedPairs);
//        setPairsCapturedNum(computer, cCapturedPairs);
//
//        t.setTotalScore(hScore, cScore);
//
//        if ("Human".equals(currPlayer)) {
//            currentPlayer = computer;
//            nextPlayer = human;
//        } else {
//            currentPlayer = human;
//            nextPlayer = computer;
//        }
//
//        if ("White".equals(currColor)) {
//            currentPlayer.setColor('B');
//            nextPlayer.setColor('W');
//        } else {
//            currentPlayer.setColor('W');
//            nextPlayer.setColor('B');
//        }
//
//        int turnNum = getTurnNum();
//        turnNum += (hCapturedPairs * 2) + (cCapturedPairs * 2);
//        setTurnNum(turnNum - 1);
//    }
//
    public void setTurnNum(int num) {
        currentTurnNum = num;
    }
//
    public void setPairsCapturedNum(Player p) {
        setPairsCapturedNum(p, getPairsCapturedNum(p) + 1);
    }
//
    public void setPairsCapturedNum(Player p, int captureCount) {
        pairsCaptured.put(p, captureCount);
    }
//
    public void setFourConsecutive(Player p, int foursCount) {
        fourConsecutive.put(p, foursCount);
    }
//
    public void setGamePoints(Player p, Tournament t) {
        gamePoints.put(p, 5);
    }
//
//    public void saveGameToFile(Board b, Player human, Player computer, Tournament t, Scanner scanner) {
//        // Ask the name for the txt file
//        // Scanner scanner = new Scanner(System.in);
//        System.out.println("Enter the name for a file for this game to be saved");
//        String fileName = scanner.next();
//
//        // Save the board into the txt file
//        try (PrintWriter outputFile = new PrintWriter(fileName + ".txt")) {
//            outputFile.println("Board:");
//            for (int i = 1; i < 20; i++) {
//                for (int j = 1; j < 20; j++) {
//                    if (b.getPiece(i, j) == '0') {
//                        outputFile.print('O');
//                    } else {
//                        outputFile.print(b.getPiece(i, j));
//                    }
//                }
//                outputFile.println();
//            }
//            outputFile.println("Human:");
//            outputFile.println("Captured pairs: " + getPairsCapturedNum(human));
//            outputFile.println("Score: " + t.getTotalScores(human, true));
//            outputFile.println();
//
//            outputFile.println("Computer:");
//            outputFile.println("Captured pairs: " + getPairsCapturedNum(computer));
//            outputFile.println("Score: " + t.getTotalScores(computer, true));
//            outputFile.println();
//
//            String colorNext = currentPlayer.getColor() == 'W' ? "White" : "Black";
//            String nextPlayerCategory = currentPlayer.getName().equals("ROBOT") ? "Computer" : "Human";
//            outputFile.println("Next Player: " + nextPlayerCategory + " - " + colorNext);
//            System.out.println("File written successfully.");
//        } catch (FileNotFoundException e) {
//            System.err.println("Error opening the file.");
//        }
//
//        // scanner.close();
//    }
//
//    public void determineWinnerOfTheRound() {
//        // Winner of The Round is the player with the most scores in the round
//        System.out.println("current player name:" + currentPlayer.getName());
//        winnerOfTheRound = getRoundEndScore(currentPlayer) < getRoundEndScore(nextPlayer) ? nextPlayer : currentPlayer;
//        loserOfTheRound = (winnerOfTheRound == currentPlayer) ? nextPlayer : currentPlayer;
//    }
//
    public void incrementTurnNum() {
        currentTurnNum++;
    }
//
    public int getTurnNum() {
        return currentTurnNum;
    }
//
    public int getPairsCapturedNum(Player p) {
        return pairsCaptured.getOrDefault(p, 0);
    }
//
    public int getFourConsecutivesNum(Player p) {
        return fourConsecutive.getOrDefault(p, 0);
    }
//
    public int getGamePoints(Player p) {
        return gamePoints.getOrDefault(p, 0);
    }
//
    public int getRoundEndScore(Player p) {

        return getPairsCapturedNum(p) + getGamePoints(p) + getFourConsecutivesNum(p);
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getNextPlayer() {
        return nextPlayer;
    }

//    public Player getWinner() {
//        return winnerOfTheRound;
//    }
//
//    public Player getLoser() {
//        return loserOfTheRound;
//    }
}
