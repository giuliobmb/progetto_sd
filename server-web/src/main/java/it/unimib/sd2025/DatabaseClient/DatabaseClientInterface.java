package it.unimib.sd2025.DatabaseClient;

import java.util.HashMap;

public interface DatabaseClientInterface {

    int PORTA = 3030;

    public void addSchema(String schemaName);

    public void addPair(String schemaName, String key, String value);

    public void removePair(String schemaName, String key);

    public void updatePair(String schemaName, String key, String value);

    public String getValue(String schemaName, String key);

    public HashMap<String,String> getAll(String schemaName);
    
} 
