# GroupsApp — Backend

> ST0263 · SI3007 · Sistemas Distribuidos 2026-1

Backend monolítico de GroupsApp construido con **Spring Boot 3**, **PostgreSQL** y **WebSocket STOMP**. Expone una API REST con autenticación JWT y comunicación en tiempo real mediante STOMP sobre SockJS.

---

## 🛠 Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.x | Framework web |
| Spring Security | 6.x | Autenticación y autorización |
| Spring WebSocket | 6.x | Mensajería en tiempo real |
| PostgreSQL | 15 | Base de datos relacional |
| Hibernate / JPA | 6.x | ORM |
| jjwt | 0.11.5 | Generación y validación de JWT |
| Docker Compose | — | Entorno local de BD |
| Maven | 3.9 | Gestión de dependencias |

---

## 📁 Estructura del proyecto

```
src/main/java/com/groupsapp/monolito/
│
├── config/
│   ├── SecurityConfig.java          ← JWT, CORS, rutas públicas/protegidas
│   ├── WebSocketConfig.java         ← STOMP endpoints y broker
│   └── GlobalExceptionHandler.java  ← Manejo global de errores → JSON
│
├── controller/
│   ├── AuthController.java          ← POST /api/auth/register|login|logout
│   ├── GroupController.java         ← CRUD grupos, miembros, búsqueda usuarios
│   ├── ChannelController.java       ← CRUD canales dentro de grupos
│   ├── MessageController.java       ← Mensajes de canal y directos + STOMP
│   └── FileController.java          ← Upload/download de archivos
│
├── service/
│   ├── AuthService.java             ← Registro, login, logout
│   ├── GroupService.java            ← Grupos, miembros, búsqueda
│   ├── ChannelService.java          ← Canales
│   ├── MessageService.java          ← Historial, envío, estados de lectura
│   └── FileService.java             ← Gestión de archivos
│
├── model/
│   ├── User.java                    ← Entidad usuario (email, username, status)
│   ├── Group.java                   ← Entidad grupo (PUBLIC/PRIVATE)
│   ├── GroupMember.java             ← Relación usuario-grupo con rol
│   ├── Channel.java                 ← Canal dentro de un grupo
│   ├── Message.java                 ← Mensaje (canal o directo)
│   ├── MessageStatus.java           ← Estado de lectura por usuario
│   └── FileMetadata.java            ← Metadatos de archivos subidos
│
├── dto/
│   ├── auth/                        ← LoginRequest, RegisterRequest, AuthResponse
│   ├── group/                       ← GroupDTO, MemberDTO, UserSearchDTO, CreateGroupRequest
│   └── message/                     ← MessageDTO, SendMessageRequest
│
├── security/
│   ├── JwtUtil.java                 ← Generación y validación de tokens
│   ├── JwtFilter.java               ← Filtro que extrae JWT de cada request
│   └── UserDetailsServiceImpl.java  ← Carga usuario por email para Spring Security
│
├── repository/                      ← Interfaces JPA con queries personalizadas
│
└── MonolitoApplication.java         ← Punto de entrada
```

---

## 🔌 API REST

### Autenticación (`/api/auth`)
| Método | Ruta | Body | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | `{username, email, password}` | Registrar usuario |
| POST | `/api/auth/login` | `{email, password}` | Login → JWT |
| POST | `/api/auth/logout` | — | Cerrar sesión |

### Grupos (`/api/groups`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/groups` | Mis grupos |
| POST | `/api/groups` | Crear grupo |
| GET | `/api/groups/{id}` | Detalle de grupo |
| GET | `/api/groups/search?name=` | Buscar grupos públicos |
| POST | `/api/groups/{id}/join` | Unirse a grupo público |
| POST | `/api/groups/{id}/leave` | Salir de grupo |
| GET | `/api/groups/{id}/members` | Listar miembros |
| POST | `/api/groups/{id}/members` | Añadir miembro (admin/owner) |
| GET | `/api/groups/search/users?q=` | Buscar usuarios por nombre o email |

### Canales (`/api/groups/{groupId}/channels`)
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/groups/{id}/channels` | Canales del grupo |
| POST | `/api/groups/{id}/channels` | Crear canal |
| DELETE | `/api/groups/{id}/channels/{channelId}` | Eliminar canal |

### Mensajes (`/api/messages`)
| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/messages` | Enviar mensaje (canal o directo) |
| GET | `/api/messages/channel/{channelId}` | Historial de canal |
| GET | `/api/messages/direct/{userId}` | Historial de chat directo |
| GET | `/api/messages/channel/{channelId}/unread` | Mensajes no leídos |

### WebSocket STOMP
- **Endpoint de conexión:** `ws://localhost:8080/ws` (con SockJS fallback)
- **Suscripción canales de grupo:** `/topic/channel/{channelId}`
- **Suscripción mensajes directos:** `/user/queue/messages`
- **Enviar por STOMP:** `/app/chat.send`

---

## 🚀 Levantar el entorno

### 1. Base de datos con Docker
```bash
docker-compose up -d
```
- PostgreSQL: `localhost:5432` · DB: `groupsapp` · User: `postgres` · Pass: `groupsapp1234`
- pgAdmin: `http://localhost:5050` · Email: `admin@groupsapp.com` · Pass: `admin123`

### 2. Correr la aplicación
```bash
./mvnw spring-boot:run
```
La API queda disponible en `http://localhost:8080`.

---

## 🔐 Seguridad

- Autenticación con **JWT** firmado con HMAC-SHA384
- El token se envía en el header `Authorization: Bearer <token>`
- Expiración: 24 horas (`app.jwt.expiration=86400000`)
- Rutas públicas: `/api/auth/**` y `/ws/**`
- CORS configurado para `localhost:3000`

---

## ⚙️ Variables de configuración (`application.properties`)

```properties
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/groupsapp
spring.datasource.username=postgres
spring.datasource.password=groupsapp1234
app.jwt.secret=groupsAppSecretKey2026SuperLongAndSecureKeyForJWT
app.jwt.expiration=86400000
app.upload.dir=uploads/
```
