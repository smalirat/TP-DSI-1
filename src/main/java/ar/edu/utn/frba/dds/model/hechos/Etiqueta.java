package ar.edu.utn.frba.dds.model.hechos;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class Etiqueta {

  @Column(name = "etiqueta_nombre", nullable = false, length = 80)
  private String nombre;

  public Etiqueta() {}

  public Etiqueta(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Etiqueta etiqueta = (Etiqueta) o;
    return Objects.equals(nombre, etiqueta.nombre);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nombre);
  }

  @Override
  public String toString() {
    return nombre;
  }
}
