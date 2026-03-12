package ar.edu.utn.frba.dds.config;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class ConfiguracionYaml {
  private final Map<String, Object> data;

  public ConfiguracionYaml(String path) {
    Yaml yaml = new Yaml();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(path)) {
      if (input == null) {
        throw new RuntimeException("No se encontró el archivo YAML: " + path);
      }
      this.data = yaml.load(input);
    } catch (Exception e) {
      throw new RuntimeException("Error al leer el YAML: " + e.getMessage(), e);
    }
  }

  public String getServicio() {
    return (String) data.get("servicio");
  }

  public String getAccion() {
    return (String) data.get("accion");
  }

  public int getIntervaloMinutos() {
    return (int) data.get("intervalo-minutos");
  }
}
