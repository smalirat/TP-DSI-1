package ar.edu.utn.frba.dds.model.solicitudes;

import ar.edu.utn.frba.dds.model.enums.EstadoSolicitud;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes_eliminacion")
public class SolicitudEliminacion {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 1000)
  private String textoFundamentacion;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EstadoSolicitud estado;

  @Column(nullable = false)
  private LocalDateTime fechaSolicitud;

  @ManyToOne
  @JoinColumn(name = "hecho_id", nullable = false)
  private Hecho hecho;

  private LocalDateTime fechaResolucion;

  @Column(length = 255)
  private String motivoRechazo;

  public SolicitudEliminacion(String textoFundamentacion, Hecho hecho) {
    validarCampos(textoFundamentacion, hecho);
    this.textoFundamentacion = textoFundamentacion;
    this.fechaSolicitud = LocalDateTime.now();
    this.hecho = hecho;
    this.estado = EstadoSolicitud.PENDIENTE;
  }

  public SolicitudEliminacion() {
  }

  private void validarCampos(String textoFundamentacion, Hecho hecho) {
    if (textoFundamentacion == null || textoFundamentacion.length() < 50) {
      throw new IllegalArgumentException("El fundamento debe tener al menos 50 caracteres");
    }
    if (textoFundamentacion.length() > 1000) {
      throw new IllegalArgumentException("El fundamento no puede exceder los 1000 caracteres");
    }
    if (hecho == null) {
      throw new IllegalArgumentException("El hecho es obligatorio");
    }
  }

  public void aprobar() {
    if (this.estado != EstadoSolicitud.PENDIENTE) {
      throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes");
    }
    this.estado = EstadoSolicitud.ACEPTADA;
    this.fechaResolucion = LocalDateTime.now();
  }

  public void rechazar(String motivo) {
    if (this.estado != EstadoSolicitud.PENDIENTE) {
      throw new IllegalStateException("Solo se pueden rechazar solicitudes pendientes");
    }
    if (motivo == null || motivo.trim().isEmpty()) {
      throw new IllegalArgumentException("El motivo del rechazo es obligatorio");
    }
    this.estado = EstadoSolicitud.RECHAZADA;
    this.fechaResolucion = LocalDateTime.now();
    this.motivoRechazo = motivo.trim();
  }

  public boolean estaPendiente() {
    return this.estado == EstadoSolicitud.PENDIENTE;
  }

  public boolean estaAceptada() {
    return this.estado == EstadoSolicitud.ACEPTADA;
  }

  public boolean estaRechazada() {
    return this.estado == EstadoSolicitud.RECHAZADA;
  }

  public String getTextoFundamentacion() {
    return textoFundamentacion;
  }

  public LocalDateTime getFechaSolicitud() {
    return fechaSolicitud;
  }

  public LocalDateTime getFechaResolucion() {
    return fechaResolucion;
  }

  public Hecho getHecho() {
    return hecho;
  }

  public EstadoSolicitud getEstado() {
    return estado;
  }

  public String getMotivoRechazo() {
    return motivoRechazo;
  }

  @Override
  public String toString() {
    return "SolicitudEliminacion{" + "id=" + id + ", estado=" + estado + ", hecho="
        + (hecho != null ? hecho.getTitulo() : null) + ", textoFundamentacion='"
        + (textoFundamentacion != null
            ? textoFundamentacion.substring(0, Math.min(20,
                textoFundamentacion.length())) + "..."
            : null)
        + "}";
  }

  public Long getId() {
    return this.id;
  }
}
