package ar.edu.utn.frba.dds.controllers;

import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public class HomeController extends BaseController {

  public void index(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    model.put("mensaje", "¡Hola, MetaMapa!");
    model.put("activeTab", "home");
    if (ctx.sessionAttribute("usuarioRol") == "ADMIN") {
      renderWithSession(ctx, "admin_home.hbs", model);
    } else {
      renderWithSession(ctx, "home.hbs", model);
    }
  }
}