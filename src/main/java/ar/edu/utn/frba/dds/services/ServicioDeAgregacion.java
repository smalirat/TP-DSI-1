package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import java.time.LocalDateTime;
import java.util.List;

public class ServicioDeAgregacion {

  public void actualizarHechos() {
    System.out.println("Actualizando hechos desde múltiples fuentes...");
    LocalDateTime d = LocalDateTime.now().minusDays(1);
    List<Hecho> hechos = List.of(
        new Hecho("Robo en Palermo", "Robo a mano armada",
            Categoria.SEGURIDAD, Provincia.BUENOS_AIRES, 0.0,
            0.0, d, d, OrigenHecho.CONTRIBUYENTE),
        new Hecho("Corte de luz", "Falla eléctrica",
            Categoria.OTROS, Provincia.BUENOS_AIRES,
            0.0, 0.0, d, d,
            OrigenHecho.CONTRIBUYENTE));

    System.out.println("Se actualizaron " + hechos.size() + " hechos.");
  }
}
