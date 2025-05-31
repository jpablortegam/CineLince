-- Eliminar y (re)crear la base de datos
DROP DATABASE IF EXISTS CinemaSystem;
CREATE DATABASE IF NOT EXISTS CinemaSystem;
USE CinemaSystem;

-- Para manejar caracteres especiales
SET NAMES utf8mb4;

-- Desactivar temporalmente revisión de FK
SET FOREIGN_KEY_CHECKS = 0;

-- =====================
-- Definición de tablas
-- =====================

-- Cine
CREATE TABLE Cine
(
    IdCine       INT AUTO_INCREMENT PRIMARY KEY,
    Nombre       VARCHAR(100) NOT NULL,
    Direccion    VARCHAR(200) NOT NULL,
    Ciudad       VARCHAR(100) NOT NULL,
    Estado       VARCHAR(100) NOT NULL,
    CodigoPostal VARCHAR(10)  NOT NULL,
    Telefono     VARCHAR(20)  NOT NULL,
    HoraApertura TIME         NOT NULL,
    HoraCierre   TIME         NOT NULL
);

-- Estudio
CREATE TABLE Estudio
(
    IdEstudio   INT AUTO_INCREMENT PRIMARY KEY,
    Nombre      VARCHAR(100) NOT NULL,
    Pais        VARCHAR(100) NOT NULL,
    Descripcion TEXT
);

-- Director
CREATE TABLE Director
(
    IdDirector      INT AUTO_INCREMENT PRIMARY KEY,
    Nombre          VARCHAR(100) NOT NULL,
    Nacionalidad    VARCHAR(100) NOT NULL,
    FechaNacimiento DATE
);

-- TipoPelicula
CREATE TABLE TipoPelicula
(
    IdTipoPelicula INT AUTO_INCREMENT PRIMARY KEY,
    Nombre         VARCHAR(50) NOT NULL UNIQUE,
    Descripcion    TEXT
);

-- Pelicula
CREATE TABLE Pelicula
(
    IdPelicula     INT AUTO_INCREMENT PRIMARY KEY,
    Titulo         VARCHAR(150) NOT NULL,
    Duracion       INT          NOT NULL, -- en minutos
    Sinopsis       TEXT,
    FechaEstreno   DATE         NOT NULL,
    Clasificacion  VARCHAR(10)  NOT NULL, -- e.g. PG-13
    Idioma         VARCHAR(50)  NOT NULL,
    Subtitulada    BOOLEAN DEFAULT FALSE,
    Fotografia     VARCHAR(255),          -- URL o ruta a imagen
    Formato        VARCHAR(10)  NOT NULL, -- e.g. 2D, 3D, IMAX
    Estado         VARCHAR(20)  NOT NULL, -- e.g. 'En cartelera'
    IdEstudio      INT,
    IdDirector     INT,
    IdTipoPelicula INT,
    FOREIGN KEY (IdEstudio) REFERENCES Estudio (IdEstudio),
    FOREIGN KEY (IdDirector) REFERENCES Director (IdDirector),
    FOREIGN KEY (IdTipoPelicula) REFERENCES TipoPelicula (IdTipoPelicula)
);

-- Actor
CREATE TABLE Actor
(
    IdActor         INT AUTO_INCREMENT PRIMARY KEY,
    Nombre          VARCHAR(100) NOT NULL,
    Nacionalidad    VARCHAR(100) NOT NULL,
    FechaNacimiento DATE
);

-- PeliculaActor (N:M)
CREATE TABLE PeliculaActor
(
    IdPelicula INT,
    IdActor    INT,
    Personaje  VARCHAR(100) NOT NULL,
    PRIMARY KEY (IdPelicula, IdActor),
    FOREIGN KEY (IdPelicula) REFERENCES Pelicula (IdPelicula) ON DELETE CASCADE,
    FOREIGN KEY (IdActor) REFERENCES Actor (IdActor) ON DELETE CASCADE
);

-- Sala
CREATE TABLE Sala
(
    IdSala    INT AUTO_INCREMENT PRIMARY KEY,
    Numero    INT         NOT NULL,
    Capacidad INT         NOT NULL,
    TipoSala  VARCHAR(20) NOT NULL, -- e.g. 'VIP'
    Estado    VARCHAR(20) NOT NULL, -- e.g. 'Disponible'
    IdCine    INT,
    FOREIGN KEY (IdCine) REFERENCES Cine (IdCine) ON DELETE CASCADE
);

-- Asiento
CREATE TABLE Asiento
(
    IdAsiento   INT AUTO_INCREMENT PRIMARY KEY,
    Fila        CHAR(2)     NOT NULL,
    Numero      INT         NOT NULL,
    TipoAsiento VARCHAR(20) NOT NULL,
    Estado      VARCHAR(20) NOT NULL,
    IdSala      INT,
    FOREIGN KEY (IdSala) REFERENCES Sala (IdSala) ON DELETE CASCADE
);

-- Funcion
CREATE TABLE Funcion
(
    IdFuncion  INT AUTO_INCREMENT PRIMARY KEY,
    FechaHora  DATETIME       NOT NULL,
    Precio     DECIMAL(10, 2) NOT NULL,
    Estado     VARCHAR(20)    NOT NULL, -- e.g. 'En Venta'
    IdPelicula INT,
    IdSala     INT,
    FOREIGN KEY (IdPelicula) REFERENCES Pelicula (IdPelicula) ON DELETE CASCADE,
    FOREIGN KEY (IdSala) REFERENCES Sala (IdSala) ON DELETE CASCADE
);

