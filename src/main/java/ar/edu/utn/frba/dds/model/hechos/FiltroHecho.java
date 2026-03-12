package ar.edu.utn.frba.dds.model.hechos;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FiltroHecho {
  private final Set<String> titulos = new HashSet<>();
  private final Set<String> categorias = new HashSet<>();
  private final Set<LocalDate> fechas = new HashSet<>();

  public void agregarTitulo(String titulo) {
    titulos.add(titulo);
  }

  public void agregarCategoria(String categoria) {
    categorias.add(categoria);
  }

  public void agregarFecha(LocalDate fecha) {
    fechas.add(fecha);
  }

  public boolean aplica(Hecho hecho) {
    boolean coincideTitulo = titulos.isEmpty() || titulos.contains(hecho.getTitulo());
    boolean coincideCategoria = categorias.isEmpty() || categorias.contains(hecho.getCategoria());
    boolean coincideFecha = fechas.isEmpty() || fechas.contains(hecho.getFechaAcontecimiento());

    return coincideTitulo || coincideCategoria || coincideFecha;
  }
}