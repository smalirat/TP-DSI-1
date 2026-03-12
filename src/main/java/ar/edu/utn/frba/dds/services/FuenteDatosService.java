package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.fuentes.Agregador;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.model.fuentes.FuenteEstatica;
import ar.edu.utn.frba.dds.model.fuentes.FuenteProxy;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.FuenteDatosRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FuenteDatosService {

  private HechoEliminadoService hechoEliminadoService;

  public void initSevice() {
    if(this.hechoEliminadoService == null){
      this.hechoEliminadoService = new HechoEliminadoService();
    }
  }

  public List<FuenteDatos> obtenerTodasLasFuentes() {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      return repo.findAll();
    } finally {
      em.close();
    }
  }

  public FuenteDatos obtenerFuentePorId(Long id) {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      return repo.findById(id);
    } finally {
      em.close();
    }
  }

  public void guardarFuente(FuenteDatos fuente) {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      em.getTransaction().begin();
      repo.save(fuente);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al guardar la fuente: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void eliminarFuente(FuenteDatos fuente) {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      em.getTransaction().begin();
      repo.delete(fuente);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al eliminar la fuente: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void eliminarFuentePorId(Long id) {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      em.getTransaction().begin();
      FuenteDatos fuente = repo.findById(id);
      if (fuente != null) {
        repo.delete(fuente);
      }
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al eliminar la fuente: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void actualizarFuente(FuenteDatos fuente) {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      em.getTransaction().begin();
      repo.save(fuente);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al actualizar la fuente: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public List<FuenteDinamica> obtenerFuentesDinamicas() {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    try {
      return em.createQuery("SELECT f FROM FuenteDinamica f", FuenteDinamica.class)
          .getResultList();
    } finally {
      em.close();
    }
  }

  public List<Hecho> obtenerHechosDeFuente(Long fuenteId) {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      FuenteDatos fuente = repo.findById(fuenteId);
      if (fuente == null) {
        throw new IllegalArgumentException("Fuente no encontrada con ID: " + fuenteId);
      }
      
      if (fuente instanceof FuenteDinamica) {
        FuenteDinamica fuenteDinamica = repo.findFuenteDinamicaById(fuenteId);
        if (fuenteDinamica == null) {
          throw new IllegalArgumentException("Fuente dinámica no encontrada con ID: " + fuenteId);
        }
        return new ArrayList<>(fuenteDinamica.obtenerHechos());
      }
      
      if (fuente instanceof FuenteProxy) {
        FuenteProxy fuenteProxy = repo.findFuenteProxyById(fuenteId);
        if (fuenteProxy == null) {
          throw new IllegalArgumentException("Fuente proxy no encontrada con ID: " + fuenteId);
        }
        return new ArrayList<>(fuenteProxy.obtenerHechos());
      }
      
      if (fuente instanceof Agregador) {
        Agregador agregador = repo.findAgregadorById(fuenteId);
        if (agregador == null) {
          throw new IllegalArgumentException("Agregador no encontrado con ID: " + fuenteId);
        }
        agregador.actualizarCache();
        return new ArrayList<>(agregador.obtenerHechos());
      }
      
      return new ArrayList<>(fuente.obtenerHechos());
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener hechos de la fuente: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public List<Hecho> obtenerTodosLosHechosDeTodasLasFuentes() {
    initSevice();
    List<Hecho> todosLosHechos = new ArrayList<>();
    
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      List<FuenteDatos> fuentes = repo.findAll();
      
      for (FuenteDatos fuente : fuentes) {
        try {
          List<Hecho> hechosFuente = obtenerHechosDeFuente(fuente.getId());
          todosLosHechos.addAll(hechosFuente);
        } catch (Exception e) {
          System.err.println("Error al obtener hechos de fuente " + fuente.getId() + ": " + e.getMessage());
        }
      }
    } finally {
      em.close();
    }
    
    return hechoEliminadoService.filtrarHechosEliminados(todosLosHechos);
  }

  public Hecho buscarHechoPorIdEnTodasLasFuentes(Long hechoId) {
    initSevice();
    if (hechoId == null) {
      return null;
    }
    
    if (hechoId < 0) {
      EntityManager em = EntityManagerProvider.createEntityManager();
      FuenteDatosRepository repo = new FuenteDatosRepository(em);
      try {
        List<FuenteDatos> fuentes = repo.findAll();
        
        for (FuenteDatos fuente : fuentes) {
          try {
            List<Hecho> hechos = obtenerHechosDeFuente(fuente.getId());
            Hecho encontrado = hechos.stream()
                .filter(h -> Objects.equals(h.getId(), hechoId))
                .findFirst()
                .orElse(null);
            
            if (encontrado != null) {
              return encontrado;
            }
          } catch (Exception e) {
          }
        }
      } finally {
        em.close();
      }
    } else {
      EntityManager em = EntityManagerProvider.createEntityManager();
      ar.edu.utn.frba.dds.repositorios.HechoRepository repo = new ar.edu.utn.frba.dds.repositorios.HechoRepository(em);
      try {
        return repo.findById(hechoId);
      } finally {
        em.close();
      }
    }
    
    return null;
  }

  public List<Hecho> obtenerTodosLosHechosDeTodasLasFuentesIncluyendoEliminados() {
    initSevice();
    List<Hecho> todosLosHechos = new ArrayList<>();
    
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);
    try {
      List<FuenteDatos> fuentes = repo.findAll();
      
      for (FuenteDatos fuente : fuentes) {
        try {
          List<Hecho> hechosFuente = obtenerHechosDeFuente(fuente.getId());
          todosLosHechos.addAll(hechosFuente);
        } catch (Exception e) {
          System.err.println("Error al obtener hechos de fuente " + fuente.getId() + ": " + e.getMessage());
        }
      }
    } finally {
      em.close();
    }
    
    return todosLosHechos;
  }

  public void actualizarFuentesExternas() {
    initSevice();
    EntityManager em = EntityManagerProvider.createEntityManager();
    FuenteDatosRepository repo = new FuenteDatosRepository(em);

    List<FuenteProxy> proxies = em.createQuery("SELECT f FROM FuenteProxy f", FuenteProxy.class).getResultList();
    List<Agregador> agregadores = em.createQuery("SELECT a FROM Agregador a", Agregador.class).getResultList();

    try {
      em.getTransaction().begin();

      for (FuenteProxy proxy : proxies) {
        proxy.actualizarHechos();
        em.merge(proxy);
      }

      for (Agregador agregador : agregadores) {
        agregador.actualizarCache();
      }

      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw new RuntimeException("Error masivo al actualizar fuentes: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

}