-- Membresia
CREATE TABLE Membresia
(
    IdMembresia           INT AUTO_INCREMENT PRIMARY KEY,
    Tipo                  VARCHAR(50)    NOT NULL,
    Descripcion           TEXT,
    Costo                 DECIMAL(10, 2) NOT NULL,
    DuracionMeses         INT            NOT NULL,
    BeneficiosDescripcion TEXT
);

-- Cliente (con ContrasenaHash)
CREATE TABLE Cliente
(
    IdCliente       INT AUTO_INCREMENT PRIMARY KEY,
    Nombre          VARCHAR(100)        NOT NULL,
    Apellido        VARCHAR(100)        NOT NULL,
    Email           VARCHAR(100) UNIQUE NOT NULL,
    ContrasenaHash  VARCHAR(255)        NOT NULL,
    Telefono        VARCHAR(20)         NOT NULL,
    FechaNacimiento DATE                NOT NULL,
    FechaRegistro   DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP,
    IdMembresia     INT,
    FOREIGN KEY (IdMembresia) REFERENCES Membresia (IdMembresia)
);

-- Empleado
CREATE TABLE Empleado
(
    IdEmpleado        INT AUTO_INCREMENT PRIMARY KEY,
    Nombre            VARCHAR(100)   NOT NULL,
    Apellido          VARCHAR(100)   NOT NULL,
    Puesto            VARCHAR(50)    NOT NULL,
    FechaContratacion DATE           NOT NULL,
    Salario           DECIMAL(10, 2) NOT NULL,
    Estado            VARCHAR(20)    NOT NULL,
    IdCine            INT,
    FOREIGN KEY (IdCine) REFERENCES Cine (IdCine)
);

-- Promocion
CREATE TABLE Promocion
(
    IdPromocion INT AUTO_INCREMENT PRIMARY KEY,
    Nombre      VARCHAR(100)       NOT NULL,
    Descripcion TEXT,
    FechaInicio DATE               NOT NULL,
    FechaFin    DATE               NOT NULL,
    Descuento   DECIMAL(5, 2)      NOT NULL, -- e.g. 0.10
    CodigoPromo VARCHAR(20) UNIQUE NOT NULL,
    Estado      VARCHAR(20)        NOT NULL
);

-- Venta
CREATE TABLE Venta
(
    IdVenta     INT AUTO_INCREMENT PRIMARY KEY,
    Fecha       DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    Total       DECIMAL(10, 2) NOT NULL,
    MetodoPago  VARCHAR(50)    NOT NULL,
    Estado      VARCHAR(20)    NOT NULL,
    Facturado   BOOLEAN                 DEFAULT FALSE,
    IdEmpleado  INT,
    IdPromocion INT,
    FOREIGN KEY (IdEmpleado) REFERENCES Empleado (IdEmpleado),
    FOREIGN KEY (IdPromocion) REFERENCES Promocion (IdPromocion)
);

-- Boleto
CREATE TABLE Boleto
(
    IdBoleto    INT AUTO_INCREMENT PRIMARY KEY,
    PrecioFinal DECIMAL(10, 2) NOT NULL,
    FechaCompra DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CodigoQR    VARCHAR(200),
    IdFuncion   INT,
    IdCliente   INT,
    IdAsiento   INT,
    IdVenta     INT,
    FOREIGN KEY (IdFuncion) REFERENCES Funcion (IdFuncion) ON DELETE CASCADE,
    FOREIGN KEY (IdCliente) REFERENCES Cliente (IdCliente) ON DELETE SET NULL, -- Permite cliente anónimo o eliminado
    FOREIGN KEY (IdAsiento) REFERENCES Asiento (IdAsiento) ON DELETE SET NULL, -- Permite asiento no asignado o eliminado
    FOREIGN KEY (IdVenta) REFERENCES Venta (IdVenta) ON DELETE CASCADE
);

-- CategoriaProducto
CREATE TABLE CategoriaProducto
(
    IdCategoria INT AUTO_INCREMENT PRIMARY KEY,
    Nombre      VARCHAR(50) NOT NULL,
    Descripcion TEXT
);

-- Producto
CREATE TABLE Producto
(
    IdProducto  INT AUTO_INCREMENT PRIMARY KEY,
    Nombre      VARCHAR(100)   NOT NULL,
    Descripcion TEXT,
    Precio      DECIMAL(10, 2) NOT NULL,
    Stock       INT            NOT NULL,
    Estado      VARCHAR(20)    NOT NULL,
    IdCategoria INT,
    FOREIGN KEY (IdCategoria) REFERENCES CategoriaProducto (IdCategoria)
);

-- DetalleVenta
CREATE TABLE DetalleVenta
(
    IdDetalleVenta INT AUTO_INCREMENT PRIMARY KEY,
    Cantidad       INT            NOT NULL,
    PrecioUnitario DECIMAL(10, 2) NOT NULL,
    Subtotal       DECIMAL(10, 2) NOT NULL,
    IdVenta        INT,
    IdProducto     INT,
    FOREIGN KEY (IdVenta) REFERENCES Venta (IdVenta) ON DELETE CASCADE,
    FOREIGN KEY (IdProducto) REFERENCES Producto (IdProducto)
);

-- =====================
-- SISTEMA DE CALIFICACIONES
-- =====================

-- Agregar campos de resumen a la tabla Pelicula para optimizar consultas
ALTER TABLE Pelicula
    ADD COLUMN CalificacionPromedio DECIMAL(3, 2) DEFAULT 0.00,
    ADD COLUMN TotalCalificaciones  INT           DEFAULT 0;

