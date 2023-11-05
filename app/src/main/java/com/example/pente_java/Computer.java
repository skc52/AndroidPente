package com.example.pente_java;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Scanner;

public class Computer extends Player {
    public Computer(String name) {
        super(name);
    }

    @Override
    public boolean makeMove(int row, int col, Round r, Board b, ComputerStrategy c, Tournament t, Activity a) {
        System.out.println("HERE AS c");

        String inputString;
//
        // First, ask if the user wants to save and quit the game
        // Then, determine the position from the ComputerStrategy class
        if (r.getTurnNum() != 0) {
//            if (!r.askPositionInput(b, t.getHuman(), t.getComputer(), t, c, scanner)) {
//                // The game will be saved and quit
//                t.setPause();
//                return false;
//            }

            inputString = c.determineBestPosition(b, r.getCurrentPlayer(), r.getNextPlayer(), r);
            b.parsePosition(inputString);
            Log.d( "ss","Computer placed at " + inputString + " to " + c.getFinalReason());

            System.out.println("Computer placed at " + inputString + " to " + c.getFinalReason());
        }
//
//        // Then, place the piece in the position provided by the strategy
//        b.setBoardDimension(row, col);

        return b.placeYourPiece(r, t, a);

//        return true;
    }
}
