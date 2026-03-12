package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.estadisticas.Estadistica;
import ar.edu.utn.frba.dds.model.enums.TipoEstadistica;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

public class EstadisticaRepository {

  private final EntityManager entityManager;

  public EstadisticaRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Estadistica findById(Long id) {
    return entityManager.find(Estadistica.class, id);
  }

  public List<Estadistica> findAll() {
    return entityManager.createQuery("SELECT e FROM Estadistica e ORDER BY e.fechaCalculo DESC", Estadistica.class)
        .getResultList();
  }

  public void save(Estadistica estadistica) {
    if (estadistica.getId() == null) {
      entityManager.persist(estadistica);
    } else {
      entityManager.merge(estadistica);
    }
  }

  public void delete(Estadistica estadistica) {
    if (entityManager.contains(estadistica)) {
      entityManager.remove(estadistica);
    } else {
      Estadistica managed = findById(estadistica.getId());
      if (managed != null) {
        entityManager.remove(managed);
      }
    }
  }

  public List<Estadistica> findByTipo(TipoEstadistica tipo) {
    TypedQuery<Estadistica> q = entityManager.createQuery(
        "SELECT e FROM Estadistica e WHERE e.tipo = :tipo ORDER BY e.fechaCalculo DESC",
        Estadistica.class);
    q.setParameter("tipo", tipo);
    return q.getResultList();
  }

  public List<Estadistica> findByFecha(LocalDateTime fechaDesde) {
    TypedQuery<Estadistica> q = entityManager.createQuery(
        "SELECT e FROM Estadistica e WHERE e.fechaCalculo >= :f ORDER BY e.fechaCalculo DESC",
        Estadistica.class);
    q.setParameter("f", fechaDesde);
    return q.getResultList();
  }

  public List<Estadistica> findByEsPublica(boolean esPublica) {
    TypedQuery<Estadistica> q = entityManager.createQuery(
        "SELECT e FROM Estadistica e WHERE e.esPublica = :p ORDER BY e.fechaCalculo DESC",
        Estadistica.class);
    q.setParameter("p", esPublica);
    return q.getResultList();
  }

  public List<Estadistica> findByTipoYPublica(TipoEstadistica tipo, boolean esPublica) {
    TypedQuery<Estadistica> q = entityManager.createQuery(
        "SELECT e FROM Estadistica e WHERE e.tipo = :t AND e.esPublica = :p ORDER BY e.fechaCalculo DESC",
        Estadistica.class);
    q.setParameter("t", tipo);
    q.setParameter("p", esPublica);
    return q.getResultList();
  }
  public Estadistica findUltimaPorTipoYParametros(TipoEstadistica tipo, String parametros) {
    String jpql;
    TypedQuery<Estadistica> q;

    if (parametros == null) {
      jpql = """
            SELECT e FROM Estadistica e
            WHERE e.tipo = :tipo AND e.parametros IS NULL
            ORDER BY e.fechaCalculo DESC
        """;
      q = entityManager.createQuery(jpql, Estadistica.class);
      q.setParameter("tipo", tipo);
    } else {
      jpql = """
            SELECT e FROM Estadistica e
            WHERE e.tipo = :tipo AND e.parametros = :parametros
            ORDER BY e.fechaCalculo DESC
        """;
      q = entityManager.createQuery(jpql, Estadistica.class);
      q.setParameter("tipo", tipo);
      q.setParameter("parametros", parametros);
    }

    q.setMaxResults(1);
    List<Estadistica> result = q.getResultList();
    return result.isEmpty() ? null : result.get(0);
  }
}


