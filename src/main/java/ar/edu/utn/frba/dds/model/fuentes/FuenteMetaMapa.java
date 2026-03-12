package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("METAMAPA")
public class FuenteMetaMapa extends FuenteProxy {

  @Column(name = "identificador_coleccion")
  private String identificadorColeccion;

  @Embedded
  private ClienteApiMetaMapa clienteApi;

  protected FuenteMetaMapa() {
  }

  public FuenteMetaMapa(ClienteApiMetaMapa clienteApi, String identificadorColeccion) {
    super(new AdaptadorFuenteMetaMapa(clienteApi, identificadorColeccion));
    this.clienteApi = clienteApi;
    this.identificadorColeccion = identificadorColeccion;
  }

  @Override
  public void actualizarHechos() {
    if (estaDisponible()) {
      actualizarHechosLocales(adaptador.obtenerHechos());
    }
  }

  public void actualizarHechosLocales(List<Hecho> hechosList) {
    this.hechos.clear();
    this.hechos.addAll(hechosList);
  }

  public String getIdentificadorColeccion() {
    return identificadorColeccion;
  }

  public ClienteApiMetaMapa getClienteApi() {
    return clienteApi;
  }
}
