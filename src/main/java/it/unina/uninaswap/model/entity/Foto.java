package it.unina.uninaswap.model.entity;

public class Foto {

    private int id;
    private String path;
    private boolean isPrincipale;
    private int idAnnuncio;

    public Foto() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public boolean isPrincipale() { return isPrincipale; }
    public void setPrincipale(boolean principale) { isPrincipale = principale; }

    public int getIdAnnuncio() { return idAnnuncio; }
    public void setIdAnnuncio(int idAnnuncio) { this.idAnnuncio = idAnnuncio; }
    
    @Override
    public String toString() {
        return path;
    }
}
