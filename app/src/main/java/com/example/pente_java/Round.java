package com.example.pente_java;

import android.app.Activity;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.io.*;

import java.io.Serializable;

/**
 * Represents a round in the Pente game.
 */
public class Round implements Serializable {
    private Player currentPlayer;
    private Player nextPlayer;
    private Player winnerOfTheRound;
    private Player loserOfTheRound;
    private Map<Player, Integer> pairsCaptured;
    private Map<Player, Integer> fourConsecutive;
    private Map<Player, Integer> gamePoints;
    private int currentTurnNum;
    private String gameLog;//it stores any important updates in an iteration of a round

    /**
     * Constructs a new Round object.
     */
    public Round() {
        pairsCaptured = new HashMap<>();
        fourConsecutive = new HashMap<>();
        gamePoints = new HashMap<>();
        currentTurnNum = 0;
    }


    /**
     * Sets the game log with the provided log.
     * @param log The log to set.
     */
    public void setGameLog(String log) {
        gameLog = log;
    }

    /**
     * Gets the current game log.
     * @return The current game log.
     */
    public String getGameLog() {
        return gameLog;
    }

    /**
     * Simulates a coin toss and determines if the human player won the toss.
     *
     * @param humanChoice The human player's choice (H or T).
     * @return True if human won the toss, false otherwise.
     */
    public boolean coinToss(char humanChoice) {
        Random rand = new Random();
        char toss = (rand.nextInt(2) == 0) ? 'H' : 'T';
        System.out.println("It was: " + toss);

        if (humanChoice == toss) {
            System.out.println("Human won the toss!");
            return true;
        } else {
            System.out.println("Computer won the toss.");
            return false;
        }
    }

    /**
     * Starts a new round.
     *
     * @param t               The tournament instance.
     * @param b               The game board.
     * @param cPlayerInitial The initial player ('H' for human, 'C' for computer).
     */
    public void startRound(Tournament t, Board b, char cPlayerInitial) {
        b.resetBoard();

        if (cPlayerInitial == 'H') {
            currentPlayer = t.getHuman();
            nextPlayer = t.getComputer();
        } else {
            nextPlayer = t.getHuman();
            currentPlayer = t.getComputer();
        }

        currentPlayer.setColor('W');
        nextPlayer.setColor('B');
        currentPlayer.makeMove(10, 10, this, b, null, t);
    }

    /**
     * Changes the turn by swapping current and next players.
     */
    public void changeTurn() {
        incrementTurnNum();
        Player temp = currentPlayer;
        currentPlayer = nextPlayer;
        nextPlayer = temp;
    }

    /**
     * Loads the round state from a string containing game data.
     *
     * @param gameData The string containing game data.
     * @param human    The human player instance.
     * @param computer The computer player instance.
     * @param t        The tournament instance.
     */
    public void loadRound(String gameData, Player human, Player computer, Tournament t) {
        int hCapturedPairs = 0;
        int cCapturedPairs = 0;
        int hScore = 0;
        int cScore = 0;
        String currPlayer = "";
        String currColor = "";

        String[] lines = gameData.split("\n");
        int i = 0;
        while (i < lines.length) {
            String line = lines[i].trim();
            if (line.contains("Human:")) {
                i++; // Move to the next line
                while (i < lines.length) {
                    line = lines[i].trim();
                    if (line.startsWith("Captured pairs:")) {
                        hCapturedPairs = Integer.parseInt(line.substring(15).trim());
                    } else if (line.startsWith("Score:")) {
                        hScore = Integer.parseInt(line.substring(6).trim());
                    } else {
                        // If a line doesn't start with "Captured pairs" or "Score", exit the loop
                        break;
                    }
                    i++; // Move to the next line
                }
            } else if (line.contains("Computer:")) {
                i++; // Move to the next line
                while (i < lines.length) {
                    line = lines[i].trim();
                    if (line.startsWith("Captured pairs:")) {
                        cCapturedPairs = Integer.parseInt(line.substring(15).trim());
                    } else if (line.startsWith("Score:")) {
                        cScore = Integer.parseInt(line.substring(6).trim());
                    } else {
                        // If a line doesn't start with "Captured pairs" or "Score", exit the loop
                        break;
                    }
                    i++; // Move to the next line
                }
            } else if (line.contains("Next Player:")) {
                int colonPos = line.indexOf(':');
                if (colonPos != -1) {
                    String playerInfo = line.substring(colonPos + 2).trim();
                    int hyphenPos = playerInfo.indexOf('-');
                    if (hyphenPos != -1) {
                        currPlayer = playerInfo.substring(0, hyphenPos - 1).trim();
                        currColor = playerInfo.substring(hyphenPos + 2).trim();
                    }
                }
                i++; // Move to the next line
            } else {
                i++; // Move to the next line
            }
        }

        setPairsCapturedNum(human, hCapturedPairs);
        setPairsCapturedNum(computer, cCapturedPairs);
        t.setTotalScore(hScore, cScore);

        if ("Human".equals(currPlayer)) {
            currentPlayer = human;
            nextPlayer = computer;
        } else {

            currentPlayer = computer;
            nextPlayer = human;
        }

        if ("White".equals(currColor)) {
            currentPlayer.setColor('W');
            nextPlayer.setColor('B');

        } else {
            currentPlayer.setColor('B');
            nextPlayer.setColor('W');
        }

        int turnNum = getTurnNum();
        turnNum += (hCapturedPairs * 2) + (cCapturedPairs * 2);
        setTurnNum(turnNum - 1);
    }

