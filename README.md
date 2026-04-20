# GroupsApp — Sistema de Mensajería en Tiempo Real

> ST0263 · SI3007 · Sistemas Distribuidos 2026-1  
> Arquitectura monolítica por capas con comunicación REST y WebSocket STOMP

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

## 📚 Documentación detallada

- [README Backend](./Backend/HELP.md) — Endpoints, seguridad, estructura
- [README Frontend](./Fronted/README.md) — Componentes, servicios, WebSocket
