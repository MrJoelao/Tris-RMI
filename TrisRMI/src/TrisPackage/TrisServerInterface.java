package TrisPackage;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface TrisServerInterface extends Remote {
    // Metodi per il login e gestione utenti
    boolean checkCredentials(String username, String password) throws RemoteException;
    boolean addCredentials(String username, String password, boolean type) throws RemoteException;
    boolean isUsernameExists(String username) throws RemoteException;
    int isPasswordSecure(String password) throws RemoteException;
    
    // Metodi per il gioco
    int move(int row, int col, char playerSymbol) throws RemoteException;
    int checkWinner() throws RemoteException;
    void initializeGame(boolean isAI, char playerSymbol, int difficulty) throws RemoteException;
    int[] getAIMove() throws RemoteException;
    
    // Metodi per il punteggio
    void addVictory(String playerName) throws RemoteException;
    void addDefeat(String playerName) throws RemoteException;
    List<String[]> getAllPlayerStats() throws RemoteException;
    int getPlayerVictories(String playerName) throws RemoteException;

    // Aggiungi questi metodi all'interfaccia
    void setUsernames(String username0, String username1, char flagTeam) throws RemoteException;
    void handleWin(String winnerName, String loserName) throws RemoteException;
    boolean winPopUp(int result, String redName, String blueName) throws RemoteException;
} 