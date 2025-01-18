package TrisPackage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * author joels
 */
public class ScoreManager {
    private String fileName; // Nome del file dove sono salvati i punteggi

    // Costruttore che imposta il nome del file
    public ScoreManager(String fileName) {
        this.fileName = fileName;
        createFileIfNotExists(); // Controllo e creazione del file se non esiste
    }

    // Metodo privato per controllare e creare il file se non esiste
    private void createFileIfNotExists() {
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("[SM] Il file " + fileName + " non esiste, creazione...");;
            try {
                file.createNewFile();
                System.out.println("[SM] Il file " + fileName + " e' stato creato con successo.");
            } catch (IOException e) {
                System.err.println("[SM] Errore durante la creazione del file " + fileName);
                e.printStackTrace();
            }
        }
    }

    // Metodo per aggiungere un nuovo giocatore
    public void addPlayer(String playerName) {
        List<PlayerScore> playerScores = loadAllPlayerScores(); // Carica tutti i punteggi dei giocatori
        boolean playerExists = false;
        for (PlayerScore playerScore : playerScores) {
            if (playerScore.getPlayerName().equals(playerName)) {
                playerExists = true;
                break;
            }
        }
        if (!playerExists) {
            PlayerScore playerScore = new PlayerScore(playerName); // Crea un nuovo punteggio per il giocatore
            playerScores.add(playerScore); // Aggiungi il nuovo punteggio alla lista
            saveAllPlayerScores(playerScores); // Salva tutti i punteggi aggiornati su file
            System.out.println("[SM] Giocatore " + playerName + " aggiunto con successo.");
        } else {
            System.err.println("[SM] Errore: Il giocatore " + playerName + " e' già presente.");
        }
    }

    // Metodo per aggiungere una vittoria a un giocatore
    public void addVictory(String playerName) {
        List<PlayerScore> playerScores = loadAllPlayerScores(); // Carica tutti i punteggi dei giocatori
        boolean playerFound = false;
        for (PlayerScore playerScore : playerScores) {
            if (playerScore.getPlayerName().equals(playerName)) {
                playerScore.addVictory(); // Aggiungi una vittoria al giocatore
                playerFound = true;
                break;
            }
        }
        if (!playerFound) {
            PlayerScore newPlayer = new PlayerScore(playerName); // Crea un nuovo giocatore
            newPlayer.addVictory(); // Aggiungi una vittoria al nuovo giocatore
            playerScores.add(newPlayer); // Aggiungi il nuovo giocatore alla lista dei punteggi
        }
        saveAllPlayerScores(playerScores); // Salva tutti i punteggi aggiornati su file
        System.out.println("[SM] Vittoria aggiunta per " + playerName);
    }

    // Metodo per aggiungere una sconfitta a un giocatore
    public void addDefeat(String playerName) {
        List<PlayerScore> playerScores = loadAllPlayerScores(); // Carica tutti i punteggi dei giocatori
        boolean playerFound = false;
        for (PlayerScore playerScore : playerScores) {
            if (playerScore.getPlayerName().equals(playerName)) {
                playerScore.addDefeat(); // Aggiungi una sconfitta al giocatore
                playerFound = true;
                break;
            }
        }
        if (!playerFound) {
            PlayerScore newPlayer = new PlayerScore(playerName); // Crea un nuovo giocatore
            newPlayer.addDefeat(); // Aggiungi una sconfitta al nuovo giocatore
            playerScores.add(newPlayer); // Aggiungi il nuovo giocatore alla lista dei punteggi
        }
        saveAllPlayerScores(playerScores); // Salva tutti i punteggi aggiornati su file
        System.out.println("[SM] Sconfitta aggiunta per " + playerName);
    }

    // Metodo privato per caricare tutti i punteggi dei giocatori da file
    private List<PlayerScore> loadAllPlayerScores() {
        List<PlayerScore> playerScores = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            while (true) {
                PlayerScore playerScore = (PlayerScore) ois.readObject();
                playerScores.add(playerScore);
            }
        } catch (EOFException | FileNotFoundException e) {
            // Fine del file
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[SM] Errore durante il caricamento dei punteggi dei giocatori");
            e.printStackTrace();
        }
        return playerScores;
    }

    // Metodo privato per salvare tutti i punteggi dei giocatori su file
    private void saveAllPlayerScores(List<PlayerScore> playerScores) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            for (PlayerScore playerScore : playerScores) {
                oos.writeObject(playerScore); // Scrivi il punteggio del giocatore su file
            }
        } catch (IOException e) {
            System.err.println("[SM] Errore durante il salvataggio dei punteggi dei giocatori");
            e.printStackTrace();
        }
    }
    
    public int getPlayerVictories(String playerName) {
        List<PlayerScore> playerScores = loadAllPlayerScores();
        for (PlayerScore playerScore : playerScores) {
            if (playerScore.getPlayerName().equals(playerName)) {
                return playerScore.getVictories();
            }
        }
        return 0; // Se il giocatore non viene trovato, restituisce 0 vittorie
    }
    
    // Metodo per ottenere tutti i nomi dei giocatori
    public List<String> getAllPlayerNames() {
        List<String> playerNames = new ArrayList<>();
        List<PlayerScore> playerScores = loadAllPlayerScores(); // Carica tutti i punteggi dei giocatori
        for (PlayerScore playerScore : playerScores) {
            playerNames.add(playerScore.getPlayerName()); // Aggiungi il nome del giocatore alla lista
        }
        return playerNames;
    }
    
    // Metodo per ottenere tutti i nomi dei giocatori con il numero di vittorie e sconfitte
    public List<String[]> getAllPlayerStats() {
        List<String[]> playerStats = new ArrayList<>();
        List<PlayerScore> playerScores = loadAllPlayerScores(); // Carica tutti i punteggi dei giocatori
        for (PlayerScore playerScore : playerScores) {
            String playerName = playerScore.getPlayerName();
            int victories = playerScore.getVictories();
            int defeats = playerScore.getDefeats();
            String[] stats = {playerName, String.valueOf(victories), String.valueOf(defeats)};
            playerStats.add(stats); // Aggiungi il nome del giocatore con le vittorie e le sconfitte alla lista
        }
        return playerStats;
    }


    // Classe PlayerScore definita direttamente all'interno di ScoreManager
    private static class PlayerScore implements Serializable {
        private static final long serialVersionUID = 1L;

        private String playerName; // Nome del giocatore
        private int victories; // Numero di vittorie
        private int defeats; // Numero di sconfitte

        // Costruttore
        public PlayerScore(String playerName) {
            this.playerName = playerName;
            this.victories = 0;
            this.defeats = 0;
        }

        // Metodo per aggiungere una vittoria
        public void addVictory() {
            victories++;
        }

        // Metodo per aggiungere una sconfitta
        public void addDefeat() {
            defeats++;
        }

        // Getters per i punteggi
        public int getVictories() {
            return victories;
        }

        public int getDefeats() {
            return defeats;
        }

        // Metodo per ottenere il nome del giocatore
        public String getPlayerName() {
            return playerName;
        }
    }
}

