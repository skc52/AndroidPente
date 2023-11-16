package com.example.pente_java;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Scanner;
/**
 * Represents a computer player in the game.
 */
public class Computer extends Player {

    /**
     * Constructor for the Computer class.
     * @param name The name of the computer player.
     */
    public Computer(String name) {
        super(name);
    }

    /**
     * Overrides the makeMove method in the Player class.
     * Makes a move based on the determined best position by the ComputerStrategy.
     * @param row The row index.
     * @param col The column index.
     * @param r The current round.
     * @param b The game board.
     * @param c The computer strategy.
     * @param t The current tournament.
     * @return True if the move is successfully made, false otherwise.
     */
    @Override
    public boolean makeMove(int row, int col, Round r, Board b, ComputerStrategy c, Tournament t) {
        String inputString;

        // First, ask if the user wants to save and quit the game
        // Then, determine the position from the ComputerStrategy class
        if (r.getTurnNum() != 0) {
            inputString = c.determineBestPosition(b, r.getCurrentPlayer(), r.getNextPlayer(), r);
            b.parsePosition(inputString);
            System.out.println("Computer placed at " + inputString + " to " + c.getFinalReason());
        }
        return b.placeYourPiece(r, t);
    }
}

