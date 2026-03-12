package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.server.templates.JavalinHandlebars;
import ar.edu.utn.frba.dds.server.templates.JavalinRenderer;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;

public class Server {
  public void start() {

    int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "9090"));
    var app = Javalin.create(config -> {
      initializeStaticFiles(config);
      initializeTemplating(config);
    });

    new Router().configure(app);
    app.start(port);
    System.out.println("Servidor iniciado en el puerto: " + port);
  }

  private void initializeTemplating(JavalinConfig config) {
    config.fileRenderer(
        new JavalinRenderer().register("hbs", new JavalinHandlebars())
    );
  }

  private static void initializeStaticFiles(JavalinConfig config) {
    config.staticFiles.add(staticFileConfig -> {
      staticFileConfig.hostedPath = "/templates";
      staticFileConfig.directory = "/templates";
    });
  }
}