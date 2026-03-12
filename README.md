# GroupsApp — Sistema de Mensajería Distribuida
>SI3007 Sistemas Distribuidos, 2026-1

## 📁 Estructura del Proyecto

```
S60_Distributed_Systems/
│
├── src/
│   ├── main/
│   │   ├── java/com/groupsapp/monolito/
│   │   │   │
│   │   │   ├── config/                        ← Configuraciones Spring
│   │   │   │   ├── SecurityConfig.java        ← JWT, CORS, permisos
│   │   │   │   └── WebSocketConfig.java       ← STOMP sobre WebSocket
│   │   │   │
│   │   │   ├── controller/                    ← Endpoints REST
│   │   │   │   ├── AuthController.java        ← /api/auth/*
│   │   │   │   ├── UserController.java        ← /api/users/*
│   │   │   │   ├── GroupController.java       ← /api/groups/*
│   │   │   │   ├── ChannelController.java     ← /api/groups/{id}/channels
│   │   │   │   ├── MessageController.java     ← /api/messages/*
│   │   │   │   └── FileController.java        ← /api/files/*
│   │   │   │
│   │   │   ├── service/                       ← Lógica de negocio
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   ├── GroupService.java
│   │   │   │   ├── ChannelService.java
│   │   │   │   ├── MessageService.java
│   │   │   │   └── FileService.java
│   │   │   │
│   │   │   ├── repository/                    ← Acceso a BD (Spring JPA)
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── GroupRepository.java
│   │   │   │   ├── ChannelRepository.java
│   │   │   │   ├── MessageRepository.java
│   │   │   │   └── FileRepository.java
│   │   │   │
│   │   │   ├── model/                         ← Entidades JPA (tablas)
│   │   │   │   ├── User.java
│   │   │   │   ├── Group.java
│   │   │   │   ├── Channel.java
│   │   │   │   ├── Message.java
│   │   │   │   ├── GroupMember.java
│   │   │   │   ├── MessageStatus.java
│   │   │   │   └── FileMetadata.java
│   │   │   │
│   │   │   ├── dto/                           ← Objetos de transferencia
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginRequest.java
│   │   │   │   │   ├── RegisterRequest.java
│   │   │   │   │   └── AuthResponse.java
│   │   │   │   ├── message/
│   │   │   │   │   ├── MessageDTO.java
│   │   │   │   │   └── SendMessageRequest.java
│   │   │   │   └── group/
│   │   │   │       ├── GroupDTO.java
│   │   │   │       └── CreateGroupRequest.java
│   │   │   │
│   │   │   ├── security/                      ← JWT y filtros
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtFilter.java
│   │   │   │   └── UserDetailsServiceImpl.java
│   │   │   │
│   │   │   └── MonolitoApplication.java       ← Punto de entrada
│   │   │
│   │   └── resources/
│   │       └── application.properties        ← Config BD, JWT, etc.
│   │
│   └── test/
│       └── java/com/groupsapp/monolito/
│           └── MonolitoApplicationTests.java
│
├── scripts/
│   └── init.sql                               ← Script inicial PostgreSQL
│
├── docs/                                      ← Documentación y diagramas
│   └── arquitectura.png
│
├── uploads/                                   ← Archivos subidos (en .gitignore)
│
├── .github/
│   └── workflows/                             ← CI/CD (futuro)
│
├── docker-compose.yml                         ← PostgreSQL + pgAdmin local
├── pom.xml                                    ← Dependencias Maven
├── .gitignore
└── README.md
```

## 🚀 Levantar el entorno de desarrollo

### 1. Iniciar la base de datos
```bash
docker-compose up -d
```
- PostgreSQL disponible en `localhost:5432`
- pgAdmin disponible en `http://localhost:5050`
  - Email: `admin@groupsapp.com`
  - Password: `admin123`

### 2. Configurar credenciales
Editar `src/main/resources/application.properties`:
```properties
spring.datasource.password=tu_password
```

### 3. Correr la aplicación
```bash
./mvnw spring-boot:run
```

## 🔌 Endpoints principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/login` | Login → JWT |
| GET | `/api/groups` | Mis grupos |
| POST | `/api/groups` | Crear grupo |
| GET | `/api/groups/{id}/channels` | Canales de un grupo |
| GET | `/api/messages/channel/{id}` | Historial de canal |
| GET | `/api/messages/direct/{userId}` | Chat privado |
| POST | `/api/files/upload` | Subir archivo |


## 📚 Tecnologías
- Java 17 + Spring Boot 3.2
- PostgreSQL 15
- WebSocket (STOMP)
- JWT (jjwt 0.11.5)
- Docker + Docker Compose