package it.unimib.sd2025.DatabaseClient;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.unimib.sd2025.model.Buono;
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

        client.addPair("haBuono", buono.getIdBuono(), buono.getCodiceFiscale());
        client.addPair("buoni", buono.getIdBuono(), BuonoString);
    }
    public void removeBuono(Buono buono){
        // Implementazione per rimuovere un buono
        client.removePair("haBuono", buono.getIdBuono());
        client.removePair("buoni", buono.getIdBuono());
    }
    public void updateBuono(Buono buono){
        // Implementazione per aggiornare un buono
        Jsonb jsonb = JsonbBuilder.create();
        String BuonoString = jsonb.toJson(buono);
        client.updatePair("buoni", buono.getIdBuono(), BuonoString);
    }
    public List<Buono> getAllBuoni(String codiceFiscale){
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
    
}

