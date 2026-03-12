package ar.edu.utn.frba.dds.model.hechos;

import java.util.List;

public interface Criterio {
  List<Hecho> pertenece(List<Hecho> hecho);

}