-- Tabla para almacenar las calificaciones individuales de los usuarios
CREATE TABLE CalificacionPelicula
(
    IdCalificacion    INT AUTO_INCREMENT PRIMARY KEY,
    IdPelicula        INT           NOT NULL,
    IdCliente         INT           NOT NULL,
    Calificacion      DECIMAL(2, 1) NOT NULL CHECK (Calificacion >= 0.0 AND Calificacion <= 5.0),
    Comentario        TEXT,
    FechaCalificacion DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Un cliente solo puede calificar una película una vez
    UNIQUE KEY unique_cliente_pelicula (IdCliente, IdPelicula),

    FOREIGN KEY (IdPelicula) REFERENCES Pelicula (IdPelicula) ON DELETE CASCADE,
    FOREIGN KEY (IdCliente) REFERENCES Cliente (IdCliente) ON DELETE CASCADE
);

-- =====================
-- TRIGGERS PARA MANTENER ESTADÍSTICAS ACTUALIZADAS
-- =====================

DELIMITER $$

-- Trigger para actualizar estadísticas cuando se inserta una nueva calificación
CREATE TRIGGER after_calificacion_insert
    AFTER INSERT
    ON CalificacionPelicula
    FOR EACH ROW
BEGIN
    UPDATE Pelicula
    SET CalificacionPromedio = (SELECT AVG(Calificacion)
                                FROM CalificacionPelicula
                                WHERE IdPelicula = NEW.IdPelicula),
        TotalCalificaciones  = (SELECT COUNT(*)
                                FROM CalificacionPelicula
                                WHERE IdPelicula = NEW.IdPelicula)
    WHERE IdPelicula = NEW.IdPelicula;
END$$

-- Trigger para actualizar estadísticas cuando se actualiza una calificación
CREATE TRIGGER after_calificacion_update
    AFTER UPDATE
    ON CalificacionPelicula
    FOR EACH ROW
BEGIN
    UPDATE Pelicula
    SET CalificacionPromedio = (SELECT AVG(Calificacion)
                                FROM CalificacionPelicula
                                WHERE IdPelicula = NEW.IdPelicula),
        TotalCalificaciones  = (SELECT COUNT(*)
                                FROM CalificacionPelicula
                                WHERE IdPelicula = NEW.IdPelicula)
    WHERE IdPelicula = NEW.IdPelicula;
END$$

-- Trigger para actualizar estadísticas cuando se elimina una calificación
CREATE TRIGGER after_calificacion_delete
    AFTER DELETE
    ON CalificacionPelicula
    FOR EACH ROW
BEGIN
    UPDATE Pelicula
    SET CalificacionPromedio = COALESCE((SELECT AVG(Calificacion)
                                         FROM CalificacionPelicula
                                         WHERE IdPelicula = OLD.IdPelicula), 0.00),
        TotalCalificaciones  = (SELECT COUNT(*)
                                FROM CalificacionPelicula
                                WHERE IdPelicula = OLD.IdPelicula)
    WHERE IdPelicula = OLD.IdPelicula;
END$$

-- =====================
-- PROCEDIMIENTOS ALMACENADOS ÚTILES
-- =====================

-- Procedimiento para obtener las películas mejor calificadas
CREATE PROCEDURE ObtenerPeliculasMejorCalificadas(
    IN limite INT,
    IN minimo_calificaciones INT
)
BEGIN
    SELECT p.IdPelicula,
           p.Titulo,
           p.CalificacionPromedio,
           p.TotalCalificaciones,
           d.Nombre AS Director,
           e.Nombre AS Estudio
    FROM Pelicula p
             LEFT JOIN Director d ON p.IdDirector = d.IdDirector
             LEFT JOIN Estudio e ON p.IdEstudio = e.IdEstudio
    WHERE p.TotalCalificaciones >= minimo_calificaciones
    ORDER BY p.CalificacionPromedio DESC, p.TotalCalificaciones DESC
    LIMIT limite;
END$$

-- Procedimiento para obtener calificaciones de una película específica
CREATE PROCEDURE ObtenerCalificacionesPelicula(
    IN pelicula_id INT
)
BEGIN
    SELECT c.Calificacion,
           c.Comentario,
           c.FechaCalificacion,
           cl.Nombre,
           cl.Apellido
    FROM CalificacionPelicula c
             JOIN Cliente cl ON c.IdCliente = cl.IdCliente
    WHERE c.IdPelicula = pelicula_id
    ORDER BY c.FechaCalificacion DESC;
END$$

-- Procedimiento para calificar una película
CREATE PROCEDURE CalificarPelicula(
    IN cliente_id INT,
    IN pelicula_id INT,
    IN nueva_calificacion DECIMAL(2, 1),
    IN comentario_texto TEXT
)
BEGIN
    -- Insertar o actualizar la calificación
    INSERT INTO CalificacionPelicula (IdCliente, IdPelicula, Calificacion, Comentario, FechaCalificacion)
    VALUES (cliente_id, pelicula_id, nueva_calificacion, comentario_texto, CURRENT_TIMESTAMP)
    ON DUPLICATE KEY UPDATE Calificacion      = nueva_calificacion,
                            Comentario        = comentario_texto,
                            FechaCalificacion = CURRENT_TIMESTAMP;
END$$

DELIMITER ;

-- =====================
-- VISTA PARA CONSULTAS COMUNES
-- =====================

-- Vista que combina información de películas con sus calificaciones
CREATE VIEW VistaPeliculasConCalificacion AS
SELECT p.IdPelicula,
       p.Titulo,
       p.Duracion,
       p.FechaEstreno,
       p.Clasificacion,
       p.Formato,
       p.Estado,
       p.CalificacionPromedio,
       p.TotalCalificaciones,
       d.Nombre                              AS Director,
       e.Nombre                              AS Estudio,
       tp.Nombre                             AS TipoPelicula,
       -- Clasificación por estrellas (redondeado a 0.5)
       ROUND(p.CalificacionPromedio * 2) / 2 AS CalificacionEstrellas
