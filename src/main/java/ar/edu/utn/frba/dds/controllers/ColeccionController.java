package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.consenso.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.model.enums.TipoAlgoritmoConsenso;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.hechos.Coleccion;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.FuenteDatosService;
import ar.edu.utn.frba.dds.services.HechoEliminadoService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ColeccionController extends BaseController {

  private ColeccionService coleccionService;
  private FuenteDatosService fuenteDatosService;
  private HechoEliminadoService hechoEliminadoService;
  private static final Logger logger = LoggerFactory.getLogger(ColeccionController.class);

  public ColeccionController() {
  }

  public void initServices(){
    if(this.coleccionService == null){
      this.coleccionService = new ColeccionService();
      this.fuenteDatosService = new FuenteDatosService();
      this.hechoEliminadoService = new HechoEliminadoService();
    }
  }

  public void listar(Context ctx) {
    try {
      initServices();
      List<FuenteDatos> todasLasFuentes = fuenteDatosService.obtenerTodasLasFuentes();

      List<Map<String, Object>> fuentesParaVista = todasLasFuentes.stream()
          .map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("tipo", f.getClass().getSimpleName().replace("Fuente", "").toUpperCase());
            return map;
          })
          .collect(Collectors.toList());


      List<Coleccion> colecciones = coleccionService.obtenerTodasLasColecciones();
      Map<String, Object> model = new HashMap<>();
      model.put("colecciones", colecciones);
      model.put("activeTab", "colecciones");
      model.put("fuentes", fuentesParaVista);
      model.put("algoritmos", List.of("NINGUNO", "ABSOLUTA", "MAYORIA_SIMPLE", "MULTIPLES_MENCIONES"));

      String statusform = ctx.sessionAttribute("statusform");
      String statusClass = null;

      if ("success".equals(statusform)) {
        statusClass = "success";
      } else if ("error".equals(statusform)) {
        statusClass = "danger";
      }

      model.put("statusform", statusform);
      model.put("statusform_message", ctx.sessionAttribute("statusform_message"));
      model.put("statusform_class", statusClass);

      ctx.sessionAttribute("statusform", null);
      ctx.sessionAttribute("statusform_message", null);
      if (ctx.sessionAttribute("usuarioRol") == "ADMIN") {
        renderWithSession(ctx, "colecciones_admin.hbs", model);
      } else {
        renderWithSession(ctx, "colecciones.hbs", model);
      }
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al obtener las colecciones.");
    }
  }

  public void mostrarFormularioCrearColeccion(Context ctx) {
    try {
      initServices();
      List<FuenteDatos> todasLasFuentes = fuenteDatosService.obtenerTodasLasFuentes();

      List<Map<String, Object>> fuentesParaVista = todasLasFuentes.stream()
          .map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("tipo", f.getClass().getSimpleName().replace("Fuente", "").toUpperCase());
            return map;
          })
          .collect(Collectors.toList());

      Map<String, Object> model = new HashMap<>();
      model.put("fuentes", fuentesParaVista);
      model.put("algoritmos", List.of("NINGUNO", "ABSOLUTA", "MAYORIA_SIMPLE", "MULTIPLES_MENCIONES"));
      model.put("activeTab", "colecciones");
      renderWithSession(ctx, "coleccion_nueva.hbs", model);
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al preparar el formulario.");
    }
  }

  public void crearColeccion(Context ctx) {
    try {
      initServices();
      Long fuenteId = Long.parseLong(Objects.requireNonNull(ctx.formParam("fuenteId"), "ID de fuente requerido"));
      String titulo = Objects.requireNonNull(ctx.formParam("titulo"), "Título requerido");
      String descripcion = ctx.formParam("descripcion");
      String handle = Objects.requireNonNull(ctx.formParam("handle"), "Handle requerido");
      String algoritmoNombre = ctx.formParam("algoritmoConsenso");

      Coleccion nuevaColeccion = coleccionService.crearColeccionConFuente(
          titulo, handle, descripcion, fuenteId, algoritmoNombre);

      ctx.sessionAttribute("statusform", "success");
      ctx.sessionAttribute(
          "statusform_message",
          "El hecho fue reportado correctamente."
      );

      ctx.redirect("/admin/colecciones");
    } catch (NumberFormatException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("ID de fuente inválido.");
    } catch (NullPointerException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("Faltan parámetros requeridos.");
    } catch (IllegalArgumentException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
    } catch (Exception e) {
      logger.error("Error al crear colección", e);

      ctx.sessionAttribute("statusform", "error");
      ctx.sessionAttribute(
          "statusform_message",
          "Error interno al crear la colección."
      );

      ctx.redirect("/admin/colecciones");
    }

  }

  public void mostrar(Context ctx) {
    String handle = ctx.pathParam("handle");
    try {
      initServices();
      Optional<Coleccion> coleccionOpt = coleccionService.obtenerColeccionPorHandle(handle);

      if (coleccionOpt.isPresent()) {
        Coleccion coleccion = coleccionOpt.get();
        List<Hecho> hechos = coleccion.obtenerHechos();
        hechos = hechoEliminadoService.filtrarHechosEliminados(hechos);
        String consulta = ctx.queryParam("q");

        if (consulta != null && !consulta.isBlank()) {
          String consultaLower = consulta.toLowerCase();
          hechos = hechos.stream()
              .filter(h -> h.getTitulo().toLowerCase().contains(consultaLower) ||
                  h.getDescripcion().toLowerCase().contains(consultaLower))
              .collect(Collectors.toList());
        }

        Map<String, Object> model = new HashMap<>();
        model.put("coleccion", coleccion);
        model.put("hechos", hechos);
        model.put("consulta", consulta);
        model.put("activeTab", "colecciones");

        renderWithSession(ctx, "coleccion.hbs", model);
      } else {
        ctx.status(HttpStatus.NOT_FOUND).result("Colección no encontrada con handle: " + handle);
      }
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al obtener la colección.");
    }
  }

  public void mostrarFormularioEditarColeccion(Context ctx) {
    String handle = ctx.pathParam("handle");
    try {
      initServices();
      Optional<Coleccion> coleccionOpt = coleccionService.obtenerColeccionPorHandle(handle);

      if (coleccionOpt.isPresent()) {
        List<FuenteDatos> todasLasFuentes = fuenteDatosService.obtenerTodasLasFuentes();
        List<Map<String, Object>> fuentesParaVista = todasLasFuentes.stream()
            .map(f -> {
              Map<String, Object> map = new HashMap<>();
              map.put("id", f.getId());
              map.put("DTYPE", f.getClass().getSimpleName().replace("Fuente", "").toUpperCase());
              if (f instanceof ar.edu.utn.frba.dds.model.fuentes.FuenteEstatica) {
                map.put("rutaArchivoCsv", ((ar.edu.utn.frba.dds.model.fuentes.FuenteEstatica) f).getRutaArchivoCsv());
              }
              if (f instanceof ar.edu.utn.frba.dds.model.fuentes.FuenteDemo) {
                map.put("url", ((ar.edu.utn.frba.dds.model.fuentes.FuenteDemo) f).getUrl());
              }
              return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> model = new HashMap<>();
        model.put("coleccion", coleccionOpt.get());
        model.put("todasLasFuentes", fuentesParaVista);
        model.put("todosLosAlgoritmos", TipoAlgoritmoConsenso.values());
        model.put("activeTab", "colecciones");

        renderWithSession(ctx, "coleccion_editar.hbs", model);
      } else {
        ctx.status(HttpStatus.NOT_FOUND).result("Colección no encontrada para editar: " + handle);
      }
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al preparar la edición.");
    }
  }

  public void actualizarColeccion(Context ctx) {
    String handle = ctx.pathParam("handle");
    try {
      initServices();
      String nuevoTitulo = Objects.requireNonNull(ctx.formParam("titulo"), "El título no puede ser nulo");
      String nuevaDescripcion = ctx.formParam("descripcion");
      Long nuevaFuenteId = Long.parseLong(Objects.requireNonNull(ctx.formParam("fuenteId"), "ID de fuente no puede ser nulo"));

      String algoritmoStr = Objects.requireNonNull(ctx.formParam("algoritmoConsenso"), "Algoritmo es requerido");
      TipoAlgoritmoConsenso nuevoTipoAlgoritmo = TipoAlgoritmoConsenso.valueOf(algoritmoStr.toUpperCase());

      if (nuevoTitulo.isBlank()) {
        ctx.status(HttpStatus.BAD_REQUEST).result("El título no puede estar vacío.");
        return;
      }

      Coleccion actualizada = coleccionService.actualizarColeccion(
          handle, nuevoTitulo, nuevaDescripcion, nuevaFuenteId, nuevoTipoAlgoritmo);

      ctx.redirect("/admin/colecciones");

    } catch (NumberFormatException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("ID de fuente inválido.");
    } catch (NullPointerException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("Faltan parámetros requeridos (título, fuenteId o algoritmo).");
    } catch (IllegalArgumentException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result(e.getMessage());
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al actualizar la colección.");
    }
  }

  public void eliminarColeccion(Context ctx) {
    String handle = ctx.pathParam("handle");
    try {
      initServices();
      coleccionService.eliminarColeccionPorHandle(handle);
      ctx.redirect("/admin/colecciones");
    } catch (IllegalArgumentException e) {
      ctx.status(HttpStatus.NOT_FOUND).result(e.getMessage());
    } catch (Exception e) {
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al eliminar la colección.");
    }
  }
}