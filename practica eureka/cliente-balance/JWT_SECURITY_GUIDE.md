# Implementación de Seguridad JWT en el servicio cliente-balance

Este documento explica cómo se implementó el patrón arquitectónico "Access Token" con JWT para proteger el endpoint `/get-message`.

## Arquitectura implementada

### Componentes principales:

1. **JwtUtil**: Clase utilitaria para generar, validar y extraer información de tokens JWT
2. **JwtAuthenticationFilter**: Filtro que intercepta las requests para validar tokens JWT
3. **SecurityConfig**: Configuración de Spring Security
4. **AuthController**: Controlador para autenticación (generar tokens)
5. **MessageController**: Controlador protegido que requiere autenticación JWT

### Flujo de autenticación:

1. El cliente hace login en `/auth/login` con credenciales
2. El servidor valida las credenciales y genera un token JWT
3. El cliente incluye el token en el header `Authorization: Bearer <token>` para acceder a endpoints protegidos
4. El filtro JWT valida el token en cada request a endpoints protegidos
5. Si el token es válido, se permite el acceso; si no, se retorna 401 Unauthorized

## Configuración

### Credenciales por defecto:
- **Usuario**: `admin`
- **Contraseña**: `password`

### Endpoints disponibles:

#### 1. Autenticación (POST /auth/login)
```json
{
  "username": "admin",
  "password": "password"
}
```

**Respuesta exitosa:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer"
}
```

#### 2. Validar token (GET /auth/validate)
Headers: `Authorization: Bearer <token>`

#### 3. Obtener mensaje (GET /get-message) - PROTEGIDO
Headers: `Authorization: Bearer <token>`

## Ejemplos de uso con curl

### 1. Obtener token de autenticación:
```bash
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### 2. Usar el token para acceder al endpoint protegido:
```bash
curl -X GET http://localhost:8084/get-message \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Validar un token:
```bash
curl -X GET http://localhost:8084/auth/validate \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## Ejemplos de uso con Postman

### 1. Login:
- **Método**: POST
- **URL**: `http://localhost:8084/auth/login`
- **Headers**: `Content-Type: application/json`
- **Body** (raw JSON):
```json
{
  "username": "admin",
  "password": "password"
}
```

### 2. Acceder al endpoint protegido:
- **Método**: GET
- **URL**: `http://localhost:8084/get-message`
- **Headers**: `Authorization: Bearer <copiar_token_del_login>`

## Configuración de seguridad

### Endpoints públicos:
- `/auth/login` - No requiere autenticación

### Endpoints protegidos:
- `/get-message` - Requiere token JWT válido

### Configuración JWT:
- **Algoritmo**: HS256
- **Tiempo de expiración**: 24 horas (86400000 ms)
- **Secret key**: Configurado en `application.properties`

## Cómo probar la implementación

### Paso 1: Iniciar los servicios
1. Iniciar `eureka-server` en puerto 8761
2. Iniciar `service-instance` en el puerto configurado
3. Iniciar `cliente-balance` en puerto 8084

### Paso 2: Probar sin autenticación (debe fallar)
```bash
curl -X GET http://localhost:8084/get-message
```
**Resultado esperado**: 401 Unauthorized

### Paso 3: Obtener token
```bash
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

### Paso 4: Usar token para acceder al servicio
```bash
curl -X GET http://localhost:8084/get-message \
  -H "Authorization: Bearer [TOKEN_OBTENIDO]"
```
**Resultado esperado**: Mensaje del servicio a través del load balancer

## Notas de seguridad

- En producción, cambiar la secret key por una más segura
- Implementar rotación de tokens
- Usar HTTPS en producción
- Implementar manejo de refresh tokens para sesiones largas
- Las credenciales hardcodeadas son solo para demo; usar base de datos en producción

## Archivos modificados/creados

1. `pom.xml` - Agregadas dependencias de Spring Security y JWT
2. `SecurityConfig.java` - Configuración de Spring Security
3. `JwtUtil.java` - Utilidades para manejo de JWT
4. `JwtAuthenticationFilter.java` - Filtro de autenticación
5. `AuthController.java` - Controlador de autenticación
6. `application.properties` - Configuración JWT
7. `MessageController.java` - Endpoint protegido (ya existía)

La implementación garantiza que el endpoint `/get-message` solo sea accesible con un token JWT válido, cumpliendo con el patrón arquitectónico "Access Token" solicitado.