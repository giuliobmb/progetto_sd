package it.unimib.sd2025.model;

public class Buono {
    private String idBuono;
    private String codiceFiscale;
    private double importo;
    private String tipologia;
    private String dataCreazione;
    private String dataConsumo;
    private String stato;
    
    public String getIdBuono() {
        return idBuono;
    }
    public void setIdBuono(String idBuono) {
        this.idBuono = idBuono;
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
    public String getStato() {
        return stato;
    }
    public void setStato(String stato) {
        this.stato = stato;
    }

    
}
