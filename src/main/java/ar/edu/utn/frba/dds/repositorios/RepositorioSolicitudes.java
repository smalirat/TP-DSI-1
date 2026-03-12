package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.solicitudes.SolicitudEliminacion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import ar.edu.utn.frba.dds.model.enums.EstadoSolicitud;

public class RepositorioSolicitudes {

  private final EntityManager entityManager;

  public RepositorioSolicitudes(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public SolicitudEliminacion findById(Long id) {
    return entityManager.find(SolicitudEliminacion.class, id);
  }

  public List<SolicitudEliminacion> findAll() {
    return entityManager.createQuery("SELECT s FROM SolicitudEliminacion s", SolicitudEliminacion.class)
        .getResultList();
  }

  public List<SolicitudEliminacion> findByEstado(EstadoSolicitud estado) {
    return entityManager.createQuery(
        "FROM SolicitudEliminacion s WHERE s.estado = :estado",
        SolicitudEliminacion.class)
        .setParameter("estado", estado)
        .getResultList();
  }

  public void save(SolicitudEliminacion solicitud) {
    if (solicitud.getId() == null) {
      entityManager.persist(solicitud);
    } else {
      entityManager.merge(solicitud);
    }
  }

  public void delete(SolicitudEliminacion solicitud) {
    entityManager.remove(solicitud);
  }

  public Long[] contarSolicitudesSpam() {
    Long total = entityManager.createQuery("SELECT COUNT(s) FROM SolicitudEliminacion s", Long.class)
        .getSingleResult();

    Long spamCount = entityManager.createQuery(
        "SELECT COUNT(s) FROM SolicitudEliminacion s WHERE s.estado = :rechazada AND s.motivoRechazo = :motivoSpam",
        Long.class)
        .setParameter("rechazada", EstadoSolicitud.RECHAZADA)
        .setParameter("motivoSpam", "Contenido detectado como spam automÃ¡ticamente.")
        .getSingleResult();

    return new Long[] { total, spamCount };
  }

}