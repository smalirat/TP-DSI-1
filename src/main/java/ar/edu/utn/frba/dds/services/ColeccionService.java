package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.consenso.AlgoritmoConsenso;
import ar.edu.utn.frba.dds.model.consenso.ConsensoAbsoluta;
import ar.edu.utn.frba.dds.model.consenso.ConsensoMayoriaSimple;
import ar.edu.utn.frba.dds.model.consenso.ConsensoMultiplesMenciones;
import ar.edu.utn.frba.dds.model.enums.ModoNavegacion; // Importar ModoNavegacion
import ar.edu.utn.frba.dds.model.enums.TipoAlgoritmoConsenso;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.hechos.Coleccion;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.ColeccionRepository;
import ar.edu.utn.frba.dds.repositorios.FuenteDatosRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ColeccionService {

  private FuenteDatosService fuenteDatosService;

  public void initService() {
    if (this.fuenteDatosService == null){
      this.fuenteDatosService = new FuenteDatosService();
    }
  }

  private AlgoritmoConsenso instanciarAlgoritmo(TipoAlgoritmoConsenso tipo, EntityManager em) {
    this.initService();
    if (tipo == null || tipo == TipoAlgoritmoConsenso.NINGUNO) {
      return null;
    }
    FuenteDatosRepository repoFuentes = new FuenteDatosRepository(em);
    switch (tipo) {
      case ABSOLUTA:
        return new ConsensoAbsoluta(repoFuentes);
      case MAYORIA_SIMPLE:
        return new ConsensoMayoriaSimple(repoFuentes);
      case MULTIPLES_MENCIONES:
        return new ConsensoMultiplesMenciones(repoFuentes);
      default:
        return null;
    }
  }

  public List<Coleccion> obtenerTodasLasColecciones() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    ColeccionRepository repo = new ColeccionRepository(em);
    try {
      return repo.findAll();
    } finally {
      em.close();
    }
  }

  public Optional<Coleccion> obtenerColeccionPorHandle(String handle) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    ColeccionRepository repo = new ColeccionRepository(em);
    try {
      Optional<Coleccion> coleccionOpt = repo.findByHandle(handle);

      if (coleccionOpt.isPresent()) {
        Coleccion c = coleccionOpt.get();

        if (c.getModoNavegacion() == ModoNavegacion.CURADA) {
          c.getHechosConsensuados().size();
        } else {
          c.getFuente().obtenerHechos().size();
        }
      }

      return coleccionOpt;

    } finally {
      em.close();
    }
  }

  public Coleccion crearColeccionConFuente(String nombre, String handle, String descripcion,
                                           Long fuenteId, String algoritmoNombre) { // Recibe String
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    ColeccionRepository repo = new ColeccionRepository(em);
    FuenteDatos fuente = fuenteDatosService.obtenerFuentePorId(fuenteId);

    if (fuente == null) {
      throw new IllegalArgumentException("No se encontró la fuente con ID: " + fuenteId);
    }

    TipoAlgoritmoConsenso tipoAlgoritmo;
    try {
      tipoAlgoritmo = TipoAlgoritmoConsenso.valueOf(algoritmoNombre.toUpperCase());
    } catch (Exception e) {
      tipoAlgoritmo = TipoAlgoritmoConsenso.NINGUNO;
    }

    try {
      em.getTransaction().begin();
      if (repo.findByHandle(handle).isPresent()) {
        throw new IllegalArgumentException("Ya existe una colección con el handle: " + handle);
      }

      Coleccion nuevaColeccion = new Coleccion(nombre, descripcion, handle, fuente);
      nuevaColeccion.setTipoAlgoritmoConsenso(tipoAlgoritmo);

      repo.save(nuevaColeccion);
      em.getTransaction().commit();
      return nuevaColeccion;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new RuntimeException("Error al crear la colección: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public Coleccion actualizarColeccion(String handle, String nuevoTitulo, String nuevaDescripcion,
                                       Long nuevaFuenteId, TipoAlgoritmoConsenso nuevoTipoAlgoritmo) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    ColeccionRepository repo = new ColeccionRepository(em);
    FuenteDatos nuevaFuente = fuenteDatosService.obtenerFuentePorId(nuevaFuenteId);

    if (nuevaFuente == null) {
      throw new IllegalArgumentException("Fuente de datos seleccionada no válida (ID: " + nuevaFuenteId + ")");
    }

    try {
      em.getTransaction().begin();
      Coleccion coleccion = repo.findByHandle(handle)
          .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada con handle: " + handle));

      coleccion.setTitulo(nuevoTitulo);
      coleccion.setDescripcion(nuevaDescripcion);
      coleccion.setFuente(nuevaFuente);

      coleccion.setTipoAlgoritmoConsenso(nuevoTipoAlgoritmo);

      Coleccion actualizada = em.merge(coleccion);
      em.getTransaction().commit();
      return actualizada;

    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new RuntimeException("Error al actualizar la colección: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void eliminarColeccionPorHandle(String handle) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    ColeccionRepository repo = new ColeccionRepository(em);
    try {
      em.getTransaction().begin();
      Coleccion coleccion = repo.findByHandle(handle)
          .orElseThrow(() -> new IllegalArgumentException("No se encontró colección para eliminar con handle: " + handle));
      repo.delete(coleccion);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new RuntimeException("Error al eliminar la colección: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void recalcularTodosLosConsensos() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    ColeccionRepository repo = new ColeccionRepository(em);
    List<Coleccion> colecciones = em.createQuery(
        "SELECT c FROM Coleccion c JOIN FETCH c.fuente", Coleccion.class
    ).getResultList();

    try {
      em.getTransaction().begin();

      for (Coleccion c : colecciones) {
        TipoAlgoritmoConsenso tipo = c.getTipoAlgoritmoConsenso();
        AlgoritmoConsenso algoritmo = instanciarAlgoritmo(tipo, em);
        List<Hecho> hechosConsensuados;

        if (algoritmo == null) {
          hechosConsensuados = fuenteDatosService.obtenerHechosDeFuente(c.getFuente().getId());
        } else {
          List<Hecho> candidatos = fuenteDatosService.obtenerHechosDeFuente(c.getFuente().getId());

          hechosConsensuados = candidatos.stream()
              .filter(algoritmo::cumple)
              .collect(Collectors.toList());
        }

        c.setHechosConsensuados(hechosConsensuados);
        em.merge(c);
      }

      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new RuntimeException("Error masivo al recalcular consensos: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }
}


