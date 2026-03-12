package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.model.usuarios.Usuario;
import ar.edu.utn.frba.dds.services.AuthService;
import io.javalin.http.Context;

import java.util.HashMap;
import java.util.Map;

public class AuthController extends BaseController {

  private AuthService authService;

  public AuthController() {
  }

  public void initServices(){
    if (this.authService == null){
      this.authService = new AuthService();
    }
  }

  public void mostrarFormularioLogin(Context ctx) {
    initServices();
    Map<String, Object> model = new HashMap<>();
    renderWithSession(ctx, "login.hbs", model);
  }

  public void login(Context ctx) {
    initServices();
    String username = ctx.formParam("username");
    String password = ctx.formParam("password");
    Usuario u = authService.login(username, password);
    if (u == null) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", "Credenciales inválidas");
      renderWithSession(ctx, "login.hbs", model);
      return;
    }
    ctx.sessionAttribute("usuarioId", u.getId());
    ctx.sessionAttribute("usuarioUsername", u.getUsername());
    ctx.sessionAttribute("usuarioRol", u.getRol().name());
    if (u.getRol().name().equals("ADMIN")) {
      ctx.redirect("/admin");
    } else {
      ctx.redirect("/");
    }
  }

  public void logout(Context ctx) {
    initServices();
    ctx.req().getSession().invalidate();
    ctx.redirect("/");
  }

  public void mostrarFormularioRegistro(Context ctx) {
    initServices();
    Map<String, Object> model = new HashMap<>();
    renderWithSession(ctx, "registro.hbs", model);
  }

  public void registrar(Context ctx) {
    initServices();
    String username = ctx.formParam("username");
    String password = ctx.formParam("password");
    String nombre = ctx.formParam("nombre");
    String apellido = ctx.formParam("apellido");
    Integer edad = null;
    try {
      String edadStr = ctx.formParam("edad");
      if (edadStr != null && !edadStr.isBlank()) {
        edad = Integer.parseInt(edadStr);
      }
      Usuario u = authService.registrarContribuyente(username, password, nombre, apellido, edad);
      ctx.sessionAttribute("usuarioId", u.getId());
      ctx.sessionAttribute("usuarioUsername", u.getUsername());
      ctx.sessionAttribute("usuarioRol", u.getRol().name());
      ctx.redirect("/");
    } catch (Exception e) {
      Map<String, Object> model = new HashMap<>();
      model.put("error", e.getMessage());
      model.put("username", username);
      model.put("nombre", nombre);
      model.put("apellido", apellido);
      model.put("edad", edad);
      renderWithSession(ctx, "registro.hbs", model);
    }
  }
}


