package it.unina.uninaswap.model.entity;

import it.unina.uninaswap.model.enums.SessoStudente;

public class Studente {

    private String matricola;
    private String email;
    private String nome;
    private String cognome;
    private String password;

    private SessoStudente sesso;

    private boolean preferisceSpedizione;
    private boolean preferisceIncontroInUni;

    public Studente() {}

    public String getMatricola() { return matricola; }
    public void setMatricola(String matricola) { this.matricola = matricola; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public SessoStudente getSesso() { return sesso; }
    public void setSesso(SessoStudente sesso) { this.sesso = sesso; }

    public boolean getPreferisceSpedizione() { return preferisceSpedizione; }
    public void setPreferisceSpedizione(boolean preferisceSpedizione) { this.preferisceSpedizione = preferisceSpedizione; }

    public boolean getPreferisceIncontroInUni() { return preferisceIncontroInUni; }
    public void setPreferisceIncontroInUni(boolean preferisceIncontroInUni) { this.preferisceIncontroInUni = preferisceIncontroInUni; }
}
