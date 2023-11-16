package com.example.pente_java;

import java.util.Map;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a strategy for the computer player in a game.
 * Uses various heuristics to determine the best position to place a piece on the game board.
 * @param t The tournament instance to which the strategy belongs.
 */
public class ComputerStrategy {
    private final Tournament t;
    private String bestReason = "";

    // Define directions for checking consecutive pieces
    private final int[] dx = { 1, 0, 1, 1 };
    private final int[] dy = { 0, 1, 1, -1 };
    private final String[] dirName = { "Col", "Row", "Backward Diagonal", "Forward Diagonal" };

    private final int SURE_ONE_POINT = 10;
    private final int GAME_WIN = 20;
    private final int SURE_CAPTURE_BUT_LOSE = 1;
    private final int GET_MORE_POINTS = 8;
    private final int ONE_OPEN_END = 4;
    private final int PROBABLY_CAPTURE = 6;
    private final int DONT_PUT_THERE = -15;
//
    /**
     * Constructor for the ComputerStrategy class.
     * @param t The tournament instance to which the strategy belongs.
     */
    public ComputerStrategy(Tournament t) {
        this.t = t;
    }
//
    /**
     * Converts row and column positions to a string input form like A10.
     * @param row The row position.
     * @param col The column position.
     * @return The string representation of the position.
     */
    public String convertPosToString(int row, int col) {
        // col to Alphabet equivalent
        // col starts from 1 to 19
        char colAlphabet = (char) ('A' + col - 1);

        // because row label is inverted
        String rowString = Integer.toString(20 - row);
        String inputString = String.valueOf(colAlphabet) + rowString;

        return inputString;
    }
//
    /**
     * Determines the best position on the board for the computer to place a piece.
     * Uses various heuristics to assign priorities to empty positions and selects the one with the highest priority.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param opponentPlayer The opponent player.
     * @param r The current round.
     * @return The best position in string form.
     */
    public String determineBestPosition(Board board, Player currentPlayer, Player opponentPlayer, Round r) {
        int maxPriority = -1;
        int bestRow = -1, bestCol = -1;
        int boardSize = 20;
        String finalReason = "";
        int[][] boardPriority = new int[20][20];

        for (int row = 1; row < boardSize; row++) {
            for (int col = 1; col < boardSize; col++) {
                if (board.getPiece(row, col) == '0') {
                    if (r.getTurnNum() == 2 && Math.abs(row - 10) <= 3 && Math.abs(col - 10) <= 3) {
                        boardPriority[row][col] = DONT_PUT_THERE;
                        continue;
                    }

                    List<Map.Entry<Integer, String>> results = new ArrayList<>();
                    results.add(calculateCapturePairsPriority(row, col, board, r.getCurrentPlayer(), r, true));
                    results.add(calculateEndGamePriorityWith5Consectuives(row, col, board, r.getCurrentPlayer(), r));
                    results.add(
                            calculateOpponentsEndGamePriorityWith5Consectuives(row, col, board, r.getNextPlayer(), r));
                    results.add(calculatePriorityWith4Consectuives(row, col, board, r.getCurrentPlayer(), r));
                    results.add(calculateOpponentPriorityWith4Consectuives(row, col, board, r.getNextPlayer(), r));
                    results.add(calculatePriorityWith3Consectuives(row, col, board, r.getCurrentPlayer(), r));
                    results.add(calculateOpponentPriorityWith3Consectuives(row, col, board, r.getNextPlayer(), r));
                    results.add(calculatePriorityWith2Consectuives(row, col, board, r.getCurrentPlayer(), r));
                    results.add(calculateOpponentPriorityWith2Consectuives(row, col, board, r.getNextPlayer(), r));
                    results.add(
                            calculateConsecutivesPriority(row, col, board, r.getCurrentPlayer()));
                    results.add(
                            calculateStopOpponentPriority(row, col, board, r.getCurrentPlayer(), r.getNextPlayer()));
                    results.add(
                            calculateCaptureRiskPriority(row, col, board, r.getCurrentPlayer(), r.getNextPlayer(), r));

                    int maxPForThisSpace = -1;
                    String maxReason = "";
                    for (int i = 0; i < results.size(); i++) {
                            //building 2 and 3 should have higher priority than stopping two or three
                        int p = results.get(i).getKey();

                        if (i == 2 || i == 3 || i == 1) {
                            p *= 2;
                        }
                        if (p > maxPForThisSpace) {
                            maxPForThisSpace = p;
                            maxReason = results.get(i).getValue();
                        }
                    }
                    boardPriority[row][col] = maxPForThisSpace;
                    if (maxPriority < maxPForThisSpace) {
                        maxPriority = maxPForThisSpace;
                        finalReason = maxReason;
                        bestRow = row;
                        bestCol = col;
                    }
                }
            }
        }
        String bestPos = convertPosToString(bestRow, bestCol);
        setFinalReason(bestPos + " to " + finalReason);
        return bestPos;
    }
    /**
     * Sets the final reason for the selected best position.
     * @param reason The reason for selecting the best position.
     */
    public void setFinalReason(String reason) {
        bestReason = reason;
    }
    /**
     * Gets the final reason for the selected best position.
     * @return The reason for selecting the best position.
     */
    public String getFinalReason() {
        return bestReason;
    }

