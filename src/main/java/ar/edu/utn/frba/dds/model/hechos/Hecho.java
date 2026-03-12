package ar.edu.utn.frba.dds.model.hechos;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "hechos")
public class Hecho {
  @Column(nullable = false)
  private LocalDateTime fechaCarga;

  @Column
  private LocalDateTime fechaUltimaModificacion;

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

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "hecho_etiquetas", joinColumns = @JoinColumn(name = "hecho_id"))
  private List<Etiqueta> etiquetas = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  private List<ContenidoMultimedia> contenidoMultimedia = new ArrayList<>();

  public Hecho() {
  }

  public Hecho(String titulo, String descripcion,
      Categoria categoria, Provincia provincia, Double latitud,
      Double longitud, LocalDateTime fechaAcontecimiento,
      LocalDateTime fechaCarga, OrigenHecho origen) {
    validarCreacionHecho(titulo, descripcion, categoria,
        provincia, fechaAcontecimiento, fechaCarga);
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.provincia = provincia;
    this.latitud = latitud;
    this.longitud = longitud;
    this.fechaAcontecimiento = fechaAcontecimiento;
    this.fechaCarga = fechaCarga;
    this.fechaUltimaModificacion = fechaCarga;
    this.origen = origen;
    this.contenidoMultimedia = new ArrayList<>();
    this.etiquetas = new ArrayList<>();
  }

  private static void validarCreacionHecho(String titulo, String descripcion, Categoria categoria,
      Provincia provincia, LocalDateTime fechaAcontecimiento, LocalDateTime fechaCarga) {
    if (titulo == null || titulo.trim().isEmpty()) {
      throw new IllegalArgumentException("El título es obligatorio");
    }
    if (descripcion == null || descripcion.trim().isEmpty()) {
      throw new IllegalArgumentException("La descripción es obligatoria");
    }
    if (categoria == null) {
      throw new IllegalArgumentException("La categoría es obligatoria");
    }
    if (fechaAcontecimiento == null) {
      throw new IllegalArgumentException("La fecha del acontecimiento es obligatoria");
    }
    if (fechaCarga == null) {
      throw new IllegalArgumentException("La fecha de carga es obligatoria");
    }
    if (fechaAcontecimiento.isAfter(LocalDateTime.now())) {
      throw new IllegalArgumentException("La fecha del acontecimiento no puede ser futura");
    }
  }

  public boolean sonIguales(Hecho otroHecho) {
    if (otroHecho == null) {
      return false;
    }
    return Objects.equals(this.titulo, otroHecho.getTitulo())
        && Objects.equals(this.descripcion, otroHecho.getDescripcion())
        && Objects.equals(this.categoria, otroHecho.getCategoria())
        && Objects.equals(this.provincia, otroHecho.getProvincia())
        && Objects.equals(this.latitud, otroHecho.getLatitud())
        && Objects.equals(this.longitud, otroHecho.getLongitud())
        && Objects.equals(this.fechaAcontecimiento, otroHecho.getFechaAcontecimiento())
        && Objects.equals(this.origen, otroHecho.getOrigen());
  }

  public boolean tieneMismoTitulo(Hecho otroHecho) {
    return this.titulo.equals(otroHecho.getTitulo());
  }

  public void agregarContenidoMultimedia(ContenidoMultimedia contenido) {
    if (contenido != null) {
      this.contenidoMultimedia.add(contenido);
      this.fechaUltimaModificacion = LocalDateTime.now();
    }
  }

  public void agregarEtiqueta(Etiqueta etiqueta) {
    if (etiqueta != null
        && this.etiquetas.stream().noneMatch(e -> e.getNombre().equalsIgnoreCase(etiqueta.getNombre()))) {
      this.etiquetas.add(etiqueta);
      this.fechaUltimaModificacion = LocalDateTime.now();
    }
  }

  public Long getId() {
    return id;
  }

  public String getTitulo() {
    return titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public Categoria getCategoria() {
    return categoria;
  }

  public Provincia getProvincia() {
    return provincia;
  }

  public Double getLatitud() {
    return latitud;
  }

  public Double getLongitud() {
    return longitud;
  }

  public LocalDateTime getFechaAcontecimiento() {
    return fechaAcontecimiento;
  }

  public LocalDateTime getFechaCarga() {
    return fechaCarga;
  }

  public OrigenHecho getOrigen() {
    return origen;
  }

  public List<Etiqueta> getEtiquetas() {
    return new ArrayList<>(etiquetas);
  } 

  public List<ContenidoMultimedia> getContenidoMultimedia() {
    return new ArrayList<>(contenidoMultimedia);
  }

  public void setFechaCarga(LocalDateTime fechaCarga) {
    this.fechaCarga = fechaCarga;
  }

  public LocalDateTime getFechaUltimaModificacion() {
    return fechaUltimaModificacion;
  }

  public void setFechaUltimaModificacion(LocalDateTime fechaUltimaModificacion) {
    this.fechaUltimaModificacion = fechaUltimaModificacion;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public void setCategoria(Categoria categoria) {
    this.categoria = categoria;
  }

  public void setProvincia(Provincia provincia) {
    this.provincia = provincia;
  }

  public void setLatitud(Double latitud) {
    this.latitud = latitud;
  }

  public void setLongitud(Double longitud) {
    this.longitud = longitud;
  }

  public void setFechaAcontecimiento(LocalDateTime fechaAcontecimiento) {
    this.fechaAcontecimiento = fechaAcontecimiento;
  }

  public void setOrigen(OrigenHecho origen) {
    this.origen = origen;
  }

  public void setEtiquetas(List<Etiqueta> etiquetas) {
    this.etiquetas = etiquetas;
  }

  public void setContenidoMultimedia(List<ContenidoMultimedia> contenidoMultimedia) {
    this.contenidoMultimedia = contenidoMultimedia;
  }

  public void setTitulo(String nuevoTitulo) {
    this.titulo = nuevoTitulo;
    this.fechaUltimaModificacion = LocalDateTime.now();
  }

}
