package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.estadisticas.Estadistica;
import ar.edu.utn.frba.dds.model.enums.TipoEstadistica;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.EstadisticaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class EstadisticaService {

  public static final Duration TIEMPO_MINIMO_ENTRE_CALCULOS = Duration.ofMinutes(30);


  public List<Estadistica> obtenerTodasLasEstadisticas() {
    EntityManager em = EntityManagerProvider.createEntityManager();
    EstadisticaRepository repo = new EstadisticaRepository(em);
    try {
      return repo.findAll();
    } finally {
      em.close();
    }
  }

  public Estadistica obtenerEstadisticaPorId(Long id) {
    EntityManager em = EntityManagerProvider.createEntityManager();
    EstadisticaRepository repo = new EstadisticaRepository(em);
    try {
      return repo.findById(id);
    } finally {
      em.close();
    }
  }

  public List<Estadistica> obtenerEstadisticasPorTipo(TipoEstadistica tipo) {
    EntityManager em = EntityManagerProvider.createEntityManager();
    EstadisticaRepository repo = new EstadisticaRepository(em);
    try {
      return repo.findByTipo(tipo);
    } finally {
      em.close();
    }
  }


  public Estadistica crearEstadistica(TipoEstadistica tipo, String nombre, String parametros,
                                      LocalDateTime fechaCalculo, boolean esPublica) {
    EntityManager em = EntityManagerProvider.createEntityManager();
    EstadisticaRepository repo = new EstadisticaRepository(em);

    try {
      em.getTransaction().begin();

      // no recalcular si no pasó el tiempo mínimo
      Estadistica ultima = repo.findUltimaPorTipoYParametros(tipo, parametros);

      if (ultima != null) {
        Duration tiempoDesdeUltima =
            Duration.between(ultima.getFechaCalculo(), fechaCalculo);

        if (tiempoDesdeUltima.compareTo(TIEMPO_MINIMO_ENTRE_CALCULOS) < 0) {
          throw new IllegalStateException(
              "La estadística ya fue generada recientemente. " +
                  "Espere al menos " + TIEMPO_MINIMO_ENTRE_CALCULOS.toMinutes() +
                  " minutos."
          );
        }
      }

      Estadistica nueva = new Estadistica(
          tipo,
          nombre,
          parametros,
          fechaCalculo,
          esPublica
      );

      repo.save(nueva);
      em.getTransaction().commit();
      return nueva;

    } catch (Exception e) {
      if (em.getTransaction().isActive()) {
        em.getTransaction().rollback();
      }
      throw e;
    } finally {
      em.close();
    }
  }

  public List<Estadistica> obtenerEstadisticasPublicas() {
    EntityManager em = EntityManagerProvider.createEntityManager();
    EstadisticaRepository repo = new EstadisticaRepository(em);
    try {
      return repo.findByEsPublica(true);
    } finally {
      em.close();
    }
  }

  public List<Estadistica> obtenerEstadisticasPrivadas() {
    EntityManager em = EntityManagerProvider.createEntityManager();
    EstadisticaRepository repo = new EstadisticaRepository(em);
    try {
      return repo.findByEsPublica(false);
    } finally {
      em.close();
    }
  }
}