FROM Pelicula p
         LEFT JOIN Director d ON p.IdDirector = d.IdDirector
         LEFT JOIN Estudio e ON p.IdEstudio = e.IdEstudio
         LEFT JOIN TipoPelicula tp ON p.IdTipoPelicula = tp.IdTipoPelicula;

-- Reactivar revisión de FK
SET FOREIGN_KEY_CHECKS = 1;


-- =====================
-- Población de datos
-- =====================

-- Cinépolis / Cinemex iniciales
INSERT INTO Cine (IdCine, Nombre, Direccion, Ciudad, Estado, CodigoPostal, Telefono, HoraApertura, HoraCierre)
VALUES (1, 'Cinépolis Las Américas', 'Av. las Américas 1500, Plaza Las Américas', 'Morelia', 'Michoacán', '58270',
        '4431234567', '10:00:00', '23:59:00'),
       (2, 'Cinemex Parque Delta', 'Av. Cuauhtémoc 462, Narvarte Poniente', 'Ciudad de México', 'CDMX', '03020',
        '5598765432', '11:00:00', '01:00:00'),
       (3, 'Cinépolis Galerías Monterrey', 'Av. Insurgentes 2500, Vista Hermosa', 'Monterrey', 'Nuevo León', '64620',
        '8181234567', '10:30:00', '00:30:00'),
       (4, 'Cinemex Plaza Patria', 'Av. Patria 1950, Zapopan', 'Guadalajara', 'Jalisco', '45160', '3331234567',
        '11:30:00', '01:30:00');

-- Estudios
INSERT INTO Estudio (IdEstudio, Nombre, Pais, Descripcion)
VALUES (1, 'Warner Bros. Pictures', 'Estados Unidos', 'Productora y distribuidora de cine y televisión.'),
       (2, 'Universal Pictures', 'Estados Unidos', 'Filial de NBCUniversal.'),
       (3, 'Videocine', 'México', 'Empresa mexicana de producción y distribución.'),
       (6, 'A24', 'Estados Unidos', 'Estudio de cine independiente.');
-- Añadido Estudio con ID 6

-- Directores
INSERT INTO Director (IdDirector, Nombre, Nacionalidad, FechaNacimiento)
VALUES (1, 'Guillermo del Toro', 'Mexicana', '1964-10-09'),
       (2, 'Alfonso Cuarón', 'Mexicana', '1961-11-28'),
       (3, 'Christopher Nolan', 'Británico-Estadounidense', '1970-07-30'),
       (4, 'Greta Gerwig', 'Estadounidense', '1983-08-04'),
       (5, 'Denis Villeneuve', 'Canadiense', '1967-10-03'),
       (6, 'Bong Joon-ho', 'Surcoreano', '1969-09-14');

-- Tipos de película
INSERT INTO TipoPelicula (IdTipoPelicula, Nombre, Descripcion)
VALUES (1, 'Acción', 'Secuencias de riesgo y efectos especiales.'),
       (2, 'Comedia', 'Provoquen risa y entretenimiento.'),
       (3, 'Drama', 'Conflictos emocionales y desarrollo.'),
       (4, 'Ciencia Ficción', 'Tecnología y viajes espaciales.'),
       (5, 'Animación', 'Técnicas de animación.'),
       (6, 'Terror', 'Provocar miedo y tensión.'),
       (7, 'Musical', 'Canciones y bailes.'),
       (8, 'Documental', 'Basadas en hechos reales.');

-- Películas
INSERT INTO Pelicula (IdPelicula, Titulo, Duracion, Sinopsis, FechaEstreno, Clasificacion, Idioma, Subtitulada,
                      Fotografia, Formato, Estado, IdEstudio, IdDirector, IdTipoPelicula)
VALUES (1, 'Roma', 135, 'Crónica de vida familiar en los 70.', '2018-11-21', 'R', 'Español', TRUE, 'posters/roma.jpg',
        '2D', 'En Cartelera', 3, 2, 3),
       (2, 'Interestelar', 169, 'Exploradores viajan por un agujero de gusano.', '2014-11-07', 'PG-13', 'Inglés', TRUE,
        'posters/interestelar.jpg', 'IMAX', 'En Cartelera', 1, 3, 4),
       (3, 'Spider-Man: A Través del Spider-Verso', 140, 'Miles Morales en el Multiverso.', '2023-06-02', 'PG',
        'Inglés', TRUE, 'posters/spiderman_spiderverse.jpg', '2D', 'Estreno', 2, NULL, 5),
       (4, 'Barbie', 114, 'Crisis existencial de Barbie.', '2023-07-21', 'PG-13', 'Inglés', TRUE, 'posters/barbie.jpg',
        '2D', 'En Cartelera', 1, 4, 2),
       (5, 'Dune: Parte Dos', 166, 'Paul Atreides entre los Fremen.', '2024-03-01', 'PG-13', 'Inglés', TRUE,
        'posters/dune2.jpg', 'IMAX', 'Estreno', 1, 5, 4),
       (6, 'Parásitos', 132, 'Familia pobre se infiltra en familia rica.', '2019-05-30', 'R', 'Coreano', TRUE,
        'posters/parasitos.jpg', '2D', 'En Cartelera', NULL, 6, 3),
       (7, 'Oppenheimer', 180, 'Historia de J. Robert Oppenheimer.', '2023-07-21', 'R', 'Inglés', TRUE,
        'posters/oppenheimer.jpg', 'IMAX', 'En Cartelera', 2, 3, 3),
       (8, 'El Gato con Botas: El Último Deseo', 102, 'Gato pierde vidas.', '2022-12-21', 'PG', 'Inglés', TRUE,
        'posters/gato_con_botas2.jpg', '3D', 'En Cartelera', 2, NULL, 5),
       (9, 'Wonka', 116, 'Juventud de Willy Wonka.', '2023-12-15', 'PG', 'Inglés', TRUE, 'posters/wonka.jpg', '2D',
        'En Cartelera', 1, NULL, 7),
       (10, 'La La Land', 128, 'Amor en LA entre pianista y actriz.', '2016-12-09', 'PG-13', 'Inglés', FALSE,
        'posters/lalaland.jpg', '2D', 'En Cartelera', 6, NULL, 7), -- IdEstudio 6
       (11, 'Todo en Todas Partes al Mismo Tiempo', 139, 'Inmigrante explora universos.', '2022-03-25', 'R', 'Inglés',
        TRUE, 'posters/everything_everywhere.jpg', '2D', 'En Cartelera', 6, NULL, 1);
