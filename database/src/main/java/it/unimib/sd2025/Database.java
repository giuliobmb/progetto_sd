package it.unimib.sd2025;

import java.net.*;
import java.io.*;
import java.util.*;

/*
 * Classe Database
 * classe singleton che permette la creazione e eliminazione di schemi chiave valore, oltre che la gestione degli schemi stessi
 */


public class Database{

    private static final Database INSTANCE = new Database();
    private HashMap<String, HashMap<String, String>> schemi;

    private Database(){
        this.schemi = new HashMap<String, HashMap<String, String>>();
    }

    public static Database get(){
        return INSTANCE;
    }

    public synchronized Database addSchema(String nomeSchema){
        if(schemi.get(nomeSchema) != null){
            return null;
        }
        schemi.put(nomeSchema, new HashMap<String, String>());
        return INSTANCE;
    }

    public synchronized Database addPair(String nomeSchema, String key, String value){
        if(schemi.get(nomeSchema) == null){
            return null;
        }
        schemi.get(nomeSchema).put(key, value);
        return INSTANCE;
    }

    public synchronized Database updatePair(String nomeSchema, String key, String value){
        if(schemi.get(nomeSchema) == null){
            return null;
        }
        if(schemi.get(nomeSchema).get(key) == null){
            return null;
        }
        schemi.get(nomeSchema).replace(key, value);
        return INSTANCE;
    }

    public synchronized Database removePair(String nomeSchema, String key, String value){
        if(schemi.get(nomeSchema) == null){
            return null;
        }
        schemi.get(nomeSchema).remove(key);
        return INSTANCE;
    }

    public synchronized String getValue(String nomeSchema, String key){
        if(schemi.get(nomeSchema) == null){
            return null;
        }
        if(schemi.get(nomeSchema).get(key) == null){
            return null;
        }
        return schemi.get(nomeSchema).get(key);
    }

    

}