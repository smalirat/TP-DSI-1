package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import ar.edu.utn.frba.dds.model.enums.TipoMultimedia;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.services.FuenteDatosService;
import ar.edu.utn.frba.dds.services.HechoEliminadoService;
import ar.edu.utn.frba.dds.services.HechoService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HechoController extends BaseController {

  private HechoService hechoService;
  private FuenteDatosService fuenteDatosService;

  public HechoController() {
  }

  public void initServices(){
    if (this.hechoService == null){
      this.hechoService = new HechoService();
      this.fuenteDatosService = new FuenteDatosService();
    }
  }

  public void listarTodos(Context ctx) {
    initServices();
    boolean esAdmin = "ADMIN".equals(ctx.sessionAttribute("usuarioRol"));

    if (esAdmin) {
      try {
        String consulta = ctx.queryParam("q");
        List<Hecho> hechos;

        if (consulta != null && !consulta.isBlank()) {
          List<Hecho> todosLosHechos = fuenteDatosService.obtenerTodosLosHechosDeTodasLasFuentes();
          String consultaLower = consulta.toLowerCase();
          hechos = todosLosHechos.stream()
              .filter(h -> h.getTitulo().toLowerCase().contains(consultaLower) ||
                  h.getDescripcion().toLowerCase().contains(consultaLower))
              .collect(Collectors.toList());
        } else {
          hechos = fuenteDatosService.obtenerTodosLosHechosDeTodasLasFuentes();
        }

        Map<String, Object> model = new HashMap<>();
        model.put("hechos", hechos);
        model.put("consulta", consulta);

        model.put("fuentes", fuenteDatosService.obtenerFuentesDinamicas());
        model.put(
            "categorias",
            Arrays.stream(Categoria.values()).map(Enum::name).sorted().collect(Collectors.toList())
        );
        model.put(
            "provincias",
            Arrays.stream(Provincia.values()).map(Provincia::getNombre).sorted().collect(Collectors.toList())
        );

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
          renderWithSession(ctx, "admin_hechos.hbs", model);
        } else {
          renderWithSession(ctx, "hechos.hbs", model);
        }

      } catch (Exception e) {
        ctx.status(500).result("Error al obtener la lista de hechos para admin: " + e.getMessage());
      }

    } else {
      try {
        String consulta = ctx.queryParam("q");
        List<Hecho> hechos;

        if (consulta != null && !consulta.isBlank()) {
          hechos = fuenteDatosService.obtenerTodosLosHechosDeTodasLasFuentes();
          String queryLower = consulta.toLowerCase();
          hechos = hechos.stream()
              .filter(h -> h.getTitulo().toLowerCase().contains(queryLower) ||
                  h.getDescripcion().toLowerCase().contains(queryLower))
              .collect(Collectors.toList());
        } else {
          hechos = fuenteDatosService.obtenerTodosLosHechosDeTodasLasFuentes();
        }

        Map<String, Object> model = new HashMap<>();
        model.put("hechos", hechos);
        model.put("consulta", consulta);
        model.put("activeTab", "hechos");

        model.put("fuentes", fuenteDatosService.obtenerFuentesDinamicas());
        model.put(
            "categorias",
            Arrays.stream(Categoria.values()).map(Enum::name).sorted().collect(Collectors.toList())
        );
        model.put(
            "provincias",
            Arrays.stream(Provincia.values()).map(Provincia::getNombre).sorted().collect(Collectors.toList())
        );

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
          renderWithSession(ctx, "admin_hechos.hbs", model);
        } else {
          renderWithSession(ctx, "hechos.hbs", model);
        }

      } catch (Exception e) {
        ctx.status(500).result("Error al obtener los hechos: " + e.getMessage());
      }

    }

  }

  public void mostrar(Context ctx) {
    try {
      initServices();
      Long id = Long.parseLong(ctx.pathParam("id"));
      Hecho hecho = fuenteDatosService.buscarHechoPorIdEnTodasLasFuentes(id);
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      Map<String, Object> model = new HashMap<>();
      if (hecho != null) {
        model.put("hecho", hecho);
        model.put(
            "fechaAcontecimientoFormateada",
            hecho.getFechaAcontecimiento().format(formatter)
        );
        model.put("esHechoPersistido", id > 0); // Los hechos de CSV tienen ID negativo
        model.put("activeTab", "hechos");
        renderWithSession(ctx, "hecho.hbs", model);
      } else {
        ctx.status(404).result("Error 404: Hecho con ID " + id + " no encontrado.");
      }
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de hecho inválido.");
    } catch (Exception e) {
      ctx.status(500).result("Error al obtener el hecho: " + e.getMessage());
    }
  }

  public void nuevo(Context ctx) {
    try {
      initServices();
      List<FuenteDinamica> fuentes = fuenteDatosService.obtenerFuentesDinamicas();
      Map<String, Object> model = new HashMap<>();
      model.put("fuentesDinamicas", fuentes);
      model.put("activeTab", "hechos");
      renderWithSession(ctx, "fuente_seleccion_hecho.hbs", model);
    } catch (Exception e) {
      System.err.println("Error al obtener fuentes dinámicas: " + e.getMessage());
      ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Error al cargar la selección de fuentes.");
    }
  }

  public void guardar(Context ctx) {
    Long fuenteId = null;
    try {
      initServices();
      fuenteId = Long.parseLong(Objects.requireNonNull(ctx.formParam("fuenteId"), "Falta ID de fuente en el formulario"));

      String titulo = Objects.requireNonNull(ctx.formParam("titulo"), "Título es requerido");
      String descripcion = Objects.requireNonNull(ctx.formParam("descripcion"), "Descripción es requerida");
      Categoria categoria = Categoria.fromString(Objects.requireNonNull(ctx.formParam("categoria"), "Categoría es requerida"));
      Provincia provincia = Provincia.fromString(Objects.requireNonNull(ctx.formParam("provincia"), "Provincia es requerida"));
      LocalDateTime fechaAcontecimiento =
          LocalDate.parse(
              Objects.requireNonNull(ctx.formParam("fechaAcontecimiento"), "Fecha es requerida")
          ).atStartOfDay();

      Double latitud = parseOrNull(ctx.formParam("latitud"));
      Double longitud = parseOrNull(ctx.formParam("longitud"));

      String esAnonimoStr = ctx.formParam("esAnonimo");
      OrigenHecho origen = (esAnonimoStr != null && esAnonimoStr.equals("true")) ? OrigenHecho.CONTRIBUYENTE : OrigenHecho.CARGA_MANUAL;

      Hecho nuevoHecho;
      String urlMultimedia = ctx.formParam("multimediaUrl");
      String tipoMultimediaStr = ctx.formParam("multimediaTipo");
      String descripcionMultimedia = ctx.formParam("multimediaDescripcion");

      if (urlMultimedia != null && !urlMultimedia.isBlank() && tipoMultimediaStr != null && !tipoMultimediaStr.isBlank()) {
        try {
          TipoMultimedia tipo = TipoMultimedia.valueOf(tipoMultimediaStr.toUpperCase());
          nuevoHecho = hechoService.crearHechoConMultimedia(
              titulo, descripcion, categoria, provincia, latitud, longitud,
              fechaAcontecimiento, origen, urlMultimedia, tipo, descripcionMultimedia, fuenteId); // Pasar fuenteId
        } catch (IllegalArgumentException e) {
          System.err.println("Tipo multimedia inválido: " + tipoMultimediaStr + ". Guardando sin multimedia.");
          nuevoHecho = hechoService.crearHecho(
              titulo, descripcion, categoria, provincia, latitud, longitud,
              fechaAcontecimiento, origen, fuenteId);
        }
      } else {
        nuevoHecho = hechoService.crearHecho(
            titulo, descripcion, categoria, provincia, latitud, longitud,
            fechaAcontecimiento, origen, fuenteId);
      }
      ctx.sessionAttribute("statusform", "success");
      ctx.sessionAttribute(
          "statusform_message",
          "El hecho fue reportado correctamente."
      );


      ctx.redirect("/hechos/");

    } catch (Exception e) {
      e.printStackTrace();
      ctx.sessionAttribute("statusform", "error");
      ctx.sessionAttribute(
          "statusform_message",
          "No se pudo guardar el hecho: " + e.getMessage()
      );

      ctx.redirect("/hechos");
    }
  }

  public void mostrarFormularioMultimedia(Context ctx) {
    try {
      initServices();
      Long id = Long.parseLong(ctx.pathParam("id"));
      Hecho hecho = hechoService.obtenerHechoPorId(id);
      if (hecho != null) {
        Map<String, Object> model = new HashMap<>();
        model.put("hecho", hecho);
        model.put("activeTab", "hechos");
        renderWithSession(ctx, "multimedia_nuevo.hbs", model);
      } else {
        ctx.status(404).result("Error 404: Hecho con ID " + id + " no encontrado.");
      }
    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de hecho inválido.");
    } catch (Exception e) {
      ctx.status(500).result("Error al preparar formulario multimedia: " + e.getMessage());
    }
  }

  public void guardarMultimedia(Context ctx) {
    Long hechoId = null;
    try {
      initServices();
      hechoId = Long.parseLong(ctx.pathParam("id"));
      String urlMultimedia = ctx.formParam("multimediaUrl");
      String tipoMultimediaStr = ctx.formParam("multimediaTipo");
      String descripcion = ctx.formParam("descripcion");
      TipoMultimedia tipo = null;

      if (tipoMultimediaStr != null && !tipoMultimediaStr.isBlank()) {
        try {
          tipo = TipoMultimedia.valueOf(tipoMultimediaStr.toUpperCase());
        } catch (IllegalArgumentException e) {
          ctx.status(400).result("Tipo de multimedia no válido: " + tipoMultimediaStr);
          return;
        }
      } else {
        ctx.status(400).result("El tipo de multimedia es obligatorio.");
        return;
      }

      if (urlMultimedia == null || urlMultimedia.isBlank()) {
        ctx.status(400).result("La URL del multimedia es obligatoria.");
        return;
      }

      hechoService.agregarMultimediaAHecho(hechoId, urlMultimedia, tipo, descripcion);
      ctx.redirect("/hechos/" + hechoId);

    } catch (NumberFormatException e) {
      ctx.status(400).result("ID de hecho inválido.");
    } catch (IllegalArgumentException e) {
      ctx.status(404).result(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      ctx.status(500).result("Error al guardar el multimedia: " + e.getMessage());
    }
  }

  private Double parseOrNull(String valor) {
    try {
      initServices();
      return (valor == null || valor.isBlank()) ? null : Double.parseDouble(valor);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}