-- IdEstudio 6

-- Actores
INSERT INTO Actor (IdActor, Nombre, Nacionalidad, FechaNacimiento)
VALUES (1, 'Yalitza Aparicio', 'Mexicana', '1993-12-11'),
       (2, 'Matthew McConaughey', 'Estadounidense', '1969-11-04'),
       (3, 'Anne Hathaway', 'Estadounidense', '1982-11-12'),
       (4, 'Shameik Moore', 'Estadounidense', '1995-05-04'),
       (5, 'Margot Robbie', 'Australiana', '1990-07-02'),
       (6, 'Ryan Gosling', 'Canadiense', '1980-11-12'),
       (7, 'Timothée Chalamet', 'Estadounidense-Francés', '1995-12-27'),
       (8, 'Zendaya', 'Estadounidense', '1996-09-01'),
       (9, 'Song Kang-ho', 'Surcoreano', '1967-01-17'),
       (10, 'Cillian Murphy', 'Irlandés', '1976-05-25'),
       (11, 'Antonio Banderas', 'Española', '1960-08-10'),
       (12, 'Florence Pugh', 'Británica', '1996-01-03'),
       (13, 'Michelle Yeoh', 'Malaya', '1962-08-06'),
       (14, 'Ke Huy Quan', 'Vietnamita-Estadounidense', '1971-08-20');

-- Relación Pelicula–Actor
INSERT INTO PeliculaActor (IdPelicula, IdActor, Personaje)
VALUES (1, 1, 'Cleodegaria "Cleo" Gutiérrez'),
       (2, 2, 'Joseph Cooper'),
       (2, 3, 'Amelia Brand'),
       (3, 4, 'Miles Morales (voz)'),
       (4, 5, 'Barbie Estereotípica'),
       (4, 6, 'Ken'),
       (5, 7, 'Paul Atreides'),
       (5, 8, 'Chani'),
       (6, 9, 'Kim Ki-taek'),
       (7, 10, 'J. Robert Oppenheimer'),
       (7, 12, 'Jean Tatlock'),
       (8, 11, 'Gato con Botas (voz)'),
       (9, 7, 'Willy Wonka'),
       (10, 6, 'Sebastian Wilder'),
       (11, 13, 'Evelyn Quan Wang'),
       (11, 14, 'Waymond Wang');

-- Salas
INSERT INTO Sala (IdSala, Numero, Capacidad, TipoSala, Estado, IdCine)
VALUES (1, 1, 120, 'Tradicional', 'Disponible', 1),
       (2, 2, 80, 'VIP', 'Disponible', 1),
       (3, 1, 200, 'IMAX', 'Disponible', 2),
       (4, 5, 100, 'Tradicional', 'Mantenimiento', 2),
       (5, 3, 150, 'Tradicional', 'Disponible', 1),
       (6, 4, 90, 'VIP', 'Disponible', 1),
       (7, 2, 180, 'Tradicional', 'Disponible', 2),
       (8, 6, 70, 'Junior', 'Disponible', 2),
       (9, 1, 160, 'IMAX', 'Disponible', 3),            -- Cine 3
       (10, 2, 100, 'VIP', 'Disponible', 3),            -- Cine 3
       (11, 3, 120, 'Tradicional', 'Mantenimiento', 3), -- Cine 3
       (12, 1, 140, 'Tradicional', 'Disponible', 4),    -- Cine 4
       (13, 2, 60, 'Platino', 'Disponible', 4);
-- Cine 4

-- Asientos
INSERT INTO Asiento (IdAsiento, Fila, Numero, TipoAsiento, Estado, IdSala)
VALUES (1, 'A', 1, 'Normal', 'Disponible', 1),
       (2, 'A', 2, 'Normal', 'Disponible', 1),
       (3, 'F', 5, 'Preferencial', 'Disponible', 1),
       (4, 'F', 6, 'Preferencial', 'Disponible', 1),
       (5, 'C', 10, 'IMAX Normal', 'Disponible', 3),
       (6, 'C', 11, 'IMAX Normal', 'Disponible', 3),
       (7, 'H', 7, 'IMAX Preferente', 'Disponible', 3),
       (8, 'A', 1, 'Normal', 'Disponible', 5),
       (9, 'A', 2, 'Normal', 'Disponible', 5),
       (10, 'G', 8, 'Preferencial', 'Reparacion', 5),
       (11, 'B', 5, 'IMAX Normal', 'Disponible', 9),
       (12, 'B', 6, 'IMAX Normal', 'Disponible', 9),
       (13, 'J', 10, 'IMAX Preferente', 'Bloqueado', 9),
       (14, 'A', 1, 'Platino Individual', 'Disponible', 13),
       (15, 'A', 2, 'Platino Individual', 'Disponible', 13),
       (16, 'C', 3, 'Platino Doble', 'Disponible', 13);

