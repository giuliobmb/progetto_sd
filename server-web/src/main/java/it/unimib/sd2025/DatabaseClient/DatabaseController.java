package it.unimib.sd2025.DatabaseClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import it.unimib.sd2025.model.Buono;
import it.unimib.sd2025.model.StatoBuono;
import it.unimib.sd2025.model.Utente;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

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

        Jsonb jsonb = JsonbBuilder.create();

        String BuonoString = jsonb.toJson(buono);

        client.addPair("haBuono", buono.getId(), buono.getCodiceFiscale());
        client.addPair("buoni", buono.getId(), BuonoString);
    }
    public void removeBuono(Buono buono){
        // Implementazione per rimuovere un buono
        client.removePair("haBuono", buono.getId());
        client.removePair("buoni", buono.getId());
    }
    public void updateBuono(Buono buono){
        // Implementazione per aggiornare un buono
        Jsonb jsonb = JsonbBuilder.create();
        String BuonoString = jsonb.toJson(buono);
        client.updatePair("buoni", buono.getId(), BuonoString);
    }
    public List<Buono> getAllBuoniUtente(String codiceFiscale){
        // Implementazione per ottenere i buoni di un utente
        List<Buono> buoniList = new ArrayList<>();

        HashMap<String,String> Allbuoni = client.getAll("haBuono");

        for(String key : Allbuoni.keySet()) {
            if(Allbuoni.get(key).equals(codiceFiscale)) {
                String buonoString = client.getValue("buoni", key);
                Jsonb jsonb = JsonbBuilder.create();
                Buono buono = jsonb.fromJson(buonoString, Buono.class);
                buoniList.add(buono);
            }
        }
        return buoniList;
    }

    public Buono getBuonoById(String idBuono) {
        // Implementazione per ottenere un buono specifico per ID
        String buonoString = client.getValue("buoni", idBuono);
        if (buonoString != null) {
            Jsonb jsonb = JsonbBuilder.create();
            return jsonb.fromJson(buonoString, Buono.class);
        }
        return null;
    }

    public List<Buono> getAllBuoni(){
        // Implementazione per ottenere tutti i buoni
        List<Buono> buoniList = new ArrayList<>();

        HashMap<String,String> Allbuoni = client.getAll("buoni");

        for(String key : Allbuoni.keySet()) {
            String buonoString = Allbuoni.get(key);
            Jsonb jsonb = JsonbBuilder.create();
            Buono buono = jsonb.fromJson(buonoString, Buono.class);
            buoniList.add(buono);
        }
        return buoniList;
    }

    public void popolaDatabase() {
        Random random = new Random();

        // Creiamo 10 utenti casuali
        for (int i = 1; i <= 10; i++) {
            String codiceFiscale = "CF" + String.format("%05d", i); // "CF00001", "CF00002", ...
            String nome = "Nome" + i; // Genera un nome casuale (Nome1, Nome2, ...)
            String cognome = "Cognome" + i; // Genera un cognome casuale
            String email = "email" + i + "@example.com"; // Genera un'email casuale

            // Crea un nuovo utente con nome, cognome, email e codice fiscale
            Utente utente = new Utente(nome, cognome, email, codiceFiscale);

            // Aggiungi l'utente al database
            client.addPair("utenti", codiceFiscale, codiceFiscale);  // Ad esempio, associando il codice fiscale come chiave e valore

            System.out.println("Aggiungendo utente con codice fiscale: " + codiceFiscale);

            // Creiamo tra 1 e 3 buoni per ogni utente
            int numBuoni = random.nextInt(3) + 1; // Genera 1, 2 o 3 buoni
            for (int j = 1; j <= numBuoni; j++) {
                String idBuono = String.format("%03d", random.nextInt(1000)); // ID buono casuale
                double importo = (random.nextInt(100) + 1) * 10.0; // Importo casuale tra 10 e 1000
                String tipologia = random.nextBoolean() ? "Sconto" : "Promozione";
                String dataCreazione = "2025-06-" + (random.nextInt(30) + 1);
                String dataConsumo = random.nextBoolean() ? "2025-07-" + (random.nextInt(30) + 1) : null;
                StatoBuono stato = random.nextBoolean() ? StatoBuono.CONSUMATO : StatoBuono.NON_CONSUMATO;

                Buono buono = new Buono(idBuono, codiceFiscale, importo, tipologia, dataCreazione, dataConsumo, stato);
                addBuono(buono);
                System.out.println("Aggiungendo buono con ID: " + idBuono + " per l'utente: " + codiceFiscale);
            }
        }
    }
}

