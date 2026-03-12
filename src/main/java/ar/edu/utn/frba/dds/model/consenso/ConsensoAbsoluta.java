package ar.edu.utn.frba.dds.model.consenso;

import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.repositorios.FuenteDatosRepository;
import java.util.List;

public class ConsensoAbsoluta implements AlgoritmoConsenso {

  private FuenteDatosRepository fuenteDatosRepository;

  public ConsensoAbsoluta(FuenteDatosRepository fuenteDatosRepository) {
    this.fuenteDatosRepository = fuenteDatosRepository;
  }

  public ConsensoAbsoluta() {
  }

  @Override
  public boolean cumple(Hecho hecho) {
    List<FuenteDatos> todasLasFuentes = fuenteDatosRepository.findAll();

    return !todasLasFuentes.isEmpty() && todasLasFuentes.stream()
        .allMatch(fuente -> fuente.obtenerHechos().stream().anyMatch(h -> h.sonIguales(hecho)));
  }

}