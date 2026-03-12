package ar.edu.utn.frba.dds.server.templates;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.rendering.FileRenderer;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;

public class JavalinHandlebars implements FileRenderer {

  private final Handlebars handlebars;

  public JavalinHandlebars() {
    TemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
    this.handlebars = new Handlebars(loader);

    this.handlebars.registerHelper("substring", (context, options) -> {
      String str = context.toString();
      int maxLength = options.param(0, 100);
      if (str == null || str.length() <= maxLength) {
        return str;
      }
      return str.substring(0, maxLength);
    });

    this.handlebars.registerHelper("eq", (context, options) -> {
      Object obj1 = context;
      Object obj2 = options.param(0);

      if (obj1 == null || obj2 == null) {
        return options.inverse(options.context);
      }

      if (Objects.equals(obj1.toString(), obj2.toString())) {
        return options.fn(options.context);
      } else {
        return options.inverse(options.context);
      }
    });

    this.handlebars.registerHelper("if_eq", (context, options) -> {
      Object obj1 = context;
      Object obj2 = options.param(0);

      if (obj1 == null || obj2 == null) {
        return options.inverse(options.context);
      }

      if (Objects.equals(obj1.toString(), obj2.toString())) {
        return options.fn(options.context);
      } else {
        return options.inverse(options.context);
      }
    });

    this.handlebars.registerHelper("ifeq", (context, options) -> {
      Object obj1 = context;
      Object obj2 = options.param(0);

      if (obj1 == null || obj2 == null) {
        return options.inverse(options.context);
      }

      if (Objects.equals(obj1.toString(), obj2.toString())) {
        return options.fn(options.context);
      } else {
        return options.inverse(options.context);
      }
    });

    this.handlebars.registerHelper("if_not_eq", (context, options) -> {
      Object obj1 = context;
      Object obj2 = options.param(0);

      if (obj1 == null || obj2 == null) {
        return options.fn(options.context);
      }

      if (!Objects.equals(obj1.toString(), obj2.toString())) {
        return options.fn(options.context);
      } else {
        return options.inverse(options.context);
      }
    });
  }

  @NotNull
  @Override
  public String render(@NotNull String path, @NotNull Map<String, ?> model, @NotNull Context context) {
    try {
      String templateName = path.replace(".hbs", "");
      Template template = handlebars.compile(templateName);
      return template.apply(model);
    } catch (IOException e) {
      e.printStackTrace();
      context.status(HttpStatus.NOT_FOUND);
      return "No se encuentra la página indicada...";
    }
  }
}