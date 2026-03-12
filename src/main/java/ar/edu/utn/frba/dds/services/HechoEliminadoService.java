package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.model.hechos.HechoEliminado;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.HechoEliminadoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import java.util.stream.Collectors;

public class HechoEliminadoService {



  public void marcarHechoComoEliminado(Hecho hecho) {
    if (hecho == null) {
      throw new IllegalArgumentException("El hecho no puede ser nulo");
    }

    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoEliminadoRepository repo = new HechoEliminadoRepository(em);
    try {
      em.getTransaction().begin();
      HechoEliminado hechoEliminado = new HechoEliminado(hecho);
      repo.save(hechoEliminado);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al marcar el hecho como eliminado: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public List<Hecho> filtrarHechosEliminados(List<Hecho> hechos) {
    if (hechos == null || hechos.isEmpty()) {
      return hechos;
    }

    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoEliminadoRepository repo = new HechoEliminadoRepository(em);
    try {
      List<HechoEliminado> hechosEliminados = repo.findAll();

      return hechos.stream()
          .filter(hecho -> hechosEliminados.stream()
              .noneMatch(he -> he.sonIguales(hecho)))
          .collect(Collectors.toList());
    } finally {
      em.close();
    }
  }
}



