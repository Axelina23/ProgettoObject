package it.unina.uninaswap.model.enums;

public enum TipoCategoria {Strumenti_musicali, Libri, Informatica, Abbigliamento, Arredo, Altro;

    @Override public String toString() {
        return name().replace('_',' ');
    }
}
