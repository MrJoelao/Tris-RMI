package TrisPackage;

/**
 *
 * @author joels,ricpi,NicMuso,AlePini
 */
public class TrisNormal {
    private char[][] board;
    private char current_player;

    // Costruttore che accetta un parametro per il giocatore iniziale
    public TrisNormal(char initialPlayer) {
        board = new char[3][3];
        current_player = initialPlayer; // Imposta il giocatore iniziale
        inizializza();
    }

    // Inizializza la scacchiera con celle vuote
    public void inizializza() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                board[i][j] = '-';
            }
        }
    }

    // Alterna tra i giocatori 'X' e 'O'
    public void switchPlayer() {
        current_player = (current_player == 'X') ? 'O' : 'X';
    }

    // Muove il giocatore corrente nella posizione specificata
    public int move(int row, int col) {
        if (row >= 0 && row < 3 && col >= 0 && col < 3 && board[row][col] == '-') {
            board[row][col] = current_player;
            switchPlayer();
            if (current_player == 'X') {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }

    // Restituisce il giocatore corrente
    public char getCurrentPlayer() {
        return current_player;
    }

    // Controlla se c'è un vincitore sulla scacchiera
    public int checkWinner() {
        // Controllo righe
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != '-' && board[i][0] == board[i][1] && board[i][1] == board[i][2]) {
                if (board[i][0] == 'X') {
                    return 1; // Vittoria di 'X'
                } else {
                    return -1; // Vittoria di 'O'
                }
            }
        }

        // Controllo colonne
        for (int i = 0; i < 3; i++) {
            if (board[0][i] != '-' && board[0][i] == board[1][i] && board[1][i] == board[2][i]) {
                if (board[0][i] == 'X') {
                    return 1; // Vittoria di 'X'
                } else {
                    return -1; // Vittoria di 'O'
                }
            }
        }

        // Controllo diagonali
        if (board[0][0] != '-' && board[0][0] == board[1][1] && board[1][1] == board[2][2]) {
            if (board[0][0] == 'X') {
                return 1; // Vittoria di 'X'
            } else {
                return -1; // Vittoria di 'O'
            }
        }
        if (board[0][2] != '-' && board[0][2] == board[1][1] && board[1][1] == board[2][0]) {
            if (board[0][2] == 'X') {
                return 1; // Vittoria di 'X'
            } else {
                return -1; // Vittoria di 'O'
            }
        }

        // Controllo pareggio
        boolean tie = true;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] == '-') {
                    tie = false;
                    break;
                }
            }
            if (!tie) {
                break;
            }
        }
        if (tie) {
            return 0; // Pareggio
        }

        return 3; // Nessun vincitore né pareggio
    }


    // Restituisce la scacchiera
    public char[][] getBoard() {
        return board;
    }
}