    /**
     * Calculates the priority and reason for capturing pairs at a specific position.
     * @param row The row position.
     * @param col The column position.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param r The current round.
     * @param checkingOwn Indicates whether checking from own perspective.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculateCapturePairsPriority(int row, int col, Board board, Player currentPlayer,
                                                                    Round r, boolean checkingOwn) {
        int priority = 0;
        String reason = "";
        // Iterate through each direction
        char enemyPiece = currentPlayer.getColor() == 'W' ? 'B' : 'W';
        // stores the number of captures from that empty position
        int sureCaptureCount = 0;
        for (int direction = 0; direction < 4; direction++) {
            // check left from the empty space
            if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                    board.getPiece(row - dx[direction], col - dy[direction]) == enemyPiece &&
                    board.isWithinBounds(row - dx[direction] * 2, col - dy[direction] * 2) &&
                    board.getPiece(row - dx[direction] * 2, col - dy[direction] * 2) == enemyPiece) {
                // sure capture
                if (board.isWithinBounds(row - dx[direction] * 3, col - dy[direction] * 3) &&
                        board.getPiece(row - dx[direction] * 3, col - dy[direction] * 3) == currentPlayer.getColor()) {
                    sureCaptureCount++;
                    reason = reason + "Capture a pair to left " + dirName[direction] + ". ";
                    if (r.getPairsCapturedNum(currentPlayer) < 4) {
                        priority = Math.max(priority, SURE_ONE_POINT);
                    } else {
                        Player opponent = r.getCurrentPlayer() == currentPlayer ? r.getNextPlayer() : currentPlayer;
                        int opponentScore = t.getTotalScores(opponent, false);
                        int ownScore = t.getTotalScores(currentPlayer, false);

                        if ((opponentScore - ownScore) > 1) {
                            priority = Math.max(priority, SURE_CAPTURE_BUT_LOSE);
                        } else {
                            priority = Math.max(priority, GET_MORE_POINTS);
                        }
                    }
                } else {
                    // chances of capture
                    // no capture
                    if (!board.isWithinBounds(row - dx[direction] * 3, col - dy[direction] * 3)) {
                        // if cannot capture, then have to stop its chance of consecutive
                        // and for that, the priority is 4 because in case of consecutives of own piece, if there is no immediate 4 for sure consecutive
                        // then the max priority to put own piece in that empty place is set to 4
                        priority = Math.max(priority, ONE_OPEN_END);
                    }
                    // probably capture
                    else {
                        // there is a chance that it may get captured and that should be higher priority than no capture
                        // that may lead to 4 consecutives in the future
                        reason = reason + "Possibly Capture a pair to left " + dirName[direction] + ". ";
                        priority = Math.max(priority, PROBABLY_CAPTURE);
                    }
                }
            }

            // check right
            if (board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                    board.getPiece(row + dx[direction], col + dy[direction]) == enemyPiece &&
                    board.isWithinBounds(row + dx[direction] * 2, col + dy[direction] * 2) &&
                    board.getPiece(row + dx[direction] * 2, col + dy[direction] * 2) == enemyPiece) {
                // sure capture
                if (board.isWithinBounds(row + dx[direction] * 3, col + dy[direction] * 3) &&
                        board.getPiece(row + dx[direction] * 3, col + dy[direction] * 3) == currentPlayer.getColor()) {
                    sureCaptureCount++;
                    reason = reason + "Capture a pair to right " + dirName[direction] + ". ";
                } else {
                    if (!board.isWithinBounds(row + dx[direction] * 3, col + dy[direction] * 3)) {
                        // if cannot capture, then have to stop its chance of consecutive
                        // and for that, the priority is 4 because in case of consecutives of own piece, if there is no immediate 4 for sure consecutive
                        // then the max priority to put own piece in that empty place is set to 4
                        priority = Math.max(priority, ONE_OPEN_END);
                    } else {
                        // there is a chance that it may get captured and that should be a higher priority than no capture
                        // that may lead to 4 consecutives in the future;
                        reason = reason + "Possibly Capture a pair to right " + dirName[direction] + ". ";
                        priority = Math.max(priority, PROBABLY_CAPTURE);
                    }
                }
            }
        }

        // if sureCaptureCount + r->getCapturedPairs(p) is > 4
        // then we consider two cases, winning or losing
        // if winning, winning by more than 3 ? then proceed with the surecapture, else hold off capturing
        // if losing, losing by more than surecapturCount? if yes, then don't put the piece there, else chance of winning so take it
        if (sureCaptureCount != 0 && sureCaptureCount + r.getPairsCapturedNum(currentPlayer) > 4) {
            Player opponent = r.getCurrentPlayer() == currentPlayer ? r.getNextPlayer() : currentPlayer;
            int opponentScore = t.getTotalScores(opponent, false);
            int ownScore = t.getTotalScores(currentPlayer, false);

            if ((ownScore - opponentScore) >= 0 && (ownScore - opponentScore) < 3) {
                // winning but the score difference is not high enough
                // decrease the priority, we wanna delay winning, focus on getting points by consecutive
                priority = 5;
            } else {
                if ((opponentScore - ownScore) > sureCaptureCount) {
                    // no way you should place the piece there because even though you will get the points,
                    // you will lose the game
                }
                priority = DONT_PUT_THERE;
            }
        } else {
            // capture count for the player is not going to >= 5, so take the points
            // high priority

            priority = Math.max(priority, SURE_ONE_POINT * sureCaptureCount);
        }

        // what if placing the piece at the empty position leads to me getting sure captured
        // see if by placing your piece there you are setting yourself for capture or not
        // 1 2 3 4
        // consider 2 or 3 positions
        // check for 1 and 4 positions for enemy pieces
        char ownPiece = currentPlayer.getColor();

        // it does not make sense to check the following code if this function is being called from the enemy perspective
        // it is currently your turn, then enemy piece does not need to check if the enemy piece gets captured by placing its piece
        // at 2 or 3 positions
        // when calling this function from the enemy perspective, it should only care about whether the enemy can capture my pairs if
        // the enemy places its piece there
        // if it can capture, then I should do my best to avoid it by placing my piece there

        if (checkingOwn) {
            for (int direction = 0; direction < 4; direction++) {
                // empty position is in the 2nd position, E is enemy, P is currentPlayer
                // E_P_
                if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                        board.getPiece(row - dx[direction], col - dy[direction]) == enemyPiece &&
                        board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                        board.getPiece(row + dx[direction], col + dy[direction]) == ownPiece &&
                        board.isWithinBounds(row + 2 * dx[direction], col + 2 * dy[direction]) &&
                        board.getPiece(row + 2 * dx[direction], col + 2 * dy[direction]) == '0') {
                    priority = DONT_PUT_THERE;
                    reason = "will get captured. don't place there\n";
                }
                // __PE
                if (board.isWithinBounds(row + 2 * dx[direction], col + 2 * dy[direction]) &&
                        board.getPiece(row + 2 * dx[direction], col + 2 * dy[direction]) == enemyPiece &&
                        board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                        board.getPiece(row + dx[direction], col + dy[direction]) == ownPiece &&
                        board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                        board.getPiece(row - dx[direction], col - dy[direction]) == '0') {
                    priority = DONT_PUT_THERE;
                    reason = "will get captured. don't place there\n";
                }
                // _P_E
                if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                        board.getPiece(row - dx[direction], col - dy[direction]) == ownPiece &&
                        board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                        board.getPiece(row + dx[direction], col + dy[direction]) == enemyPiece &&
                        board.isWithinBounds(row - 2 * dx[direction], col - 2 * dy[direction]) &&
                        board.getPiece(row - 2 * dx[direction], col - 2 * dy[direction]) == '0') {
                    priority = DONT_PUT_THERE;
                    reason = "will get captured. don't place there\n";
                }

                // EP__
                if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                        board.getPiece(row - dx[direction], col - dy[direction]) == ownPiece &&
                        board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                        board.getPiece(row + dx[direction], col + dy[direction]) == '0' &&
                        board.isWithinBounds(row - 2 * dx[direction], col - 2 * dy[direction]) &&
                        board.getPiece(row - 2 * dx[direction], col - 2 * dy[direction]) == enemyPiece) {
                    priority = DONT_PUT_THERE;
                    reason = "will get captured. don't place there\n";
                }
            }
        }

        if (r.getCurrentPlayer() != currentPlayer) {
            reason = "Stop opponent " + reason;
        }

        return new SimpleEntry<>(priority, reason);
    }
    /**
     * Calculates the priority and reason for placing a piece to achieve four consecutive pieces in any direction.
     * Checks for existing four consecutive pieces and the possibility of forming a Tessera (open-ended four consecutive).
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculatePriorityWith4Consectuives(int row, int col, Board board,
                                                                         Player currentPlayer, Round r) {
        int priority = 0;
        String reason = "";

        int sure4ConsCount = 0;
        boolean tessera = false;
        int tesseraCount = 0;

        for (int direction = 0; direction < 4; direction++) {
            boolean isConsecutive = true;
            int leftCount = 0;
            int rightCount = 0;

            while (isConsecutive) {
                int newRow = row - dx[direction] * (leftCount + 1);
                int newCol = col - dy[direction] * (leftCount + 1);

                if (board.isWithinBounds(newRow, newCol)
                        && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                    leftCount++;
                } else {
                    isConsecutive = false;
                }
            }

            isConsecutive = true;
            while (isConsecutive) {
                int newRow = row + dx[direction] * (rightCount + 1);
                int newCol = col + dy[direction] * (rightCount + 1);

                if (board.isWithinBounds(newRow, newCol)
                        && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                    rightCount++;
                } else {
                    isConsecutive = false;
                }
            }

            if (leftCount + rightCount == 3) {
                sure4ConsCount++;
                reason += "Have 4 consecutive in " + dirName[direction] + ". ";
                boolean leftEndOpen = false;
                boolean rightEndOpen = false;

                if (board.isWithinBounds(row - dx[direction] * (leftCount + 1), col - dy[direction] * (leftCount + 1))
                        &&
                        board.isWithinBounds(row + dx[direction] * (rightCount + 1),
                                col + dy[direction] * (rightCount + 1))) {
                    reason += "Have 4 consecutive in " + dirName[direction] + ". ";
                    if (board.getPiece(row - dx[direction] * (leftCount + 1),
                            col - dy[direction] * (leftCount + 1)) == '0' &&
                            board.getPiece(row + dx[direction] * (rightCount + 1),
                                    col + dy[direction] * (rightCount + 1)) == '0') {
                        tessera = true;
                        tesseraCount++;
                    }
                } else {
                    // Handle out of bounds by treating both ends as open
                    leftEndOpen = rightEndOpen = true;
                }

                if (leftEndOpen && rightEndOpen) {
                    tessera = true;
                    tesseraCount++;
                }

            }
        }

        priority = sure4ConsCount * SURE_ONE_POINT;
        if (tessera) {
            priority += tesseraCount * SURE_ONE_POINT;
            reason += "Have Tessera Formation.";
        }

        if (!currentPlayer.equals(r.getCurrentPlayer())) {
            reason = "Stop opponent " + reason;
        }

        return new SimpleEntry<>(priority, reason);
    }
    /**
     * Calculates the priority and reason for preventing the opponent from achieving four consecutive pieces in any direction.
     * Delegates the calculation to the calculatePriorityWith4Consectuives method with opponentPlayer as the current player.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param opponentPlayer The opponent player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculateOpponentPriorityWith4Consectuives(int row, int col, Board board,
                                                                                 Player opponentPlayer, Round r) {
        return calculatePriorityWith4Consectuives(row, col, board, opponentPlayer, r);
    }
    /**
     * Calculates the priority and reason for placing a piece to achieve three consecutive pieces in any direction.
     * Checks for existing three consecutive pieces and the possibility of forming a sequence that could lead to four consecutive.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculatePriorityWith3Consectuives(int row, int col, Board board,
                                                                     Player currentPlayer, Round r) {
    int priority = 0;
    String reason = "";

    int sure3ConsCount = 0;
    boolean oneOpenEnd = false, bothOpenEnds = false;
    // Iterate through each direction
    for (int direction = 0; direction < 4; ++direction) {
        boolean isConsecutive = true;
        int leftCount = 0;
        int rightCount = 0;
        // determine consecutive count in left side
        while (isConsecutive) {
            int newRow = row - dx[direction] * (leftCount + 1);
            int newCol = col - dy[direction] * (leftCount + 1);
            if (board.isWithinBounds(newRow, newCol)
                    && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                leftCount++;
            } else {
                isConsecutive = false;
            }
        }
        isConsecutive = true;
        // determine consecutive count in right side
        while (isConsecutive) {
            int newRow = row + dx[direction] * (rightCount + 1);
            int newCol = col + dy[direction] * (rightCount + 1);
            if (board.isWithinBounds(newRow, newCol)
                    && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                rightCount++;
            } else {
                isConsecutive = false;
            }
        }

        if (leftCount + rightCount == 2) {
            // sure 3 consecutive
            sure3ConsCount++;
            // check for possibility of growing into 4 in either side
            if (board.isWithinBounds(row - dx[direction] * (leftCount + 1), col - dy[direction] * (leftCount + 1))
                    &&
                    board.isWithinBounds(
                            row + dx[direction] * (rightCount + 1), col + dy[direction] * (rightCount + 1))
                    &&
                    board.getPiece(row - dx[direction] * (leftCount + 1),
                            col - dy[direction] * (leftCount + 1)) == '0'
                    &&
                    board.getPiece(row + dx[direction] * (rightCount + 1),
                            col + dy[direction] * (rightCount + 1)) == '0') {
                reason = reason + "Have 3 consecutive in " + dirName[direction] + " with both ends open, ";
                bothOpenEnds = true;
            } else if (board.isWithinBounds(row - dx[direction] * (leftCount + 1),
                    col - dy[direction] * (leftCount + 1)) &&
                    board.getPiece(row - dx[direction] * (leftCount + 1),
                            col - dy[direction] * (leftCount + 1)) == '0') {
                reason = reason + "Have 3 consecutive in " + dirName[direction] + " with one open end, ";
                oneOpenEnd = true;
            } else if (board.isWithinBounds(row + dx[direction] * (rightCount + 1),
                    col + dy[direction] * (rightCount + 1)) &&
                    board.getPiece(row + dx[direction] * (rightCount + 1),
                            col + dy[direction] * (rightCount + 1)) == '0') {
                reason = reason + "Have 3 consecutive in " + dirName[direction] + " with one open end, ";
                oneOpenEnd = true;
            }
        }
    }

    if (oneOpenEnd) {
        priority = sure3ConsCount * ONE_OPEN_END;
    }
    if (bothOpenEnds) {
        priority = sure3ConsCount * GET_MORE_POINTS;
    }
    // prioritize getting your consecutive than blocking opponents
    if (r.getCurrentPlayer() != currentPlayer) {
        priority -= sure3ConsCount * 2;
        reason = "Stop opponent " + reason;
    }
    return new AbstractMap.SimpleEntry<>(priority, reason);
}

    /**
     * Calculates the priority and reason for preventing the opponent from achieving three consecutive pieces in any direction.
     * Delegates the calculation to the calculatePriorityWith3Consectuives method with opponentPlayer as the current player.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param opponentPlayer The opponent player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculateOpponentPriorityWith3Consectuives(int row, int col, Board board,
                                                                                 Player opponentPlayer, Round r) {
        return calculatePriorityWith3Consectuives(row, col, board, opponentPlayer, r);
    }


    /**
     * Calculates the priority and reason for placing a piece to achieve two consecutive pieces in any direction.
     * Checks for existing two consecutive pieces and the possibility of forming a sequence that could lead to four consecutive.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculatePriorityWith2Consectuives(int row, int col, Board board,
                                                                         Player currentPlayer, Round r) {
        int priority = 0;
        String reason = "";

        int sure2ConsCount = 0;
        boolean possibleCapture = false, bothOpenEnds = false;
        // Iterate through each direction
        for (int direction = 0; direction < 4; ++direction) {
            boolean isConsecutive = true;
            int leftCount = 0;
            int rightCount = 0;
            // determine consecutive count in left side
            while (isConsecutive) {
                int newRow = row - dx[direction] * (leftCount + 1);
                int newCol = col - dy[direction] * (leftCount + 1);
                if (board.isWithinBounds(newRow, newCol)
                        && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                    leftCount++;
                } else {
                    isConsecutive = false;
                }
            }
            isConsecutive = true;
            // determine consecutive count in right side
            while (isConsecutive) {
                int newRow = row + dx[direction] * (rightCount + 1);
                int newCol = col + dy[direction] * (rightCount + 1);
                if (board.isWithinBounds(newRow, newCol)
                        && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                    rightCount++;
                } else {
                    isConsecutive = false;
                }
            }

            if (leftCount + rightCount == 1) {
                // sure 2 consecutive
                sure2ConsCount++;

                // check for possibility of growing into 4 in either side, if one side is
                // blocked by enemy, then don't put there
                if (board.isWithinBounds(row - dx[direction] * (leftCount + 1), col - dy[direction] * (leftCount + 1))
                        &&
                        board.isWithinBounds(
                                row + dx[direction] * (rightCount + 1), col + dy[direction] * (rightCount + 1))
                        &&
                        board.getPiece(row - dx[direction] * (leftCount + 1),
                                col - dy[direction] * (leftCount + 1)) == '0'
                        &&
                        board.getPiece(row + dx[direction] * (rightCount + 1),
                                col + dy[direction] * (rightCount + 1)) == '0') {
                    reason = reason + "Have 2 consecutive in " + dirName[direction] + " with both ends open, ";
                    bothOpenEnds = true;
                } else if (board.isWithinBounds(row - dx[direction] * (leftCount + 1),
                        col - dy[direction] * (leftCount + 1)) &&
                        board.getPiece(row - dx[direction] * (leftCount + 1),
                                col - dy[direction] * (leftCount + 1)) == '0') {
                    possibleCapture = true;
                } else if (board.isWithinBounds(row + dx[direction] * (rightCount + 1),
                        col + dy[direction] * (rightCount + 1)) &&
                        board.getPiece(row + dx[direction] * (rightCount + 1),
                                col + dy[direction] * (rightCount + 1)) == '0') {
                    possibleCapture = true;
                }
            }
        }

        // if both ends are open and possibleCapture is true for any direction
        if (!possibleCapture && bothOpenEnds) {
            priority = sure2ConsCount * 2;
        }
        if (r.getCurrentPlayer() != currentPlayer) {
            priority -= 2;

            reason = "Stop opponent " + reason;
        }
        return new AbstractMap.SimpleEntry<>(priority, reason);
    }


    /**
     * Calculates the priority and reason for preventing the opponent from achieving two consecutive pieces in any direction.
     * Delegates the calculation to the calculatePriorityWith2Consectuives method with opponentPlayer as the current player.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param opponentPlayer The opponent player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculateOpponentPriorityWith2Consectuives(int row, int col, Board board,
                                                                                 Player opponentPlayer, Round r) {
        return calculatePriorityWith2Consectuives(row, col, board, opponentPlayer, r);
    }

    /**
     * Calculates the priority and reason for placing a piece to achieve a potential game-winning position with five consecutive pieces.
     * Checks each direction for the possibility of creating five consecutive pieces by placing a piece in the specified row and column.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason.
     */
    public Map.Entry<Integer, String> calculateEndGamePriorityWith5Consectuives(int row, int col, Board board,
                                                                                Player currentPlayer, Round r) {
        int priority = 0;
        String reason = "";

        int fiveConsCount = 0;
        // Iterate through each direction
        for (int direction = 0; direction < 4; ++direction) {
            // check to see if in the current direction, placing own piece in the empty
            // space will create a 5 consecutive
            // get the number of consecutives in the left and the number of consecutives in
            // the right
            // if the sum >= 4, game ends for sure

            boolean isConsecutive = true;
            int leftCount = 0;
            int rightCount = 0;
            // determine consecutive count in the left side
            while (isConsecutive) {
                int newRow = row - dx[direction] * (leftCount + 1);
                int newCol = col - dy[direction] * (leftCount + 1);
                if (board.isWithinBounds(newRow, newCol)
                        && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                    leftCount++;
                } else {
                    isConsecutive = false;
                }
            }
            isConsecutive = true;
            // determine consecutive count in the right side
            while (isConsecutive) {
                int newRow = row + dx[direction] * (rightCount + 1);
                int newCol = col + dy[direction] * (rightCount + 1);
                if (board.isWithinBounds(newRow, newCol)
                        && board.getPiece(newRow, newCol) == currentPlayer.getColor()) {
                    rightCount++;
                } else {
                    isConsecutive = false;
                }
            }

            if (leftCount + rightCount >= 4) {
                fiveConsCount++;
                // if opponent's score is significantly higher and if you have a chance to get 5
                // consecutive for sure, then take it
                // it does not make sense to wait for getting more points as at any point the
                // opponent can end the game and even further the score gap
                Player opponent = r.getCurrentPlayer() == currentPlayer ? r.getNextPlayer() : currentPlayer;
                int opponentScore = t.getTotalScores(opponent, false);
                int ownScore = t.getTotalScores(currentPlayer, false);

                reason = reason + " Have 5 consecutives in " + dirName[direction] + ". ";
                // if you are winning by a major difference, then not that important...delay
                // winning
                if ((ownScore - opponentScore) > 5) {
                    priority = Math.max(priority, GET_MORE_POINTS);
                } else {
                    priority = Math.max(priority, GAME_WIN);
                }

                // make it more important to create 5 by placing in the middle than in the edges
                // because placing in the edge won't account for a four consecutive, while in
                // the center
                // will get you a 5 because it had no immediate 4 to begin with
                // only for the currentPlayer because it does not make sense to consider this
                // for the opponent
                // only increase opponent's score
                if (leftCount != 0 && rightCount != 0 && r.getCurrentPlayer() == currentPlayer) {
                    priority *= 2;
                }
            }
        }

        // one five cons will end the game, and others can be used for getting points as
        // 4 consecutives
        priority = priority + SURE_ONE_POINT * (fiveConsCount - 1);
        if (r.getCurrentPlayer() != currentPlayer) {
            reason = "Stop opponent " + reason;
        }

        return new AbstractMap.SimpleEntry<>(priority, reason);
    }

