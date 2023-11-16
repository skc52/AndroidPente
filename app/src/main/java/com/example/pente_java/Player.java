package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

import java.util.Scanner;

/**
 * Represents a player in the game.
 */
public abstract class Player {
    private String name;
    private char color;
    private int resourceId;

    /**
     * Constructor for the Player class.
     * @param name The name of the player.
     */
    public Player(String name) {
        this.name = name;
    }

    /**
     * Abstract method for making a move in the game.
     * @param row The row index.
     * @param col The column index.
     * @param r The current round.
     * @param b The game board.
     * @param c The computer strategy.
     * @param t The current tournament.
     * @return True if the move is successfully made, false otherwise.
     */
    public abstract boolean makeMove(int row, int col, Round r, Board b, ComputerStrategy c, Tournament t);

    /**
     * Gets the name of the player.
     * @return The name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color of the player.
     * @return The color of the player.
     */
    public char getColor() {
        return color;
    }

    /**
     * Sets the color of the player.
     * @param c The color to set.
     */
    public void setColor(char c) {
        color = c;
    }

    /**
     * Sets the background resource ID for the player.
     * @param resourceId The resource ID to set.
     */
    public void setBackground(int resourceId){
        this.resourceId = resourceId;
    }

    /**
     * Gets the background resource ID for the player.
     * @return The background resource ID.
     */
    public int getBackground(){
        return resourceId;
    }
}
