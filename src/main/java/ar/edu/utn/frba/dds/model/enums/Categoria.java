package ar.edu.utn.frba.dds.model.enums;

public enum Categoria {
  POLITICA, ECONOMIA, DEPORTES, CULTURA, TECNOLOGIA, SALUD,
  EDUCACION, MEDIO_AMBIENTE, SEGURIDAD, SOCIAL, OTROS;

  public static Categoria fromString(String valor) {
    if (valor == null || valor.isBlank()) {
      throw new IllegalArgumentException("La categoría no puede ser nula o vacía");
    }

    String normalizado = valor.trim().toUpperCase().replace(" ", "_");

    try {
      return Categoria.valueOf(normalizado);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Categoría desconocida: " + valor, e);
    }
  }
}
