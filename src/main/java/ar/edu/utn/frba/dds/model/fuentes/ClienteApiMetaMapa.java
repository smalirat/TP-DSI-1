package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.model.solicitudes.SolicitudEliminacion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Embeddable
public class ClienteApiMetaMapa {
  @Transient
  private static final ObjectMapper objectMapper = new ObjectMapper();
  @Column(name = "base_url", length = 255)
  private String baseUrl;
  @Transient
  private HttpClient httpClient;

  public ClienteApiMetaMapa() {
  }

  public ClienteApiMetaMapa(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<Hecho> obtenerHechos(Map<String, String> filtros) {
    String path = "/hechos";
    return ejecutarRequestHechos(path, filtros);
  }

  public List<Hecho> obtenerHechosPorColeccion(String identificadorColeccion,
                                               Map<String, String> filtros) {
    String path = "/colecciones/" + identificadorColeccion + "/hechos";
    return ejecutarRequestHechos(path, filtros);
  }

  public void enviarSolicitudEliminacion(SolicitudEliminacion solicitud) {
    String jsonSolicitud = serializarSolicitud(solicitud);
    HttpRequest request = crearRequestPost("/solicitudes", jsonSolicitud);
    ejecutarRequestConCodigoEsperado(request, 201, "Error al enviar solicitud");
  }

  private List<Hecho> ejecutarRequestHechos(String path, Map<String, String> filtros) {
    HttpRequest request = crearRequestGet(path, filtros);
    HttpResponse<String> response = ejecutarRequest(request);

    if (response.statusCode() == 200) {
      return deserializarHechos(response.body());
    } else {
      throw new RuntimeException("Error al obtener hechos: código " + response.statusCode());
    }
  }

  private HttpRequest crearRequestGet(String path, Map<String, String> filtros) {
    String urlCompleta = construirUrlCompleta(path, filtros);
    return HttpRequest.newBuilder().uri(URI.create(urlCompleta)).GET().build();
  }

  private HttpRequest crearRequestPost(String path, String jsonBody) {
    String urlCompleta = baseUrl + path;
    return HttpRequest.newBuilder().uri(URI.create(urlCompleta))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
  }

  private HttpResponse<String> ejecutarRequest(HttpRequest request) {
    try {
      return getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    } catch (Exception e) {
      throw new RuntimeException("Error en la comunicación con la API", e);
    }
  }

  private void ejecutarRequestConCodigoEsperado(HttpRequest request,
                                                int codigoEsperado, String mensajeError) {
    HttpResponse<String> response = ejecutarRequest(request);
    if (response.statusCode() != codigoEsperado) {
      throw new RuntimeException(mensajeError + ": código " + response.statusCode());
    }
  }

  private String construirUrlCompleta(String path, Map<String, String> filtros) {
    return baseUrl + construirUrl(path, filtros);
  }

  private String construirUrl(String path, Map<String, String> filtros) {
    if (filtros.isEmpty()) {
      return path;
    }

    StringBuilder urlBuilder = new StringBuilder(path).append("?");
    filtros.entrySet().stream().filter(entry ->
            entry.getValue() != null && !entry.getValue().isEmpty())
        .forEach(entry -> {
          if (urlBuilder.charAt(urlBuilder.length() - 1) != '?') {
            urlBuilder.append("&");
          }
          urlBuilder.append(entry.getKey()).append("=")
              .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        });

    return urlBuilder.toString();
  }

  private List<Hecho> deserializarHechos(String json) {
    try {
      return getObjectMapper().readValue(json, new TypeReference<List<Hecho>>() {
      });
    } catch (Exception e) {
      throw new RuntimeException("Error al deserializar hechos", e);
    }
  }

  private String serializarSolicitud(SolicitudEliminacion solicitud) {
    try {
      return getObjectMapper().writeValueAsString(solicitud);
    } catch (Exception e) {
      throw new RuntimeException("Error al serializar la solicitud de eliminación", e);
    }
  }

  // Getters con inicialización lazy para campos @Transient
  private HttpClient getHttpClient() {
    if (httpClient == null) {
      httpClient = HttpClient.newHttpClient();
    }
    return httpClient;
  }

  private ObjectMapper getObjectMapper() {
    return objectMapper;
  }
}