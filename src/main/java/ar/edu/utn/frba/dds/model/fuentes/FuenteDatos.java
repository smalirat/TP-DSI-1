package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "fuente_datos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_fuente", discriminatorType = DiscriminatorType.STRING)
public abstract class   FuenteDatos {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract List<Hecho> obtenerHechos();

  // REVISAR ELIMINACION DE HECHOS. NO SE HACE EN FUENTE.
  /*
   * public void eliminarHecho(Hecho hecho) { List<Hecho> hechos = this.obtenerHechos();
   *
   * boolean encontrado = hechos.stream() .anyMatch(h -> h.getTitulo().equals(hecho.getTitulo()));
   *
   * if (!encontrado) { throw new RuntimeException("El hecho no fue encontrado en la fuente."); }
   *
   * hecho.setEliminado(true); }
   */

  public Long getId() {
    return id;
  }
}
