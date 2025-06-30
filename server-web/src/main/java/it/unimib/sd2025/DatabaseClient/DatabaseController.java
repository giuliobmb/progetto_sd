package it.unimib.sd2025.DatabaseClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.sd2025.model.Buono;
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
    
}

