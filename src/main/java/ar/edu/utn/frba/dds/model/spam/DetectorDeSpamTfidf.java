package ar.edu.utn.frba.dds.model.spam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DetectorDeSpamTfidf implements DetectorDeSpam {
  private static final int MAX_MENSAJES_RECIENTES = 10;
  private final List<String> mensajesRecientes;
  private final Map<String, Double> idf;
  private double nivelSimilitud;

  public DetectorDeSpamTfidf() {
    this.mensajesRecientes = new ArrayList<>();
    this.idf = new HashMap<>();
    this.nivelSimilitud = 0.7;
  }

  @Override
  public boolean esSpam(String texto) {

    actualizarIdf();

    if (mensajesRecientes.isEmpty()) {
      mensajesRecientes.add(texto);
      return false;
    }

    double maxSimilitud = mensajesRecientes.stream()
        .mapToDouble(mensaje -> calcularSimilitudCoseno(texto, mensaje))
        .max().orElse(0.0);

    mensajesRecientes.add(texto);
    if (mensajesRecientes.size() > MAX_MENSAJES_RECIENTES) {
      mensajesRecientes.remove(0);
    }
    return maxSimilitud > nivelSimilitud;
  }

  private void actualizarIdf() {
    Map<String, Integer> frecuenciaDocumentos = new HashMap<>();
    int totalDocumentos = mensajesRecientes.size();

    for (String mensaje : mensajesRecientes) {
      Set<String> palabrasUnicas = new HashSet<>(Arrays.asList(preprocesarTexto(mensaje)));
      for (String palabra : palabrasUnicas) {
        frecuenciaDocumentos.merge(palabra, 1, Integer::sum);
      }
    }

    for (Map.Entry<String, Integer> entry : frecuenciaDocumentos.entrySet()) {
      double idf = Math.log((double) (totalDocumentos + 1) / (entry.getValue() + 1)) + 1;
      this.idf.put(entry.getKey(), idf);
    }
  }

  private double calcularSimilitudCoseno(String texto1, String texto2) {
    Map<String, Double> vector1 = calcularVectorTfidf(texto1);
    Map<String, Double> vector2 = calcularVectorTfidf(texto2);

    double productoPunto = 0.0;
    double norma1 = 0.0;
    double norma2 = 0.0;

    for (String palabra : vector1.keySet()) {
      double valor1 = vector1.get(palabra);
      double valor2 = vector2.getOrDefault(palabra, 0.0);
      productoPunto += valor1 * valor2;
      norma1 += valor1 * valor1;
    }

    for (double valor : vector2.values()) {
      norma2 += valor * valor;
    }

    if (norma1 == 0 || norma2 == 0) {
      return 0.0;
    }

    return productoPunto / (Math.sqrt(norma1) * Math.sqrt(norma2));
  }

  private Map<String, Double> calcularVectorTfidf(String texto) {
    String[] palabras = preprocesarTexto(texto);
    Map<String, Double> tf = new HashMap<>();

    for (String palabra : palabras) {
      tf.merge(palabra, 1.0, Double::sum);
    }

    for (String palabra : tf.keySet()) {
      tf.put(palabra, tf.get(palabra) / palabras.length);
    }

    Map<String, Double> tfidf = new HashMap<>();
    for (String palabra : tf.keySet()) {
      double idf = this.idf.getOrDefault(palabra, 0.0);
      tfidf.put(palabra, tf.get(palabra) * idf);
    }

    return tfidf;
  }

  private String[] preprocesarTexto(String texto) {
    return texto.toLowerCase().replaceAll("[^a-zA-Z0-9áéíóúüñÁÉÍÓÚÜÑ\\s]", "").split("\\s+");
  }

  public void setNivelSimilitud(double similitud) {
    this.nivelSimilitud = similitud;
  }
}
