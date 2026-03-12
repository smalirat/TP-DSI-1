package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.solicitudes.SolicitudEliminacion;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.FuenteDatosService;
import ar.edu.utn.frba.dds.services.HechoService;
import ar.edu.utn.frba.dds.services.SolicitudService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdminController extends BaseController {

  private SolicitudService solicitudService;
  private HechoService hechoService;
  private FuenteDatosService fuenteDatosService;
  private ColeccionService coleccionService;

  public AdminController() {

  }

  public void initServices(){
    if (this.solicitudService == null){
      this.coleccionService = new ColeccionService();
      this.hechoService = new HechoService();
      this.fuenteDatosService = new FuenteDatosService();
      this.solicitudService = new SolicitudService();
    }
  }

  public void mostrarPanelPrincipal(Context ctx) {
    initServices();
    Map<String, Object> model = new HashMap<>();
    renderWithSession(ctx, "admin_home.hbs", model);
  }

  public void listarSolicitudes(Context ctx) {
    try {
      initServices();
      List<SolicitudEliminacion> solicitudes = solicitudService.obtenerSolicitudesPendientes();
      Map<String, Object> model = new HashMap<>();
      model.put("solicitudes", solicitudes);
      renderWithSession(ctx, "solicitudes.hbs", model);
    } catch (Exception e) {
      ctx.status(500).result("Error al obtener las solicitudes: " + e.getMessage());
    }
  }

  public void aprobarSolicitud(Context ctx) {
    try {
      initServices();
      Long id = Long.parseLong(ctx.pathParam("id"));
      solicitudService.aprobarSolicitud(id);
      ctx.redirect("/admin/solicitudes");
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de solicitud inválido.");
    } catch (IllegalArgumentException e) {
      ctx.status(404).result(e.getMessage());
    } catch (Exception e) {
      ctx.status(500).result("Error al aprobar la solicitud: " + e.getMessage());
    }
  }

  public void rechazarSolicitud(Context ctx) {
    try {
      initServices();
      Long id = Long.parseLong(ctx.pathParam("id"));
      solicitudService.rechazarSolicitud(id, "Rechazado por un administrador.");
      ctx.redirect("/admin/solicitudes");
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de solicitud inválido.");
    } catch (IllegalArgumentException e) {
      ctx.status(404).result(e.getMessage());
    } catch (Exception e) {
      ctx.status(500).result("Error al rechazar la solicitud: " + e.getMessage());
    }
  }

  public void marcarHechoComoEliminadoAdmin(Context ctx) {
    try {
      initServices();
      Long hechoId = Long.parseLong(ctx.pathParam("id"));
      hechoService.marcarHechoComoEliminado(hechoId);
      ctx.redirect("/admin/hechos");
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de hecho inválido.");
    } catch (IllegalArgumentException e) {
      ctx.status(404).result(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      ctx.status(500).result("Error al eliminar el hecho: " + e.getMessage()); // Equivalente a 500 Internal Server Error
    }
  }

  public void actualizarTodasLasFuentes(Context ctx) {
    try {
      initServices();
      fuenteDatosService.actualizarFuentesExternas();
      ctx.redirect("/admin");
    } catch (Exception e) {
      System.err.println("Error en AdminController::actualizarTodasLasFuentes: " + e.getMessage());
      e.printStackTrace();
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al actualizar fuentes: " + e.getMessage());
    }
  }

  public void recalcularTodosLosConsensos(Context ctx) {
    try {
      initServices();
      coleccionService.recalcularTodosLosConsensos();
      ctx.redirect("/admin");
    } catch (Exception e) {
      System.err.println("Error en AdminController::recalcularTodosLosConsensos: " + e.getMessage());
      e.printStackTrace();
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al recalcular consensos: " + e.getMessage());
    }
  }
}