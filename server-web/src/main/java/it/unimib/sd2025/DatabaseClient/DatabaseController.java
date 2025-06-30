package it.unimib.sd2025.DatabaseClient;

import java.io.IOException;

import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.Utente;

public class DatabaseController{

    private static final DatabaseController INSTANCE = new DatabaseController();

    private DatabaseClient client;

    private DatabaseController(){
        try {
            this.client = new DatabaseClient();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.addSchema("utenti");
        client.addSchema("haBuono");
        client.addSchema("buoni");

    }

    public static DatabaseController get(){
        return INSTANCE;
    }

    public void addBuono(Buono buono){
        
    }
    public void removeBuono(Buono buono){
        // Implementazione per rimuovere un buono
    }
    public void updateBuono(Buono buono){
        // Implementazione per aggiornare un buono
    }
    public List<Buono> getBuoni(Utente utente){
        // Implementazione per ottenere i buoni di un utente
    }


    
}

