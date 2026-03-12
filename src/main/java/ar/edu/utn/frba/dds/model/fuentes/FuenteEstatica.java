package ar.edu.utn.frba.dds.model.fuentes;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import ar.edu.utn.frba.dds.model.exceptions.ImportacionHechosException;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ESTATICA")
public class FuenteEstatica extends FuenteDatos {

  @Transient
  private final String[] columnasEsperadas = {"Título", "Descripción",
      "Categoría", "Provincia", "Latitud",
      "Longitud", "Fecha del hecho"};
  @Column(name = "ruta_archivo_csv")
  private String rutaArchivoCsv;

  protected FuenteEstatica() {
  }

  public FuenteEstatica(String rutaArchivoCsv) {
    if (!rutaArchivoCsv.toLowerCase().endsWith(".csv")) {
      throw new IllegalArgumentException(
          "El archivo debe ser de tipo CSV");
    }
    this.rutaArchivoCsv = rutaArchivoCsv;
  }

  @Override
  public List<Hecho> obtenerHechos() {
    List<Hecho> hechos = new ArrayList<>();
    
    try (BufferedReader reader = obtenerReader()) {
      String encabezado = reader.readLine();
      validarEncabezado(encabezado);

      String linea;
      long contadorId = -1; // IDs negativos para hechos temporales
      while ((linea = reader.readLine()) != null) {
        Hecho hecho = parsearLinea(linea);
        asignarIdTemporal(hecho, contadorId--);
        hechos.add(hecho);
      }
    } catch (IOException e) {
      throw new ImportacionHechosException("Error al leer el archivo CSV", e);
    }
    return hechos;
  }

  private void asignarIdTemporal(Hecho hecho, long id) {
    // Usar reflexión para asignar el ID temporal
    try {
      java.lang.reflect.Field idField = Hecho.class.getDeclaredField("id");
      idField.setAccessible(true);
      idField.set(hecho, id);
    } catch (Exception e) {
      throw new ImportacionHechosException("Error al asignar ID temporal al hecho", e);
    }
  }

  private BufferedReader obtenerReader() throws IOException {
    File archivoCsv = new File(rutaArchivoCsv);
    if (archivoCsv.exists()) {
      return new BufferedReader(new FileReader(archivoCsv));
    }
    
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(rutaArchivoCsv);
    if (inputStream != null) {
      return new BufferedReader(new InputStreamReader(inputStream));
    }
    
    throw new ImportacionHechosException("El archivo CSV no existe: " + rutaArchivoCsv, null);
  }

  private void validarEncabezado(String encabezado) {
    if (encabezado == null) {
      throw new ImportacionHechosException("El archivo CSV está vacío", null);
    }
    List<String> actuales = parsearCSVLinea(encabezado);
    if (actuales.size() != columnasEsperadas.length) {
      throw new ImportacionHechosException(
          "Cantidad de columnas incorrecta en el encabezado. Esperado: " + columnasEsperadas.length + ", Encontrado: " + actuales.size(), null);
    }
    for (int i = 0; i < columnasEsperadas.length; i++) {
      if (!columnasEsperadas[i].equalsIgnoreCase(actuales.get(i).trim())) {
        throw new ImportacionHechosException("Encabezado inválido en la columna " + (i + 1) + ". Esperado: " + columnasEsperadas[i] + ", Encontrado: " + actuales.get(i), null);
      }
    }
  }

  private List<String> parsearCSVLinea(String linea) {
    List<String> campos = new ArrayList<>();
    boolean dentroComillas = false;
    StringBuilder campo = new StringBuilder();
    
    for (int i = 0; i < linea.length(); i++) {
      char c = linea.charAt(i);
      
      if (c == '"') {
        dentroComillas = !dentroComillas;
      } else if (c == ',' && !dentroComillas) {
        campos.add(campo.toString());
        campo = new StringBuilder();
      } else {
        campo.append(c);
      }
    }
    campos.add(campo.toString()); // Último campo
    
    return campos;
  }

  private Hecho parsearLinea(String linea) {
    List<String> campos = parsearCSVLinea(linea);
    if (campos.size() < columnasEsperadas.length) {
      throw new ImportacionHechosException(
          "La línea no tiene todos los campos requeridos: " + linea + " (Campos encontrados: " + campos.size() + ")", null);
    }
    try {
      String titulo = campos.get(0).trim();
      String descripcion = campos.get(1).trim();
      Categoria categoria = Categoria.fromString(campos.get(2).trim());
      Provincia provincia = Provincia.fromString(campos.get(3).trim());
      double latitud = Double.parseDouble(campos.get(4).trim());
      double longitud = Double.parseDouble(campos.get(5).trim());
      LocalDateTime fechaAcontecimiento = LocalDateTime.parse(campos.get(6).trim());

      validarCampos(titulo, descripcion, latitud, longitud, fechaAcontecimiento);

      return new Hecho(titulo, descripcion, categoria,
          provincia, latitud, longitud, fechaAcontecimiento,
          LocalDateTime.now(), OrigenHecho.DATASET);
    } catch (NumberFormatException e) {
      throw new ImportacionHechosException("Error al parsear coordenadas", e);
    } catch (DateTimeParseException e) {
      throw new ImportacionHechosException("Error al parsear fecha", e);
    }
  }

  private void validarCampos(String titulo, String descripcion, double latitud, double longitud,
                             LocalDateTime fechaAcontecimiento) {
    if (titulo.isEmpty()) {
      throw new ImportacionHechosException("El título no puede estar vacío", null);
    }
    if (descripcion.isEmpty()) {
      throw new ImportacionHechosException("La descripción no puede estar vacía", null);
    }
    if (latitud < -90 || latitud > 90) {
      throw new ImportacionHechosException("La latitud debe estar entre -90 y 90", null);
    }
    if (longitud < -180 || longitud > 180) {
      throw new ImportacionHechosException("La longitud debe estar entre -180 y 180", null);
    }
    if (fechaAcontecimiento.isAfter(LocalDateTime.now())) {
      throw new ImportacionHechosException("La fecha del hecho no puede ser futura", null);
    }
  }

  public String getRutaArchivoCsv() {
    return this.rutaArchivoCsv;
  }
}