-- Funciones
INSERT INTO Funcion (IdFuncion, FechaHora, Precio, Estado, IdPelicula, IdSala)
VALUES (1, '2025-05-25 16:00:00', 85.00, 'En Venta', 1, 1),
       (2, '2025-05-25 19:00:00', 120.00, 'En Venta', 2, 3),
       (3, '2025-05-26 17:30:00', 90.00, 'Programada', 3, 1),
       (4, '2025-05-25 20:30:00', 85.00, 'En Venta', 1, 1),
       (5, '2025-05-26 18:00:00', 95.00, 'En Venta', 4, 5),
       (6, '2025-05-26 21:00:00', 150.00, 'En Venta', 5, 9),
       (7, '2025-05-27 15:00:00', 80.00, 'Programada', 6, 7),
       (8, '2025-05-24 20:00:00', 130.00, 'Pasada', 7, 3),
       (9, '2025-05-28 16:30:00', 100.00, 'En Venta', 8, 6),
       (10, '2025-05-29 19:45:00', 90.00, 'En Venta', 9, 12),
       (11, '2025-05-20 17:00:00', 75.00, 'Cancelada', 1, 2),
       (12, '2025-06-01 20:15:00', 160.00, 'Próximamente en Venta', 5, 3),
       (13, '2025-05-27 17:00:00', 110.00, 'En Venta', 10, 10),
       (14, '2025-05-28 20:30:00', 95.00, 'En Venta', 11, 5);

-- Membresías
INSERT INTO Membresia (IdMembresia, Tipo, Descripcion, Costo, DuracionMeses, BeneficiosDescripcion)
VALUES (1, 'Fan', 'Nivel básico con algunos descuentos.', 100.00, 12, 'Acumula puntos, precio especial martes.'),
       (2, 'Fanático', 'Nivel intermedio con más beneficios.', 250.00, 12, 'Descuentos en dulcería, preventas.'),
       (3, 'Súper Fanático', 'Nivel premium con todos los beneficios.', 500.00, 12, 'Boletos gratis y combos.'),
       (4, 'Estudiante', 'Descuento para estudiantes.', 80.00, 6, 'Precio especial L-J.'),
       (5, 'Senior', 'Beneficios >60 años.', 70.00, 12, '10% en dulcería.');

-- Clientes (con hash de ejemplo)
INSERT INTO Cliente (IdCliente, Nombre, Apellido, Email, ContrasenaHash, Telefono, FechaNacimiento, FechaRegistro,
                     IdMembresia)
VALUES (1, 'Mariana', 'López', 'mariana.lopez@example.com',
        '$2a$10$N9qo8uLOickq.KbGMxL2Q.9.E4J.C2Q9S9zL/a3F.1aC1I.9K.zO6', '4435551122', '1995-08-23',
        '2024-03-15 10:00:00', 2),
       (2, 'Carlos', 'Fuentes', 'carlos.f@example.com', '$2a$10$hXzL6c2mK5nB8qR7vX9wY.u7e6fS5gH4jK3lM2nO1pP0qR9sT8uV2',
        '5551112233', '1988-02-10', '2025-01-20 14:30:00', NULL),
       (3, 'Sofía', 'Hernández', 'sofia.hdz@example.com',
        '$2a$10$aB1cD2eF3gH4iJ5kL6mN7.oP8qR9sT0uV1wX2yZ3A4B5C6D7E8F9G', '8112345678', '2001-11-05',
        '2023-11-01 18:00:00', 3),
       (4, 'Luis', 'Ramírez', 'luis.ram@example.com', '$2a$10$kL9mN8oP7qR6sT5uV4wX3.yZ2A1B0C9D8E7F6G5H4I3J2K1L0M9N',
        '3312345678', '1999-04-15', '2025-02-10 11:00:00', 4),
       (5, 'Ana', 'García', 'ana.garcia@example.com', '$2a$10$N9qo8uLOickq.KbGMxL2Q.9.E4J.C2Q9S9zL/a3F.1aC1I.9K.zO6',
        '5567890123', '1960-07-30', '2024-12-01 09:30:00', 5),
       (6, 'Jorge', 'Torres', 'jorge.t@example.com', '$2a$10$hXzL6c2mK5nB8qR7vX9wY.u7e6fS5gH4jK3lM2nO1pP0qR9sT8uV2',
        '4429876543', '1985-12-01', '2025-03-01 17:45:00', 1),
       (7, 'Laura', 'Pérez', 'laura.p@example.com', '$2a$10$aB1cD2eF3gH4iJ5kL6mN7.oP8qR9sT0uV1wX2yZ3A4B5C6D7E8F9G',
        '8119876500', '2003-06-20', '2025-04-22 12:10:00', NULL);

