package it.unimib.sd2025.DatabaseClient;

public interface DatabaseClientInterface {

    int PORTA = 3030;

    public void addSchema(String schemaName);

    public void addPair(String schemaName, String key, String value);

    public void removePair(String schemaName, String key);

    public void updatePair(String schemaName, String key, String value);

    public void getValue(String schemaName, String key);

    public void getAll(String schemaName);
    
} 
