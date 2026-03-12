package ar.edu.utn.frba.dds.model.usuarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false, length = 80)
  private String username;

  @Column(nullable = false, length = 255)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RolUsuario rol;

  @Column(length = 80)
  private String nombre;

  @Column(length = 80)
  private String apellido;

  public Usuario() {
  }

  public Usuario(String username, String passwordHash, RolUsuario rol, String nombre, String apellido, Integer edad) {
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("El username es obligatorio");
    }
    if (passwordHash == null || passwordHash.isBlank()) {
      throw new IllegalArgumentException("El passwordHash es obligatorio");
    }
    if (rol == null) {
      throw new IllegalArgumentException("El rol es obligatorio");
    }
    this.username = username.trim();
    this.passwordHash = passwordHash;
    this.rol = rol;
    this.nombre = nombre;
    this.apellido = apellido;
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public RolUsuario getRol() {
    return rol;
  }

  public String getNombre() {
    return nombre;
  }

  public String getApellido() {
    return apellido;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public void setApellido(String apellido) {
    this.apellido = apellido;
  }

}
