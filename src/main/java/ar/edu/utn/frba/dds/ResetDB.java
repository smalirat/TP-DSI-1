package ar.edu.utn.frba.dds;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class ResetDB {
  public static void main(String[] args) {
    EntityManagerFactory emf = null;
    try {
      System.out.println("⚠️  Iniciando conexión para actualizar la base de datos...");

      emf = Persistence.createEntityManagerFactory("sistemaPU");

      System.out.println("✅ ¡Base de datos reseteada con éxito!");
      System.out.println("   Las nuevas tablas han sido creadas según tus clases @Entity.");

    } catch (Exception e) {
      System.err.println("❌ Ocurrió un error durante el reseteo:");
      e.printStackTrace();
    } finally {
      if (emf != null) {
        emf.close();
      }
    }
  }
}