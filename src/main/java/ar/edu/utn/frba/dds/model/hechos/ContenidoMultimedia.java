package ar.edu.utn.frba.dds.model.hechos;

import ar.edu.utn.frba.dds.model.enums.TipoMultimedia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "contenidos_multimedia")
public class ContenidoMultimedia {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String url;

  @Column(nullable = false, length = 255)
  private String formato;

  @Column(length = 255)
  private String descripcion;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TipoMultimedia tipo;

  public ContenidoMultimedia() {
  }

  public ContenidoMultimedia(String url, TipoMultimedia tipo, String descripcion, String formato) {
    validarCampos(url, tipo, formato);
    this.url = url;
    this.tipo = tipo;
    this.descripcion = descripcion;
    this.formato = formato;
  }

  private void validarCampos(String url, TipoMultimedia tipo, String formato) {
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalArgumentException("La URL es obligatoria");
    }
    if (tipo == null) {
      throw new IllegalArgumentException("El tipo de multimedia es obligatorio");
    }
    if (formato == null || formato.trim().isEmpty()) {
      throw new IllegalArgumentException("El formato es obligatorio");
    }
  }

  public String getUrl() {
    return url;
  }

  public TipoMultimedia getTipo() {
    return tipo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public String getFormato() {
    return formato;
  }

  public Long getId() {
    return id;
  }

}