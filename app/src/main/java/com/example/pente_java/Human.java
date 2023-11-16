package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

import java.util.Scanner;

/**
 * Represents a human player in the game.
 */
public class Human extends Player {

    /**
     * Constructor for the Human class.
     * @param name The name of the human player.
     */
    public Human(String name) {
        super(name);
    }

    /**
     * Overrides the makeMove method in the Player class.
     * Makes a move based on user input.
     * @param row The row index.
     * @param col The column index.
     * @param r The current round.
     * @param b The game board.
     * @param c The computer strategy.
     * @param t The current tournament.
     * @return True if the move is successfully made, false otherwise.
     */
    //@Override
    public boolean makeMove(int row, int col, Round r, Board b, ComputerStrategy c, Tournament t) {
        System.out.println("HERE AS HUMAN");


        // Call setBoardDimension function from the Board
        b.setBoardDimension(row, col);
        // Call placeYourPiece function from the Board
        return b.placeYourPiece(r, t);
    }
}
