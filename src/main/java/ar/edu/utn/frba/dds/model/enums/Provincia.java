package ar.edu.utn.frba.dds.model.enums;

import java.util.Arrays;

public enum Provincia {
  BUENOS_AIRES("Buenos Aires"), CATAMARCA("Catamarca"),
  CHACO("Chaco"), CHUBUT("Chubut"), CORDOBA("Córdoba"),
  CORRIENTES("Corrientes"), ENTRE_RIOS("Entre Ríos"),
  FORMOSA("Formosa"), JUJUY("Jujuy"), LA_PAMPA("La Pampa"),
  LA_RIOJA("La Rioja"), MENDOZA("Mendoza"), MISIONES("Misiones"),
  NEUQUEN("Neuquén"), RIO_NEGRO("Río Negro"),
  SALTA("Salta"), SAN_JUAN("San Juan"), SAN_LUIS("San Luis"),
  SANTA_CRUZ("Santa Cruz"), SANTA_FE("Santa Fe"),
  SANTIAGO_DEL_ESTERO("Santiago del Estero"), TIERRA_DEL_FUEGO("Tierra del Fuego"),
  TUCUMAN("Tucumán"),
  CIUDAD_AUTONOMA_DE_BUENOS_AIRES("Ciudad Autónoma de Buenos Aires"), OTRAS("Otras");

  private final String nombre;

  Provincia(String nombre) {
    this.nombre = nombre;
  }

  public static Provincia fromString(String valor) {
    if (valor == null || valor.isBlank()) {
      throw new IllegalArgumentException("La provincia no puede ser nula o vacía");
    }

    return Arrays.stream(Provincia.values()).filter(p -> p
            .getNombre().equalsIgnoreCase(valor.trim())).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Provincia desconocida: " + valor));
  }

  public String getNombre() {
    return nombre;
  }
}
