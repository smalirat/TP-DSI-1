package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.controllers.*;
import io.javalin.Javalin;

public class Router {

  public void configure(Javalin app) {
    HomeController homeController = new HomeController();
    ColeccionController coleccionController = new ColeccionController();
    HechoController hechoController = new HechoController();
    SolicitudController solicitudController = new SolicitudController();
    AdminController adminController = new AdminController();
    FuentesController fuentesController = new FuentesController();
    MapaController mapaController = new MapaController();
    AuthController authController = new AuthController();
    EstadisticaController estadisticaController = new EstadisticaController();

    app.get("/login", authController::mostrarFormularioLogin);
    app.post("/login", authController::login);
    app.post("/logout", authController::logout);
    app.get("/registro", authController::mostrarFormularioRegistro);
    app.post("/registro", authController::registrar);

    app.get("/", homeController::index);
    app.get("/mapa", mapaController::mostrar);

    app.get("/estadisticas", estadisticaController::listarPublicas);
    app.get("/estadisticas/{id}", estadisticaController::detalle);
    app.get("/estadisticas/{id}/csv", estadisticaController::descargarCSV);

    app.get("/colecciones", coleccionController::listar);
    app.get("/colecciones/{handle}", coleccionController::mostrar);

    app.post("/admin/colecciones/recalcular", adminController::recalcularTodosLosConsensos);
    app.post("/admin/fuentes/actualizar", adminController::actualizarTodasLasFuentes);
    app.get("/admin/colecciones/nueva", coleccionController::mostrarFormularioCrearColeccion);
    app.post("/admin/colecciones", coleccionController::crearColeccion);
    app.get("/admin/colecciones/{handle}/editar", coleccionController::mostrarFormularioEditarColeccion);
    app.post("/admin/colecciones/{handle}", coleccionController::actualizarColeccion);
    app.post("/admin/colecciones/{handle}/delete", coleccionController::eliminarColeccion);
    app.get("/admin/colecciones", coleccionController::listar);

    app.get("/hechos", hechoController::listarTodos);
    app.get("/hechos/nuevo", hechoController::nuevo);
    app.post("/hechos", hechoController::guardar);
    app.get("/hechos/{id}", hechoController::mostrar);

    app.get("/hechos/{id}/multimedia/nuevo", hechoController::mostrarFormularioMultimedia);
    app.post("/hechos/{id}/multimedia", hechoController::guardarMultimedia);

    app.get("/hechos/{id}/solicitudes/nueva", solicitudController::nuevo);
    app.post("/hechos/{id}/solicitudes", solicitudController::save);

    app.get("/fuentes", fuentesController::listar);
    app.get("/fuentes/{id}/colecciones/nueva", fuentesController::mostrarFormularioCrearColeccion);
    app.post("/colecciones", fuentesController::guardarColeccion);
    app.get("/fuentes/{id}/hechos", fuentesController::mostrarHechosDeFuente);

    app.before("/admin/*", ctx -> {
      String rol = ctx.sessionAttribute("usuarioRol");
      if (rol == null || !rol.equals("ADMIN")) {
        ctx.redirect("/login");
      }
    });

    app.get("/admin", adminController::mostrarPanelPrincipal);
    // Estadísticas admin
    app.get("/admin/estadisticas", estadisticaController::listarAdmin);
    app.post("/admin/estadisticas", estadisticaController::crear);
    app.get("/admin/estadisticas/{id}", estadisticaController::detalle);
    app.get("/admin/estadisticas/{id}/csv", estadisticaController::descargarCSV);
    app.get("/admin/solicitudes", adminController::listarSolicitudes);
    app.post("/admin/solicitudes/{id}/aprobar", adminController::aprobarSolicitud);
    app.post("/admin/solicitudes/{id}/rechazar", adminController::rechazarSolicitud);
    app.post("/admin/fuentes", fuentesController::guardarNuevaFuente);
    app.get("/admin/hechos", hechoController::listarTodos);
    app.post("/admin/hechos/{id}/delete", adminController::marcarHechoComoEliminadoAdmin);

  }
}