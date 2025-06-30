package it.unimib.sd2025.model;

import java.time.LocalDateTime;

public class Buono {
    public String id;
    public double importo;
    public Tipologia tipologia;
    public StatoBuono stato = StatoBuono.NON_CONSUMATO;
    public LocalDateTime dataCreazione;
    public LocalDateTime dataConsumo;

    public Buono() {}
}
