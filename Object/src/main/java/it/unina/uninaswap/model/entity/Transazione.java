package it.unina.uninaswap.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transazione {

    private int id;
    private LocalDate data;

    private BigDecimal importoFinale;     // nullable (scambio/regalo)
    private int annuncioConcluso;         // id annuncio
    private Integer idOffertaAccettata;   // nullable (ma di solito presente)

    private String matricolaVenditore;
    private String matricolaAcquirente;

    public Transazione() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public BigDecimal getImportoFinale() { return importoFinale; }
    public void setImportoFinale(BigDecimal importoFinale) { this.importoFinale = importoFinale; }

    public int getAnnuncioConcluso() { return annuncioConcluso; }
    public void setAnnuncioConcluso(int annuncioConcluso) { this.annuncioConcluso = annuncioConcluso; }

    public Integer getIdOffertaAccettata() { return idOffertaAccettata; }
    public void setIdOffertaAccettata(Integer idOffertaAccettata) { this.idOffertaAccettata = idOffertaAccettata; }

    public String getMatricolaVenditore() { return matricolaVenditore; }
    public void setMatricolaVenditore(String matricolaVenditore) { this.matricolaVenditore = matricolaVenditore; }

    public String getMatricolaAcquirente() { return matricolaAcquirente; }
    public void setMatricolaAcquirente(String matricolaAcquirente) { this.matricolaAcquirente = matricolaAcquirente; }
}
