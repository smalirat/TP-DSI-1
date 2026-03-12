# Navbars Reutilizables - MetaMapa

## Descripción
Se han creado dos componentes navbar reutilizables para mantener la sesión del usuario y la navegación coherente en toda la aplicación. Los navbars utilizan detección automática del tab activo mediante JavaScript para evitar la necesidad de pasar parámetros manualmente.

## Componentes Creados

### 1. `navbar_admin.hbs`
- **Uso**: Para administradores en el panel de administración
- **Título**: "Administración - MetaMapa"
- **Navegación**: Solicitudes, Colecciones, Fuentes, Hechos, Estadísticas
- **Características**:
  - Muestra información de sesión del usuario (nombre y rol)
  - **Detección automática del tab activo** basada en la URL actual
  - Botón de cerrar sesión
  - Botón de login si no hay sesión activa
  - Estilo visual destacado para el tab activo (negrita y color azul)

### 2. `navbar_user.hbs`
- **Uso**: Para usuarios normales en la aplicación principal
- **Título**: "MetaMapa"
- **Navegación**: Inicio, Mapa Interactivo, Colecciones, Hechos, Estadísticas
- **Características**:
  - Muestra información de sesión del usuario (nombre y rol)
  - **Detección automática del tab activo** basada en la URL actual
  - Botón "Panel Admin" para administradores
  - Botones de login y registro si no hay sesión
  - Botón de cerrar sesión para usuarios logueados
  - Estilo visual destacado para el tab activo (negrita y color azul)

## Cómo Usar

### Incluir en templates (NUEVA SINTAXIS SIMPLIFICADA):
```handlebars
<!-- Para administradores -->
{{> navbar_admin}}

<!-- Para usuarios normales -->
{{> navbar_user}}
```

**¡IMPORTANTE!** Ya no necesitas pasar el parámetro `activeTab`. El JavaScript interno se encarga automáticamente de detectar qué tab debe estar activo según la URL actual.

### Valores de `activeTab`:
**Para navbar_admin:**
- `home` - Panel de administración
- `solicitudes` - Solicitudes de eliminación
- `colecciones` - Gestión de colecciones
- `fuentes` - Gestión de fuentes
- `hechos` - Gestión de hechos
- `estadisticas` - Reportes estadísticos

**Para navbar_user:**
- `home` - Página principal
- `mapa` - Mapa interactivo
- `colecciones` - Vista de colecciones
- `hechos` - Vista de hechos
- `estadisticas` - Vista de estadísticas

## Variables de Sesión Requeridas
Los navbars esperan las siguientes variables del controlador:
- `usuarioUsername` - Nombre de usuario logueado
- `usuarioRol` - Rol del usuario (ADMINISTRADOR, USUARIO, etc.)

## Templates Actualizados
### Admin:
- admin_home.hbs
- admin_hechos.hbs  
- admin_estadisticas.hbs
- colecciones_admin.hbs
- solicitudes.hbs
- coleccion_nueva.hbs

### Usuario:
- home.hbs
- colecciones.hbs
- hechos.hbs
- estadisticas.hbs
- mapa.hbs
- hecho_nuevo.hbs

## Detección Automática de Tab Activo

### Cómo funciona:
Cada navbar incluye un script JavaScript que:

1. **Obtiene la URL actual** del navegador (`window.location.pathname`)
2. **Mapea URLs a tabs** usando un objeto de configuración
3. **Encuentra coincidencias** entre la URL actual y los paths configurados
4. **Aplica la clase CSS `active`** al enlace correspondiente

### Mapeo de URLs:

**Para navbar_admin:**
```javascript
const pathToTab = {
    '/admin/solicitudes': 'solicitudes',
    '/admin/colecciones': 'colecciones', 
    '/fuentes': 'fuentes',
    '/admin/hechos': 'hechos',
    '/admin/estadisticas': 'estadisticas'
};
```

**Para navbar_user:**
```javascript
const pathToTab = {
    '/': 'home',
    '/mapa': 'mapa',
    '/colecciones': 'colecciones', 
    '/hechos': 'hechos',
    '/estadisticas': 'estadisticas'
};
```

### Ventajas de la Detección Automática:
- ✅ **Cero configuración**: No necesitas pasar parámetros `activeTab`
- ✅ **A prueba de errores**: Imposible olvidar marcar el tab correcto
- ✅ **Mantenimiento reducido**: Cambios automáticos al añadir nuevas rutas
- ✅ **Funciona con URLs complejas**: Detecta `/admin/colecciones/123/editar`

## Estilos CSS Personalizados

Los navbars incluyen estilos CSS específicos en `base.css` para resaltar el tab activo:

```css
.navbar .nav-link.active {
    color: #007bff !important;
    font-weight: bold !important;
    background-color: rgba(0, 123, 255, 0.1) !important;
    border-radius: 6px;
    text-decoration: none;
}

.navbar .nav-link.active:hover {
    color: #0056b3 !important;
    background-color: rgba(0, 123, 255, 0.15) !important;
}
```

## Beneficios
1. **Consistencia**: Navegación uniforme en toda la aplicación
2. **Mantenibilidad**: Un solo punto de edición para cambios en navegación
3. **Automatización**: Detección automática sin intervención manual
4. **Flexibilidad**: Fácil extensión para nuevas rutas y módulos
3. **Sesión persistente**: La información del usuario se mantiene en todas las páginas
4. **Indicador visual**: Tab activo claramente marcado con negrita y color azul
5. **Responsive**: Bootstrap navbar responsive incluido
6. **Estilos CSS**: Utilizan clases CSS en lugar de estilos inline para mejor mantenibilidad