# GroupsApp — Sistema de Mensajería en Tiempo Real

> ST0263 · SI3007 · Sistemas Distribuidos 2026-1  
> Arquitectura monolítica por capas con comunicación REST, gRPC y SQS

---

## 📌 Descripción

GroupsApp es una aplicación de mensajería instantánea que permite a los usuarios crear grupos, comunicarse en canales temáticos y enviarse mensajes directos, todo en tiempo real. El sistema está dividido en dos módulos independientes: un backend en Spring Boot y un frontend en Next.js.

---

## 📁 Estructura del repositorio

```
S63_Groups_App/
│
├── Backend/                          ← API REST + WebSocket (Spring Boot)
│   ├── src/
│   ├── docker-compose.yml            ← PostgreSQL + pgAdmin
│   ├── pom.xml
│   └── HELP.md                       ← README del backend
│
├── Fronted/                          ← Interfaz de usuario (Next.js)
│   ├── app/
│   ├── components/
│   ├── lib/
│   ├── hooks/
│   ├── .env.local                    ← Variables de entorno
│   └── README.md                     ← README del frontend
│
├── README.md                         ← Este archivo
└── ST0263-SI3007-261-Proyecto1_GroupsApp.docx
```

---

## 🏗 Arquitectura

```
┌─────────────────────┐         HTTP/REST          ┌─────────────────────┐
│                     │ ─────────────────────────► │                     │
│   Frontend          │                            │   Backend           │
│   Next.js 16        │ ◄───────────────────────── │   Spring Boot 3     │
│   React 19          │         JSON               │   PostgreSQL 15     │
│   TypeScript        │                            │   JWT + STOMP       │
│                     │ ◄═══════════════════════► │                     │
│   localhost:3000    │       WebSocket STOMP      │   localhost:8080    │
└─────────────────────┘                            └─────────────────────┘
```

**Flujo de autenticación:**
1. El usuario hace login → el backend genera un JWT
2. El frontend guarda el JWT en `localStorage`
3. Cada request REST incluye `Authorization: Bearer <token>`
4. La conexión WebSocket STOMP también autentica con el JWT

**Flujo de mensajería en tiempo real:**
1. El frontend se conecta a STOMP via SockJS (`/ws`)
2. Se suscribe a `/topic/channel/{id}` para mensajes de canal
3. Se suscribe a `/user/queue/messages` para mensajes directos
4. Al enviar un mensaje, el backend lo persiste en PostgreSQL y lo broadcast por STOMP

---

## 🚀 Levantar el proyecto completo

### Paso 1: Base de datos
```bash
cd Backend
docker-compose up -d
```

### Paso 2: Backend
```bash
cd Backend
./mvnw spring-boot:run
```
API disponible en `http://localhost:8080`

### Paso 3: Frontend
```bash
cd Fronted
npm install
npm run dev
```
App disponible en `http://localhost:3000`

---

## 👥 Credenciales de prueba (pgAdmin)

| Campo | Valor |
|---|---|
| URL | http://localhost:5050 |
| Email | admin@groupsapp.com |
| Password | admin123 |
| Host BD | postgres |
| BD | groupsapp |
| User BD | postgres |
| Pass BD | groupsapp1234 |

---

## ✅ Funcionalidades del sistema

### Usuarios
- Registro con username, email y contraseña
- Login con JWT (expiración 24h)
- Logout con invalidación de sesión

### Grupos
- Crear grupos públicos o privados
- Buscar grupos públicos por nombre
- Unirse o salir de grupos
- Añadir miembros (solo admins/owners)
- Ver lista de miembros con roles

### Canales
- Cada grupo tiene un canal `general` creado automáticamente
- Crear y eliminar canales dentro de un grupo

### Mensajería
- Chat de canal con historial persistido en BD
- Mensajes directos entre usuarios
- Tiempo real via WebSocket STOMP para ambos tipos
- Estado de lectura de mensajes

### Búsqueda
- Buscar usuarios por nombre o email (para iniciar DMs o añadir a grupos)
- Buscar grupos públicos por nombre

---