    /**
     * Calculates the opponent's priority and reason for blocking a potential game-winning position with five consecutive pieces.
     * Delegates to the method calculating end game priority with five consecutives for the opponent player.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param opponentPlayer The opponent player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason for the opponent.
     */
    public Map.Entry<Integer, String> calculateOpponentsEndGamePriorityWith5Consectuives(int row, int col, Board board,
                                                                                         Player opponentPlayer, Round r) {
        return calculateEndGamePriorityWith5Consectuives(row, col, board, opponentPlayer, r);
    }


    /**
     * Calculates the priority and reason for creating chances of consecutives in different directions.
     * Checks for potential consecutive pieces in both forward and backward directions along each axis.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @return A Map.Entry containing the priority and reason for creating chances of consecutives.
     */

    public Map.Entry<Integer, String> calculateConsecutivesPriority(int row, int col, Board board,
                                                                    Player currentPlayer) {
        int priority = 0;
        String reason = "";

        for (int direction = 0; direction < 4; ++direction) {
            int ownPiecesCount = 0;
            int emptyCount = 1;

            if ((board.isWithinBounds(row + dx[direction], col + dy[direction])
                    && board.getPiece(row + dx[direction], col + dy[direction]) == '0')
                    || !(board.isWithinBounds(row + dx[direction], col + dy[direction]))) {
                for (int d = 1; d < 5; ++d) {
                    if (!board.isWithinBounds(row - d * dx[direction], col - d * dy[direction])) {
                        break;
                    }
                    if (d < 4) {
                        if (board.getPiece(row - d * dx[direction], col - d * dy[direction]) == currentPlayer
                                .getColor()) {
                            ownPiecesCount++;
                        } else if (board.getPiece(row - d * dx[direction], col - d * dy[direction]) == '0') {
                            emptyCount++;
                        } else {
                            break;
                        }
                    }
                }

                if (emptyCount + ownPiecesCount == 4 && ownPiecesCount != 3) {
                    if (priority < ownPiecesCount){
                        reason = dirName[direction];
                    }
                    priority = Math.max(priority, ownPiecesCount);

                }
            }

            ownPiecesCount = 0;
            emptyCount = 1;

            if ((board.isWithinBounds(row - dx[direction], col - dy[direction])
                    && board.getPiece(row - dx[direction], col - dy[direction]) == '0')
                    || !(board.isWithinBounds(row - dx[direction], col - dy[direction]))) {
                for (int d = 1; d < 5; ++d) {
                    if (!board.isWithinBounds(row + d * dx[direction], col + d * dy[direction])) {
                        break;
                    }
                    if (d < 4) {
                        if (board.getPiece(row + d * dx[direction], col + d * dy[direction]) == currentPlayer
                                .getColor()) {
                            ownPiecesCount++;
                        } else if (board.getPiece(row + d * dx[direction], col + d * dy[direction]) == '0') {
                            emptyCount++;
                        } else {
                            break;
                        }
                    }
                }

                if (emptyCount + ownPiecesCount == 4 && ownPiecesCount != 3) {
                    if (priority < ownPiecesCount){
                        reason = dirName[direction];
                    }
                    priority = Math.max(priority, ownPiecesCount);

                }
            }

            ownPiecesCount = 0;
            emptyCount = 0;

            if (board.isWithinBounds(row - dx[direction], col - dy[direction])) {
                if (board.getPiece(row - dx[direction], col - dy[direction]) == currentPlayer.getColor()) {
                    ownPiecesCount++;
                } else if (board.getPiece(row - dx[direction], col - dy[direction]) == '0') {
                    emptyCount++;
                }
            }

            if (emptyCount + ownPiecesCount == 1) {
                int prevOwnPiecesCount = ownPiecesCount;
                emptyCount = 0;
                ownPiecesCount = 0;

                for (int d = 1; d < 4; ++d) {
                    if (!board.isWithinBounds(row + d * dx[direction], col + d * dy[direction])) {
                        break;
                    }
                    if (d < 3) {
                        if (board.getPiece(row + d * dx[direction], col + d * dy[direction]) == currentPlayer
                                .getColor()) {
                            ownPiecesCount++;
                        } else if (board.getPiece(row + d * dx[direction], col + d * dy[direction]) == '0') {
                            emptyCount++;
                        } else {
                            break;
                        }
                    }
                }

                if (emptyCount + ownPiecesCount == 1) {
                    int totalPiecesCount = ownPiecesCount + prevOwnPiecesCount;
                    if (priority < totalPiecesCount){
                        reason = dirName[direction];
                    }
                    priority = Math.max(priority, totalPiecesCount);

                }
            }

            ownPiecesCount = 0;
            emptyCount = 0;

            if (board.isWithinBounds(row + dx[direction], col + dy[direction])) {
                if (board.getPiece(row + dx[direction], col + dy[direction]) == currentPlayer.getColor()) {
                    ownPiecesCount++;
                } else if (board.getPiece(row + dx[direction], col + dy[direction]) == '0') {
                    emptyCount++;
                }
            }

            if (emptyCount + ownPiecesCount == 1) {
                int prevOwnPiecesCount = ownPiecesCount;
                emptyCount = 0;
                ownPiecesCount = 0;

                for (int d = 1; d < 4; ++d) {
                    if (!board.isWithinBounds(row - d * dx[direction], col - d * dy[direction])) {
                        break;
                    }
                    if (d < 3) {
                        if (board.getPiece(row - d * dx[direction], col - d * dy[direction]) == currentPlayer
                                .getColor()) {
                            ownPiecesCount++;
                        } else if (board.getPiece(row - d * dx[direction], col - d * dy[direction]) == '0') {
                            emptyCount++;
                        } else {
                            break;
                        }
                    }
                }

                if (emptyCount + ownPiecesCount == 1) {
                    int totalPiecesCount = ownPiecesCount + prevOwnPiecesCount;
                    if (priority < totalPiecesCount){
                        reason = dirName[direction];
                    }
                    priority = Math.max(priority, totalPiecesCount);

                }
            }
        }
        reason = "Create chances of consecutives in " + reason;
        return new AbstractMap.SimpleEntry<>(priority, reason);
    }


