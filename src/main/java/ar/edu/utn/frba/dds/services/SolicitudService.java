package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.hechos.Hecho;
import ar.edu.utn.frba.dds.model.enums.EstadoSolicitud;
import ar.edu.utn.frba.dds.model.solicitudes.SolicitudEliminacion;
import ar.edu.utn.frba.dds.model.spam.DetectorDeSpam;
import ar.edu.utn.frba.dds.model.spam.DetectorDeSpamTfidf;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.HechoRepository;
import ar.edu.utn.frba.dds.repositorios.RepositorioSolicitudes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class SolicitudService {

  private DetectorDeSpam detectorSpam;
  private HechoEliminadoService hechoEliminadoService;

  public void initService() {
    if(this.detectorSpam == null){
      this.detectorSpam = new DetectorDeSpamTfidf();
      this.hechoEliminadoService = new HechoEliminadoService();
    }
  }

  public List<SolicitudEliminacion> obtenerTodasLasSolicitudes() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);
    try {
      return repo.findAll();
    } finally {
      em.close();
    }
  }

  public SolicitudEliminacion obtenerSolicitudPorId(Long id) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);
    try {
      return repo.findById(id);
    } finally {
      em.close();
    }
  }

  public SolicitudEliminacion crearSolicitudEliminacion(Long hechoId, String motivo) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    HechoRepository repoHechos = new HechoRepository(em);
    RepositorioSolicitudes repoSolicitudes = new RepositorioSolicitudes(em);
    
    try {
      em.getTransaction().begin();

      Hecho hecho = repoHechos.findById(hechoId);
      if (hecho == null) {
        throw new IllegalArgumentException("No se encontró el hecho con ID: " + hechoId);
      }

      SolicitudEliminacion nuevaSolicitud = new SolicitudEliminacion(motivo, hecho);

      if (detectorSpam.esSpam(nuevaSolicitud.getTextoFundamentacion())) {
        nuevaSolicitud.rechazar("Contenido detectado como spam automáticamente.");
      }

      repoSolicitudes.save(nuevaSolicitud);
      em.getTransaction().commit();
      return nuevaSolicitud;
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al crear la solicitud: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void aprobarSolicitud(Long solicitudId) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);

    try {
      em.getTransaction().begin();

      SolicitudEliminacion solicitud = repo.findById(solicitudId);
      if (solicitud == null) {
        throw new IllegalArgumentException("No se encontró la solicitud con ID: " + solicitudId);
      }
      if (!solicitud.estaPendiente()) {
        throw new IllegalStateException("Solo se pueden aprobar solicitudes pendientes.");
      }

      solicitud.aprobar();

      Hecho hecho = solicitud.getHecho();
      hechoEliminadoService.marcarHechoComoEliminado(hecho);


      repo.save(solicitud);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al aprobar la solicitud: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public void rechazarSolicitud(Long solicitudId, String motivoRechazo) {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);
    
    try {
      em.getTransaction().begin();

      SolicitudEliminacion solicitud = repo.findById(solicitudId);
      if (solicitud == null) {
        throw new IllegalArgumentException("No se encontró la solicitud con ID: " + solicitudId);
      }

      solicitud.rechazar(motivoRechazo);
      repo.save(solicitud);
      em.getTransaction().commit();
    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw new RuntimeException("Error al rechazar la solicitud: " + e.getMessage(), e);
    } finally {
      em.close();
    }
  }

  public List<SolicitudEliminacion> obtenerSolicitudesPendientes() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);
    try {
      return repo.findByEstado(EstadoSolicitud.PENDIENTE);
    } finally {
      em.close();
    }
  }

  public List<SolicitudEliminacion> obtenerSolicitudesAprobadas() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);
    try {
      return repo.findByEstado(EstadoSolicitud.ACEPTADA);
    } finally {
      em.close();
    }
  }

  public List<SolicitudEliminacion> obtenerSolicitudesRechazadas() {
    this.initService();
    EntityManager em = EntityManagerProvider.createEntityManager();
    RepositorioSolicitudes repo = new RepositorioSolicitudes(em);
    try {
      return repo.findByEstado(EstadoSolicitud.RECHAZADA);
    } finally {
      em.close();
    }
  }
}