-- Empleados
INSERT INTO Empleado (IdEmpleado, Nombre, Apellido, Puesto, FechaContratacion, Salario, Estado, IdCine)
VALUES (1, 'Juan', 'Rodríguez', 'Taquillero', '2023-05-10', 6500.00, 'Activo', 1),
       (2, 'Ana', 'Martínez', 'Gerente de Sucursal', '2022-01-20', 15000.00, 'Activo', 1),
       (3, 'Pedro', 'Gómez', 'Staff Dulcería', '2024-02-01', 6000.00, 'Activo', 2),
       (4, 'Laura', 'Sánchez', 'Staff Dulcería', '2024-03-01', 6200.00, 'Activo', 1),
       (5, 'Miguel', 'Hernández', 'Proyeccionista', '2023-08-15', 8000.00, 'Activo', 2),
       (6, 'Claudia', 'Vargas', 'Gerente', '2023-11-01', 16000.00, 'Activo', 3), -- Cine 3
       (7, 'Roberto', 'Díaz', 'Taquillero', '2024-01-10', 6800.00, 'Activo', 4), -- Cine 4
       (8, 'Fernanda', 'Castillo', 'Limpieza', '2023-09-01', 5500.00, 'Inactivo', 2);

-- Promociones
INSERT INTO Promocion (IdPromocion, Nombre, Descripcion, FechaInicio, FechaFin, Descuento, CodigoPromo, Estado)
VALUES (1, 'Martes 2x1', 'Boletos 2x1 martes', '2025-01-01', '2025-12-31', 0.50, 'MARTES2X1', 'Activa'),
       (2, 'Combo Estreno', '10% en combo estreno', '2025-05-01', '2025-07-31', 0.10, 'COMBOESTRENO10', 'Activa'),
       (3, 'Miércoles de Estreno', '20% en estreno miércoles', '2025-02-01', '2025-11-30', 0.20, 'MIERCOLES20',
        'Activa'),
       (4, 'Combo Familiar', '15% en dulcería familiar', '2025-04-01', '2025-06-30', 0.15, 'FAMILIA15', 'Activa'),
       (5, 'Promo Verano Pasado', 'Descuento verano 2024', '2024-07-01', '2024-08-31', 0.25, 'VERANO24', 'Expirada');

-- Ventas iniciales
INSERT INTO Venta (IdVenta, Fecha, Total, MetodoPago, Estado, Facturado, IdEmpleado, IdPromocion)
VALUES (1, '2025-05-25 15:45:00', 170.00, 'Tarjeta', 'Completada', FALSE, 1, NULL),
       (2, '2025-05-25 18:30:00', 108.00, 'Efectivo', 'Completada', FALSE, 3, 2), -- Total se actualizará después
       (3, '2025-05-25 20:00:00', 115.00, 'Efectivo', 'Completada', TRUE, 3, NULL),
       (4, '2025-05-26 17:45:00', 190.00, 'Tarjeta', 'Completada', TRUE, 1, NULL),
       (5, '2025-05-26 20:30:00', 270.00, 'Tarjeta', 'Completada', FALSE, 7, 3),  -- Total se actualizará después
       (6, '2025-05-28 16:15:00', 200.00, 'Efectivo', 'Completada', FALSE, 4, NULL),
       (7, '2025-05-20 16:30:00', 0.00, 'N/A', 'Cancelada', FALSE, 1, NULL),
       (8, '2025-05-27 14:30:00', 120.00, 'Tarjeta', 'Completada', TRUE, 6, NULL),
       (9, '2025-05-27 18:00:00', 95.00, 'Efectivo', 'Completada', FALSE, 4, NULL),
       (10, '2025-05-27 16:30:00', 110.00, 'Tarjeta', 'Completada', FALSE, 6, 1);

-- Ajustes de totales para Ventas
UPDATE Venta
SET Total = 258.00
WHERE IdVenta = 2;
UPDATE Venta
SET Total = 275.00
WHERE IdVenta = 5;

-- Boletos
INSERT INTO Boleto (IdBoleto, PrecioFinal, FechaCompra, CodigoQR, IdFuncion, IdCliente, IdAsiento, IdVenta)
VALUES (1, 85.00, '2025-05-25 15:45:00', 'QR20250525001A1F1', 1, 1, 1, 1),
       (2, 85.00, '2025-05-25 15:45:00', 'QR20250525001A2F1', 1, 2, 1, 1),
       (3, 108.00, '2025-05-25 18:30:00', 'QR20250525001C10F2', 2, NULL, 5,
        2),  -- Precio de Venta 2 se actualizó, este podría ajustarse
       (4, 95.00, '2025-05-26 17:45:00', 'QR20250526001A1F5', 5, 6, 8, 4),
       (5, 95.00, '2025-05-26 17:45:00', 'QR20250526001A2F5', 5, NULL, 9, 4),
       (6, 120.00, '2025-05-26 20:30:00', 'QR20250526001B5F9', 6, 4, 11,
        5),  -- Precio de Venta 5 se actualizó, este podría ajustarse
       (7, 120.00, '2025-05-26 20:30:00', 'QR20250526001B6F9', 6, 4, 12,
        5),  -- Precio de Venta 5 se actualizó, este podría ajustarse
       (8, 100.00, '2025-05-28 16:15:00', 'QR20250528001XXF9', 9, 7, NULL, 6),
       (9, 100.00, '2025-05-28 16:15:00', 'QR20250528002XXF9', 9, 7, NULL, 6),
       (10, 110.00, '2025-05-27 16:30:00', 'QR20250527001XXF13', 13, 5, NULL,
        10), -- Esta venta usa promo 2x1, podrían ser 2 boletos por 55 c/u
       (11, 0.00, '2025-05-27 16:30:00', 'QR20250527002XXF13', 13, 5, NULL, 10);
-- O el segundo boleto de la promo 2x1

-- Categorías de productos
INSERT INTO CategoriaProducto (IdCategoria, Nombre, Descripcion)
VALUES (1, 'Bebidas', 'Refrescos, aguas, jugos, cafés.'),
       (2, 'Palomitas', 'Sabores variados.'),
       (3, 'Dulces y Chocolates', 'Golosinas.'),
       (4, 'Snacks Calientes', 'Nachos, hot dogs.'),
       (5, 'Combos', 'Paquetes promocionales.'),
       (6, 'Helados', 'Helados y paletas.');

