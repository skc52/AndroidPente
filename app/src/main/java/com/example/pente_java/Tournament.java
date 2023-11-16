package com.example.pente_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Tournament implements Serializable {
    private Map<Player, Integer> loadedScores;
    private Player human;
    private Player computer;
    private List<Round> rounds;
    private boolean pauseState;

    /**
     * Constructs a new Tournament object.
     */
    public Tournament() {
        loadedScores = new HashMap<>();
        rounds = new ArrayList<>();
        pauseState = false;
    }

    /**
     * Creates a new round and adds it to the list of rounds.
     *
     * @return The newly created round.
     */
    public Round createANewRound() {
        Round round = new Round();
        rounds.add(round);
        return round;
    }

    /**
     * Gets the total scores of the specified player throughout the tournament.
     *
     * @param p      The player whose total scores are requested.
     * @param saving Indicates whether the scores are being calculated for saving purposes.
     * @return The total scores of the player.
     */
    public int getTotalScores(Player p, boolean saving) {
        int totalScore = 0;
        int roundsCount = rounds.size();

        for (int i = 0; i < roundsCount; i++) {
            Round r = rounds.get(i);

            totalScore += r.getPairsCapturedNum(p);
            totalScore += r.getFourConsecutivesNum(p);
            totalScore += r.getGamePoints(p);
        }

        // Include the loaded scores from saved games
        if (loadedScores.containsKey(p)) {
            totalScore += loadedScores.get(p);
        }

        return totalScore;
    }

    /**
     * Gets the final scores of the specified player in different categories.
     *
     * @param p      The player whose final scores are requested.
     * @param saving Indicates whether the scores are being calculated for saving purposes.
     * @return An array containing the total final scores, captured pairs score, four consecutives score, and five consecutives score.
     */
    public int[] getFinalScores(Player p, boolean saving) {
        int totalCaptureScore = 0;
        int totalFourConsScore = 0;
        int totalFiveConsScore = 0;
        int roundsCount = rounds.size();

        for (int i = 0; i < roundsCount; i++) {
            Round r = rounds.get(i);

            totalCaptureScore += r.getPairsCapturedNum(p);
            totalFourConsScore += r.getFourConsecutivesNum(p);
            totalFiveConsScore += r.getGamePoints(p);
        }

        int[] allFinalScores = {loadedScores.get(p) + totalCaptureScore + totalFiveConsScore + totalFourConsScore,
                totalCaptureScore, totalFourConsScore, totalFiveConsScore};
        return allFinalScores;
    }

    /**
     * Loads game data into the board and round.
     *
     * @param gameData The game data to be loaded.
     * @param b        The board to load the data into.
     * @param r        The round to load the data into.
     * @return True if loading is successful, false otherwise.
     */
    public boolean loadGame(String gameData, Board b, Round r) {
        try {
            // Find and skip the "Board:" line
            String updatedGameData = gameData.replaceAll("\n", "");
            int boardIndex = updatedGameData.indexOf("Board:");
            if (boardIndex != -1) {
                boardIndex += 6; // Skip "Board:"
            } else {
                System.err.println("Error: Board data not found in the game data.");
                return false;
            }

            // Initialize the board with the data
            int turnNum = 0;
            for (int i = 0; i < 19; i++) {
                // Read a line with the board data
                String line = updatedGameData.substring(boardIndex, boardIndex + 19);
                boardIndex += 19;

                if (line.length() < 19) {
                    System.err.println("Error: Insufficient characters in the board data.");
                    return false;
                }

                // Store the characters in the board
                for (int j = 0; j < 19; j++) {
                    if (line.charAt(j) != 'O') {
                        // Since the board content starts from 1 row and 1 col
                        b.setPiece(i + 1, j + 1, line.charAt(j));
                        turnNum++;
                    } else {
                        b.setPiece(i + 1, j + 1, '0');
                    }
                }
            }

            r.setTurnNum(turnNum);

            return true;
        } catch (Exception e) {
            System.err.println("Error loading game data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Sets the total score for both players.
     *
     * @param humanScore The total score of the human player.
     * @param comScore   The total score of the computer player.
     */
    public void setTotalScore(int humanScore, int comScore) {
        loadedScores.put(human, humanScore);
        loadedScores.put(computer, comScore);
    }

    /**
     * Sets up the human and computer players for the tournament.
     *
     * @param h The human player.
     * @param c The computer player.
     */
    public void setUpPlayers(Player h, Player c) {
        human = h;
        computer = c;
    }

    /**
     * Toggles the pause state of the tournament.
     */
    public void setPause() {
        pauseState = !pauseState;
    }

    /**
     * Gets the human player.
     *
     * @return The human player.
     */
    public Player getHuman() {
        return human;
    }

    /**
     * Gets the computer player.
     *
     * @return The computer player.
     */
    public Player getComputer() {
        return computer;
    }

    /**
     * Gets the number of rounds played in the tournament.
     *
     * @return The number of rounds.
     */
    public int getRoundsCount() {
        return rounds.size();
    }
}
