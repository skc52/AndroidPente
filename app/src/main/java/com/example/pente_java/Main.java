package com.example.pente_java;

import java.util.Scanner;

public class Main {
//    public static void main(String[] args) {
//        Tournament tournament = new Tournament();
//
//        // Get the name of the human
//        String humanName = "Human";
//        System.out.println("Welcome to Pente, " + humanName);
//
//        // Create the human player
//        Player human = new Human(humanName);
//
//        // Create the computer player
//        String computerName = "ROBOT";
//        Player computer = new Computer(computerName);
//
//        // Setup players for the tournament
//        tournament.setUpPlayers(human, computer);
//
//        // Create the board
//        Board board = new Board();
//
//        // Create ComputerStrategy object
//        ComputerStrategy c = new ComputerStrategy(tournament);
//
//        // Ask if the user wants to load a game
//        char loader;
//        Scanner scanner = new Scanner(System.in);
//
//        do {
//            System.out.println("Do you want to load a game? Enter y/Y for yes or n/N for no");
//            loader = Character.toLowerCase(scanner.next().charAt(0));
//        } while (loader != 'y' && loader != 'n');
//
//        boolean wishToContinue = true;
//        while (wishToContinue) {
//            Round r = tournament.createANewRound();
//            if (loader == 'y') {
//                System.out.println("Enter the name of the file that you want to load the game from");
//                String filename = scanner.next();
//
//                // If filename does not have .txt extension, add it
//                if (filename.length() > 4) {
//                    if (!filename.substring(filename.length() - 4).equals(".txt")) {
//                        filename += ".txt";
//                    }
//                }
//
//                if (!tournament.loadGame(filename, board, r)) {
//                    scanner.close();
//                    return;
//                }
//
//                // Set current player
//                r.loadRound(filename, human, computer, tournament);
//                loader = 'n';
//            } else {
//                // Start the round
//                r.startRound(tournament, board, scanner);
//            }
//            gameLoop(r, board, human, computer, tournament, c, scanner);
//            if (!tournament.getPause()) {
//                wishToContinue = r.askToContinueGame(scanner);
//            } else {
//                wishToContinue = false;
//            }
//        }
//
//        // Tournament is over and not paused for serialization
//        if (!tournament.getPause()) {
//            // tournament.displayScoresForAllRounds();
//            tournament.showTotalScoresForBoth();
//            tournament.announceWinner();
//        }
//
//        scanner.close();
//
//    }
//
//    public static void gameLoop(Round r, Board b, Player human, Player computer, Tournament t, ComputerStrategy c,
//                                Scanner scanner) {
//        b.displayBoard(r, t);
//        boolean gameEnded = false;
//        while (!gameEnded) {
//            r.changeTurn(b, human, computer, t);
//
//            // Game won check?
//            if (!r.getCurrentPlayer().makeMove(r, b, c, t, scanner)) {
//                gameEnded = true;
//            }
//            if (!t.getPause()) {
//                b.displayBoard(r, t);
//            }
//        }
//
//        // Display scores for both round and the overall tournament
//        if (!t.getPause()) {
//            r.displayClosingStats();
//            t.showTotalScoresForBoth();
//        }
//    }
}
