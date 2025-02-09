package TrisPackage;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import javax.swing.SwingUtilities;

public class TrisServer extends UnicastRemoteObject implements TrisServerInterface {
    private LoginManager loginManager;
    private ScoreManager scoreManager;
    private TrisNormal trisNormal;
    private TrisAI trisAI;
    private boolean isAIGame;
    private String username0 = "";
    private String username1 = "";
    private String usernameRed;
    private String usernameBlue;
    private static TrisServerGUI gui;
    
    public TrisServer() throws RemoteException {
        super();
        loginManager = new LoginManager("credentials.dat", "admin", "admin", false);
        scoreManager = new ScoreManager("score.dat");
        logDebug("Server instance created");
    }
    
    @Override
    public void initializeGame(boolean isAI, char playerSymbol, int difficulty) throws RemoteException {
        this.isAIGame = isAI;
        if (isAI) {
            trisAI = new TrisAI(playerSymbol, difficulty);
            logDebug("AI game initialized with difficulty: " + difficulty);
        } else {
            trisNormal = new TrisNormal(playerSymbol);
            logDebug("PvP game initialized");
        }
    }
    
    @Override
    public int move(int row, int col, char playerSymbol) throws RemoteException {
        if (isAIGame) {
            if (trisAI.playerMove(row, col)) {
                return 1;
            }
            return -1;
        } else {
            return trisNormal.move(row, col);
        }
    }
    
    @Override
    public int[] getAIMove() throws RemoteException {
        if (isAIGame) {
            return trisAI.computerMove();
        }
        return null;
    }
    
    @Override
    public int checkWinner() throws RemoteException {
        if (isAIGame) {
            return trisAI.checkWinner();
        } else {
            return trisNormal.checkWinner();
        }
    }
    
    // Implementazione dei metodi per login e punteggi
    @Override
    public boolean checkCredentials(String username, String password) throws RemoteException {
        return loginManager.checkCredentials(username, password);
    }
    
    @Override
    public boolean addCredentials(String username, String password, boolean type) throws RemoteException {
        return loginManager.addCredentials(username, password, type);
    }
    
    @Override
    public boolean isUsernameExists(String username) throws RemoteException {
        return loginManager.isUsernameExists(username);
    }
    
    @Override
    public int isPasswordSecure(String password) throws RemoteException {
        return loginManager.isPasswordSecure(password);
    }
    
    @Override
    public void addVictory(String playerName) throws RemoteException {
        scoreManager.addVictory(playerName);
    }
    
    @Override
    public void addDefeat(String playerName) throws RemoteException {
        scoreManager.addDefeat(playerName);
    }
    
    @Override
    public List<String[]> getAllPlayerStats() throws RemoteException {
        return scoreManager.getAllPlayerStats();
    }
    
    @Override
    public int getPlayerVictories(String playerName) throws RemoteException {
        return scoreManager.getPlayerVictories(playerName);
    }
    
    @Override
    public void setUsernames(String username0, String username1, char flagTeam) throws RemoteException {
        this.username0 = username0;
        this.username1 = username1;
        if(flagTeam == 'X') {
            usernameBlue = username1;
            usernameRed = username0;
            logDebug("Team X assigned to: " + username0);
        } else {
            usernameRed = username1;
            usernameBlue = username0;
            logDebug("Team O assigned to: " + username0);
        }
    }
    
    @Override
    public void handleWin(String winnerName, String loserName) throws RemoteException {
        scoreManager.addVictory(winnerName);
        scoreManager.addDefeat(loserName);
        logDebug("Game ended - Winner: " + winnerName + ", Loser: " + loserName);
    }
    
    @Override
    public boolean winPopUp(int result, String redName, String blueName) throws RemoteException {
        if (result == 1) {
            handleWin(redName, blueName);
            return true;
        } else if (result == -1) {
            handleWin(blueName, redName);
            return true;
        } else if (result == 0) {
            return true;
        }
        return false;
    }
    
    // Metodo per inviare i log al ServerGUI
    private void logDebug(String message) {
        if (gui != null) {
            gui.addToLog(message);
        }
    }
    
    public static void setGUI(TrisServerGUI serverGUI) {
        gui = serverGUI;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            gui = new TrisServerGUI();
            gui.setVisible(true);
        });
    }
} 