# GroupsApp — Frontend

> ST0263 · SI3007 · Sistemas Distribuidos 2026-1

Frontend de GroupsApp construido con **Next.js 16**, **TypeScript** y **Tailwind CSS**. Interfaz de mensajería en tiempo real conectada al backend Spring Boot mediante REST y WebSocket STOMP.

---

## 🛠 Tecnologías

| Tecnología | Versión | Uso |
|---|---|---|
| Next.js | 16.2 | Framework React con App Router |
| React | 19 | UI components |
| TypeScript | 5.7 | Tipado estático |
| Tailwind CSS | 4.x | Estilos utilitarios |
| @stomp/stompjs | 7.x | Cliente WebSocket STOMP |
| SockJS | 1.x | Fallback WebSocket (requerido por Spring) |
| Radix UI | — | Componentes accesibles (shadcn/ui) |
| Lucide React | — | Iconos |
| Zod | 3.x | Validación de formularios |

---

## 📁 Estructura del proyecto

```
app/
├── layout.tsx              ← Layout global + StompProvider (conexión WS)
├── page.tsx                ← Página principal: grupos, canales, chat
├── login/page.tsx          ← Login y registro de usuarios
└── messages/page.tsx       ← Mensajes directos entre usuarios

components/
├── groups-sidebar.tsx      ← Sidebar de grupos + modal crear grupo
├── channels-sidebar.tsx    ← Canales del grupo seleccionado
├── chat-area.tsx           ← Área de chat de canal (historial + envío)
├── members-sidebar.tsx     ← Lista de miembros del grupo
├── direct-message-chat.tsx ← Chat directo (legacy, reemplazado en messages/page)
├── stomp-provider.tsx      ← Mantiene conexión STOMP global en toda la app
└── ui/                     ← Componentes shadcn/ui (button, input, etc.)

hooks/
├── use-current-user.ts     ← Lee usuario de localStorage (sin hydration mismatch)
├── use-mobile.ts           ← Detecta pantallas móviles
└── use-toast.ts            ← Notificaciones toast

lib/
├── api.ts                  ← Cliente HTTP base con JWT interceptor
├── utils.ts                ← Utilidades (cn, etc.)
└── services/
    ├── auth.service.ts      ← Login, register, logout
    ├── groups.service.ts    ← Grupos, miembros, búsqueda de usuarios
    ├── channels.service.ts  ← Canales
    ├── messages.service.ts  ← Historial de mensajes
    └── websocket.service.ts ← Conexión STOMP, suscripciones a canales y DMs
```

---

## 🚀 Levantar el frontend

### Requisitos
- Node.js 18+
- Backend corriendo en `localhost:8080`

### Instalación y arranque
```bash
npm install
npm run dev
```
La app queda disponible en `http://localhost:3000`.

---

## 🔐 Autenticación

- Al hacer login, el token JWT se guarda en `localStorage` con la clave `token`
- El usuario se guarda en `localStorage` con la clave `user`
- Cada request al backend incluye `Authorization: Bearer <token>`
- Si el servidor responde 401, el token se elimina y se redirige a `/login`

---

## 🌐 Variables de entorno (`.env.local`)

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_WS_URL=ws://localhost:8080
```

---

## 📡 WebSocket en tiempo real

La conexión STOMP se inicia globalmente en `StompProvider` (montado en `layout.tsx`) para que los mensajes lleguen sin importar en qué página esté el usuario.

- **Canales de grupo:** suscripción a `/topic/channel/{channelId}`
- **Mensajes directos:** suscripción global a `/user/queue/messages`
- Los mensajes entrantes se emiten como eventos del navegador (`dm:received`) para evitar doble suscripción

---

## 🧩 Funcionalidades implementadas

- ✅ Registro e inicio de sesión con JWT
- ✅ Ver y seleccionar grupos propios
- ✅ Crear grupos (público o privado)
- ✅ Ver canales de un grupo
- ✅ Chat de canal con historial persistido
- ✅ Mensajes en tiempo real via STOMP
- ✅ Mensajes directos entre usuarios
- ✅ Buscar usuarios por nombre o email
- ✅ Ver miembros de un grupo
- ✅ Cerrar sesión
- ✅ Usuario actual siempre visible en sidebar
