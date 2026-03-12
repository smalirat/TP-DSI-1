package ar.edu.utn.frba.dds.model.estadisticas.calculadores;

import ar.edu.utn.frba.dds.repositorios.HechoRepository;
import ar.edu.utn.frba.dds.repositorios.RepositorioSolicitudes;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class HoraPicoPorCategoria implements CalculadorEstadistica {

    @Override
    public byte[] calcularYGenerarCSV(HechoRepository hechoRepo, RepositorioSolicitudes solRepo,
                                    Map<String, String> parametros) {
        String categoriaStr = Objects.requireNonNull(parametros.get("categoria"),
            "El parametro 'categoria' es obligatorio.");

        List<Object[]> resultados = hechoRepo.contarHechosPorHoraYCategoria(categoriaStr);
        StringBuilder sb = new StringBuilder();

        sb.append(String.join(",", getEncabezadoCSV())).append("\n");

        for (Object[] res : resultados) {
            String hora = String.format("%02d:00", (Integer) res[0]);
            sb.append(hora).append(",").append(res[1]).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String[] getEncabezadoCSV() {
        return new String[]{"Hora_del_Dia", "Cantidad_Hechos"};
    }
}