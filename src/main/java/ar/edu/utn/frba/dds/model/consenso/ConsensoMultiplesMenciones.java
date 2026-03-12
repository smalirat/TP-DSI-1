package ar.edu.utn.frba.dds.model.consenso;

import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.repositorios.FuenteDatosRepository;
import java.util.List;

public class ConsensoMultiplesMenciones implements AlgoritmoConsenso {

  private FuenteDatosRepository fuenteDatosRepository;

  public ConsensoMultiplesMenciones(FuenteDatosRepository fuenteDatosRepository) {
    this.fuenteDatosRepository = fuenteDatosRepository;
  }

  public ConsensoMultiplesMenciones() {
  }

  @Override
  public boolean cumple(Hecho hecho) {
    List<FuenteDatos> todasLasFuentes = fuenteDatosRepository.findAll();

    if (todasLasFuentes.size() < 2) {
      return false;
    }

    boolean hayContradicciones = todasLasFuentes.stream().anyMatch(fuente ->
        fuente.obtenerHechos().stream()
        .anyMatch(h -> h.tieneMismoTitulo(hecho) && !h.sonIguales(hecho)));

    if (hayContradicciones) {
      return false;
    }

    long fuentesConHechoIgual = todasLasFuentes.stream()
        .mapToLong(fuente -> fuente.obtenerHechos().stream().anyMatch(h
            -> h.sonIguales(hecho)) ? 1 : 0).sum();

    return fuentesConHechoIgual >= 2;
  }
}