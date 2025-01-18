package TrisPackage;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

//<editor-fold defaultstate="collapsed" desc="Scelte e spiegazione del Login Manager ">
/* 

Ho usato ObjectInputStream e ObjectOutputStream invece della BufferedReader e BufferedWriter perché volevo memorizzare interi oggetti.
La BufferedReader e BufferedWriter non permettono di leggere e scrivere interi oggetti, per questo non li ho usati.

ObjectInputStream e ObjectOutputStream convertono gli oggetti in una sequenza di byte che possono essere scritti su un file e poi ripristinati 
quando necessario. Per questo però bisogna aggiungere "implements Serializable" a questa classe e a quelle che usa per salvarsi i dati (Credentials). 
Consente agli oggetti della classe di essere convertiti in una sequenza di byte che è becessario per la ObjectInputStream e ObjectOutputStream.

Per quanto riguarda le eccezioni, ho aggiuto delle print di debug in ogni try catch per individuare facilmente ogni possibile errore.
Nella casualità che il file delle credenziali non esista, viene automaticamente creato e aggiunte delle credenziali predefinite.

Quanto alla struttura dati, ho optato per un'ArrayList per memorizzare le credenziali. Posso aggiungere, rimuovere e modificare le credenziali 
senza dovermi preoccupare di gestire manualmente la dimensione dell'array o spostare gli elementi. 

*/
 //</editor-fold>

public class LoginManager implements Serializable {
    private String FILENAME;
    private String DEFAULT_USERNAME;
    private String DEFAULT_PASSWORD;
    private boolean DEFAULT_TYPE;
    
    // Costanti per i codici d'errore della password
    public static final int PASSWORD_TOO_SHORT = 1;
    public static final int MISSING_UPPERCASE = 2;
    public static final int MISSING_LOWERCASE = 3;
    public static final int MISSING_NUMBER = 4;
    public static final int MISSING_SPECIAL_CHARACTER = 5;
    public static final int PASSWORD_SECURE = 0;

    private ArrayList<Credentials> credentialsList;

    // Costruttore con parametri per inizializzare le variabili
    public LoginManager(String filename, String defaultUsername, String defaultPassword, boolean defaultType) {
        this.FILENAME = filename;
        this.DEFAULT_USERNAME = defaultUsername;
        this.DEFAULT_PASSWORD = defaultPassword;
        this.DEFAULT_TYPE = defaultType;
        this.credentialsList = new ArrayList<>();
        
        // Caricamento delle credenziali
        loadCredentials();
    }

