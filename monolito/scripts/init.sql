-- ════════════════════════════════════════════════════
--  GroupsApp - Script de inicialización PostgreSQL
--  Se ejecuta automáticamente al crear el contenedor
-- ════════════════════════════════════════════════════

-- Crear la base de datos (por si acaso)
-- (Docker ya la crea con POSTGRES_DB, esto es un respaldo)

-- Extensiones útiles
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";  -- Para generar UUIDs
CREATE EXTENSION IF NOT EXISTS "pg_trgm";    -- Para búsqueda de texto

-- Nota: Hibernate creará las tablas automáticamente
-- con spring.jpa.hibernate.ddl-auto=update
-- Este script es solo para extensiones y datos iniciales