> La información anterior de este README describe principalmente el monolito local del proyecto (Backend + Frontend).
> La sección siguiente añade la arquitectura y el despliegue planificado para el despliegue en AWS.

## ☁️ Despliegue y arquitectura distribuida

### Arquitectura de despliegue

```
Cliente (frontend) ─── REST ───> Nginx ─── REST ───> Monolito Spring Boot
                                                          │
                                                          ├── gRPC ──> Presence Service (Spring Boot + H2)
                                                          │
                                                          ├── SQL ──> RDS PostgreSQL
                                                          │
                                                          ├── SDK S3 ──> Bucket S3
                                                          │
                                                          └── SQS SDK ──> SQS Queue ──> Notifications Consumer (Python)

Todo en 1 EC2 t3.small excepto RDS y S3 (servicios gestionados).
Prometheus + Grafana en misma EC2 para observabilidad.
```

- Despliegue en una sola EC2 `t3.small` con Nginx, el monolito, el servicio de presencia, y observabilidad.
- Base de datos administrada: RDS PostgreSQL `db.t3.micro` single-AZ.
- Almacenamiento de archivos en S3 y eventos de notificación en SQS.
- `presence-service/` para presencia de usuarios vía gRPC.
- `notifications-consumer/` para procesar mensajes de la cola SQS.
- `deploy/docker-compose.local.yml` para levantar el monolito y Postgres en local.
- `monolito/` contiene el backend principal en Spring Boot.
- **Link del swagger:** http://100.50.74.221/swagger-ui/index.html#/
- **Link de Grafana:** http://100.50.74.221:3000/d/spring_boot_21/spring-boot-3-x-statistics?orgId=1&from=now-15m&to=now&timezone=browser&var-application=groupsapp-monolito&var-Namespace=&var-instance=monolito:8080&var-hikaricp=HikariPool-1&var-memory_pool_heap=$__all&var-memory_pool_nonheap=$__all

## Estructura del repositorio

```
S63_Groups_App/
├── deploy/
│   └── docker-compose.local.yml  ← LEVANTA monolito + postgres localmente
├── Fronted/                      
├── monolito/                     ← Spring Boot (el backend principal)
│   ├── Dockerfile
│   ├── pom.xml
│   ├── src/main/java/com/groupsApp/monolito/
│   │   ├── config/  (SecurityConfig, WebSocketConfig, OpenApiConfig, S3Config)
│   │   ├── controller/  (Auth, User, Group, Channel, Message, File)
│   │   ├── dto/
│   │   ├── model/  (User, Group, Channel, Message, GroupMember, MessageStatus, FileMetadata)
│   │   ├── repository/
│   │   ├── security/  (JwtUtil, JwtFilter, UserDetailsServiceImpl)
│   │   ├── service/
│   │   └── storage/  ← NUEVO Día 2 (FileStorage interface, LocalFileStorage, S3FileStorage)
│   └── src/main/resources/
│       ├── application.properties          (común)
│       ├── application-local.properties    (perfil local)
│       └── application-aws.properties      (perfil aws, usa env vars)
    
├── notifications-consumer/       
├── presence-service
    ├──/src/main/
        ├──java/com/groupsapp/presence/
          ├── config/
          ├── grpc/
          ├── model/UserPresence.java
          ├── repository/UserPresenceRepository.java
          ├── service/PresenceBusinessService.java
          └── PresenceServiceApplication.java   
        ├──resources/
          └── application.properties     
    ├──target/generated-sources/protobuf/java/com/groupsapp/presence/grpc/
        ├── PresenceProto.java                 (16290 bytes — metadata)
        ├── PresenceRequest.java               (DTO de entrada)
        ├── PresenceRequestOrBuilder.java      (interfaz builder)
        ├── PresenceResponse.java              (DTO de salida)
        └── PresenceResponseOrBuilder.java     (interfaz builder)
    ├── DockerFile
    ├──mvnw
    ├──mvnw.cmd
    └──pom.xml
├── proto/     
    └── presence.proto  ← contratos .proto
└── README.md
```

## 📚 Documentación detallada

- [Wiki](https://github.com/maortizc2/S63_Groups_App/wiki/Documentaci%C3%B3n)
