package ar.edu.utn.frba.dds.repositorios;

import ar.edu.utn.frba.dds.model.usuarios.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

public class UsuarioRepository {

  private final EntityManager entityManager;

  public UsuarioRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  public Usuario findByUsername(String username) {
    try {
      TypedQuery<Usuario> q = entityManager.createQuery("SELECT u FROM Usuario u WHERE u.username = :u", Usuario.class);
      q.setParameter("u", username);
      return q.getSingleResult();
    } catch (NoResultException e) {
      return null;
    }
  }

  public void save(Usuario usuario) {
    if (usuario.getId() == null) {
      entityManager.persist(usuario);
    } else {
      entityManager.merge(usuario);
    }
  }
}


