package TrisPackage;

import java.util.Random;

public class TrisAI {
    static final char EMPTY = '-';
    static final char PLAYER_X = 'X';
    static final char PLAYER_O = 'O';
    private static final int BOARD_SIZE = 3;

    private char[][] board;
    private Random random;
    private char playerSymbol;
    private int difficulty;
    private char aiSymbol;

    public TrisAI(char playerSymbol, int difficulty) {
        this.playerSymbol = playerSymbol;
        this.aiSymbol = (playerSymbol == 'X') ? 'O' : 'X';
        this.difficulty = difficulty;
        this.board = new char[BOARD_SIZE][BOARD_SIZE];
        this.random = new Random();
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

    public int checkWinner() {
        // Controllo righe e colonne
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] != EMPTY && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return (board[i][0] == PLAYER_X) ? 1 : -1;
            }
            if (board[0][i] != EMPTY && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                return (board[0][i] == PLAYER_X) ? 1 : -1;
            }
        }

        // Controllo diagonali
        if (board[0][0] != EMPTY && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return (board[0][0] == PLAYER_X) ? 1 : -1;
        }
        if (board[0][2] != EMPTY && board[0][2] == board[1][1] && board[0][2] == board[2][0]) {
            return (board[0][2] == PLAYER_X) ? 1 : -1;
        }

        // Controllo pareggio
        boolean isFull = true;
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    isFull = false;
                    break;
                }
            }
            if (!isFull) break;
        }
        
        return isFull ? 0 : 10; // 0 per pareggio, 10 per gioco in corso
    }

    public boolean playerMove(int row, int col) {
        if (isValidMove(row, col)) {
            board[row][col] = playerSymbol;
            return true;
        }
        return false;
    }

    public int[] computerMove() {
        int[] move;
        
        // Strategia basata sulla difficoltà
        switch (difficulty) {
            case 1: // Facile: 80% casuale, 20% intelligente
                move = (random.nextInt(100) < 80) ? getRandomMove() : findBestMove();
                break;
            case 2: // Medio: 50% casuale, 50% intelligente
                move = (random.nextInt(100) < 50) ? getRandomMove() : findBestMove();
                break;
            case 3: // Difficile: sempre la mossa migliore
                move = findBestMove();
                break;
            default:
                move = getRandomMove();
        }
        
        board[move[0]][move[1]] = aiSymbol;
        return move;
    }

    private int[] getRandomMove() {
        int[] move = new int[2];
        do {
            move[0] = random.nextInt(BOARD_SIZE);
            move[1] = random.nextInt(BOARD_SIZE);
        } while (!isValidMove(move[0], move[1]));
        return move;
    }

    private int[] findBestMove() {
        int bestVal = Integer.MIN_VALUE;
        int[] bestMove = {-1, -1};

        // Se è la prima mossa, prendi il centro o un angolo
        if (isFirstMove()) {
            if (isValidMove(1, 1)) return new int[]{1, 1};
            return new int[]{0, 0};
        }

        // Valuta tutte le celle vuote
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) {
                    // Prova questa mossa
                    board[i][j] = aiSymbol;
                    
                    // Calcola il valore della mossa
                    int moveVal = minimax(0, false);
                    
                    // Annulla la mossa
                    board[i][j] = EMPTY;
                    
                    if (moveVal > bestVal) {
                        bestMove[0] = i;
                        bestMove[1] = j;
                        bestVal = moveVal;
                    }
                }
            }
        }
        return bestMove;
    }

    private boolean isFirstMove() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] != EMPTY) return false;
            }
        }
        return true;
    }

    private int minimax(int depth, boolean isMax) {
        int score = evaluate();

        // Se ha vinto qualcuno o è pareggio
        if (score == 10) return score - depth;  // AI vince
        if (score == -10) return score + depth; // Player vince
        if (!isMovesLeft()) return 0;          // Pareggio

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = aiSymbol;
                        best = Math.max(best, minimax(depth + 1, !isMax));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (board[i][j] == EMPTY) {
                        board[i][j] = playerSymbol;
                        best = Math.min(best, minimax(depth + 1, !isMax));
                        board[i][j] = EMPTY;
                    }
                }
            }
            return best;
        }
    }

    private boolean isMovesLeft() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == EMPTY) return true;
            }
        }
        return false;
    }

    private int evaluate() {
        // Controllo righe
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2]) {
                if (board[row][0] == aiSymbol) return 10;
                if (board[row][0] == playerSymbol) return -10;
            }
        }

        // Controllo colonne
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col]) {
                if (board[0][col] == aiSymbol) return 10;
                if (board[0][col] == playerSymbol) return -10;
            }
        }

        // Controllo diagonali
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == aiSymbol) return 10;
            if (board[0][0] == playerSymbol) return -10;
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == aiSymbol) return 10;
            if (board[0][2] == playerSymbol) return -10;
        }

        return 0;
    }

    private boolean isValidMove(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE && board[row][col] == EMPTY;
    }
}
