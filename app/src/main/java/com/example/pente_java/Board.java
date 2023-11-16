package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

public class Board {
    private static final int BOARD_DIMENSION = 20;
    private char[][] board = new char[BOARD_DIMENSION][BOARD_DIMENSION];
    private int row;
    private int col;

    /**
     * Constructor for the Board class.
     * Initializes the board and other members.
     */
    public Board() {
        resetBoard();
    }

    /**
     * Sets the dimensions of the board.
     * @param row The number of rows.
     * @param col The number of columns.
     */
    public void setBoardDimension(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Gets the number of rows on the board.
     * @return The number of rows.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the number of columns on the board.
     * @return The number of columns.
     */
    public int getCol() {
        return col;
    }

    /**
     * Parses the input string to set the position on the board.
     * @param input The input string representing the position (e.g., "A1").
     * @return True if the position is valid, false otherwise.
     */
    public boolean parsePosition(String input) {
        char colChar = Character.toUpperCase(input.charAt(0));
        int numericEquivalent = colChar - 'A' + 1;

        if (numericEquivalent < 1 || numericEquivalent > BOARD_DIMENSION - 1) {
            // Display an error comment
            return false;
        }

        this.col = numericEquivalent;

        String rowString = input.substring(1);
        try {
            this.row = BOARD_DIMENSION - Integer.parseInt(rowString);
        } catch (NumberFormatException e) {
            // Display an error comment
            return false;
        }

        if (this.row < 1 || this.row > BOARD_DIMENSION - 1) {
            // Display an error comment
            return false;
        }

        return true;
    }

    /**
     * Checks if the specified position is within the bounds of the board.
     * @param row The row index.
     * @param col The column index.
     * @return True if the position is within bounds, false otherwise.
     */
    public boolean isWithinBounds(int row, int col) {
        return row >= 1 && row <= BOARD_DIMENSION - 1 && col >= 1 && col <= BOARD_DIMENSION - 1;
    }

    /**
     * Checks the validity of a position in the game.
     * @param r The current round.
     * @return True if the position is valid, false otherwise.
     */
    public boolean checkValidity(Round r) {
        // When is a position valid?
        // Private members 'row' and 'col' hold the inputted values.
        // The position must be within bounds and empty.

        // For the second turn of White ('W'), it must be placed 3 steps away from the
        // center.
        // In the 'ComputerStrategy' class, you can account for this rule by giving
        // negative priorities to positions within 3 intersections of the center.

        if (r.getTurnNum() == 2 && r.getCurrentPlayer().getColor() == 'W') {
            // If a human inputs within 3 steps from the center, re-ask for input.
            if (Math.abs(row - 10) <= 3 && Math.abs(col - 10) <= 3) {

                return false;
            }
        }

        // Case when it is valid
        return true;
    }

    /**
     * Checks for pairs in a specified direction and captures them.
     * @param r The current round.
     * @param p The current player.
     * @param e The opponent player.
     * @return True if the game ends after capturing pairs, false otherwise.
     */
    public boolean checkAndCapture(Round r, Player p, Player e) {
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };
        boolean gameEnd = false;

        for (int i = 0; i < 4; i++) {
            int dx = directions[i][0];
            int dy = directions[i][1];

            if (checkForPairs(p, e, row, col, dx, dy)) {
                if (capturePairs(r, p, dx, dy)) {
                    gameEnd = true;
                }
            }

            if (checkForPairs(p, e, row, col, -dx, -dy)) {
                if (capturePairs(r, p, -dx, -dy)) {
                    gameEnd = true;
                }
            }
        }

        return gameEnd;
    }

    /**
     * Checks for pairs in a specified direction.
     * @param p The current player.
     * @param e The opponent player.
     * @param row The row index.
     * @param col The column index.
     * @param dx The change in row.
     * @param dy The change in column.
     * @return True if pairs are found, false otherwise.
     */
    public boolean checkForPairs(Player p, Player e, int row, int col, int dx, int dy) {

        int nextRow1 = row + dx;
        int nextCol1 = col + dy;
        int nextRow2 = row + 2 * dx;
        int nextCol2 = col + 2 * dy;
        int nextRow3 = row + 3 * dx;
        int nextCol3 = col + 3 * dy;

        if (!isWithinBounds(nextRow1, nextCol1) || !isWithinBounds(nextRow2, nextCol2)
                || !isWithinBounds(nextRow3, nextCol3)) {
            return false;
        }

        if (board[nextRow1][nextCol1] == e.getColor() &&
                board[nextRow2][nextCol2] == e.getColor() &&
                board[nextRow3][nextCol3] == p.getColor()) {
            return true;
        }

        return false;
    }

    /**
     * Captures pairs in a specified direction.
     * @param r The current round.
     * @param p The current player.
     * @param dx The change in row.
     * @param dy The change in column.
     * @return True if the specified player captures pairs, false otherwise.
     */
    public boolean capturePairs(Round r, Player p, int dx, int dy) {

        int nextRow1 = row + dx;
        int nextCol1 = col + dy;
        int nextRow2 = row + 2 * dx;
        int nextCol2 = col + 2 * dy;

        board[nextRow1][nextCol1] = '0';
        board[nextRow2][nextCol2] = '0';

        r.setPairsCapturedNum(p);

        if (r.getPairsCapturedNum(p) == 5) {
            return true;
        }

        return false;
    }

