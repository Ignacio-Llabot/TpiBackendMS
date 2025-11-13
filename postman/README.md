# 📮 Colecciones y Entornos de Postman - TPI Backend

Este directorio contiene las colecciones y entornos de Postman para facilitar las pruebas de la API del backend.

## 📁 Estructura

```
postman/
├── collections/
│   └── TPI-Backend-API.postman_collection.json
├── environments/
│   └── Local-Development.postman_environment.json
└── README.md
```

## 🚀 Cómo Importar en Postman

### 1. Importar la Colección

1. Abre Postman
2. Haz clic en **"Import"** (botón superior izquierdo)
3. Selecciona el archivo: `collections/TPI-Backend-API.postman_collection.json`
4. Haz clic en **"Import"**

### 2. Importar el Entorno

1. En Postman, haz clic en **"Import"**
2. Selecciona el archivo: `environments/Local-Development.postman_environment.json`
3. Haz clic en **"Import"**
4. En la esquina superior derecha, selecciona el entorno **"Local Development"**

## ⚙️ Configuración del Entorno

Antes de usar las peticiones, debes configurar las siguientes variables en el entorno:

### Variables Requeridas

Haz clic en el ícono de "ojo" 👁️ al lado del selector de entornos y edita estas variables:

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `base_url` | URL del API Gateway | `http://localhost:8080` |
| `keycloak_url` | URL de Keycloak | `http://localhost:9090` |
| `keycloak_realm` | Realm de Keycloak | `tpi-realm` |
| `client_id` | ID del cliente OAuth2 | `tu-client-id` |
| `client_secret` | Secret del cliente OAuth2 | `tu-client-secret` |
| `username` | Usuario para login | `tu-usuario` |
| `password` | Contraseña del usuario | `tu-password` |

### Variables Automáticas (no editar)

Estas variables se llenan automáticamente al obtener el token:

- `access_token`: Token de acceso JWT
- `refresh_token`: Token para refrescar
- `token_expiry`: Fecha de expiración del token

## 📝 Uso de la Colección

### 1. Autenticación

**Primero debes obtener un token:**

1. Ve a la carpeta **"Authentication"**
2. Ejecuta la petición **"Obtener Token (Password Grant)"**
3. El token se guardará automáticamente en `{{access_token}}`
4. Todas las demás peticiones usarán este token automáticamente

### 2. Refrescar Token

Cuando el token expire:

1. Ejecuta la petición **"Refrescar Token"**
2. Se generará un nuevo `access_token` automáticamente

### 3. Usar los Endpoints

Todas las peticiones están organizadas en carpetas:

#### 📦 Contenedores - Solicitudes
- **POST** Crear Solicitud
- **PUT** Modificar Solicitud

#### 👥 Contenedores - Clientes
- **GET** Obtener Cliente por DNI
- **POST** Crear Cliente

#### 🚛 Transportes - Camiones
- **GET** Obtener Camión por Patente
- **POST** Crear Camión
- **PUT** Actualizar Camión
- **DELETE** Eliminar Camión

#### 🗺️ Transportes - Rutas
- **POST** Crear Ruta (Estrategia Urgente)
- **POST** Crear Ruta (Estrategia Menor Costo)
- **POST** Crear Ruta (Estrategia Óptima)

## 🔐 Seguridad

La colección está configurada con **Bearer Token Authentication** a nivel de colección:

- Todas las peticiones (excepto las de autenticación) usan `{{access_token}}`
- Los tokens se manejan automáticamente
- Los secretos están marcados como tipo `secret` en el entorno

## 💡 Tips y Trucos

### Ver el Token Actual
En la consola de Postman (View → Show Postman Console), verás mensajes como:
```
✅ Token guardado exitosamente
Token expira en: 3600 segundos
```

### Scripts Pre-request
Si necesitas verificar si el token expiró antes de cada petición, puedes agregar este script a la colección:

```javascript
const tokenExpiry = pm.environment.get('token_expiry');
if (tokenExpiry && new Date(tokenExpiry) < new Date()) {
    console.warn('⚠️ El token ha expirado. Refresca el token.');
}
```

### Variables Dinámicas en los Body
Puedes usar variables de Postman en los cuerpos de las peticiones:

```json
{
  "dni": "{{$randomInt}}",
  "email": "{{$randomEmail}}"
}
```

## 🔄 Actualizar Valores de Ejemplo

Los valores de ejemplo en las peticiones son plantillas. Ajústalos según tus datos:

- IDs de clientes, contenedores, transportistas
- Patentes de camiones
- Coordenadas GPS (latitud/longitud)
- Fechas y horarios

## 📊 Ejemplos de Coordenadas (Argentina)

Para las rutas, puedes usar estas coordenadas de ejemplo:

| Ciudad | Latitud | Longitud |
|--------|---------|----------|
| Buenos Aires (CABA) | -34.6037 | -58.3816 |
| La Plata | -34.9214 | -57.9544 |
| Rosario | -32.9442 | -60.6505 |
| Córdoba | -31.4201 | -64.1888 |

## 🐛 Troubleshooting

### Error 401 Unauthorized
- Verifica que hayas ejecutado "Obtener Token"
- Revisa que el token no haya expirado
- Confirma que `client_id` y `client_secret` sean correctos

### Error 404 Not Found
- Verifica que el `base_url` sea correcto
- Asegúrate de que los servicios estén corriendo
- Revisa que el path del endpoint sea correcto

### Error de Conexión
- Verifica que Docker Compose esté corriendo
- Confirma que los puertos 8080 y 9090 estén disponibles
- Revisa los logs de los contenedores

## 📚 Recursos Adicionales

- [Documentación de Postman](https://learning.postman.com/docs/)
- [Keycloak OAuth2 Flows](https://www.keycloak.org/docs/latest/securing_apps/#_oidc)
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)

## 🤝 Contribuir

Para agregar nuevos endpoints a la colección:

1. Crea la petición en Postman
2. Exporta la colección actualizada
3. Reemplaza el archivo JSON en este directorio
4. Actualiza este README si es necesario
