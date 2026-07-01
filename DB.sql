-- ==========================================================
-- SCRIPT COMPLETO DE BASE DE DATOS - BOOKPOINT CHILE
-- ==========================================================
-- Este script crea todas las bases de datos, tablas, relaciones,
-- índices y datos iniciales para todos los microservicios,
-- compatible con el entorno XAMPP.
-- ==========================================================

-- Desactivar comprobación de llaves foráneas para facilitar creación
SET FOREIGN_KEY_CHECKS = 0;

-- ==========================================================
-- 1. CREACIÓN DE BASES DE DATOS
-- ==========================================================
CREATE DATABASE IF NOT EXISTS bookpoint_auth_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_inventario_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_pedidos CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_carrito_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_ventas_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_despacho_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_clientes_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_sucursal_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_bodega_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS bookpoint_soporte_service CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ==========================================================
-- 2. BASE DE DATOS: bookpoint_auth_service
-- ==========================================================
USE bookpoint_auth_service;

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario_permisos (
    usuario_id BIGINT NOT NULL,
    permisos VARCHAR(100),
    CONSTRAINT fk_usuario_permisos FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índice para búsqueda por rol
CREATE INDEX idx_usuario_rol ON usuarios (rol);


-- ==========================================================
-- 3. BASE DE DATOS: bookpoint_inventario_service
-- ==========================================================
USE bookpoint_inventario_service;

CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    precio DOUBLE NOT NULL,
    stock_actual INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS catalogo (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255),
    autor VARCHAR(255),
    editorial VARCHAR(255),
    genero VARCHAR(255),
    precio DOUBLE,
    descripcion TEXT,
    producto_id BIGINT,
    CONSTRAINT fk_catalogo_producto FOREIGN KEY (producto_id) REFERENCES productos (id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Índices
CREATE INDEX idx_producto_nombre ON productos (nombre);
CREATE INDEX idx_producto_precio ON productos (precio);
CREATE INDEX idx_catalogo_genero ON catalogo (genero);


-- ==========================================================
-- 4. BASE DE DATOS: bookpoint_clientes_service
-- ==========================================================
USE bookpoint_clientes_service;

CREATE TABLE IF NOT EXISTS perfiles_clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    email VARCHAR(255),
    telefono VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS cliente_direcciones (
    cliente_id BIGINT NOT NULL,
    direcciones VARCHAR(255),
    CONSTRAINT fk_cliente_direcciones FOREIGN KEY (cliente_id) REFERENCES perfiles_clientes (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_cliente_email ON perfiles_clientes (email);


-- ==========================================================
-- 5. BASE DE DATOS: bookpoint_sucursal_service
-- ==========================================================
USE bookpoint_sucursal_service;

CREATE TABLE IF NOT EXISTS sucursales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255),
    direccion VARCHAR(255),
    ciudad VARCHAR(255),
    horario VARCHAR(255),
    personal_encargado VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_sucursal_ciudad ON sucursales (ciudad);


-- ==========================================================
-- 6. BASE DE DATOS: bookpoint_bodega_service
-- ==========================================================
USE bookpoint_bodega_service;

CREATE TABLE IF NOT EXISTS bodega_central (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    stock_central INT,
    stock_minimo INT,
    ubicacion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS transferencias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    sucursal_origen_id BIGINT,
    sucursal_destino_id BIGINT,
    estado VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_transferencia_estado ON transferencias (estado);


-- ==========================================================
-- 7. BASE DE DATOS: bookpoint_pedidos
-- ==========================================================
USE bookpoint_pedidos;

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha_pedido DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT NOT NULL,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    total DOUBLE NOT NULL,
    direccion_envio VARCHAR(255),
    codigo_cupon VARCHAR(100),
    descuento DOUBLE DEFAULT 0.0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS items_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    CONSTRAINT fk_items_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_pedido_cliente ON pedidos (cliente_id);
CREATE INDEX idx_pedido_estado ON pedidos (estado);
CREATE INDEX idx_pedido_fecha ON pedidos (fecha_pedido);


-- ==========================================================
-- 8. BASE DE DATOS: bookpoint_carrito_service
-- ==========================================================
USE bookpoint_carrito_service;

CREATE TABLE IF NOT EXISTS carritos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    session_id VARCHAR(255),
    subtotal DOUBLE DEFAULT 0.0,
    impuestos DOUBLE DEFAULT 0.0,
    total DOUBLE DEFAULT 0.0,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS items_carrito (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(255),
    precio_unitario DOUBLE,
    cantidad INT NOT NULL,
    subtotal DOUBLE,
    CONSTRAINT fk_items_carrito FOREIGN KEY (carrito_id) REFERENCES carritos (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_carrito_usuario ON carritos (usuario_id);
CREATE INDEX idx_carrito_session ON carritos (session_id);


-- ==========================================================
-- 9. BASE DE DATOS: bookpoint_ventas_service
-- ==========================================================
USE bookpoint_ventas_service;

CREATE TABLE IF NOT EXISTS ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    cliente_id BIGINT,
    total DOUBLE NOT NULL,
    descuento DOUBLE DEFAULT 0.0,
    tipo_documento VARCHAR(50),
    estado VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS detalle_ventas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    venta_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    subtotal DOUBLE NOT NULL,
    CONSTRAINT fk_detalle_ventas FOREIGN KEY (venta_id) REFERENCES ventas (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_venta_cliente ON ventas (cliente_id);
CREATE INDEX idx_venta_fecha ON ventas (fecha);


-- ==========================================================
-- 10. BASE DE DATOS: bookpoint_despacho_service
-- ==========================================================
USE bookpoint_despacho_service;

CREATE TABLE IF NOT EXISTS despachos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT,
    direccion_destino VARCHAR(255),
    estado VARCHAR(50),
    transportista VARCHAR(255),
    ruta_optimizada VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_despacho_estado ON despachos (estado);
CREATE INDEX idx_despacho_pedido ON despachos (pedido_id);


-- ==========================================================
-- 11. BASE DE DATOS: bookpoint_soporte_service
-- ==========================================================
USE bookpoint_soporte_service;

CREATE TABLE IF NOT EXISTS soporte_reseñas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    producto_id BIGINT,
    mensaje TEXT,
    calificacion INT,
    estado VARCHAR(50),
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_soporte_tipo ON soporte_reseñas (tipo);
CREATE INDEX idx_soporte_estado ON soporte_reseñas (estado);
CREATE INDEX idx_soporte_cliente ON soporte_reseñas (cliente_id);


-- ==========================================================
-- 12. INSERCIÓN DE DATOS INICIALES
-- ==========================================================
-- Usuarios (auth-service)
USE bookpoint_auth_service;
INSERT INTO usuarios (id, username, password, rol) VALUES
(1, 'admin', 'admin123', 'ADMIN'),
(2, 'jefe_sucursal', 'jefe123', 'JEFE_SUCURSAL'),
(3, 'bodega_user', 'bodega123', 'BODEGA'),
(4, 'ventas_user', 'ventas123', 'ASISTENTE_VENTAS');

INSERT INTO usuario_permisos (usuario_id, permisos) VALUES
(1, 'LEER'), (1, 'ESCRIBIR'), (1, 'BORRAR'), (1, 'GESTION_USUARIOS'),
(2, 'LEER'), (2, 'ESCRIBIR'), (2, 'GESTION_SUCURSAL'),
(3, 'LEER'), (3, 'ESCRIBIR'), (3, 'GESTION_INVENTARIO'),
(4, 'LEER'), (4, 'ESCRIBIR'), (4, 'CREAR_VENTA');

-- Productos e Inventario (inventario-service)
USE bookpoint_inventario_service;
INSERT INTO productos (id, nombre, descripcion, precio, stock_actual) VALUES
(1, 'Cien años de soledad', 'Obra maestra de Gabriel García Márquez', 25000.0, 50),
(2, '1984', 'Distopía clásica de George Orwell', 18000.0, 30),
(3, 'El Principito', 'Libro infantil ilustrado', 12000.0, 100),
(4, 'Rayuela', 'Novela experimental de Julio Cortázar', 22000.0, 15),
(5, 'Don Quijote de la Mancha', 'Clásico de la literatura española', 35000.0, 20);

INSERT INTO catalogo (id, titulo, autor, editorial, genero, precio, descripcion, producto_id) VALUES
(1, 'Cien años de soledad', 'Gabriel García Márquez', 'Sudamericana', 'Realismo Mágico', 25000.0, 'Historia de la familia Buendía', 1),
(2, '1984', 'George Orwell', 'Seix Barral', 'Ciencia Ficción', 18000.0, 'Sociedad totalitaria', 2),
(3, 'El Principito', 'Antoine de Saint-Exupéry', 'Salamandra', 'Infantil', 12000.0, 'Fábula sobre la vida', 3),
(4, 'Rayuela', 'Julio Cortázar', 'Alfaguara', 'Novela', 22000.0, 'Contranovela existencialista', 4);

-- Clientes y Direcciones (clientes-service)
USE bookpoint_clientes_service;
INSERT INTO perfiles_clientes (id, nombre, email, telefono) VALUES
(1, 'Juan Pérez', 'juan.perez@email.com', '+56912345678'),
(2, 'María González', 'maria.g@email.com', '+56987654321'),
(3, 'Carlos Soto', 'csoto@email.com', '+56955566677');

INSERT INTO cliente_direcciones (cliente_id, direcciones) VALUES
(1, 'Av. Providencia 1234, Santiago'),
(1, 'Calle Los Olivos 456, Viña del Mar'),
(2, 'Pasaje Las Flores 789, Concepción'),
(3, 'Av. Alemania 555, Temuco');

-- Sucursales (sucursal-service)
USE bookpoint_sucursal_service;
INSERT INTO sucursales (id, nombre, direccion, ciudad, horario, personal_encargado) VALUES
(1, 'BookPoint Santiago Centro', 'Paseo Ahumada 45', 'Santiago', '09:00 - 20:00', 'Roberto Jara'),
(2, 'BookPoint Providencia', 'Av. Nueva Providencia 2155', 'Santiago', '10:00 - 21:00', 'Ana Morales'),
(3, 'BookPoint Viña', 'Libertad 670', 'Viña del Mar', '10:00 - 20:00', 'Claudio Ruiz');

-- Bodega Central y Transferencias (bodega-service)
USE bookpoint_bodega_service;
INSERT INTO bodega_central (id, producto_id, stock_central, stock_minimo, ubicacion) VALUES
(1, 1, 500, 50, 'Pasillo A - Estante 1'),
(2, 2, 300, 30, 'Pasillo A - Estante 2'),
(3, 3, 1000, 100, 'Pasillo B - Estante 1'),
(4, 4, 150, 20, 'Pasillo C - Estante 4'),
(5, 5, 100, 10, 'Pasillo D - Estante 2');

INSERT INTO transferencias (id, producto_id, cantidad, sucursal_origen_id, sucursal_destino_id, estado) VALUES
(1, 1, 10, 1, 2, 'EN_TRANSITO'),
(2, 4, 5, 3, 1, 'SOLICITADA');

-- Pedidos y Items (pedidos-service)
USE bookpoint_pedidos;
INSERT INTO pedidos (id, fecha_pedido, cliente_id, estado, total, direccion_envio, codigo_cupon, descuento) VALUES
(1, NOW(), 1, 'PENDIENTE', 63000.0, 'Av. Providencia 1234, Santiago', 'BIENVENIDA', 7000.0),
(2, NOW(), 2, 'CONFIRMADO', 18000.0, 'Pasaje Las Flores 789, Concepción', NULL, 0.0);

INSERT INTO items_pedido (id, pedido_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 2, 25000.0, 50000.0),
(2, 1, 3, 1, 12000.0, 12000.0),
(3, 2, 1, 1, 18000.0, 18000.0);

-- Ventas y Detalle (ventas-service)
USE bookpoint_ventas_service;
INSERT INTO ventas (id, fecha, cliente_id, total, descuento, tipo_documento, estado) VALUES
(1, NOW(), 3, 35000.0, 0.0, 'BOLETA', 'COMPLETADA');

INSERT INTO detalle_ventas (id, venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 5, 1, 35000.0, 35000.0);

-- Despachos (despacho-service)
USE bookpoint_despacho_service;
INSERT INTO despachos (id, pedido_id, direccion_destino, estado, transportista, ruta_optimizada) VALUES
(1, 1, 'Av. Providencia 1234, Santiago', 'PENDIENTE', 'ChileExpress', 'Ruta Norte-Oriente'),
(2, 2, 'Pasaje Las Flores 789, Concepción', 'EN_RUTA', 'Starken', 'Ruta Sur-Concepción');

-- Soporte y Reseñas (soporte-service)
USE bookpoint_soporte_service;
INSERT INTO soporte_reseñas (id, cliente_id, tipo, producto_id, mensaje, calificacion, estado, fecha) VALUES
(1, 1, 'SOPORTE', NULL, 'Mi pedido se ha demorado más de lo esperado.', NULL, 'ABIERTO', NOW()),
(2, 2, 'RESEÑA', 1, 'Excelente libro, llegó en perfecto estado.', 5, 'CERRADO', NOW()),
(3, 3, 'RESEÑA', 5, 'Edición de lujo muy bien cuidada.', 5, 'CERRADO', NOW());

-- ==========================================================
-- REACTIVAR COMPROBACIÓN DE LLAVES FORÁNEAS
-- ==========================================================
SET FOREIGN_KEY_CHECKS = 1;

-- ==========================================================
-- FIN DEL SCRIPT COMPLETO
-- ==========================================================
