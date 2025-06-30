package it.unimib.sd2025.model;

import java.util.ArrayList;
import java.util.List;

public class Utente {
    public String nome;
    public String cognome;
    public String email;
    public String codiceFiscale;
    public List<Buono> buoni = new ArrayList<>();

    public Utente() {}
}
