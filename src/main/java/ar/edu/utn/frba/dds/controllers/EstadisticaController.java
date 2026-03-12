package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.estadisticas.Estadistica;
import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.TipoEstadistica;
import ar.edu.utn.frba.dds.model.estadisticas.calculadores.*;
import ar.edu.utn.frba.dds.model.hechos.Coleccion;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.HechoRepository;
import ar.edu.utn.frba.dds.repositorios.RepositorioSolicitudes;
import ar.edu.utn.frba.dds.services.ColeccionService;
import ar.edu.utn.frba.dds.services.EstadisticaService;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class EstadisticaController extends BaseController {

  private final EstadisticaService estadisticaService;
  private final ColeccionService coleccionService;

  public EstadisticaController() {
    this.estadisticaService = new EstadisticaService();
    this.coleccionService = new ColeccionService();
  }

  /* ===========================
     LISTADOS
     =========================== */

  public void listarPublicas(Context ctx) {
    String tipoParam = ctx.queryParam("tipo");
    List<Estadistica> estadisticas;

    if (tipoParam != null && !tipoParam.isBlank()) {
      try {
        TipoEstadistica tipo = TipoEstadistica.valueOf(tipoParam);
        estadisticas = estadisticaService.obtenerEstadisticasPorTipo(tipo)
            .stream()
            .filter(Estadistica::isEsPublica)
            .toList();
      } catch (IllegalArgumentException e) {
        ctx.status(HttpStatus.BAD_REQUEST).result("Tipo de estadística inválido");
        return;
      }
    } else {
      estadisticas = estadisticaService.obtenerEstadisticasPublicas();
    }

    Map<String, Object> model = new HashMap<>();
    model.put("estadisticas", estadisticas);
    model.put("tipos", TipoEstadistica.values());
    model.put("seleccionado", tipoParam);
    model.put("activeTab", "estadisticas");

    cargarStatusForm(ctx, model);
    if (ctx.sessionAttribute("usuarioRol") == "ADMIN") {
      renderWithSession(ctx, "admin_estadisticas.hbs", model);
    } else {
      renderWithSession(ctx, "estadisticas.hbs", model);
    }

  }

  public void listarAdmin(Context ctx) {
    String tipoParam = ctx.queryParam("tipo");
    List<Estadistica> estadisticas;

    if (tipoParam != null && !tipoParam.isBlank()) {
      try {
        TipoEstadistica tipo = TipoEstadistica.valueOf(tipoParam);
        estadisticas = estadisticaService.obtenerEstadisticasPorTipo(tipo);
      } catch (IllegalArgumentException e) {
        ctx.status(HttpStatus.BAD_REQUEST).result("Tipo de estadística inválido");
        return;
      }
    } else {
      estadisticas = estadisticaService.obtenerTodasLasEstadisticas();
    }

    List<String> categorias = Arrays.stream(Categoria.values())
        .map(Enum::name)
        .sorted()
        .collect(Collectors.toList());

    List<Coleccion> colecciones = coleccionService.obtenerTodasLasColecciones();

    Map<String, Object> model = new HashMap<>();
    model.put("estadisticas", estadisticas);
    model.put("tipos", TipoEstadistica.values());
    model.put("seleccionado", tipoParam);
    model.put("todasLasCategorias", categorias);
    model.put("todasLasColecciones", colecciones);
    model.put("activeTab", "estadisticas");

    cargarStatusForm(ctx, model);
    if (ctx.sessionAttribute("usuarioRol") == "ADMIN") {
      renderWithSession(ctx, "admin_estadisticas.hbs", model);
    } else {
      renderWithSession(ctx, "estadisticas.hbs", model);
    }
  }

  /* ===========================
     DETALLE
     =========================== */

  public void detalle(Context ctx) {
    try {
      Long id = Long.parseLong(ctx.pathParam("id"));
      Estadistica e = estadisticaService.obtenerEstadisticaPorId(id);

      if (e == null) {
        ctx.status(HttpStatus.NOT_FOUND).result("Estadística no encontrada");
        return;
      }

      Map<String, Object> model = new HashMap<>();
      model.put("estadistica", e);
      model.put("activeTab", "estadisticas");

      renderWithSession(ctx, "estadistica_detalle.hbs", model);

    } catch (NumberFormatException e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("ID inválido");
    }
  }

  /* ===========================
     CREAR
     =========================== */

  public void crear(Context ctx) {
    try {
      TipoEstadistica tipo = TipoEstadistica.valueOf(ctx.formParam("tipo"));
      String nombre = ctx.formParam("nombre");
      boolean esPublica = "on".equalsIgnoreCase(ctx.formParam("esPublica"));

      if (nombre == null || nombre.isBlank()) {
        throw new IllegalArgumentException("El nombre es obligatorio");
      }

      Map<String, String> parametros = extraerParametros(ctx, tipo);

      estadisticaService.crearEstadistica(
          tipo,
          nombre,
          parametros.isEmpty() ? null : parametros.toString(),
          LocalDateTime.now(),
          esPublica
      );

      ctx.sessionAttribute("statusform", "success");
      ctx.sessionAttribute("statusform_message", "Estadística creada correctamente");
      ctx.redirect("/admin/estadisticas");

    } catch (Exception e) {
      ctx.sessionAttribute("statusform", "error");
      ctx.sessionAttribute("statusform_message", e.getMessage());
      ctx.redirect("/admin/estadisticas");
    }
  }

  /* ===========================
     CSV
     =========================== */

  public void descargarCSV(Context ctx) {
    try {
      Long id = Long.parseLong(ctx.pathParam("id"));
      Estadistica e = estadisticaService.obtenerEstadisticaPorId(id);

      if (e == null) {
        ctx.status(HttpStatus.NOT_FOUND).result("Estadística no encontrada");
        return;
      }

      Map<String, String> parametros = reconstruirParametros(e.getParametros());
      byte[] csv = calcularCSV(e.getTipo(), parametros);

      ctx.header("Content-Disposition",
          "attachment; filename=\"estadistica_" + e.getNombre() + ".csv\"");
      ctx.contentType("text/csv");
      ctx.result(csv);

    } catch (Exception e) {
      ctx.status(HttpStatus.BAD_REQUEST).result("Error al generar CSV: " + e.getMessage());
    }
  }

  /* ===========================
     PRIVADOS
     =========================== */

  private byte[] calcularCSV(TipoEstadistica tipo, Map<String, String> parametros) {
    CalculadorEstadistica calculador = obtenerCalculador(tipo);

    try (EntityManager em = EntityManagerProvider.createEntityManager()) {
      return calculador.calcularYGenerarCSV(
          new HechoRepository(em),
          new RepositorioSolicitudes(em),
          parametros
      );
    }
  }

  private CalculadorEstadistica obtenerCalculador(TipoEstadistica tipo) {
    return switch (tipo) {
      case HECHOS_POR_PROVINCIA_COLECCION -> new HechosPorProvinciaColeccion();
      case CATEGORIA_MAS_REPORTADA -> new CategoriaMasReportada();
      case PROVINCIA_MAS_HECHOS_POR_CATEGORIA -> new ProvinciaMasHechosPorCategoria();
      case HORA_PICO_POR_CATEGORIA -> new HoraPicoPorCategoria();
      case SOLICITUDES_SPAM -> new SpamSolicitudes();
    };
  }

  private Map<String, String> extraerParametros(Context ctx, TipoEstadistica tipo) {
    Map<String, String> p = new HashMap<>();

    if (tipo == TipoEstadistica.HECHOS_POR_PROVINCIA_COLECCION) {
      p.put("coleccionHandle", ctx.formParam("coleccionHandle"));
    } else if (tipo == TipoEstadistica.PROVINCIA_MAS_HECHOS_POR_CATEGORIA
        || tipo == TipoEstadistica.HORA_PICO_POR_CATEGORIA) {
      p.put("categoria", ctx.formParam("categoria"));
    }

    return p;
  }

  private Map<String, String> reconstruirParametros(String raw) {
    Map<String, String> params = new HashMap<>();
    if (raw == null || raw.isBlank()) return params;

    raw.replace("{", "").replace("}", "").lines().forEach(line -> {
      String[] kv = line.split("=");
      if (kv.length == 2) params.put(kv[0].trim(), kv[1].trim());
    });
    return params;
  }

  private void cargarStatusForm(Context ctx, Map<String, Object> model) {
    String status = ctx.sessionAttribute("statusform");
    if (status != null) {
      model.put("statusform", status);
      model.put("statusform_message", ctx.sessionAttribute("statusform_message"));
      model.put("statusform_class", status.equals("success") ? "success" : "danger");
      ctx.sessionAttribute("statusform", null);
      ctx.sessionAttribute("statusform_message", null);
    }
  }
}
