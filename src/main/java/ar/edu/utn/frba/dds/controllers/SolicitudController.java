package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.services.HechoService;
import ar.edu.utn.frba.dds.services.SolicitudService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class SolicitudController {

  private HechoService hechoService;
  private SolicitudService solicitudService;

  public SolicitudController() {
  }

  public void initServices(){
    if(this.hechoService == null){
      this.hechoService = new HechoService();
      this.solicitudService = new SolicitudService();
    }
  }

  public void nuevo(Context ctx) {
    try {
      initServices();
      Long hechoId = Long.parseLong(ctx.pathParam("id"));
      Hecho hecho = hechoService.obtenerHechoPorId(hechoId);

      if (hecho != null) {
        Map<String, Object> model = new HashMap<>();
        model.put("hecho", hecho);
        if (ctx.sessionAttribute("usuarioRol") == "ADMIN") {
          ctx.render("solicitud_nueva.hbs", model);
        } else {
          ctx.redirect("/");
        }
      } else {
        ctx.status(404).result("Hecho no encontrado.");
      }
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de hecho inválido.");
    } catch (Exception e) {
      ctx.status(500).result("Error al obtener el hecho: " + e.getMessage());
    }
  }

  public void save(Context ctx) {
    try {
      initServices();
      Long hechoId = Long.parseLong(ctx.pathParam("id"));
      String motivo = ctx.formParam("motivo");

      solicitudService.crearSolicitudEliminacion(hechoId, motivo);
      ctx.render("solicitud_enviada.hbs");

    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de hecho inválido.");
    } catch (IllegalArgumentException e) {
      ctx.status(404).result("Hecho no encontrado.");
    } catch (Exception e) {
      ctx.status(500).result("Error al procesar la solicitud: " + e.getMessage());
    }
  }
}