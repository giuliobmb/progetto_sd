package it.unimib.sd2025.model;

public class Buono {
    private String id;
    private String codiceFiscale;
    private double importo;
    private String tipologia;
    private String dataCreazione;
    private String dataConsumo;
    private StatoBuono stato = StatoBuono.NON_CONSUMATO;


    public Buono() {}
    public String getId() {
        return id;
    }
    
    public Buono(String id, String codiceFiscale, double importo, String tipologia, String dataCreazione, String dataConsumo, StatoBuono stato) {
        this.id = id;
        this.codiceFiscale = codiceFiscale;
        this.importo = importo;
        this.tipologia = tipologia;
        this.dataCreazione = dataCreazione;
        this.dataConsumo = dataConsumo;
        this.stato = stato;
    }
    public void setId(String id) {
        this.id = id;
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
    public String getTipologia() {
        return tipologia;
    }
    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }
    public String getDataCreazione() {
        return dataCreazione;
    }
    public void setDataCreazione(String dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    public String getDataConsumo() {
        return dataConsumo;
    }
    public void setDataConsumo(String dataConsumo) {
        this.dataConsumo = dataConsumo;
    }
    public StatoBuono getStato() {
        return stato;
    }
    public void setStato(StatoBuono stato) {
        this.stato = stato;
    }
    public boolean isConsumato() {
        if (stato == StatoBuono.CONSUMATO) {
            return true;
        } else if (stato == StatoBuono.NON_CONSUMATO) {
            return false;
        } else {
            throw new IllegalStateException("Stato del buono non valido: " + stato);
        }

    }
    @Override
    public String toString() {
        return "Buono{" +
                "id='" + id + '\'' +
                ", codiceFiscale='" + codiceFiscale + '\'' +
                ", importo=" + importo +
                ", tipologia='" + tipologia + '\'' +
                ", dataCreazione='" + dataCreazione + '\'' +
                ", dataConsumo='" + dataConsumo + '\'' +
                ", stato=" + stato +
                '}';
    }

}