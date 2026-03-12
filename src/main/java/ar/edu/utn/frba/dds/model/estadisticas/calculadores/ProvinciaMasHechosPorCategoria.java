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

public class ProvinciaMasHechosPorCategoria implements CalculadorEstadistica {

    @Override
    public byte[] calcularYGenerarCSV(HechoRepository hechoRepo, RepositorioSolicitudes solRepo,
                                    Map<String, String> parametros) {
        String categoriaStr = Objects.requireNonNull(parametros.get("categoria"),
            "El parametro 'categoria' es obligatorio.");

        List<Object[]> resultados = hechoRepo.contarHechosPorProvinciaYCategoria(categoriaStr);
        StringBuilder sb = new StringBuilder();

        sb.append(String.join(",", getEncabezadoCSV())).append("\n");

        for (Object[] res : resultados) {
            sb.append(res[0]).append(",").append(res[1]).append("\n");
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String[] getEncabezadoCSV() {
        return new String[]{"Provincia", "Cantidad_Hechos"};
    }
}