package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Embeddable
public class AdaptadorFuenteDemo implements AdaptadorFuenteExterna {

  @Transient
  private Conexion conexion;

  @Column(name = "url")
  private String url;

  @Column(name = "ultima_actualizacion")
  private LocalDateTime ultimaActualizacion;

  public AdaptadorFuenteDemo() {
  }

  public AdaptadorFuenteDemo(Conexion conexion, String url) {
    this.conexion = conexion;
    this.url = url;
    this.ultimaActualizacion = LocalDateTime.now();
  }

  @Override
  public List<Hecho> obtenerHechos() {

    List<Hecho> hechos = new ArrayList<>();
    Map<String, Object> hechoExterno;
    while ((hechoExterno = conexion.siguienteHecho(url, ultimaActualizacion)) != null) {
      hechos.add(mapearHechoExterno(hechoExterno));
    }
    ultimaActualizacion = LocalDateTime.now();
    return hechos;
  }

  @Override
  public boolean estaDisponible() {

    return conexion != null && url != null;
  }

  private Hecho mapearHechoExterno(Map<String, Object> datos) {
    Categoria categoria = Categoria.fromString((String) datos.get("categoria"));
    Provincia provincia = Provincia.fromString((String) datos.get("provincia"));
    return new Hecho((String) datos.get("titulo"),
        (String) datos.get("descripcion"), categoria, provincia,
        (double) datos.get("latitud"), (double) datos.get("longitud"),
        (LocalDateTime) datos.get("fechaAcontecimiento"), LocalDateTime.now(), OrigenHecho.FUENTE_EXTERNA);
  }
}