package ar.edu.utn.frba.dds.model.hechos;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "hechos_eliminados")
public class HechoEliminado {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 200)
  private String titulo;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String descripcion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Categoria categoria;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Provincia provincia;

  @Column
  private Double latitud;

  @Column
  private Double longitud;

  @Column(nullable = false)
  private LocalDateTime fechaAcontecimiento;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OrigenHecho origen;

  @Column(nullable = false)
  private LocalDateTime fechaEliminacion;

  public HechoEliminado() {
  }

  public HechoEliminado(Hecho hecho) {
    if (hecho == null) {
      throw new IllegalArgumentException("El hecho no puede ser nulo");
    }
    this.titulo = hecho.getTitulo();
    this.descripcion = hecho.getDescripcion();
    this.categoria = hecho.getCategoria();
    this.provincia = hecho.getProvincia();
    this.latitud = hecho.getLatitud();
    this.longitud = hecho.getLongitud();
    this.fechaAcontecimiento = hecho.getFechaAcontecimiento();
    this.origen = hecho.getOrigen();
    this.fechaEliminacion = LocalDateTime.now();
  }

  public HechoEliminado(String titulo, String descripcion, Categoria categoria,
                        Provincia provincia, Double latitud, Double longitud,
                        LocalDateTime fechaAcontecimiento, OrigenHecho origen) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.provincia = provincia;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.origen = origen;
    this.fechaEliminacion = LocalDateTime.now();
  }

  public boolean sonIguales(Hecho otroHecho) {
    if (otroHecho == null) {
      return false;
    }
    return java.util.Objects.equals(this.titulo, otroHecho.getTitulo())
        && java.util.Objects.equals(this.descripcion, otroHecho.getDescripcion())
        && java.util.Objects.equals(this.categoria, otroHecho.getCategoria())
        && java.util.Objects.equals(this.provincia, otroHecho.getProvincia())
        && java.util.Objects.equals(this.latitud, otroHecho.getLatitud())
        && java.util.Objects.equals(this.longitud, otroHecho.getLongitud())
        && java.util.Objects.equals(this.fechaAcontecimiento, otroHecho.getFechaAcontecimiento())
        && java.util.Objects.equals(this.origen, otroHecho.getOrigen());
  }

  public Long getId() {
    return this.id;
  }
}

