package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.consenso.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.model.consenso.ConsensoAbsoluta;
import ar.edu.utn.frba.dds.model.consenso.ConsensoMayoriaSimple;
import ar.edu.utn.frba.dds.model.consenso.ConsensoMultiplesMenciones;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.model.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.model.hechos.Coleccion;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.FuenteDatosService;
import ar.edu.utn.frba.dds.services.HechoEliminadoService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FuentesController extends BaseController {

  private FuenteDatosService fuenteDatosService;
  private ColeccionService coleccionService;
  private HechoEliminadoService hechoEliminadoService;

  public FuentesController() {
  }

  private void initServices() {
    if (coleccionService == null) {
      coleccionService = new ColeccionService();
      fuenteDatosService = new FuenteDatosService();
      hechoEliminadoService = new HechoEliminadoService();
    }
  }

  public void listar(Context ctx) {
    try {
      initServices();
      List<FuenteDatos> fuentes = fuenteDatosService.obtenerTodasLasFuentes();
      Map<String, Object> model = new HashMap<>();

      List<Map<String, Object>> fuentesParaVista = fuentes.stream()
          .map(f -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", f.getId());
            map.put("tipo_fuente", f.getClass().getSimpleName().toUpperCase());
            map.put("rutaArchivoCsv", (f instanceof FuenteEstatica) ? ((FuenteEstatica) f).getRutaArchivoCsv() : null);
            return map;
          })
          .collect(Collectors.toList());

      model.put("fuentes", fuentesParaVista);

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

      renderWithSession(ctx, "fuentes.hbs", model);
    } catch (Exception e) {
      ctx.status(500).result("Error al obtener las fuentes: " + e.getMessage());
    }
  }

  public void guardarNuevaFuente(Context ctx) {
    try {
      initServices();
      FuenteDinamica nuevaFuente = new FuenteDinamica();
      fuenteDatosService.guardarFuente(nuevaFuente);

      // ✅ STATUSFORM OK
      ctx.sessionAttribute("statusform", "success");
      ctx.sessionAttribute(
          "statusform_message",
          "El hecho fue reportado correctamente."
      );

      ctx.redirect("/fuentes");
    } catch (Exception e) {
      ctx.status(500).result("Error al guardar la fuente: " + e.getMessage());
    }
  }

  public void mostrarFormularioCrearColeccion(Context ctx) {
    try {
      initServices();
      Long fuenteId = Long.parseLong(ctx.pathParam("id"));
      FuenteDatos fuente = fuenteDatosService.obtenerFuentePorId(fuenteId);

      if (fuente != null) {
        Map<String, Object> model = new HashMap<>();
        model.put("fuente", fuente);
        model.put("algoritmos", List.of("NINGUNO", "ABSOLUTA", "MAYORIA_SIMPLE", "MULTIPLES_MENCIONES"));
        renderWithSession(ctx, "coleccion_nueva.hbs", model);
      } else {
        ctx.status(404).result("Fuente de datos no encontrada.");
      }
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de fuente inválido.");
    } catch (Exception e) {
      ctx.status(500).result("Error al obtener la fuente: " + e.getMessage());
    }
  }

  public void guardarColeccion(Context ctx) {
    try {
      initServices();
      Long fuenteId = Long.parseLong(ctx.formParam("fuenteId"));
      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String handle = ctx.formParam("handle");
      String algoritmoNombre = ctx.formParam("algoritmoConsenso");


      Coleccion nuevaColeccion = coleccionService.crearColeccionConFuente(
          titulo, handle, descripcion, fuenteId, algoritmoNombre);
      
      ctx.redirect("/colecciones/" + nuevaColeccion.getHandle());
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de fuente inválido.");
    } catch (IllegalArgumentException e) {
      ctx.status(400).result(e.getMessage());
    } catch (Exception e) {
      ctx.status(500).result("Error al guardar la colección: " + e.getMessage());
    }
  }


  private AlgoritmoConsenso crearAlgoritmo(String nombre) {
    initServices();
    if (nombre == null || nombre.equals("NINGUNO")) {
      return null;
    }
    switch (nombre) {
      case "ABSOLUTA":
      case "MAYORIA_SIMPLE":
      case "MULTIPLES_MENCIONES":
        // TODO: Implementar creación de algoritmos de consenso en el servicio
        return null;
      default:
        return null;
    }
  }

  public void guardarFuenteDinamica(Context ctx) {
    try {
      initServices();
      FuenteDinamica nuevaFuente = new FuenteDinamica();
      fuenteDatosService.guardarFuente(nuevaFuente);
      ctx.redirect("/admin/fuentes");
    } catch (Exception e) {
      System.err.println("Error al guardar la fuente de datos: " + e.getMessage());
      ctx.status(500).result("Error al guardar la fuente: " + e.getMessage());
    }
  }

  public void mostrarHechosDeFuente(Context ctx) {
    Long fuenteId = null;
    try {
      initServices();
      fuenteId = Long.parseLong(ctx.pathParam("id"));
      FuenteDatos fuenteInfo = fuenteDatosService.obtenerFuentePorId(fuenteId);

      if (fuenteInfo == null) {
        ctx.status(HttpStatus.NOT_FOUND).result("Fuente no encontrada.");
        return;
      }

      List<Hecho> hechos = fuenteDatosService.obtenerHechosDeFuente(fuenteId);
      hechos = hechoEliminadoService.filtrarHechosEliminados(hechos);

      Map<String, Object> model = new HashMap<>();
      model.put("fuente", Map.of(
          "id", fuenteInfo.getId(),
          "DTYPE", fuenteInfo.getClass().getSimpleName().toUpperCase()
      ));
      model.put("listaDeHechos", hechos);

      renderWithSession(ctx, "fuentes_hechos.hbs", model);

    } catch (NumberFormatException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("ID de fuente inválido.");
    } catch (Exception e) {
      e.printStackTrace();
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error interno al obtener los hechos de la fuente: " + e.getMessage());
    }
  }

}