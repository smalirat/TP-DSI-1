package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("AGREGADOR")

public class Agregador extends FuenteDatos {

  @ManyToMany
  @JoinTable(name = "agregador_fuentes", joinColumns = @JoinColumn(name = "agregador_id"),
      inverseJoinColumns = @JoinColumn(name = "fuente_id"))
  private List<FuenteDatos> fuentes = new ArrayList<>();

  @Transient
  private List<Hecho> hechosCache = new ArrayList<>();

  public Agregador() {
    this.fuentes = new ArrayList<>();
    this.hechosCache = new ArrayList<>();
  }

  public Agregador(List<FuenteDatos> fuentes) {
    this.fuentes = new ArrayList<>(fuentes);
    this.hechosCache = new ArrayList<>();
    actualizarCache();
  }

  @Override
  public List<Hecho> obtenerHechos() {
    return new ArrayList<>(hechosCache);
  }

  public synchronized void actualizarCache() {
    this.hechosCache = fuentes.stream().flatMap(fuente -> fuente.obtenerHechos().stream())
        .collect(Collectors.toList());
  }

  public void agregarFuente(FuenteDatos fuente) {
    this.fuentes.add(fuente);
    actualizarCache();
  }

  public void removerFuente(FuenteDatos fuente) {
    this.fuentes.remove(fuente);
    actualizarCache();
  }

  public List<FuenteDatos> getFuentes() {
    return new ArrayList<>(fuentes);
  }
}