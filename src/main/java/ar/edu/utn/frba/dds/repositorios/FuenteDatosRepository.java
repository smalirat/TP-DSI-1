package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.fuentes.Agregador;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDatos;
import ar.edu.utn.frba.dds.model.fuentes.FuenteDinamica;
import ar.edu.utn.frba.dds.model.fuentes.FuenteProxy;
import jakarta.persistence.EntityManager;
import java.util.List;

public class FuenteDatosRepository {

  private final EntityManager entityManager;

  public FuenteDatosRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }


  public FuenteDatos findById(Long fuenteId) {

    return entityManager.find(FuenteDatos.class, fuenteId);
  }

  public FuenteDinamica findFuenteDinamicaById(Long fuenteId) {
    List<FuenteDinamica> results = entityManager.createQuery(
        "SELECT f FROM FuenteDinamica f LEFT JOIN FETCH f.hechos WHERE f.id = :id", 
        FuenteDinamica.class)
        .setParameter("id", fuenteId)
        .getResultList();
    
    if (results.isEmpty()) {
      return null;
    }
    return results.get(0);
  }

  public FuenteProxy findFuenteProxyById(Long fuenteId) {
    List<FuenteProxy> results = entityManager.createQuery(
        "SELECT f FROM FuenteProxy f LEFT JOIN FETCH f.hechos WHERE f.id = :id", 
        FuenteProxy.class)
        .setParameter("id", fuenteId)
        .getResultList();
    
    if (results.isEmpty()) {
      return null;
    }
    return results.get(0);
  }

  public Agregador findAgregadorById(Long fuenteId) {
    List<Agregador> results = entityManager.createQuery(
        "SELECT a FROM Agregador a LEFT JOIN FETCH a.fuentes WHERE a.id = :id", 
        Agregador.class)
        .setParameter("id", fuenteId)
        .getResultList();
    
    if (results.isEmpty()) {
      return null;
    }
    return results.get(0);
  }

  public List<FuenteDatos> findAll() {
    return entityManager.createQuery("SELECT f FROM FuenteDatos f", FuenteDatos.class)
        .getResultList();
  }

  public void save(FuenteDatos fuente) {
    if (fuente.getId() == null) {
      entityManager.persist(fuente);
    } else {
      entityManager.merge(fuente);
    }
  }

  public void delete(FuenteDatos fuente) {
    entityManager.remove(fuente);
  }
}