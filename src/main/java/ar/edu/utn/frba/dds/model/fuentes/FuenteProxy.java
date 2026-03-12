package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("PROXY")
public abstract class FuenteProxy extends FuenteDatos {

  @Embedded
  protected AdaptadorFuenteExterna adaptador;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "fuente_dinamica_id")
  protected List<Hecho> hechos = new ArrayList<>();

  protected FuenteProxy() {
  }

  protected FuenteProxy(AdaptadorFuenteExterna adaptador) {
    this.adaptador = adaptador;
  }

  public boolean estaDisponible() {
    return adaptador != null && adaptador.estaDisponible();
  }

  public abstract void actualizarHechos();

  @Override
  public List<Hecho> obtenerHechos() {
    return new ArrayList<>(hechos);
  }
}
