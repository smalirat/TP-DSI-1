package ar.edu.utn.frba.dds.persistance;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerProvider {
  private static final EntityManagerFactory emf =
      Persistence.createEntityManagerFactory("sistemaPU");

  private EntityManagerProvider() {}

  public static EntityManager createEntityManager() {
    return emf.createEntityManager();
  }
}
