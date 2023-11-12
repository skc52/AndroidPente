package com.example.pente_java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.Serializable;
public class Tournament  implements Serializable {
    private Map<Player, Integer> loadedScores;

    private Player human;
    private Player computer;
    private List<Round> rounds;
    private boolean pauseState;

    public Tournament() {
        loadedScores = new HashMap<>();
        rounds = new ArrayList<>();
        pauseState = false;
    }
//
    public Round createANewRound() {
        Round round = new Round();
        rounds.add(round);
        return round;
    }
//
    public int getTotalScores(Player p, boolean saving) {
        int totalScore = 0;
        int roundsCount = rounds.size();

        for (int i = 0; i < roundsCount; i++) {
            Round r = rounds.get(i);

            // If saving is true and it's the current round, then don't include its score
//            if (saving && i == roundsCount - 1) {
//                break;
//            }

            totalScore += r.getPairsCapturedNum(p);
            totalScore += r.getFourConsecutivesNum(p);
            totalScore += r.getGamePoints(p);
        }

//         Include the loaded scores from saved games
        if (loadedScores.containsKey(p)) {
            totalScore += loadedScores.get(p);
        }

        return totalScore;
    }

    public int[] getFinalScores(Player p, boolean saving) {

        int totalCaptureScore = 0;
        int totalFourConsScore = 0;
        int totalFiveConsScore = 0;
        int roundsCount = rounds.size();

        for (int i = 0; i < roundsCount; i++) {
            Round r = rounds.get(i);

            // If saving is true and it's the current round, then don't include its score
//            if (saving && i == roundsCount - 1) {
//                break;
//            }

            totalCaptureScore += r.getPairsCapturedNum(p);
            totalFourConsScore += r.getFourConsecutivesNum(p);
            totalFiveConsScore += r.getGamePoints(p);
        }


        int[] allFinalScores = {loadedScores.get(p)+totalCaptureScore+ totalFiveConsScore + totalFourConsScore, totalCaptureScore, totalFourConsScore, totalFiveConsScore};
        return allFinalScores;
    }

//
//    public void showTotalScoresForBoth() {
//        System.out.println("Showing Tournament Scores:");
//        System.out.println("Human: " + getTotalScores(human, false));
//        System.out.println("Computer: " + getTotalScores(computer, false));
//    }
//
//    public void announceWinner() {
//        Player winner = null;
//        int humanTotalScores = getTotalScores(human, false);
//        int computerTotalScores = getTotalScores(computer, false);
//
//        if (humanTotalScores > computerTotalScores) {
//            winner = human;
//        } else if (humanTotalScores < computerTotalScores) {
//            winner = computer;
//        }
//
//        if (winner != null) {
//            System.out.println(winner.getName() + " won the tournament");
//        } else {
//            System.out.println("It's a draw");
//        }
//    }
//
//    public void displayScoresForAllRounds() {
//        System.out.println(
//                "--------------------------------------------------------------------------------------------");
//        System.out.println("DISPLAYING TOURNAMENT STATS BY ROUND");
//
//        for (int i = 0; i < rounds.size(); i++) {
//            System.out.println("Round " + (i + 1) + " scores");
//
//            System.out.println(human.getName() + " : " +
//                    "Pairs captured = " + rounds.get(i).getPairsCapturedNum(human) +
//                    " Four consecutives = " + rounds.get(i).getFourConsecutivesNum(human) +
//                    " Game Points " + rounds.get(i).getGamePoints(human));
//
//            System.out.println(computer.getName() + " : " +
//                    "Pairs captured = " + rounds.get(i).getPairsCapturedNum(computer) +
//                    " Four consecutives = " + rounds.get(i).getFourConsecutivesNum(computer) +
//                    " Game Points " + rounds.get(i).getGamePoints(computer));
//
//            Player roundWinner = rounds.get(i).getWinner();
//            if (roundWinner != null) {
//                System.out.println("ROUND WINNER " + roundWinner.getName());
//            } else {
//                System.out.println("ROUND WAS A DRAW");
//            }
//
//            System.out.println(
//                    "--------------------------------------------------------------------------------------------");
//        }
//    }
//
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


    public void setTotalScore(int humanScore, int comScore) {
        loadedScores.put(human, humanScore);
        loadedScores.put(computer, comScore);
    }
//
    public void setUpPlayers(Player h, Player c) {
        human = h;
        computer = c;
    }
//
    public void setPause() {
        pauseState = !pauseState;
    }
//
    public Player getHuman() {
        return human;
    }

    public Player getComputer() {
        return computer;
    }
//
    public int getRoundsCount() {
        return rounds.size();
    }
//
//    public Player getPreviousWinner() {
//        // Check if there is at least one round in the tournament
//        if (getRoundsCount() > 1) {
//            // Get the index of the previous round
//            int prevRoundIndex = getRoundsCount() - 2;
//
//            // Check if the index is valid
//            if (prevRoundIndex >= 0) {
//                // Get the winner of the previous round
//                return rounds.get(prevRoundIndex).getWinner();
//            }
//        }
//
//        // Return null if there is no previous round or no winner
//        return null;
//    }
//
//    public Player getPreviousLoser() {
//        // Check if there is at least one round in the tournament
//        if (getRoundsCount() > 1) {
//            // Get the index of the previous round
//            int prevRoundIndex = getRoundsCount() - 2;
//
//            // Check if the index is valid
//            if (prevRoundIndex >= 0) {
//                // Get the loser of the previous round
//                return rounds.get(prevRoundIndex).getLoser();
//            }
//        }
//
//        // Return null if there is no previous round or no loser
//        return null;
//    }
//
//    public boolean getPause() {
//        return pauseState;
//    }
}

