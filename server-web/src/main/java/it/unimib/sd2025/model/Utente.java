package it.unimib.sd2025.model;

public class Utente {

    private String codiceFiscale;

    public Utente(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    public Utente() {
        // Costruttore vuoto per la serializzazione/deserializzazione
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
    
}