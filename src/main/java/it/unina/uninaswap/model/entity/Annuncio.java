package it.unina.uninaswap.model.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Annuncio {

    private Integer id; // SERIAL -> Integer (null quando non ancora salvato)
    private String titolo;
    private String descrizione; // nullable
    private LocalDate dataPubblicazione;

    // enum DB -> per ora String (es: "Vendita", "Scambio", "Regalo")
    private String tipologia;

    // enum DB -> per ora String (es: "Libri", "Informatica", ...)
    private String categoria;

    private String oggettoRichiesto; // nullable
    private boolean concluso;

    // NUMERIC(9,2) -> BigDecimal (nullable: non c'è per Scambio/Regalo)
    private BigDecimal prezzo; // nullable

    private boolean offreSpedizione;
    private boolean offreIncontroInUni;

    private String matricolaVenditore;

    public Annuncio() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getTitolo() { return titolo; }
    public void setTitolo(String titolo) { this.titolo = titolo; }

    public String getDescrizione() { return descrizione; }
    public void setDescrizione(String descrizione) { this.descrizione = descrizione; }

    public LocalDate getDataPubblicazione() { return dataPubblicazione; }
    public void setDataPubblicazione(LocalDate dataPubblicazione) { this.dataPubblicazione = dataPubblicazione; }

    public String getTipologia() { return tipologia; }
    public void setTipologia(String tipologia) { this.tipologia = tipologia; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getOggettoRichiesto() { return oggettoRichiesto; }
    public void setOggettoRichiesto(String oggettoRichiesto) { this.oggettoRichiesto = oggettoRichiesto; }

    public boolean isConcluso() { return concluso; }
    public void setConcluso(boolean concluso) { this.concluso = concluso; }

    public BigDecimal getPrezzo() { return prezzo; }
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    public boolean isOffreSpedizione() { return offreSpedizione; }
    public void setOffreSpedizione(boolean offreSpedizione) { this.offreSpedizione = offreSpedizione; }

    public boolean isOffreIncontroInUni() { return offreIncontroInUni; }
    public void setOffreIncontroInUni(boolean offreIncontroInUni) { this.offreIncontroInUni = offreIncontroInUni; }

    public String getMatricolaVenditore() { return matricolaVenditore; }
    public void setMatricolaVenditore(String matricolaVenditore) { this.matricolaVenditore = matricolaVenditore; }

    @Override
    public String toString() {
        return "Annuncio{" +
                "id=" + id +
                ", titolo='" + titolo + '\'' +
                ", tipologia='" + tipologia + '\'' +
                ", categoria='" + categoria + '\'' +
                ", prezzo=" + prezzo +
                ", concluso=" + concluso +
                ", matricolaVenditore='" + matricolaVenditore + '\'' +
                '}';
    }
}
