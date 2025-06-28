package it.unimib.sd2025.DatabaseUtility;

public interface DatabaseInterface {

    int PORTA = 3030;

    public void addSchema();

    public void addPair();

    public void removePair();

    public void updatePair();

    public void getValue();

    public void getAll();
    
} 
