package ar.edu.utn.frba.dds.model.fuentes;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("DEMO")
public class FuenteDemo extends FuenteProxy {

  @Transient
  private Conexion conexion;

  @Column(name = "url")
  private String url;

  protected FuenteDemo() {
  }

  public FuenteDemo(Conexion conexion, String url) {
    super(new AdaptadorFuenteDemo(validarConexion(conexion), url));
    this.conexion = validarConexion(conexion);
    this.url = validarUrl(url);
  }

  private static Conexion validarConexion(Conexion conexion) {
    if (conexion == null) {
      throw new IllegalArgumentException("La conexión no puede ser nula");
    }
    return conexion;
  }

  private static String validarUrl(String url) {
    if (url == null) {
      throw new IllegalArgumentException("La URL no puede ser nula");
    }
    return url;
  }

  @Override
  public void actualizarHechos() {
    if (estaDisponible()) {
      this.hechos = adaptador.obtenerHechos();
    }
  }

  public Conexion getConexion() {
    return conexion;
  }

  public String getUrl() {
    return url;
  }
}