    /**
     * Checks for five consecutive pieces in all directions.
     * @param r The current round.
     * @param t The current tournament.
     * @return True if five consecutive pieces are found, false otherwise.
     */
    public boolean checkForFive(Round r, Tournament t) {
        char currentPlayerPiece = board[row][col]; // Current player's piece

        // Define directions: horizontal, vertical, and both diagonals
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };

        // Check for five consecutive pieces in each direction
        for (int i = 0; i < 4; i++) {
            // x-direction
            int dx = directions[i][0];
            // y-direction
            int dy = directions[i][1];

            // Initialize the count to 1 (counting the current piece)
            int count = 1;

            // Check in the positive direction
            int x = row + dx;
            int y = col + dy;
            while (isWithinBounds(x, y) && board[x][y] == currentPlayerPiece) {
                count++;
                x += dx;
                y += dy;
            }

            // Check in the negative direction
            x = row - dx;
            y = col - dy;
            while (isWithinBounds(x, y) && board[x][y] == currentPlayerPiece) {
                count++;
                x -= dx;
                y -= dy;
            }

            if (count >= 5) {
                // The 5 in a row cannot be considered for 4 in a row, so cancel that
                r.setFourConsecutive(r.getCurrentPlayer(), -1);
                r.setGamePoints(r.getCurrentPlayer(), t);
                return true;
            }
        }
        // No five consecutive pieces found
        return false;
    }

    /**
     * Counts the number of consecutive fours throughout the board.
     * @param r The current round.
     * @param piece The piece to check for consecutive fours.
     * @param p The current player.
     * @return The number of consecutive fours.
     */
    public int checkForFours(Round r, char piece, Player p) {
        int foursCount = 0;

        // Define directions: horizontal, vertical, and both diagonals
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 1, 1 }, { 1, -1 } };

        // Check in horizontal and vertical directions
        for (int d = 0; d < 4; d++) {
            // Iterate through rows and columns
            for (int i = 1; i < BOARD_DIMENSION; i++) {
                for (int j = 1; j < BOARD_DIMENSION; j++) {
                    boolean consecutive4 = true;

                    // Check four consecutive pieces in the current direction
                    for (int k = 0; k < 4; k++) {
                        int newRow = i + k * directions[d][0];
                        int newCol = j + k * directions[d][1];

                        // Check if the indices are within bounds
                        if (newRow >= 1 && newRow <= BOARD_DIMENSION - 1 && newCol >= 1
                                && newCol <= BOARD_DIMENSION - 1) {
                            if (board[newRow][newCol] != piece) {
                                consecutive4 = false;
                                break;
                            }
                        } else {
                            consecutive4 = false;
                            break;
                        }
                    }

                    if (consecutive4) {
                        foursCount++;

                        j += 4;
                        // For diagonals, we need to increment the row value by 4 too
                        if (d == 2 || d == 3) {
                            i += 4;
                        }
                    }
                }
            }
        }

        r.setFourConsecutive(p, r.getFourConsecutivesNum(p) + foursCount);
        System.out.println("4 consecutives for " + p.getName() + " " + foursCount);

        return foursCount;
    }

    /**
     * Gets the piece at the specified row and column on the board.
     * @param row The row index.
     * @param col The column index.
     * @return The piece at the specified position.
     */
    public char getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Gets the dimension of the game board.
     * @return The dimension of the game board.
     */
    public int getBoardDimension() {
        return BOARD_DIMENSION;
    }

    /**
     * Sets the piece at the specified row and column on the board.
     * @param row The row index.
     * @param col The column index.
     * @param c The piece to set at the specified position.
     */
    public void setPiece(int row, int col, char c) {
        board[row][col] = c;
    }

    /**
     * Places the piece in the specified row and column on the board.
     * Checks for pairs to capture, 5 consecutive pieces, and updates the game log.
     * @param r The current round.
     * @param t The tournament.
     * @return True if the piece is successfully placed, false otherwise.
     */
    public boolean placeYourPiece(Round r, Tournament t) {
        // Place the piece in the specified row and column on the board

        if (r.getTurnNum() == 0 && r.getCurrentPlayer().getColor() == 'W') {
            board[10][10] = 'W';
        } else {
            board[this.row][this.col] = r.getCurrentPlayer().getColor();

            // Check for pairs to capture
            if (checkAndCapture(r, r.getCurrentPlayer(), r.getNextPlayer())) {
                // Count the fours and update in the round
                checkForFours(r, r.getCurrentPlayer().getColor(), r.getCurrentPlayer());
                checkForFours(r, r.getNextPlayer().getColor(), r.getNextPlayer());
                r.changeTurn();
                return false;
            }

            // Check for 5 consecutive pieces, if found, the game terminates
            if (checkForFive(r, t)) {
                checkForFours(r, r.getCurrentPlayer().getColor(), r.getCurrentPlayer());
                checkForFours(r, r.getNextPlayer().getColor(), r.getNextPlayer());
                r.changeTurn();
                return false;
            }
        }

        r.changeTurn();
        // ((PenteBoard) a).initBoard();
        return true;
    }

    /**
     * Resets the game board to its initial state.
     */
    public void resetBoard() {
        // add col labels
        for (int i = 1; i < BOARD_DIMENSION; i++) {
            board[0][i] = (char) ('A' + i - 1);
        }
        for (int i = 1; i < BOARD_DIMENSION; i++) {
            for (int j = 1; j < BOARD_DIMENSION; j++) {
                // if (i == 10 && j == 10){
                //     board[i][j] = 'W';
                // }
                // else if (i == 17 && j == 10){
                //     board[i][j] = 'B';
                // }
                // else{
                board[i][j] = '0';
                // }

            }
        }
    }
}
