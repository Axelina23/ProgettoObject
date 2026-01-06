package it.unina.uninaswap.model.entity;

public class Recensione {

    private int id;
    private String titolo;
    private String corpo;
    private int valutazione; // 1..5

    private int idTransazione;
    private String autore;    // matricola acquirente
    private String recensito; // matricola venditore

    public Recensione() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getCorpo() { return corpo; }
    public void setCorpo(String corpo) { this.corpo = corpo; }

    public int getValutazione() { return valutazione; }
    public void setValutazione(int valutazione) { this.valutazione = valutazione; }

    public int getIdTransazione() { return idTransazione; }
    public void setIdTransazione(int idTransazione) { this.idTransazione = idTransazione; }

    public String getAutore() { return autore; }
    public void setAutore(String autore) { this.autore = autore; }

    public String getRecensito() { return recensito; }
    public void setRecensito(String recensito) { this.recensito = recensito; }
}
