package it.unimib.sd2025.model;

public class BuonoRequestPayload {
    private String CF;
    private String importo;

    // Getters e setters
    public String getCF() {
        return CF;
    }

    public void setCF(String CF) {
        this.CF = CF;
    }

    public String getImporto() {
        return importo;
    }

    public void setImporto(String importo) {
        this.importo = importo;
    }
}