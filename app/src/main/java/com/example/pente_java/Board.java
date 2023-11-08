package com.example.pente_java;

import android.app.Activity;
import android.content.Context;

public class Board {
    private static final int BOARD_DIMENSION = 20;
    private char[][] board = new char[BOARD_DIMENSION][BOARD_DIMENSION];
    private int row;
    private int col;

    public Board() {
        // Constructor implementation in Java
        // Initialize the board and other members here if needed
        resetBoard();
    }
    public void setBoardDimension(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }
    public boolean parsePosition(String input) {
        char colChar = Character.toUpperCase(input.charAt(0));
        int numericEquivalent = colChar - 'A' + 1;

        if (numericEquivalent < 1 || numericEquivalent > BOARD_DIMENSION - 1) {
            showComment("Enter column from A - S only");
            return false;
        }

        this.col = numericEquivalent;

        String rowString = input.substring(1);
        try {
            this.row = BOARD_DIMENSION - Integer.parseInt(rowString);
        } catch (NumberFormatException e) {
            showComment("Invalid row position. Enter row between 1 to " + (BOARD_DIMENSION - 1));
            return false;
        }

        if (this.row < 1 || this.row > BOARD_DIMENSION - 1) {
            showComment("Invalid row position. Enter row between 1 to " + (BOARD_DIMENSION - 1));
            return false;
        }

        return true;
    }
//
    public void showComment(String comment) {
        System.out.println("Error: " + comment);
    }
//
    public boolean isWithinBounds(int row, int col) {
        // Assuming BOARD_DIMENSION is the maximum valid index (e.g., 19)
        return row >= 1 && row <= BOARD_DIMENSION - 1 && col >= 1 && col <= BOARD_DIMENSION - 1;
    }

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
                showComment(
                        "White cannot put its piece within 3 steps of the center on its second turn. Re-input your position.");
                return false;
            }
        }

        // Case when it is valid
//        if (isWithinBounds(row, col) && getPiece(row, col) == '0') {
//            return true;
//        }
//
//        // Case when it is out of bounds
//        if (!isWithinBounds(row, col)) {
//            showComment("Out of Bounds");
//            return false;
//        }

        // We would have returned from this function if the position was valid or out of
        // bounds,
        // so at this point, the position is not empty, which accounts for its
        // invalidity.
//        showComment("Position not empty!");
        return true;
    }

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
//
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
//
        return false;
    }
//
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
//
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
//
//    // Member function to count the number of consecutive 4s throughout the board
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
//
//    // Member function to display the current state of the game board along with
//    // player information
//    public void displayBoard(Round r, Tournament t) {
//        System.out.println(
//                "----------------------------------------------------------------------------------------------------------");
//
//        for (int i = 0; i < BOARD_DIMENSION; i++) {
//            // Display row labels separately as they are not stored in the board
//            if (i != 0) {
//                if (BOARD_DIMENSION - i <= 9) {
//                    System.out.print(" ");
//                }
//                System.out.print(BOARD_DIMENSION - i + " ");
//            } else {
//                System.out.print("   ");
//            }
//
//            // Column labels are stored in the board
//            for (int j = 1; j < BOARD_DIMENSION; j++) {
//                if (board[i][j] == '0') {
//                    System.out.print("- ");
//                } else {
//                    System.out.print(board[i][j] + " ");
//                }
//            }
//
//            if (i == 10) {
//                System.out.print("      ");
//                System.out.print(r.getCurrentPlayer().getName() + "'s Color is " + r.getCurrentPlayer().getColor());
//                System.out.print(". Pairs captured = " + r.getPairsCapturedNum(r.getCurrentPlayer()));
//                System.out.print(". Tournament Score = " + t.getTotalScores(r.getCurrentPlayer(), true));
//            }
//            if (i == 11) {
//                System.out.print("      ");
//                System.out.print(r.getNextPlayer().getName() + "'s Color is " + r.getNextPlayer().getColor());
//                System.out.print(". Pairs captured = " + r.getPairsCapturedNum(r.getNextPlayer()));
//                System.out.print(". Tournament Score = " + t.getTotalScores(r.getNextPlayer(), true));
//            }
//            if (i == 12) {
//                System.out.print("      ");
//                System.out.print("Next player is " + r.getNextPlayer().getName());
//            }
//            if (i == 13) {
//                System.out.print("      ");
//                System.out.print("Turn num is " + r.getTurnNum());
//            }
//
//            System.out.println();
//        }
//        System.out.println(
//                "----------------------------------------------------------------------------------------------------------");
//    }
//
    public char getPiece(int row, int col) {
        return board[row][col];
    }

    public int getBoardDimension() {
        return BOARD_DIMENSION;
    }
//
    public void setPiece(int row, int col, char c) {
        board[row][col] = c;
    }
//
    public boolean placeYourPiece(Round r, Tournament t, Activity a) {
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
//                r.determineWinnerOfTheRound();
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
//        ((PenteBoard) a).initBoard();
        return true;
    }
//
    public void resetBoard() {
        // add col labels
        for (int i = 1; i < BOARD_DIMENSION; i++) {
            board[0][i] = (char) ('A' + i - 1);
        }
        for (int i = 1; i < BOARD_DIMENSION; i++) {
            for (int j = 1; j < BOARD_DIMENSION; j++) {
//                if (i == 10 && j == 10){
//                    board[i][j] = 'W';
//                }
//                else if (i == 17 && j == 10){
//                    board[i][j] = 'B';
//                }
//                else{
                    board[i][j] = '0';
//                }

            }
        }
    }
}
