package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.enums.OrigenHecho;
import ar.edu.utn.frba.dds.model.enums.Provincia;
import ar.edu.utn.frba.dds.model.enums.TipoMultimedia;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.model.hechos.ContenidoMultimedia;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.FuenteDatosRepository;
import ar.edu.utn.frba.dds.repositorios.HechoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.LocalDateTime;
import java.util.List;

public class HechoService {

  private HechoEliminadoService hechoEliminadoService;

  public void initService() {
    if(this.hechoEliminadoService == null){
      this.hechoEliminadoService = new HechoEliminadoService();
    }
  }

  private FuenteDinamica obtenerFuenteDinamicaContribucion(EntityManager em) {
    this.initService();
    FuenteDatosRepository repoFuentes = new FuenteDatosRepository(em);
    FuenteDatos fuente = repoFuentes.findById(1L);
    if (fuente instanceof FuenteDinamica) {
      return (FuenteDinamica) fuente;
    }
    throw new IllegalStateException("No se encontró la Fuente Dinámica para contribuciones (ID 1).");
  }

  public List<Hecho> obtenerTodosLosHechos() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repo = new HechoRepository(em);
    try {
      List<Hecho> hechos = repo.findAll();
      return hechoEliminadoService.filtrarHechosEliminados(hechos);
    } finally {
      em.close();
    }
  }

  public Hecho obtenerHechoPorId(Long id) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repo = new HechoRepository(em);
    try {
      return repo.findById(id);
    } finally {
      em.close();
    }
  }

  public List<Hecho> buscarHechosPorTexto(String consulta) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repo = new HechoRepository(em);
    try {
      List<Hecho> hechos = repo.buscarPorTexto(consulta);
      return hechoEliminadoService.filtrarHechosEliminados(hechos);
    } finally {
      em.close();
    }
  }



  public Hecho crearHecho(String titulo, String descripcion, Categoria categoria,
                          Provincia provincia, Double latitud, Double longitud,
                          LocalDateTime fechaAcontecimiento, OrigenHecho origen, Long fuenteId) { // Añadido fuenteId
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    try {
      em.getTransaction().begin();

      FuenteDatosRepository repoFuentes = new FuenteDatosRepository(em);
      FuenteDatos fuente = repoFuentes.findById(fuenteId);
      if (!(fuente instanceof FuenteDinamica)) {
        throw new IllegalArgumentException("La fuente seleccionada (ID: " + fuenteId + ") no es dinámica.");
      }
      FuenteDinamica fuenteDinamica = (FuenteDinamica) fuente;

      Hecho nuevoHecho = new Hecho(
          titulo, descripcion, categoria, provincia, latitud, longitud,
          fechaAcontecimiento, LocalDateTime.now(), origen
      );

      fuenteDinamica.cargarHecho(nuevoHecho);
      em.merge(fuenteDinamica);

      em.getTransaction().commit();
      return nuevoHecho;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al crear el hecho en Fuente Dinámica: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public Hecho crearHechoConMultimedia(String titulo, String descripcionHecho, Categoria categoria,
                                       Provincia provincia, Double latitud, Double longitud,
                                       LocalDateTime fechaAcontecimiento, OrigenHecho origen,
                                       String urlMultimedia, TipoMultimedia tipoMultimedia, String descripcionMultimedia,
                                       Long fuenteId) { // Añadido fuenteId
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    try {
      em.getTransaction().begin();

      FuenteDatosRepository repoFuentes = new FuenteDatosRepository(em);
      FuenteDatos fuente = repoFuentes.findById(fuenteId);
      if (!(fuente instanceof FuenteDinamica)) {
        throw new IllegalArgumentException("La fuente seleccionada (ID: " + fuenteId + ") no es dinámica.");
      }
      FuenteDinamica fuenteDinamica = (FuenteDinamica) fuente;


      Hecho nuevoHecho = new Hecho(
          titulo, descripcionHecho, categoria, provincia, latitud, longitud,
          fechaAcontecimiento, LocalDateTime.now(), origen
      );

      if (urlMultimedia != null && !urlMultimedia.isBlank() && tipoMultimedia != null) {
        String descFinal = (descripcionMultimedia == null || descripcionMultimedia.isBlank()) ? "Adjunto inicial" : descripcionMultimedia;
        ContenidoMultimedia contenido = crearContenidoMultimediaDesdeUrl(urlMultimedia, tipoMultimedia, descFinal);
        nuevoHecho.agregarContenidoMultimedia(contenido);
      }

      fuenteDinamica.cargarHecho(nuevoHecho);
      em.merge(fuenteDinamica);

      em.getTransaction().commit();
      return nuevoHecho;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al crear el hecho con multimedia en Fuente Dinámica: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void agregarMultimediaAHecho(Long hechoId, String urlMultimedia, TipoMultimedia tipoMultimedia, String descripcion) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repoHechos = new HechoRepository(em);
    try {
      em.getTransaction().begin();
      Hecho hecho = repoHechos.findById(hechoId);
      if (hecho == null) {
        throw new IllegalArgumentException("No se encontró el hecho con ID: " + hechoId);
      }

      String descFinal = (descripcion == null || descripcion.isBlank()) ? "Adjunto por usuario" : descripcion;
      ContenidoMultimedia contenido = crearContenidoMultimediaDesdeUrl(urlMultimedia, tipoMultimedia, descFinal);

      hecho.agregarContenidoMultimedia(contenido);
      repoHechos.save(hecho); // Merge
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al agregar multimedia al hecho: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  private ContenidoMultimedia crearContenidoMultimediaDesdeUrl(String urlMultimedia, TipoMultimedia tipoMultimedia, String descripcionDefault) {
    this.initService();
    String urlParaFormato = urlMultimedia;

    int fragmentIndex = urlParaFormato.indexOf('#');
    if (fragmentIndex != -1) urlParaFormato = urlParaFormato.substring(0, fragmentIndex);
    int queryIndex = urlParaFormato.indexOf('?');
    if (queryIndex != -1) urlParaFormato = urlParaFormato.substring(0, queryIndex);

    String formato = "desconocido";
    int lastDotIndex = urlParaFormato.lastIndexOf('.');
    int lastSlashIndex = urlParaFormato.lastIndexOf('/');

    if (lastDotIndex != -1 && lastDotIndex < urlParaFormato.length() - 1 && lastDotIndex > lastSlashIndex) {
      String posibleFormato = urlParaFormato.substring(lastDotIndex + 1);
      if (posibleFormato.matches("^[a-zA-Z0-9]+$") && posibleFormato.length() <= 8) {
        formato = posibleFormato.toLowerCase();
      } else {
        System.err.println("Advertencia: Se encontró un punto pero '" + posibleFormato + "' no parece un formato de archivo válido en URL: " + urlMultimedia);
      }
    } else {
    }

    if (formato.length() > 255) formato = formato.substring(0, 255);

    return new ContenidoMultimedia(urlMultimedia, tipoMultimedia, descripcionDefault, formato);
  }

  public void actualizarHecho(Hecho hecho) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repo = new HechoRepository(em);
    try {
      em.getTransaction().begin();
      repo.save(hecho);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al actualizar el hecho: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void marcarHechoComoEliminado(Long id) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repo = new HechoRepository(em);
    try {
      Hecho hecho = repo.findById(id);
      if (hecho == null) {
        throw new IllegalArgumentException("No se encontró el hecho con ID: " + id + " para eliminar.");
      }
      hechoEliminadoService.marcarHechoComoEliminado(hecho);
    } finally {
      em.close();
    }
  }


  private Double parseOrNull(String valor) {
    try {
      this.initService();
      return (valor == null || valor.isBlank()) ? null : Double.parseDouble(valor);
    } catch (NumberFormatException e) {
      return null;
    }
  }
}