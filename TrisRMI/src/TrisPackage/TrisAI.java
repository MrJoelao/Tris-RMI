package TrisPackage;

import java.util.Random;

public class TrisAI {
    static final char EMPTY = '-';
    static final char PLAYER_X = 'X';
    static final char PLAYER_O = 'O';
    private static final int BOARD_SIZE = 3;

    private char[][] board = new char[BOARD_SIZE][BOARD_SIZE];
    private Random random = new Random();
    private char playerSymbol; // Added
    private int difficulty;

    public TrisAI(char playerSymbol, int difficulty) { // Modified
        this.playerSymbol = playerSymbol;
        this.difficulty = difficulty;
        initializeBoard();
    }

    public void initializeBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = EMPTY;
            }
        }
    }

    public void printBoard() {
        System.out.println("-------------");
        for (int i = 0; i < BOARD_SIZE; i++) {
            System.out.print("| ");
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " | ");
            }
            System.out.println();
            System.out.println("-------------");
        }
    }

    public int checkWinner() { // Modified
        for (int i = 0; i < BOARD_SIZE; i++) {
            // Check rows
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                if (board[i][0] == PLAYER_X)
                    return 1; // Player X wins
                else
                    return -1; // Player O wins
            }
            // Check columns
            if (board[0][i] != EMPTY && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                if (board[0][i] == PLAYER_X)
                    return 1; // Player X wins
                else
                    return -1; // Player O wins
            }
        }
        // Check diagonals
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            if (board[0][0] == PLAYER_X)
                return 1; // Player X wins
            else
                return -1; // Player O wins
        }
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            if (board[0][2] == PLAYER_X)
                return 1; // Player X wins
            else
                return -1; // Player O wins
        }
        // Check for draw
        boolean boardFull = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    boardFull = false;
                    break;
                }
            }
        }
        if (boardFull) {
            return 0; // Draw
        }
        return 10; // No winner yet
    }

    public boolean playerMove(int row, int column) {
        if (!isValidMove(row, column)) {
            return false;
        }
        board[row][column] = PLAYER_X;
        return true;
    }

    public int[] computerMove() {
        int[] move;
        switch (difficulty) {
            case 1:
                move = getRandomMove();
                break;
            case 2:
                if (random.nextInt(100) < 40) {
                    move = getBestMove();
                } else {
                    move = getRandomMove();
                }
                break;
            case 3:
                move = getBestMove();
                break;
            default:
                move = getRandomMove();
        }
        board[move[0]][move[1]] = PLAYER_O;
        return move;
    }

    private int[] getRandomMove() {
        int row, column;
        do {
            row = random.nextInt(BOARD_SIZE);
            column = random.nextInt(BOARD_SIZE);
        } while (!isValidMove(row, column));
        return new int[]{row, column};
    }

    private int[] getBestMove() {
        int[] move = {-1, -1};
        int bestScore = Integer.MIN_VALUE;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    board[i][j] = PLAYER_O;
                    int score = minimax(board, 0, false);
                    board[i][j] = EMPTY;
                    if (score > bestScore) {
                        bestScore = score;
                        move[0] = i;
                        move[1] = j;
                    }
                }
            }
        }
        return move;
    }

    private int minimax(char[][] board, int depth, boolean isMaximizing) {
        int result = checkWinner();
        
        if (result != EMPTY) {
            return result;
        }

        if (isMaximizing) {
            int bestScore = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER_O;
                        int score = minimax(board, depth + 1, false);
                        board[i][j] = EMPTY;
                        bestScore = Math.max(score, bestScore);
                    }
                }
            }
            return bestScore;
        } else {
            int bestScore = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = PLAYER_X;
                        int score = minimax(board, depth + 1, true);
                        board[i][j] = EMPTY;
                        bestScore = Math.min(score, bestScore);
                    }
                }
            }
            return bestScore;
        }
    }

    private boolean isValidMove(int row, int column) {
        return row >= 0 && row < BOARD_SIZE && column >= 0 && column < BOARD_SIZE && board[row][column] == EMPTY;
    }
}
