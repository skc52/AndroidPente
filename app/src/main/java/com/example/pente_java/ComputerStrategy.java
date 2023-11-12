package com.example.pente_java;

import java.util.Map;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

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
    public ComputerStrategy(Tournament t) {
        this.t = t;
    }
//
//    // Convert the best position to a string input form like A10
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
//    // Determine the best position on the board for the computer to place a piece
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
                        int p = results.get(i).getKey();
                        if (i == 2 || i == 3 || i == 4) {
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
//
//    // Setter for bestReason
    public void setFinalReason(String reason) {
        bestReason = reason;
    }
//
//    // Getter for bestReason
    public String getFinalReason() {
        return bestReason;
    }
//
//    // Implement the remaining helper methods similar to how they are defined in C++
    public Map.Entry<Integer, String> calculateCapturePairsPriority(int row, int col, Board board, Player currentPlayer,
                                                                    Round r, boolean checkingOwn) {
        int priority = 0;
        String reason = "";

        char enemyPiece = (currentPlayer.getColor() == 'W') ? 'B' : 'W';
        int sureCaptureCount = 0;

        for (int direction = 0; direction < 4; direction++) {
            if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                    board.getPiece(row - dx[direction], col - dy[direction]) == enemyPiece &&
                    board.isWithinBounds(row - dx[direction] * 2, col - dy[direction] * 2) &&
                    board.getPiece(row - dx[direction] * 2, col - dy[direction] * 2) == enemyPiece) {
                if (board.isWithinBounds(row - dx[direction] * 3, col - dy[direction] * 3) &&
                        board.getPiece(row - dx[direction] * 3, col - dy[direction] * 3) == currentPlayer.getColor()) {
                    sureCaptureCount++;
                    reason += "Capture a pair to the left " + dirName[direction] + ". ";
                    if (r.getPairsCapturedNum(currentPlayer) < 4) {
                        priority = Math.max(priority, SURE_ONE_POINT);
                    } else {
                        Player opponent = (r.getCurrentPlayer() == currentPlayer) ? r.getNextPlayer() : currentPlayer;
                        int opponentScore = t.getTotalScores(opponent, false);
                        int ownScore = t.getTotalScores(currentPlayer, false);
                        if ((opponentScore - ownScore) > 1) {
                            priority = Math.max(priority, SURE_CAPTURE_BUT_LOSE);
                        } else {
                            priority = Math.max(priority, GET_MORE_POINTS);
                        }
                    }
                } else {
                    if (!board.isWithinBounds(row - dx[direction] * 3, col - dy[direction] * 3)) {
                        priority = Math.max(priority, ONE_OPEN_END);
                    } else {
                        reason += "Possibly Capture a pair to the left " + dirName[direction] + ". ";
                        priority = Math.max(priority, PROBABLY_CAPTURE);
                    }
                }
            }

            if (board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                    board.getPiece(row + dx[direction], col + dy[direction]) == enemyPiece &&
                    board.isWithinBounds(row + dx[direction] * 2, col + dy[direction] * 2) &&
                    board.getPiece(row + dx[direction] * 2, col + dy[direction] * 2) == enemyPiece) {
                if (board.isWithinBounds(row + dx[direction] * 3, col + dy[direction] * 3) &&
                        board.getPiece(row + dx[direction] * 3, col + dy[direction] * 3) == currentPlayer.getColor()) {
                    sureCaptureCount++;
                    reason += "Capture a pair to the right " + dirName[direction] + ". ";
                } else {
                    if (!board.isWithinBounds(row + dx[direction] * 3, col + dy[direction] * 3)) {
                        priority = Math.max(priority, ONE_OPEN_END);
                    } else {
                        reason += "Possibly Capture a pair to the right " + dirName[direction] + ". ";
                        priority = Math.max(priority, PROBABLY_CAPTURE);
                    }
                }
            }
        }

        if (sureCaptureCount != 0 && (sureCaptureCount + r.getPairsCapturedNum(currentPlayer)) > 4) {
            Player opponent = (r.getCurrentPlayer() == currentPlayer) ? r.getNextPlayer() : currentPlayer;
            int opponentScore = t.getTotalScores(opponent, false);
            int ownScore = t.getTotalScores(currentPlayer, false);
            if (ownScore - opponentScore >= 0 && ownScore - opponentScore < 3) {
                priority = 5;
            } else {
                if ((opponentScore - ownScore) > sureCaptureCount) {
                    priority = DONT_PUT_THERE;
                }
            }
        } else {
            priority = Math.max(priority, SURE_ONE_POINT * sureCaptureCount);
            char ownPiece = currentPlayer.getColor();

            if (checkingOwn) {
                for (int direction = 0; direction < 4; direction++) {
                    if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                            board.getPiece(row - dx[direction], col - dy[direction]) == ownPiece &&
                            board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                            board.getPiece(row + dx[direction], col + dy[direction]) == '0' &&
                            board.isWithinBounds(row - 2 * dx[direction], col - 2 * dy[direction]) &&
                            board.getPiece(row - 2 * dx[direction], col - 2 * dy[direction]) == enemyPiece) {
                        priority = DONT_PUT_THERE;
                        reason = "will get captured. don't place there\n";
                    }
                    if (board.isWithinBounds(row + 2 * dx[direction], col + 2 * dy[direction]) &&
                            board.getPiece(row + 2 * dx[direction], col + 2 * dy[direction]) == enemyPiece &&
                            board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                            board.getPiece(row + dx[direction], col + dy[direction]) == ownPiece &&
                            board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                            board.getPiece(row - dx[direction], col - dy[direction]) == '0') {
                        priority = DONT_PUT_THERE;
                        reason = "will get captured. don't place there\n";
                    }
                    if (board.isWithinBounds(row - dx[direction], col - dy[direction]) &&
                            board.getPiece(row - dx[direction], col - dy[direction]) == '0' &&
                            board.isWithinBounds(row + 2 * dx[direction], col + 2 * dy[direction]) &&
                            board.getPiece(row + 2 * dx[direction], col + 2 * dy[direction]) == enemyPiece &&
                            board.isWithinBounds(row + dx[direction], col + dy[direction]) &&
                            board.getPiece(row + dx[direction], col + dy[direction]) == ownPiece) {
                        priority = DONT_PUT_THERE;
                        reason = "will get captured. don't place there\n";
                    }
                }
            }
        }

        if (!currentPlayer.equals(r.getCurrentPlayer())) {
            reason = "Stop opponent " + reason;
        }
        return new SimpleEntry<>(priority, reason);
    }
