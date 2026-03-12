package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("DINAMICA")
public class FuenteDinamica extends FuenteDatos {

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "fuente_dinamica_id")
  private List<Hecho> hechos = new ArrayList<>();

  public void cargarHecho(Hecho hecho) {
    hechos.add(hecho);
  }

  @Override
  public List<Hecho> obtenerHechos() {
    return new ArrayList<>(hechos);
  }

  public void modificarHecho(Hecho hechoOriginal, Hecho hechoModificado) {
    if (!hechos.contains(hechoOriginal)) {
      throw new RuntimeException("El hecho original no fue encontrado en la fuente.");
    }

    long diasDesdeCreacion = java.time.temporal.ChronoUnit
        .DAYS.between(hechoOriginal.getFechaAcontecimiento(),
        java.time.LocalDate.now());
    if (diasDesdeCreacion > 7) {
      throw new RuntimeException("El hecho solo puede modificarse dentro de una semana.");
    }

    int index = hechos.indexOf(hechoOriginal);
    hechos.set(index, hechoModificado);
  }

  public List<Hecho> getHechos() {
    return this.hechos;
  }
}
