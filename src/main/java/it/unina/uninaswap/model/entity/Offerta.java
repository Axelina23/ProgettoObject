package it.unina.uninaswap.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import it.unina.uninaswap.model.enums.StatoOfferta;

public class Offerta {

    private int id;
    private LocalDate data;
    private StatoOfferta stato;

    private BigDecimal importoOfferto; // nullable (solo Vendita)
    private String messaggio;          // obbligatorio per Regalo
    private String oggettoOfferto;     // obbligatorio per Scambio

    private int idAnnuncio;
    private String matricolaOfferente;

    public Offerta() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public StatoOfferta getStato() { return stato; }
    public void setStato(StatoOfferta stato) { this.stato = stato; }

    public BigDecimal getImportoOfferto() { return importoOfferto; }
    public void setImportoOfferto(BigDecimal importoOfferto) { this.importoOfferto = importoOfferto; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    public String getOggettoOfferto() { return oggettoOfferto; }
    public void setOggettoOfferto(String oggettoOfferto) { this.oggettoOfferto = oggettoOfferto; }

    public int getIdAnnuncio() { return idAnnuncio; }
    public void setIdAnnuncio(int idAnnuncio) { this.idAnnuncio = idAnnuncio; }

    public String getMatricolaOfferente() { return matricolaOfferente; }
    public void setMatricolaOfferente(String matricolaOfferente) { this.matricolaOfferente = matricolaOfferente; }
}
