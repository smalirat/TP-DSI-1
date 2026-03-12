package ar.edu.utn.frba.dds.model.exceptions;

public class ImportacionHechosException extends RuntimeException {
  public ImportacionHechosException(String mensaje, Throwable causa) {
    super(mensaje, causa);
  }
}
