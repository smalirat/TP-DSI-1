package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import java.util.List;

public interface AdaptadorFuenteExterna {

  List<Hecho> obtenerHechos();

  boolean estaDisponible();

}
