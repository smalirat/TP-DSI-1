package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.services.FuenteDatosService;
import com.google.gson.Gson;
import io.javalin.http.Context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapaController extends BaseController {

  private  FuenteDatosService fuenteDatosService;
  private final Gson gson = new Gson();

  public MapaController() {
  }

  public void initServices(){
    if (this.fuenteDatosService == null){
      this.fuenteDatosService = new FuenteDatosService();
    }
  }

  public void mostrar(Context ctx) {
    try {
      initServices();
      List<Hecho> todosLosHechos = fuenteDatosService.obtenerTodosLosHechosDeTodasLasFuentes();

      List<String> categorias = Arrays.stream(Categoria.values()).map(Enum::name).sorted().collect(Collectors.toList());
      List<String> origenes = Arrays.stream(OrigenHecho.values()).map(Enum::name).sorted().collect(Collectors.toList());
      List<String> provincias = Arrays.stream(Provincia.values()).map(Enum::name).sorted().collect(Collectors.toList());

      List<Map<String, Object>> hechosParaMapa = todosLosHechos.stream()
          .filter(h -> h.getLatitud() != null && h.getLongitud() != null && 
                       h.getCategoria() != null && h.getProvincia() != null && h.getOrigen() != null)
          .map(hecho -> {
            Map<String, Object> mapaHecho = new HashMap<>();
            mapaHecho.put("id", hecho.getId());
            mapaHecho.put("titulo", hecho.getTitulo());
            mapaHecho.put("lat", hecho.getLatitud());
            mapaHecho.put("lon", hecho.getLongitud());
            mapaHecho.put("categoria", hecho.getCategoria().name());
            mapaHecho.put("provincia", hecho.getProvincia().name());
            mapaHecho.put("origen", hecho.getOrigen().name());
            mapaHecho.put("fechaAcontecimiento", hecho.getFechaAcontecimiento().toString());
            return mapaHecho;
          })
          .collect(Collectors.toList());

      String hechosJson = gson.toJson(hechosParaMapa);

      Map<String, Object> model = new HashMap<>();
      model.put("hechosJson", hechosJson);
      model.put("categorias", categorias);
      model.put("provincias", provincias);
      model.put("origenes", origenes);
      model.put("activeTab", "mapa");
      renderWithSession(ctx, "mapa.hbs", model);

    } catch (Exception e) {
      ctx.status(500).result("Error al cargar el mapa: " + e.getMessage());
    }
  }
}