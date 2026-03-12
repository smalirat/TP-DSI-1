package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.hechos.Coleccion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class ColeccionRepository {

  private final EntityManager entityManager;

  public ColeccionRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Optional<Coleccion> findByHandle(String handle) {
    TypedQuery<Coleccion> query = entityManager
        .createQuery("SELECT c FROM Coleccion c WHERE c.handle = :handle", Coleccion.class);
    query.setParameter("handle", handle);
    try {
      return Optional.of(query.getSingleResult());
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public List<Coleccion> findAll() {
    return entityManager.createQuery("SELECT c FROM Coleccion c", Coleccion.class)
        .getResultList();
  }

  public void save(Coleccion coleccion) {
    if (coleccion.getId() == null) {
      entityManager.persist(coleccion);
    } else {
      entityManager.merge(coleccion);
    }
  }

  public void delete(Coleccion coleccion) {
    entityManager.remove(coleccion);
  }
}