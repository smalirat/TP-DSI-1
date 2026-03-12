package ar.edu.utn.frba.dds.model.consenso;

import ar.edu.utn.frba.dds.model.hechos.Hecho;

public interface AlgoritmoConsenso {

  boolean cumple(Hecho hecho);
}