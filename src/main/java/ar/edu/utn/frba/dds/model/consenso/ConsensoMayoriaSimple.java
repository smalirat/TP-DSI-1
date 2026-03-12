package ar.edu.utn.frba.dds.model.consenso;

import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.repositorios.FuenteDatosRepository;
import java.util.List;

public class ConsensoMayoriaSimple implements AlgoritmoConsenso {

  private FuenteDatosRepository fuenteDatosRepository;

  public ConsensoMayoriaSimple(FuenteDatosRepository fuenteDatosRepository) {
    this.fuenteDatosRepository = fuenteDatosRepository;
  }

  public ConsensoMayoriaSimple() {
  }

  @Override
  public boolean cumple(Hecho hecho) {
    List<FuenteDatos> todasLasFuentes = fuenteDatosRepository.findAll();

    if (todasLasFuentes.isEmpty()) {
      return false;
    }

    long fuentesConHecho = todasLasFuentes.stream()
        .mapToLong(fuente ->
            fuente.obtenerHechos().stream().anyMatch(h -> h.sonIguales(hecho)) ? 1 : 0).sum();

    return fuentesConHecho > (todasLasFuentes.size() / 2);
  }
}