package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

import java.util.Scanner;

public class Human extends Player {
    public Human(String name) {
        super(name);
    }

//    @Override
    public boolean makeMove(int row, int col, Round r, Board b, ComputerStrategy c, Tournament t, Activity a) {
        System.out.println("HERE AS HUMAn");
//        // If it's not the first turn, call askPositionInput from Round
//        if (r.getTurnNum() != 0) {
//            if (!r.askPositionInput(b, t.getHuman(), t.getComputer(), t, c, scanner)) {
//                // Save and quit the game
//                t.setPause();
//                return false;
//            }
//        }
////
////        // Call placeYourPiece function from the Board
        b.setBoardDimension(row, col);
        return b.placeYourPiece(r, t, a);
//        return true;
    }

}
