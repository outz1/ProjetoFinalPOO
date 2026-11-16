package model;

import java.io.Serializable;

/** Todo objeto do modelo tem um codigo e pode ser gravado em arquivo */
public interface Identificavel extends Serializable {
    // TODO: devolve o codigo unico do objeto
    int getId();
}
