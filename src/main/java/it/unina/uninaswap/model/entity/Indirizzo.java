package it.unina.uninaswap.model.entity;

public class Indirizzo {

    private int id;
    private String via;
    private String citta;
    private int cap;
    private int civico;
    private String stato;

    private String matricolaStudente; // FK verso studente

    public Indirizzo() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVia() { return via; }
    public void setVia(String via) { this.via = via; }

    public String getCitta() { return citta; }
    public void setCitta(String citta) { this.citta = citta; }

    public int getCap() { return cap; }
    public void setCap(int cap) { this.cap = cap; }

    public int getCivico() { return civico; }
    public void setCivico(int civico) { this.civico = civico; }

    public String getStato() { return stato; }
    public void setStato(String stato) { this.stato = stato; }

    public String getMatricolaStudente() { return matricolaStudente; }
    public void setMatricolaStudente(String matricolaStudente) { this.matricolaStudente = matricolaStudente; }
    
    @Override
    public String toString() {
        return via + " " + civico + ", " + cap + " " + citta + " (" + stato + ")";
    }

}
