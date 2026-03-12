package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Embeddable
public class AdaptadorFuenteMetaMapa implements AdaptadorFuenteExterna {

  @Embedded
  private ClienteApiMetaMapa clienteApi;

  @Column(name = "identificador_coleccion")
  private String identificadorColeccion;

  public AdaptadorFuenteMetaMapa() {

  }

  public AdaptadorFuenteMetaMapa(ClienteApiMetaMapa clienteApi,
                                 String identificadorColeccion) {
    this.clienteApi = clienteApi;
    this.identificadorColeccion = identificadorColeccion;
  }

  @Override
  public List<Hecho> obtenerHechos() {
    Map<String, String> filtros = new HashMap<>();
    if (identificadorColeccion != null) {
      filtros.put("coleccion", identificadorColeccion);
    }
    return clienteApi.obtenerHechos(filtros);
  }

  @Override
  public boolean estaDisponible() {
    return clienteApi != null;
  }
}