//
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

    public Map.Entry<Integer, String> calculateOpponentPriorityWith4Consectuives(int row, int col, Board board,
                                                                                 Player opponentPlayer, Round r) {
        return calculatePriorityWith4Consectuives(row, col, board, opponentPlayer, r);
    }
//
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
            // Determine consecutive count in the left side
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
            // Determine consecutive count in the right side
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
                // Sure 3 consecutive
                sure3ConsCount++;

                // Check for the possibility of growing into 4 in either side

                boolean leftEndOpen = false;
                boolean rightEndOpen = false;

                if (board.isWithinBounds(row - dx[direction] * (leftCount + 1), col - dy[direction] * (leftCount + 1))
                        &&
                        board.isWithinBounds(row + dx[direction] * (rightCount + 1),
                                col + dy[direction] * (rightCount + 1))) {
                    if (board.getPiece(row - dx[direction] * (leftCount + 1),
                            col - dy[direction] * (leftCount + 1)) == '0' &&
                            board.getPiece(row + dx[direction] * (rightCount + 1),
                                    col + dy[direction] * (rightCount + 1)) == '0') {
                        reason = reason + "Have 3 consecutive in " + dirName[direction] + " with both ends open, ";
                        bothOpenEnds = true;
                    } else {
                        leftEndOpen = board.getPiece(row - dx[direction] * (leftCount + 1),
                                col - dy[direction] * (leftCount + 1)) == '0';
                        rightEndOpen = board.getPiece(row + dx[direction] * (rightCount + 1),
                                col + dy[direction] * (rightCount + 1)) == '0';
                    }
                } else {
                    leftEndOpen = rightEndOpen = true; // Treat both ends as open if out of bounds
                }

                if (leftEndOpen || rightEndOpen) {
                    if (leftEndOpen && rightEndOpen) {
                        reason = reason + "Have 3 consecutive in " + dirName[direction] + " with both ends open, ";
                        bothOpenEnds = true;
                    } else {
                        reason = reason + "Have 3 consecutive in " + dirName[direction] + " with one open end, ";
                        oneOpenEnd = true;
                    }
                }

            }

        }

        if (oneOpenEnd) {
            priority = sure3ConsCount * ONE_OPEN_END;
        }
        if (bothOpenEnds) {
            priority = sure3ConsCount * GET_MORE_POINTS;
        }
        // Prioritize getting your consecutive than blocking opponents
        if (!r.getCurrentPlayer().equals(currentPlayer)) {
            priority -= sure3ConsCount * 2;
            reason = "Stop opponent " + reason;
        }
        return new AbstractMap.SimpleEntry<>(priority, reason);
    }

    public Map.Entry<Integer, String> calculateOpponentPriorityWith3Consectuives(int row, int col, Board board,
                                                                                 Player opponentPlayer, Round r) {
        return calculatePriorityWith3Consectuives(row, col, board, opponentPlayer, r);
    }

    public Map.Entry<Integer, String> calculatePriorityWith2Consectuives(int row, int col, Board board,
                                                                         Player currentPlayer, Round r) {
        int priority = 0;
        String reason = "";

        int sure2ConsCount = 0;
        boolean possibleCapture = false;
        boolean bothOpenEnds = false;
        // Iterate through each direction
        for (int direction = 0; direction < 4; ++direction) {
            boolean isConsecutive = true;
            int leftCount = 0;
            int rightCount = 0;
            // Determine consecutive count in the left side
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
            // Determine consecutive count in the right side
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
                // Sure 2 consecutive
                sure2ConsCount++;

                // Check for the possibility of growing into 4 in either side, if one side is
                // blocked by the enemy, then don't put there

                if (board.isWithinBounds(row - dx[direction] * (leftCount + 1), col - dy[direction] * (leftCount + 1))
                        &&
                        board.isWithinBounds(row + dx[direction] * (rightCount + 1),
                                col + dy[direction] * (rightCount + 1))) {
                    if (board.getPiece(row - dx[direction] * (leftCount + 1),
                            col - dy[direction] * (leftCount + 1)) == '0' &&
                            board.getPiece(row + dx[direction] * (rightCount + 1),
                                    col + dy[direction] * (rightCount + 1)) == '0') {
                        reason = reason + "Have 2 consecutive in " + dirName[direction] + " with both ends open, ";
                        bothOpenEnds = true;
                    }
                } else {
                    possibleCapture = true;
                }

                if (board.isWithinBounds(row - dx[direction] * (leftCount + 1),
                        col - dy[direction] * (leftCount + 1))) {
                    if (board.getPiece(row - dx[direction] * (leftCount + 1),
                            col - dy[direction] * (leftCount + 1)) == '0') {
                        possibleCapture = true;
                    }
                }

                if (board.isWithinBounds(row + dx[direction] * (rightCount + 1),
                        col + dy[direction] * (rightCount + 1))) {
                    if (board.getPiece(row + dx[direction] * (rightCount + 1),
                            col + dy[direction] * (rightCount + 1)) == '0') {
                        possibleCapture = true;
                    }
                }

            }
        }

        // If both ends are open, possibleCapture will be true if for any direction from
        // that position, it leads to its capture
        if (!possibleCapture && bothOpenEnds) {
            priority = sure2ConsCount * 2;
        }
        if (r.getCurrentPlayer() != currentPlayer) {
            reason = "Stop opponent " + reason;
        }
        return new AbstractMap.SimpleEntry<>(priority, reason);
    }

    public Map.Entry<Integer, String> calculateOpponentPriorityWith2Consectuives(int row, int col, Board board,
                                                                                 Player opponentPlayer, Round r) {
        return calculatePriorityWith2Consectuives(row, col, board, opponentPlayer, r);
    }

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

    public Map.Entry<Integer, String> calculateOpponentsEndGamePriorityWith5Consectuives(int row, int col, Board board,
                                                                                         Player opponentPlayer, Round r) {
        return calculateEndGamePriorityWith5Consectuives(row, col, board, opponentPlayer, r);
    }

    public Map.Entry<Integer, String> calculateConsecutivesPriority(int row, int col, Board board,
                                                                    Player currentPlayer) {
        int priority = 0;
        String reason = "Create chances of consecutives ";

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
                    priority = Math.max(priority, ownPiecesCount);
                    reason = dirName[direction];
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
                    priority = Math.max(priority, ownPiecesCount);
                    reason = dirName[direction];
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
                    priority = Math.max(priority, totalPiecesCount);
                    reason = dirName[direction];
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
                    priority = Math.max(priority, totalPiecesCount);
                    reason = dirName[direction];
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(priority, reason);
    }

    public Map.Entry<Integer, String> calculateStopOpponentPriority(int row, int col, Board board, Player currentPlayer,
                                                                    Player opponentPlayer) {
        Map.Entry<Integer, String> priorityPair = calculateConsecutivesPriority(row, col, board, opponentPlayer);
        priorityPair.setValue("Stop Opponent " + priorityPair.getValue());
        return priorityPair;
    }

    public Map.Entry<Integer, String> calculateCaptureRiskPriority(int row, int col, Board board, Player currentPlayer,
                                                                   Player opponentPlayer, Round r) {
        // See from enemy's viewpoint, if for that position, if the enemy places its
        // piece,
        // what damage will it do, will it capture my pairs
        return calculateCapturePairsPriority(row, col, board, opponentPlayer, r, false);
    }

}
