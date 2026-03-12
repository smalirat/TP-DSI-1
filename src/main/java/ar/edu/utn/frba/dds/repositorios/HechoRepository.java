package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.enums.Categoria;
import ar.edu.utn.frba.dds.model.hechos.Hecho;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class HechoRepository {

  private final EntityManager entityManager;

  public HechoRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public List<Hecho> findAll() {
    return entityManager.createQuery("SELECT h FROM Hecho h", Hecho.class)
        .getResultList();
  }

  public Hecho findById(Long id) {
    return entityManager.find(Hecho.class, id);
  }

  public void save(Hecho hecho) {
    if (hecho.getId() == null) {
      entityManager.persist(hecho);
    } else {
      entityManager.merge(hecho);
    }
  }

  public void delete(Hecho hecho) {
    if (entityManager.contains(hecho)) {
      entityManager.remove(hecho);
    } else {
      Hecho managedHecho = findById(hecho.getId());
      if (managedHecho != null) {
        entityManager.remove(managedHecho);
      }
    }
  }

  public List<Hecho> buscarPorTexto(String consulta) {
    if (consulta == null || consulta.trim().isEmpty()) {
      return findAll();
    }

    String consultaLike = "%" + consulta.toLowerCase() + "%";
    TypedQuery<Hecho> query = entityManager.createQuery(
        "SELECT h FROM Hecho h " +
            "WHERE (LOWER(h.titulo) LIKE :consulta " +
            "OR LOWER(h.descripcion) LIKE :consulta)",
        Hecho.class);
    query.setParameter("consulta", consultaLike);
    return query.getResultList();
  }

  public List<Hecho> findAllIncludingEliminated() {
    return entityManager.createQuery("SELECT h FROM Hecho h", Hecho.class)
        .getResultList();
  }

  public List<Object[]> contarHechosPorCategoria() {
    return entityManager.createQuery(
        "SELECT h.categoria, COUNT(h) FROM Hecho h GROUP BY h.categoria ORDER BY COUNT(h) DESC",
        Object[].class)
        .getResultList();
  }

  public List<Object[]> contarHechosPorProvinciaYCategoria(String categoriaNombre) {
    Categoria categoria = Categoria.valueOf(categoriaNombre.toUpperCase());
    return entityManager.createQuery(
        "SELECT h.provincia, COUNT(h) FROM Hecho h WHERE h.categoria = :categoria GROUP BY h.provincia ORDER BY COUNT(h) DESC",
        Object[].class)
        .setParameter("categoria", categoria)
        .getResultList();
  }

  public List<Object[]> contarHechosPorHoraYCategoria(String categoriaNombre) {
    Categoria categoria = Categoria.valueOf(categoriaNombre.toUpperCase());
    return entityManager.createQuery(
            "SELECT EXTRACT(HOUR FROM h.fechaCarga), COUNT(h) FROM Hecho h WHERE h.categoria = :categoria GROUP BY 1 ORDER BY 2 DESC",
            Object[].class)
        .setParameter("categoria", categoria)
        .getResultList();
  }

  public List<Object[]> contarHechosPorProvinciaDeColeccion(String handleColeccion) {
    return entityManager.createQuery(
        "SELECT h.provincia, COUNT(h) FROM Coleccion c JOIN c.hechosConsensuados h " +
            "WHERE c.handle = :handle GROUP BY h.provincia ORDER BY COUNT(h) DESC",
        Object[].class)
        .setParameter("handle", handleColeccion)
        .getResultList();
  }

}