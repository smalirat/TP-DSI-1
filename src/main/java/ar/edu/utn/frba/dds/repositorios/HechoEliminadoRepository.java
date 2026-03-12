package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.model.hechos.HechoEliminado;
import jakarta.persistence.EntityManager;
import java.util.List;

public class HechoEliminadoRepository {

  private final EntityManager entityManager;

  public HechoEliminadoRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public void save(HechoEliminado hechoEliminado) {
    if (hechoEliminado.getId() == null) {
      entityManager.persist(hechoEliminado);
    } else {
      entityManager.merge(hechoEliminado);
    }
  }

  public List<HechoEliminado> findAll() {
    return entityManager.createQuery("SELECT h FROM HechoEliminado h", HechoEliminado.class)
        .getResultList();
  }

}