    /**
     * Sets the current turn number.
     *
     * @param num The turn number to set.
     */
    public void setTurnNum(int num) {
        currentTurnNum = num;
    }

    /**
     * Increments the number of pairs captured by the given player by one.
     *
     * @param p The player whose captured pairs count will be incremented.
     */
    public void setPairsCapturedNum(Player p) {
        setPairsCapturedNum(p, getPairsCapturedNum(p) + 1);
    }

    /**
     * Sets the number of pairs captured by the given player.
     *
     * @param p            The player whose captured pairs count will be set.
     * @param captureCount The number of pairs captured to set.
     */
    public void setPairsCapturedNum(Player p, int captureCount) {
        pairsCaptured.put(p, captureCount);
    }

    /**
     * Sets the number of consecutive fours achieved by the given player.
     *
     * @param p          The player whose consecutive fours count will be set.
     * @param foursCount The number of consecutive fours to set.
     */
    public void setFourConsecutive(Player p, int foursCount) {
        fourConsecutive.put(p, foursCount);
    }

    /**
     * Sets the game points for the given player in the tournament.
     *
     * @param p The player for whom game points will be set.
     * @param t The tournament instance.
     */
    public void setGamePoints(Player p, Tournament t) {
        gamePoints.put(p, 5);
    }

    /**
     * Saves the current game state to a text file.
     *
     * @param b       The game board.
     * @param human   The human player instance.
     * @param computer The computer player instance.
     * @param t       The tournament instance.
     * @param scanner The scanner to get input from the user.
     */
    public void saveGameToFile(Board b, Player human, Player computer, Tournament t, Scanner scanner) {
        System.out.println("Enter the name for a file for this game to be saved");
        String fileName = scanner.next();

        try (PrintWriter outputFile = new PrintWriter(fileName + ".txt")) {
            // Save board state
            outputFile.println("Board:");
            for (int i = 1; i < 20; i++) {
                for (int j = 1; j < 20; j++) {
                    outputFile.print((b.getPiece(i, j) == '0') ? 'O' : b.getPiece(i, j));
                }
                outputFile.println();
            }

            // Save human player's information
            outputFile.println("Human:");
            outputFile.println("Captured pairs: " + getPairsCapturedNum(human));
            outputFile.println("Score: " + t.getTotalScores(human, true));
            outputFile.println();

            // Save computer player's information
            outputFile.println("Computer:");
            outputFile.println("Captured pairs: " + getPairsCapturedNum(computer));
            outputFile.println("Score: " + t.getTotalScores(computer, true));
            outputFile.println();

            // Save information about the next player
            String colorNext = currentPlayer.getColor() == 'W' ? "White" : "Black";
            String nextPlayerCategory = currentPlayer.getName().equals("ROBOT") ? "Computer" : "Human";
            outputFile.println("Next Player: " + nextPlayerCategory + " - " + colorNext);
            System.out.println("File written successfully.");
        } catch (FileNotFoundException e) {
            System.err.println("Error opening the file.");
        }
    }

    /**
     * Determines the winner and loser of the round based on the scores.
     * Winner of the round is the player with the most scores.
     * The winner and loser are updated accordingly.
     */
    public void determineWinnerOfTheRound() {
        winnerOfTheRound = getRoundEndScore(currentPlayer) < getRoundEndScore(nextPlayer) ? nextPlayer : currentPlayer;
        loserOfTheRound = (winnerOfTheRound == currentPlayer) ? nextPlayer : currentPlayer;
    }

    /**
     * Increments the current turn number.
     */
    public void incrementTurnNum() {
        currentTurnNum++;
    }

    /**
     * Gets the current turn number.
     *
     * @return The current turn number.
     */
    public int getTurnNum() {
        return currentTurnNum;
    }

    /**
     * Gets the number of captured pairs by the specified player.
     *
     * @param p The player whose captured pairs count is requested.
     * @return The number of captured pairs.
     */
    public int getPairsCapturedNum(Player p) {
        return pairsCaptured.getOrDefault(p, 0);
    }

    /**
     * Gets the number of consecutive fours achieved by the specified player.
     *
     * @param p The player whose consecutive fours count is requested.
     * @return The number of consecutive fours.
     */
    public int getFourConsecutivesNum(Player p) {
        return fourConsecutive.getOrDefault(p, 0);
    }

    /**
     * Gets the game points of the specified player.
     *
     * @param p The player whose game points are requested.
     * @return The game points of the player.
     */
    public int getGamePoints(Player p) {
        return gamePoints.getOrDefault(p, 0);
    }

    /**
     * Calculates the total score for the specified player at the end of the round.
     *
     * @param p The player whose total score is calculated.
     * @return The total score of the player at the end of the round.
     */
    public int getRoundEndScore(Player p) {
        return getPairsCapturedNum(p) + getGamePoints(p) + getFourConsecutivesNum(p);
    }

    /**
     * Gets the current player.
     *
     * @return The current player.
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the next player.
     *
     * @return The next player.
     */
    public Player getNextPlayer() {
        return nextPlayer;
    }

    /**
     * Gets the winner of the round.
     *
     * @return The winner of the round.
     */
    public Player getWinner() {
        return winnerOfTheRound;
    }

    /**
     * Gets the loser of the round.
     *
     * @return The loser of the round.
     */
    public Player getLoser() {
        return loserOfTheRound;
    }


}