    // Metodo per caricare le credenziali dal file
    public void loadCredentials() {
        try {
            // Stampa messaggio di inizio caricamento credenziali
            System.out.println("[LM] Caricamento delle credenziali in corso...");

            // Apre uno stream di input da un file
            FileInputStream fileInputStream = new FileInputStream(FILENAME);
            // Crea un ObjectInputStream per leggere gli oggetti dallo stream di input
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            // Legge l'oggetto ArrayList<Credentials> dallo stream di input e lo assegna a credentialsList
            credentialsList = (ArrayList<Credentials>) objectInputStream.readObject();

            // Chiude lo stream di input e l'ObjectInputStream
            objectInputStream.close();
            fileInputStream.close();

            // Stampa messaggio di successo nel caricamento delle credenziali
            System.out.println("[LM] Credenziali caricate con successo.");
        } catch (FileNotFoundException e) {
            // Se il file non esiste, crea un file con credenziali predefinite
            System.err.println("[LM] Il file non esiste. Creazione del file...");
            createDefaultCredentialsFile();
        } catch (IOException | ClassNotFoundException e) {
            // Gestisce eventuali errori durante il caricamento delle credenziali
            System.err.println("[LM] Errore durante il caricamento delle credenziali: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metodo per salvare le credenziali nel file
    public void saveCredentials() {
        try {
            // Stampa messaggio di inizio salvataggio credenziali
            System.out.println("[LM] Salvataggio delle credenziali in corso...");

            // Apre uno stream di output per scrivere su un file
            FileOutputStream fileOutputStream = new FileOutputStream(FILENAME);
            // Crea un ObjectOutputStream per scrivere oggetti nello stream di output
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Scrive l'oggetto credentialsList nello stream di output
            objectOutputStream.writeObject(credentialsList);

            // Chiude lo stream di output e l'ObjectOutputStream
            objectOutputStream.close();
            fileOutputStream.close();

            // Stampa messaggio di successo nel salvataggio delle credenziali
            System.out.println("[LM] Credenziali salvate con successo.");

        } catch (IOException e) {
            // Gestisce eventuali errori durante il salvataggio delle credenziali
            System.err.println("[LM] Errore durante il salvataggio delle credenziali: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Metodo per creare un file con credenziali predefinite se il file non esiste
    private void createDefaultCredentialsFile() {
        try {
            // Stampa messaggio di inizio creazione file con credenziali predefinite
            System.out.println("[LM] Creazione del file con credenziali predefinite...");

            // Apre uno stream di output per scrivere su un file
            FileOutputStream fileOutputStream = new FileOutputStream(FILENAME);
            // Crea un ObjectOutputStream per scrivere oggetti nello stream di output
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            // Crea un oggetto Credentials con credenziali predefinite
            Credentials defaultCredentials = new Credentials(DEFAULT_USERNAME, DEFAULT_PASSWORD, DEFAULT_TYPE);
            // Aggiunge le credenziali predefinite alla lista di credenziali
            credentialsList.add(defaultCredentials);

            // Scrive l'oggetto credentialsList nello stream di output
            objectOutputStream.writeObject(credentialsList);

            // Chiude lo stream di output e l'ObjectOutputStream
            objectOutputStream.close();
            fileOutputStream.close();

            // Stampa messaggio di successo nella creazione del file con credenziali predefinite
            System.out.println("[LM] File creato con successo con credenziali predefinite.");

        } catch (IOException e) {
            // Gestisce eventuali errori durante la creazione del file con credenziali predefinite
            System.err.println("[LM] Errore durante la creazione del file con credenziali predefinite: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Metodo per aggiornare le credenziali
    public boolean updateCredentials(String oldUsername, String newUsername, String newPassword, boolean newType) {
        System.out.println("[LM] Aggiornamento delle credenziali...");
        
        // Cerca le credenziali dell'utente con l'username specificato
        for (Credentials credentials : credentialsList) {
            if (credentials.getUsername().equals(oldUsername)) {
                // Rimuovi le vecchie credenziali
                credentialsList.remove(credentials);

                // Aggiungi le nuove credenziali
                Credentials updatedCredentials = new Credentials(newUsername, newPassword, newType);
                credentialsList.add(updatedCredentials);
                saveCredentials();

                System.out.println("[LM] Credenziali aggiornate con successo.");
                return true; // Aggiornamento riuscito
            }
        }
        System.err.println("[LM] Username non trovato per l'aggiornamento.");
        return false; // Aggiornamento non riuscito (username non trovato)
    }

    // Metodo per verificare le credenziali
    public boolean checkCredentials(String username, String password) {
        System.out.println("[LM] Verifcando credenziali di: " + username + ".");
        for (Credentials credentials : credentialsList) {
            if (credentials.getUsername().equals(username) && credentials.getPassword().equals(password)) {
                System.out.println("[LM] Riscontro per: " + username + " trovato");
                return true; //Credenziali trovate
            }
        }
        System.out.println("[LM] Riscontro per: " + username + " non trovato");
        return false;//Credenziali non trovate
    }

    // Metodo per verificare se l'username è admin o meno
    public boolean isAdmin(String username) {
        System.out.println("[LM] Verifcando i poteri di: " + username + ".");
        for (Credentials credentials : credentialsList) {
            if (credentials.getUsername().equals(username) && credentials.getTipologia()) {
                System.out.println("[LM] Riscontro per: " + username + " trovato");
                return true; // L'utente è admin
            }
        }
        System.out.println("[LM] Riscontro per: " + username + " non trovato");
        return false; // L'utente non è admin
    }
    
    // Metodo per controllare la presenza di un username
    public boolean isUsernameExists(String username) {
        System.out.println("[LM] Verifcando l'esistenza di: " + username + ".");
        for (Credentials credentials : credentialsList) {
            if (credentials.getUsername().equals(username)) {
                System.out.println("[LM] Riscontro per: " + username + " trovato");
                return true; // Username trovato
            }
        }
        System.out.println("[LM] Riscontro per: " + username + " non trovato");
        return false; // Username non trovato
    }
    
    // Metodo per controllare la sicurezza delle password
    public int isPasswordSecure(String password) {
        // Controlla la lunghezza della password
        if (password.length() < 8) {
            return PASSWORD_TOO_SHORT;
        }

        // Controlla se la password contiene almeno una lettera maiuscola
        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return MISSING_UPPERCASE;
        }

        // Controlla se la password contiene almeno una lettera minuscola
        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return MISSING_LOWERCASE;
        }

        // Controlla se la password contiene almeno un numero
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return MISSING_NUMBER;
        }

        // Controlla se la password contiene almeno un carattere speciale
        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            return MISSING_SPECIAL_CHARACTER;
        }

        // Se la password supera tutti i controlli, è considerata sicura
        return PASSWORD_SECURE;
    }

    // Metodo per aggiungere credenziali
    public boolean addCredentials(String username, String password, boolean type) {
        System.out.println("[LM] Aggiunta delle nuove credenziali...");
        
        // Controlla se l'username esiste già
        if (isUsernameExists(username)) {
            System.err.println("[LM] Username già esistente.");
            return false; // L'aggiunta non è andata a buon fine
        }

        // Se l'username non esiste già, aggiungi le nuove credenziali
        Credentials newCredentials = new Credentials(username, password, type);
        
        System.out.println("[LM] Aggiunta avvenuta con successo");
        credentialsList.add(newCredentials);
        
        saveCredentials(); //salvo i cambiamenti
        return true; // L'aggiunta è andata a buon fine
    }

    // Metodo per rimuovere credenziali di un utente
    public boolean removeCredentials(String username) {
        System.out.println("[LM] Rimozione credenziali dell'utente: " + username + "...");
        for (Credentials credentials : credentialsList) {
            if (credentials.getUsername().equals(username)) {
                credentialsList.remove(credentials); //salvo i cambiamenti
                System.out.println("[LM] Credenziali dell'utente: " + username + " rimosse con successo.");
                saveCredentials();
                return true; // Rimozione riuscita
            }
        }
        System.err.println("[LM] Username non trovato per la rimozione delle credenziali.");
        return false; // Username non trovato (rimozione non riuscita)
    }
    
    // Metodo per ottenere le credenziali in base all'username
    public Credentials getCredentialsByUsername(String username) {
        System.out.println("[LM] Ricerca delle credenziali per l'username: " + username);

        // Itera attraverso la lista delle credenziali
        for (Credentials credentials : credentialsList) {
            // Controlla se l'username corrisponde a quello cercato
            if (credentials.getUsername().equals(username)) {
                // Restituisce le credenziali se l'username corrisponde
                System.out.println("[LM] Credenziali trovate per l'username: " + username);
                return credentials;
            }
        }
        // Se non trova corrispondenze, restituisce null
        System.out.println("[LM] Credenziali non trovate per l'username: " + username);
        return null;
    }

    
    // Metodo di debug per stampare l'array list delle credenziali
    public void printCredentialsList() {
        System.out.println("[LM] Elenco delle credenziali:");
        for (Credentials credentials : credentialsList) {
            System.out.println("Username: " + credentials.getUsername() + ", Password: " + credentials.getPassword() + ", Tipologia: " + credentials.getTipologia());
        } 
    }

    public ArrayList<Credentials> getCredentialsList() {
        return credentialsList;
    }


    // Classe interna per le credenziali
    public class Credentials implements Serializable {
        private static final long serialVersionUID = 1;

        private String username;
        private String password;
        private boolean tipologia;

        public Credentials(String username, String password, boolean tipologia) {
            this.username = username;
            this.password = password;
            this.tipologia = tipologia;
        }

        public String getUsername() {
            return username;
        }

        public String getPassword() {
            return password;
        }

        public boolean getTipologia() {
            return tipologia;
        }
    }
}

