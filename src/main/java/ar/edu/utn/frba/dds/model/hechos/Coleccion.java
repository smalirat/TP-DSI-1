package ar.edu.utn.frba.dds.model.hechos;

import ar.edu.utn.frba.dds.model.enums.ModoNavegacion;
import ar.edu.utn.frba.dds.model.enums.TipoAlgoritmoConsenso;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "colecciones", uniqueConstraints = @UniqueConstraint(columnNames = "handle"))
public class Coleccion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 80)
  private String titulo;

  @Column(length = 255)
  private String descripcion;

  @Column(nullable = false, unique = true, length = 40)
  private String handle;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ModoNavegacion modoNavegacion = ModoNavegacion.IRRESTRICTA;

  @ManyToOne
  @JoinColumn(name = "fuente_id", nullable = false)
  private FuenteDatos fuente;

  @Enumerated(EnumType.STRING)
  @Column(name = "tipo_algoritmo_consenso", length = 50, nullable = true)
  private TipoAlgoritmoConsenso tipoAlgoritmoConsenso;

  @ManyToMany
  private List<Hecho> hechosConsensuados = new ArrayList<>();

  public Coleccion() {
  }

  public Coleccion(String titulo, String descripcion, String handle, FuenteDatos fuenteOriginal) {
    validarHandle(handle);
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.handle = handle;
    this.fuente = fuenteOriginal;
    this.modoNavegacion = ModoNavegacion.IRRESTRICTA;
    this.tipoAlgoritmoConsenso = null;
  }

  public List<Hecho> obtenerHechos() {
    if (this.modoNavegacion == ModoNavegacion.IRRESTRICTA) {
      return fuente.obtenerHechos();
    }
    return this.hechosConsensuados;
  }

  private void validarHandle(String handle) {
    if (handle == null || !handle.matches("^[a-zA-Z0-9]+$")) {
      throw new IllegalArgumentException("El handle debe ser un string alfanumérico sin espacios");
    }
  }


  public Long getId() {
    return id;
  }

  public String getHandle() {
    return handle;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public FuenteDatos getFuente() {
    return this.fuente;
  }

  public void setFuente(FuenteDatos nuevaFuente) {
    this.fuente = nuevaFuente;
  }

  public ModoNavegacion getModoNavegacion() {
    return modoNavegacion;
  }

  public void setModoNavegacion(ModoNavegacion modoNavegacion) {
    this.modoNavegacion = modoNavegacion;
  }

  public List<Hecho> getHechosConsensuados() {
    return hechosConsensuados;
  }


  public void setHechosConsensuados(List<Hecho> hechos) {
    this.hechosConsensuados = hechos;
  }

  public TipoAlgoritmoConsenso getTipoAlgoritmoConsenso() {
    return tipoAlgoritmoConsenso;
  }


  public void setTipoAlgoritmoConsenso(TipoAlgoritmoConsenso tipoAlgoritmo) {
    if (tipoAlgoritmo == null || tipoAlgoritmo == TipoAlgoritmoConsenso.NINGUNO) {
      this.tipoAlgoritmoConsenso = null; // Guardamos null en la BD
      this.modoNavegacion = ModoNavegacion.IRRESTRICTA;
    } else {
      this.tipoAlgoritmoConsenso = tipoAlgoritmo;
      this.modoNavegacion = ModoNavegacion.CURADA;
    }
  }

}