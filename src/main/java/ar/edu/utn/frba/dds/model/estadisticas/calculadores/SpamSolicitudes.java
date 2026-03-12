package ar.edu.utn.frba.dds.model.estadisticas.calculadores;

import ar.edu.utn.frba.dds.repositorios.HechoRepository;
import ar.edu.utn.frba.dds.repositorios.RepositorioSolicitudes;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class SpamSolicitudes implements CalculadorEstadistica {


    @Override
    public byte[] calcularYGenerarCSV(HechoRepository hechoRepo, RepositorioSolicitudes solRepo,
                                    Map<String, String> parametros) {
        Long[] conteo = solRepo.contarSolicitudesSpam();

        Long totalSolicitudes = conteo[0];
        Long solicitudesSpam = conteo[1];
        double porcentajeSpam = (totalSolicitudes > 0)
            ? (solicitudesSpam.doubleValue() / totalSolicitudes.doubleValue()) * 100 : 0.0;

        StringBuilder sb = new StringBuilder();

        sb.append(String.join(",", getEncabezadoCSV())).append("\n");
        sb.append(totalSolicitudes).append(",")
            .append(solicitudesSpam).append(",")
            .append(String.format("%.2f", porcentajeSpam)).append("\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String[] getEncabezadoCSV() {
        return new String[]{"Total_Solicitudes", "Cantidad_Spam", "Porcentaje_Spam"};
    }
}