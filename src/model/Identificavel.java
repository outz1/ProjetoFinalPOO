package model;

import java.io.Serializable;

/** Todo objeto do modelo tem um código e pode ser gravado em arquivo. */
public interface Identificavel extends Serializable {
    int getId();
}
