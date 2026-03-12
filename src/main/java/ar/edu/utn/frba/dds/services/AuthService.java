package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.model.usuarios.RolUsuario;
import ar.edu.utn.frba.dds.model.usuarios.Usuario;
import ar.edu.utn.frba.dds.persistance.EntityManagerProvider;
import ar.edu.utn.frba.dds.repositorios.UsuarioRepository;
import jakarta.persistence.EntityManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class AuthService {


  public AuthService() {
  }

  public Usuario registrarContribuyente(String username, String passwordPlano, String nombre, String apellido, Integer edad) {
    EntityManager em = EntityManagerProvider.createEntityManager();
    try {
      em.getTransaction().begin();
      UsuarioRepository repo = new UsuarioRepository(em);
      if (repo.findByUsername(username) != null) {
        throw new IllegalArgumentException("El nombre de usuario ya existe");
      }
      if (nombre == null || nombre.isBlank()) {
        throw new IllegalArgumentException("El nombre es obligatorio");
      }
      String hash = hashPassword(passwordPlano);
      Usuario u = new Usuario(username, hash, RolUsuario.CONTRIBUYENTE, nombre, apellido, edad);
      repo.save(u);
      em.getTransaction().commit();
      return u;
    } catch (RuntimeException e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  public Usuario login(String username, String passwordPlano) {
    EntityManager em = EntityManagerProvider.createEntityManager();
    try {
      UsuarioRepository repo = new UsuarioRepository(em);
      Usuario u = repo.findByUsername(username);
      if (u == null) return null;
      String hash = hashPassword(passwordPlano);
      return u;
    } finally {
      em.close();
    }
  }

  public void ensureDefaultAdmin() {
    EntityManager em = EntityManagerProvider.createEntityManager();
    try {
      em.getTransaction().begin();
      UsuarioRepository repo = new UsuarioRepository(em);
      Usuario admin = repo.findByUsername("admin");
      if (admin == null) {
        String hash = hashPassword("admin");
        admin = new Usuario("admin", hash, RolUsuario.ADMIN, "Administrador", null, null);
        repo.save(admin);
      }
      em.getTransaction().commit();
    } catch (RuntimeException e) {
      if (em.getTransaction().isActive()) em.getTransaction().rollback();
      throw e;
    } finally {
      em.close();
    }
  }

  private String hashPassword(String passwordPlano) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] digest = md.digest(passwordPlano.getBytes(StandardCharsets.UTF_8));
      return HexFormat.of().formatHex(digest);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("Algoritmo de hash no disponible", e);
    }
  }
}


