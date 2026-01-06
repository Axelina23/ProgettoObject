package it.unina.uninaswap.model.entity;

import java.time.LocalDate;

public class Consegna {

    private Integer id;

    private String note;

    // Solo per incontro in uni
    private String sedeUniversita;
    private String fasciaOraria;
    private LocalDate dataIncontro;

    private boolean spedizione;
    private boolean incontroInUni;

    private Integer idTransazione;
    private Integer idIndirizzo; // solo se spedizione

    // getter & setter

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getSedeUniversita() { return sedeUniversita; }
    public void setSedeUniversita(String sedeUniversita) { this.sedeUniversita = sedeUniversita; }

    public String getFasciaOraria() { return fasciaOraria; }
    public void setFasciaOraria(String fasciaOraria) { this.fasciaOraria = fasciaOraria; }

    public LocalDate getDataIncontro() { return dataIncontro; }
    public void setDataIncontro(LocalDate dataIncontro) { this.dataIncontro = dataIncontro; }

    public boolean isSpedizione() { return spedizione; }
    public void setSpedizione(boolean spedizione) { this.spedizione = spedizione; }

    public boolean isIncontroInUni() { return incontroInUni; }
    public void setIncontroInUni(boolean incontroInUni) { this.incontroInUni = incontroInUni; }

    public Integer getIdTransazione() { return idTransazione; }
    public void setIdTransazione(Integer idTransazione) { this.idTransazione = idTransazione; }

    public Integer getIdIndirizzo() { return idIndirizzo; }
    public void setIdIndirizzo(Integer idIndirizzo) { this.idIndirizzo = idIndirizzo; }
}
