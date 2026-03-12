package ar.edu.utn.frba.dds.model.estadisticas.calculadores;

import ar.edu.utn.frba.dds.repositorios.HechoRepository;
import ar.edu.utn.frba.dds.repositorios.RepositorioSolicitudes;
import java.util.Map;

public interface CalculadorEstadistica {

    byte[] calcularYGenerarCSV(HechoRepository hechoRepo,
                               RepositorioSolicitudes solRepo,
                               Map<String, String> parametros);



    String[] getEncabezadoCSV();

}