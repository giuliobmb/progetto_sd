package it.unimib.sd2025.model;

import java.util.ArrayList;
import java.util.List;

public class Utente {

    public String nome;
    public String cognome;
    public String email;
    public String codiceFiscale;
    public double importo;


    public Utente() {}
    
    public String getNome() {
        return nome;
    }

    public Utente(String nome, String cognome, String email, String codiceFiscale) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.codiceFiscale = codiceFiscale;
        this.importo = 500;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }
    public double getImporto() {
        return importo;
    }
    public void setImporto(double importo) {
        this.importo = importo;
    }
}