    /**
     * Calculates the priority and reason for stopping the opponent from creating chances of consecutives.
     * Delegates to the calculateConsecutivesPriority method for the opponent player.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param opponentPlayer The opponent player.
     * @return A Map.Entry containing the priority and reason for stopping the opponent.
     */
    public Map.Entry<Integer, String> calculateStopOpponentPriority(int row, int col, Board board, Player currentPlayer,
                                                                    Player opponentPlayer) {
        Map.Entry<Integer, String> priorityPair = calculateConsecutivesPriority(row, col, board, opponentPlayer);
        priorityPair.setValue("Stop Opponent " + priorityPair.getValue());
        return priorityPair;
    }

    /**
     * Calculates the priority and reason for capturing risk by considering the opponent's move.
     * Delegates to the calculateCapturePairsPriority method for the opponent player.
     * @param row The row position to check.
     * @param col The column position to check.
     * @param board The game board.
     * @param currentPlayer The current player.
     * @param opponentPlayer The opponent player.
     * @param r The current round.
     * @return A Map.Entry containing the priority and reason for capturing risk.
     */
    public Map.Entry<Integer, String> calculateCaptureRiskPriority(int row, int col, Board board, Player currentPlayer,
                                                                   Player opponentPlayer, Round r) {
        // See from enemy's viewpoint, if for that position, if the enemy places its
        // piece,
        // what damage will it do, will it capture my pairs
        return calculateCapturePairsPriority(row, col, board, opponentPlayer, r, false);
    }

}