-- Productos
INSERT INTO Producto (IdProducto, Nombre, Descripcion, Precio, Stock, Estado, IdCategoria)
VALUES (1, 'Refresco Grande', '946ml', 45.00, 200, 'Disponible', 1),
       (2, 'Palomitas Grandes Mantequilla', 'Recien hechas', 70.00, 150, 'Disponible', 2),
       (3, 'Nachos con Queso y Jalapeños', 'Totopos con queso', 65.00, 100, 'Disponible', 4),
       (4, 'Combo Pareja', '2 Refrescos + Palomitas', 150.00, 50, 'Disponible', 5),
       (5, 'Agua Embotellada 600ml', 'Agua natural', 20.00, 300, 'Disponible', 1),
       (6, 'Chocolate Grande', 'Barra importada', 35.00, 80, 'Disponible', 3),
       (7, 'Palomitas Caramelizadas Chicas', 'Caramelo', 55.00, 70, 'Agotado', 2),
       (8, 'Hot Dog Clásico', 'Salchicha de pavo', 50.00, 90, 'Disponible', 4),
       (9, 'Combo Nachos', 'Nachos + Refresco mediano', 100.00, 60, 'Disponible', 5),
       (10, 'Helado de Vainilla (vaso)', 'Helado cremoso', 40.00, 50, 'Disponible', 6);

-- Detalles de venta
INSERT INTO DetalleVenta (IdDetalleVenta, Cantidad, PrecioUnitario, Subtotal, IdVenta, IdProducto)
VALUES (1, 1, 150.00, 150.00, 2,
        4),                          -- Venta 2, Combo Pareja. El total de venta se ajustó a 258, aquí hay 150. Faltan 108
       (2, 1, 70.00, 70.00, 3, 2),   -- Venta 3, Palomitas. Total Venta 115. Faltan 45
       (3, 1, 45.00, 45.00, 3, 1),   -- Venta 3, Refresco. Suma 70+45 = 115. OK.
       (4, 1, 35.00, 35.00, 5, 6),   -- Venta 5, Chocolate. Total Venta se ajustó a 275, aquí hay 35. Faltan 240
       (5, 1, 100.00, 100.00, 8, 9), -- Venta 8, Combo Nachos. Total Venta 120. Faltan 20
       (6, 1, 20.00, 20.00, 8, 5),   -- Venta 8, Agua. Suma 100+20 = 120. OK.
       (7, 1, 50.00, 50.00, 9, 8),   -- Venta 9, Hotdog. Total Venta 95. Faltan 45.
       (8, 1, 45.00, 45.00, 9, 1);
-- Venta 9, Refresco. Suma 50+45 = 95. OK.


-- Calificacion de Pelicula

INSERT INTO CalificacionPelicula (IdPelicula, IdCliente, Calificacion, Comentario)
VALUES (1, 1, 4.5, 'Excelente película, gran dirección y actuaciones. ¡Un clásico moderno!'),
       (1, 2, 5.0, 'Obra maestra del cine, la recomiendo totalmente. Visualmente impactante.'),
       (1, 3, 4.0, 'Muy buena, aunque un poco larga para mi gusto. Buenas actuaciones.'),
       (2, 1, 3.5, 'Entretenida pero predecible. Los efectos especiales son lo mejor.'),
       (2, 2, 4.0, 'Buena película familiar, ideal para una tarde de cine en casa.'),
       (2, 4, 4.2, 'Me mantuvo al borde del asiento. ¡Qué gran aventura espacial!'),
       (3, 1, 5.0, 'Increíble experiencia cinematográfica. La animación es espectacular.'),
       (3, 5, 4.8, 'Una secuela que supera a la original. No puedo esperar por la siguiente.'),
       (4, 2, 3.0, 'No fue lo que esperaba, un poco decepcionante la trama.'),
       (4, 3, 3.8, 'Divertida y con un mensaje interesante, aunque algunas partes se sienten lentas.'),
       (4, 6, 4.1, 'Muy original y visualmente atractiva. Margot Robbie está genial.'),
       (5, 4, 4.9, 'Épica en todos los sentidos. Denis Villeneuve no decepciona.'),
       (5, 7, 4.7, 'Impresionante. La cinematografía y la banda sonora son de otro nivel.'),
       (6, 1, 4.6, 'Una joya del cine coreano. Inteligente y con giros inesperados.'),
       (6, 5, 5.0, 'Simplemente brillante. Te hace reflexionar mucho.'),
       (7, 2, 4.3, 'Intensa y muy bien actuada. Cillian Murphy merece todos los premios.'),
       (7, 6, 4.0, 'Un biopic fascinante, aunque denso por momentos.'),
       (8, 3, 4.2, 'Muy divertida y con un gran mensaje para los niños (y no tan niños).'),
       (8, 7, 3.9, 'Animación colorida y personajes entrañables.'),
       (9, 4, 3.7, 'Una historia dulce, pero le faltó un poco más de magia para mi gusto.'),
       (10, 1, 4.4, 'Un musical hermoso, con canciones que se te quedan grabadas.'),
       (10, 5, 4.0, 'Me encantó la química entre los protagonistas y la estética de la película.'),
       (11, 6, 4.8, 'Original, caótica y profundamente emotiva. De lo mejor que he visto últimamente.'),
       (11, 7, 4.9, 'Una montaña rusa de emociones. Michelle Yeoh es increíble.');

