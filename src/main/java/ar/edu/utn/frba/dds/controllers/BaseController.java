package ar.edu.utn.frba.dds.controllers;

import io.javalin.http.Context;
import java.util.Map;

public class BaseController {

    /**
     * Agrega las variables de sesión del usuario al modelo para que estén disponibles en todos los templates
     */
    protected void addSessionDataToModel(Context ctx, Map<String, Object> model) {
        model.put("usuarioUsername", ctx.sessionAttribute("usuarioUsername"));
        model.put("usuarioRol", ctx.sessionAttribute("usuarioRol"));
    }
    
    /**
     * Renderiza el template agregando automáticamente las variables de sesión
     */
    protected void renderWithSession(Context ctx, String template, Map<String, Object> model) {
        addSessionDataToModel(ctx, model);
        ctx.render(template, model);
    }
}