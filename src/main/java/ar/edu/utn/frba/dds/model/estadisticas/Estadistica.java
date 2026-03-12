package ar.edu.utn.frba.dds.model.estadisticas;

import ar.edu.utn.frba.dds.model.enums.TipoEstadistica;
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
@Table(name = "estadisticas")
public class Estadistica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoEstadistica tipo;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(columnDefinition = "TEXT")
  private String parametros;

  @Column(nullable = false)
  private LocalDateTime fechaCalculo;

  @Column(nullable = false)
  private boolean esPublica = true;

  public Estadistica() {
  }

  public Estadistica(TipoEstadistica tipo, String nombre, String parametros, LocalDateTime fechaCalculo,
      boolean esPublica) {
    validarCreacionEstadistica(tipo, nombre);
    this.tipo = tipo;
    this.nombre = nombre;
    this.parametros = parametros;
    this.fechaCalculo = fechaCalculo;
    this.esPublica = esPublica;
  }

  private static void validarCreacionEstadistica(TipoEstadistica tipo, String nombre) {
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de estadística es obligatorio");
    }
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre de la estadística es obligatorio");
    }
  }

  public Long getId() {
    return id;
  }

  public TipoEstadistica getTipo() {
    return tipo;
  }

  public String getParametros() {
    return parametros;
  }

  public void setParametros(String parametros) {
    this.parametros = parametros;
  }

  public LocalDateTime getFechaCalculo() {
    return fechaCalculo;
  }

  public boolean isEsPublica() {
    return esPublica;
  }

  public void setEsPublica(boolean esPublica) {
    this.esPublica = esPublica;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    if (nombre == null || nombre.trim().isEmpty()) {
      throw new IllegalArgumentException("El nombre es obligatorio");
    }
    this.nombre = nombre;
  }
}