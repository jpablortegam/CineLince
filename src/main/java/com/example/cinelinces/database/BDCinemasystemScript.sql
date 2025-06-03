-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 02-06-2025 a las 09:24:49
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `cinemasystem`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `actor`
--

CREATE TABLE `actor`
(
    `IdActor`         int(11)      NOT NULL,
    `Nombre`          varchar(100) NOT NULL,
    `Nacionalidad`    varchar(100) NOT NULL,
    `FechaNacimiento` date DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `actor`
--

INSERT INTO `actor` (`IdActor`, `Nombre`, `Nacionalidad`, `FechaNacimiento`)
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

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asiento`
--

CREATE TABLE `asiento`
(
    `IdAsiento`   int(11)     NOT NULL,
    `Fila`        char(2)     NOT NULL,
    `Numero`      int(11)     NOT NULL,
    `TipoAsiento` varchar(20) NOT NULL,
    `IdSala`      int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `asiento`
--

INSERT INTO `asiento` (`IdAsiento`, `Fila`, `Numero`, `TipoAsiento`, `IdSala`)
VALUES (1, 'A', 1, 'Normal', 1),
       (2, 'A', 2, 'Normal', 1),
       (3, 'A', 3, 'Normal', 1),
       (4, 'B', 1, 'Normal', 1),
       (5, 'B', 2, 'Normal', 1),
       (6, 'B', 3, 'Normal', 1),
       (7, 'C', 1, 'VIP', 2),
       (8, 'C', 2, 'VIP', 2),
       (9, 'D', 1, 'VIP', 2),
       (10, 'A', 1, 'IMAX Normal', 3),
       (11, 'A', 2, 'IMAX Normal', 3),
       (12, 'B', 1, 'IMAX Normal', 3),
       (13, 'E', 1, 'Tradicional', 5),
       (14, 'E', 2, 'Tradicional', 5),
       (15, 'F', 1, 'IMAX', 9),
       (16, 'F', 2, 'IMAX', 9),
       (17, 'G', 1, 'Tradicional', 12),
       (18, 'G', 2, 'Tradicional', 12),
       (19, 'P', 1, 'Platino Individual', 13),
       (20, 'P', 2, 'Platino Individual', 13),
       (23, 'A', 1, 'Normal', 8),
       (24, 'A', 2, 'Normal', 8),
       (25, 'A', 3, 'Normal', 8),
       (26, 'B', 1, 'Normal', 8),
       (27, 'B', 2, 'Normal', 8),
       (28, 'B', 3, 'Normal', 8),
       (29, 'C', 1, 'Normal', 8),
       (30, 'C', 2, 'Normal', 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `boleto`
--

CREATE TABLE `boleto`
(
    `IdBoleto`    int(11)        NOT NULL,
    `PrecioFinal` decimal(10, 2) NOT NULL,
    `FechaCompra` datetime       NOT NULL DEFAULT current_timestamp(),
    `CodigoQR`    varchar(200)            DEFAULT NULL,
    `IdFuncion`   int(11)                 DEFAULT NULL,
    `IdCliente`   int(11)                 DEFAULT NULL,
    `IdAsiento`   int(11)                 DEFAULT NULL,
    `IdVenta`     int(11)                 DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `boleto`
--

INSERT INTO `boleto` (`IdBoleto`, `PrecioFinal`, `FechaCompra`, `CodigoQR`, `IdFuncion`, `IdCliente`, `IdAsiento`,
                      `IdVenta`)
VALUES (1, 85.00, '2025-06-01 09:05:00', 'QR12345', 1, 1, 1, 1),
       (2, 65.00, '2025-06-01 09:05:00', 'QR12346', 1, 1, 2, 1),
       (3, 120.00, '2025-06-01 10:05:00', 'QR12347', 2, 2, 7, 2),
       (4, 150.00, '2025-06-02 06:42:47', 'QR-DC4B56AB-EC7', 9, NULL, 11, 4),
       (5, 150.00, '2025-06-02 06:42:47', 'QR-767D6C1F-A68', 9, NULL, 12, 4),
       (6, 80.00, '2025-06-02 06:47:41', 'QR-1A241DD4-A45', 10, NULL, 25, 5),
       (7, 80.00, '2025-06-02 06:47:41', 'QR-844C906B-0E7', 10, NULL, 26, 5),
       (8, 80.00, '2025-06-02 06:58:50', 'QR-28D458C9-B68', 10, NULL, 24, 6),
       (9, 80.00, '2025-06-02 06:58:50', 'QR-F01D66EC-FA3', 10, NULL, 23, 6),
       (10, 80.00, '2025-06-02 06:59:14', 'QR-96849D3B-EB0', 10, NULL, 27, 7),
       (11, 80.00, '2025-06-02 07:00:05', 'QR-84E65A51-1EF', 10, NULL, 29, 8),
       (12, 80.00, '2025-06-02 07:16:49', 'QR-05594CEA-0C0', 10, NULL, 30, 9),
       (13, 75.00, '2025-06-02 07:23:01', 'QR-67DDA8C6-B04', 7, NULL, 10, 10),
       (14, 75.00, '2025-06-02 07:23:53', 'QR-EB1AA2D6-B38', 7, 10, 11, 11),
       (15, 75.00, '2025-06-02 07:23:53', 'QR-CE222466-73A', 7, 10, 12, 11);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `calificacionpelicula`
--

CREATE TABLE `calificacionpelicula`
(
    `IdCalificacion`    int(11)       NOT NULL,
    `IdPelicula`        int(11)       NOT NULL,
    `IdCliente`         int(11)       NOT NULL,
    `Calificacion`      decimal(2, 1) NOT NULL CHECK (`Calificacion` >= 0.0 and `Calificacion` <= 5.0),
    `Comentario`        text                   DEFAULT NULL,
    `FechaCalificacion` datetime      NOT NULL DEFAULT current_timestamp()
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `calificacionpelicula`
--

INSERT INTO `calificacionpelicula` (`IdCalificacion`, `IdPelicula`, `IdCliente`, `Calificacion`, `Comentario`,
                                    `FechaCalificacion`)
VALUES (1, 1, 1, 4.5, 'Excelente película, gran dirección y actuaciones. ¡Un clásico moderno!', '2025-06-01 10:00:00'),
       (2, 1, 2, 4.0, 'Muy buena, aunque un poco larga para mi gusto. Buenas actuaciones.', '2025-06-01 11:00:00'),
       (3, 2, 1, 4.8, 'Una obra maestra de la ciencia ficción, visualmente impresionante.', '2025-06-01 10:15:00'),
       (4, 2, 3, 4.7, 'Me dejó pensando por días. Increíble!', '2025-06-01 11:15:00'),
       (5, 3, 4, 4.9, 'La mejor película de Spider-Man hasta ahora. La animación es genial!', '2025-06-01 10:30:00'),
       (6, 3, 5, 5.0, 'Absolutamente espectacular, superó mis expectativas.', '2025-06-01 11:30:00'),
       (7, 4, 6, 3.5, 'Divertida y con un mensaje. No es para todos pero me gustó.', '2025-06-01 10:45:00'),
       (8, 4, 7, 3.8, 'Más profunda de lo que parece. Ryan Gosling genial.', '2025-06-01 11:45:00'),
       (9, 5, 8, 4.9, 'Épica en todos los sentidos. Villeneuve es un genio.', '2025-06-01 12:00:00'),
       (10, 5, 9, 4.8, 'Visualmente impresionante, una experiencia inmersiva.', '2025-06-01 12:15:00'),
       (11, 6, 1, 4.7, 'Inteligente y con un giro inesperado. Muy recomendable.', '2025-06-01 12:30:00'),
       (12, 6, 2, 4.6, 'Me atrapó de principio a fin. Gran historia.', '2025-06-01 12:45:00'),
       (13, 7, 3, 4.5, 'Intensa y muy bien actuada. Merece todos los premios.', '2025-06-01 13:00:00'),
       (14, 7, 4, 4.4, 'Un biopic fascinante, aunque denso por momentos.', '2025-06-01 13:15:00'),
       (15, 8, 5, 4.2, 'Una secuela muy divertida. El gato sigue siendo carismático.', '2025-06-01 13:30:00'),
       (16, 8, 6, 4.1, 'Animación excelente y una buena historia para todas las edades.', '2025-06-01 13:45:00'),
       (17, 9, 7, 3.9, 'Tierna y mágica, aunque no tan impactante como la original.', '2025-06-01 14:00:00'),
       (18, 9, 8, 3.7, 'Divertida para pasar el rato, pero un poco lenta.', '2025-06-01 14:15:00'),
       (19, 10, 9, 4.3, 'Preciosa película, la música es fantástica.', '2025-06-01 14:30:00'),
       (20, 10, 1, 4.2, 'Un musical que te hace soñar.', '2025-06-01 14:45:00'),
       (21, 11, 2, 5.0, 'Una locura brillante. De lo mejor que he visto en años.', '2025-06-01 15:00:00'),
       (22, 11, 3, 4.9, 'Original, divertida y emotiva. Michelle Yeoh es una diosa.', '2025-06-01 15:15:00'),
       (23, 12, 4, 5.0, 'El Padrino es la perfección cinematográfica.', '2025-06-01 15:30:00'),
       (24, 12, 5, 5.0, 'Obra maestra. Punto.', '2025-06-01 15:45:00'),
       (25, 13, 6, 4.9, 'Coco es pura magia. Una película para el alma.', '2025-06-01 16:00:00'),
       (26, 13, 7, 4.8, 'Preciosa y emotiva. Un 10 en animación y guion.', '2025-06-01 16:15:00'),
       (27, 14, 8, 4.7, 'Matrix redefinió el cine de ciencia ficción. Imprescindible.', '2025-06-01 16:30:00'),
       (28, 14, 9, 4.6, 'Me voló la cabeza en su día. Sigue siendo relevante.', '2025-06-01 16:45:00'),
       (29, 15, 1, 4.5, 'Toy Story es un clásico. Inocente y divertida.', '2025-06-01 17:00:00'),
       (30, 15, 2, 4.4, 'Andy y sus juguetes son lo mejor. Gran historia.', '2025-06-01 17:15:00'),
       (31, 16, 3, 5.0, 'Forrest Gump te toca el corazón. Un clásico absoluto.', '2025-06-01 17:30:00'),
       (32, 16, 4, 4.9, 'La vida es como una caja de bombones...', '2025-06-01 17:45:00'),
       (33, 17, 5, 4.8, 'El inicio de una aventura épica. Un mundo increíble.', '2025-06-01 18:00:00'),
       (34, 17, 6, 4.7, 'Magnífica adaptación. Gandalf es el mejor.', '2025-06-01 18:15:00'),
       (35, 18, 7, 4.6, 'Un clásico de culto. Diálogos geniales.', '2025-06-01 18:30:00'),
       (36, 18, 8, 4.5, 'Pulp Fiction es única. Tarantinazo puro.', '2025-06-01 18:45:00'),
       (37, 19, 9, 4.7, 'Visualmente deslumbrante. Una secuela digna del original.', '2025-06-01 19:00:00'),
       (38, 19, 1, 4.8, 'Un viaje a otro mundo. Denis Villeneuve es el maestro.', '2025-06-01 19:15:00'),
       (39, 20, 2, 4.9, 'Emocionado por el re-estreno de Interestelar. Una joya.', '2025-06-01 19:30:00');

--
-- Disparadores `calificacionpelicula`
--
DELIMITER $$
CREATE TRIGGER `after_calificacion_delete`
    AFTER DELETE
    ON `calificacionpelicula`
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
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_calificacion_insert`
    AFTER INSERT
    ON `calificacionpelicula`
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
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `after_calificacion_update`
    AFTER UPDATE
    ON `calificacionpelicula`
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
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoriaproducto`
--

CREATE TABLE `categoriaproducto`
(
    `IdCategoria` int(11)     NOT NULL,
    `Nombre`      varchar(50) NOT NULL,
    `Descripcion` text DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `categoriaproducto`
--

INSERT INTO `categoriaproducto` (`IdCategoria`, `Nombre`, `Descripcion`)
VALUES (1, 'Bebidas', 'Refrescos, aguas, jugos, cafés.'),
       (2, 'Palomitas', 'Sabores variados.'),
       (3, 'Dulces y Chocolates', 'Golosinas.'),
       (4, 'Snacks Calientes', 'Nachos, hot dogs.'),
       (5, 'Combos', 'Paquetes promocionales.'),
       (6, 'Helados', 'Helados y paletas.');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cine`
--

CREATE TABLE `cine`
(
    `IdCine`       int(11)      NOT NULL,
    `Nombre`       varchar(100) NOT NULL,
    `Direccion`    varchar(200) NOT NULL,
    `Ciudad`       varchar(100) NOT NULL,
    `Estado`       varchar(100) NOT NULL,
    `CodigoPostal` varchar(10)  NOT NULL,
    `Telefono`     varchar(20)  NOT NULL,
    `HoraApertura` time         NOT NULL,
    `HoraCierre`   time         NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cine`
--

INSERT INTO `cine` (`IdCine`, `Nombre`, `Direccion`, `Ciudad`, `Estado`, `CodigoPostal`, `Telefono`, `HoraApertura`,
                    `HoraCierre`)
VALUES (1, 'Cinépolis Las Américas', 'Av. las Américas 1500, Plaza Las Américas', 'Morelia', 'Michoacán', '58270',
        '4431234567', '10:00:00', '23:59:00'),
       (2, 'Cinemex Parque Delta', 'Av. Cuauhtémoc 462, Narvarte Poniente', 'Ciudad de México', 'CDMX', '03020',
        '5598765432', '11:00:00', '01:00:00'),
       (3, 'Cinépolis Galerías Monterrey', 'Av. Insurgentes 2500, Vista Hermosa', 'Monterrey', 'Nuevo León', '64620',
        '8181234567', '10:30:00', '00:30:00'),
       (4, 'Cinemex Plaza Patria', 'Av. Patria 1950, Zapopan', 'Guadalajara', 'Jalisco', '45160', '3331234567',
        '11:30:00', '01:30:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cliente`
--

CREATE TABLE `cliente`
(
    `IdCliente`       int(11)      NOT NULL,
    `Nombre`          varchar(100) NOT NULL,
    `Apellido`        varchar(100) NOT NULL,
    `Email`           varchar(100) NOT NULL,
    `ContrasenaHash`  varchar(255) NOT NULL,
    `Telefono`        varchar(20)  NOT NULL,
    `FechaNacimiento` date         NOT NULL,
    `FechaRegistro`   datetime     NOT NULL DEFAULT current_timestamp(),
    `IdMembresia`     int(11)               DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `cliente`
--

INSERT INTO `cliente` (`IdCliente`, `Nombre`, `Apellido`, `Email`, `ContrasenaHash`, `Telefono`, `FechaNacimiento`,
                       `FechaRegistro`, `IdMembresia`)
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
        '8119876500', '2003-06-20', '2025-04-22 12:10:00', NULL),
       (8, 'María', 'Rodríguez', 'maria.rodriguez@test.com',
        '$2a$10$P3Qz2dhyD6oXJB3GZq7kbeY3hwAPONGvXpu6VE.jQ6.RUQ3U23fi.', '555-987-6543', '1990-08-15',
        '2024-03-15 10:30:00', 1),
       (9, 'TestUser', 'ApellidoTest', 'test@example.com',
        '$2a$10$WBG3inTA6ziNo7YUpMlXrukrHkeU7SLvOq/Zmaud98jfEQvJnLNty', 'N/A', '2000-01-01', '2025-05-30 00:58:15',
        NULL),
       (10, 'test', '', 'test@ejemplo.com', '$2a$10$yNFC8Z3xjb8Tu9aAu5LUt.B7zK69G45PcPwvT.7fC7JJC1Gz/mANu', 'N/A',
        '2000-01-01', '2025-06-02 06:43:25', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `detalleventa`
--

CREATE TABLE `detalleventa`
(
    `IdDetalleVenta` int(11)        NOT NULL,
    `Cantidad`       int(11)        NOT NULL,
    `PrecioUnitario` decimal(10, 2) NOT NULL,
    `Subtotal`       decimal(10, 2) NOT NULL,
    `IdVenta`        int(11) DEFAULT NULL,
    `IdProducto`     int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `detalleventa`
--

INSERT INTO `detalleventa` (`IdDetalleVenta`, `Cantidad`, `PrecioUnitario`, `Subtotal`, `IdVenta`, `IdProducto`)
VALUES (1, 1, 70.00, 70.00, 1, 2),
       (2, 1, 45.00, 45.00, 1, 1),
       (3, 2, 65.00, 130.00, 2, 3),
       (4, 2, 45.00, 90.00, 2, 1),
       (5, 1, 70.00, 70.00, 3, 2),
       (6, 3, 45.00, 135.00, 4, 1),
       (7, 3, 70.00, 210.00, 4, 2),
       (8, 4, 45.00, 180.00, 11, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `director`
--

CREATE TABLE `director`
(
    `IdDirector`      int(11)      NOT NULL,
    `Nombre`          varchar(100) NOT NULL,
    `Nacionalidad`    varchar(100) NOT NULL,
    `FechaNacimiento` date DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `director`
--

INSERT INTO `director` (`IdDirector`, `Nombre`, `Nacionalidad`, `FechaNacimiento`)
VALUES (1, 'Guillermo del Toro', 'Mexicana', '1964-10-09'),
       (2, 'Alfonso Cuarón', 'Mexicana', '1961-11-28'),
       (3, 'Christopher Nolan', 'Británico-Estadounidense', '1970-07-30'),
       (4, 'Greta Gerwig', 'Estadounidense', '1983-08-04'),
       (5, 'Denis Villeneuve', 'Canadiense', '1967-10-03'),
       (6, 'Bong Joon-ho', 'Surcoreano', '1969-09-14');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `empleado`
--

CREATE TABLE `empleado`
(
    `IdEmpleado`        int(11)        NOT NULL,
    `Nombre`            varchar(100)   NOT NULL,
    `Apellido`          varchar(100)   NOT NULL,
    `Puesto`            varchar(50)    NOT NULL,
    `FechaContratacion` date           NOT NULL,
    `Salario`           decimal(10, 2) NOT NULL,
    `Estado`            varchar(20)    NOT NULL,
    `IdCine`            int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `empleado`
--

INSERT INTO `empleado` (`IdEmpleado`, `Nombre`, `Apellido`, `Puesto`, `FechaContratacion`, `Salario`, `Estado`,
                        `IdCine`)
VALUES (1, 'Juan', 'Rodríguez', 'Taquillero', '2023-05-10', 6500.00, 'Activo', 1),
       (2, 'Ana', 'Martínez', 'Gerente de Sucursal', '2022-01-20', 15000.00, 'Activo', 1),
       (3, 'Pedro', 'Gómez', 'Staff Dulcería', '2024-02-01', 6000.00, 'Activo', 2),
       (4, 'Laura', 'Sánchez', 'Staff Dulcería', '2024-03-01', 6200.00, 'Activo', 1),
       (5, 'Miguel', 'Hernández', 'Proyeccionista', '2023-08-15', 8000.00, 'Activo', 2),
       (6, 'Claudia', 'Vargas', 'Gerente', '2023-11-01', 16000.00, 'Activo', 3),
       (7, 'Roberto', 'Díaz', 'Taquillero', '2024-01-10', 6800.00, 'Activo', 4),
       (8, 'Fernanda', 'Castillo', 'Limpieza', '2023-09-01', 5500.00, 'Inactivo', 2);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estudio`
--

CREATE TABLE `estudio`
(
    `IdEstudio`   int(11)      NOT NULL,
    `Nombre`      varchar(100) NOT NULL,
    `Pais`        varchar(100) NOT NULL,
    `Descripcion` text DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `estudio`
--

INSERT INTO `estudio` (`IdEstudio`, `Nombre`, `Pais`, `Descripcion`)
VALUES (1, 'Warner Bros. Pictures', 'Estados Unidos', 'Productora y distribuidora de cine y televisión.'),
       (2, 'Universal Pictures', 'Estados Unidos', 'Filial de NBCUniversal.'),
       (3, 'Videocine', 'México', 'Empresa mexicana de producción y distribución.'),
       (6, 'A24', 'Estados Unidos', 'Estudio de cine independiente.');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `funcion`
--

CREATE TABLE `funcion`
(
    `IdFuncion`  int(11)        NOT NULL,
    `FechaHora`  datetime       NOT NULL,
    `Precio`     decimal(10, 2) NOT NULL,
    `Estado`     varchar(20)    NOT NULL,
    `IdPelicula` int(11) DEFAULT NULL,
    `IdSala`     int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `funcion`
--

INSERT INTO `funcion` (`IdFuncion`, `FechaHora`, `Precio`, `Estado`, `IdPelicula`, `IdSala`)
VALUES (1, '2025-06-02 14:00:00', 85.00, 'En Venta', 1, 1),
       (2, '2025-06-02 17:00:00', 120.00, 'En Venta', 2, 2),
       (3, '2025-06-02 20:00:00', 90.00, 'En Venta', 12, 5),
       (4, '2025-06-03 15:30:00', 85.00, 'En Venta', 1, 1),
       (5, '2025-06-03 18:30:00', 120.00, 'En Venta', 16, 2),
       (6, '2025-06-04 16:00:00', 90.00, 'En Venta', 4, 5),
       (7, '2025-06-02 13:00:00', 150.00, 'En Venta', 3, 3),
       (8, '2025-06-02 16:00:00', 80.00, 'En Venta', 13, 7),
       (9, '2025-06-02 22:00:00', 150.00, 'En Venta', 14, 3),
       (10, '2025-06-03 14:30:00', 80.00, 'En Venta', 8, 8),
       (11, '2025-06-03 17:30:00', 150.00, 'En Venta', 3, 3),
       (12, '2025-06-02 15:00:00', 160.00, 'En Venta', 5, 9),
       (13, '2025-06-02 19:00:00', 110.00, 'En Venta', 7, 10),
       (14, '2025-06-03 16:30:00', 160.00, 'En Venta', 19, 9),
       (15, '2025-06-03 20:30:00', 90.00, 'En Venta', 6, 10),
       (16, '2025-06-02 12:00:00', 90.00, 'En Venta', 9, 12),
       (17, '2025-06-02 18:00:00', 180.00, 'En Venta', 11, 13),
       (18, '2025-06-03 14:00:00', 90.00, 'En Venta', 10, 12),
       (19, '2025-06-03 21:00:00', 180.00, 'En Venta', 18, 13),
       (20, '2025-06-15 17:00:00', 95.00, 'Próximamente', 20, 3),
       (21, '2025-07-01 19:00:00', 100.00, 'Próximamente', 17, 1),
       (22, '2025-06-03 16:00:00', 80.00, 'En Venta', 8, 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `funcionasiento`
--

CREATE TABLE `funcionasiento`
(
    `IdFuncion`     int(11)     NOT NULL,
    `IdAsiento`     int(11)     NOT NULL,
    `EstadoAsiento` varchar(20) NOT NULL DEFAULT 'Disponible'
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `funcionasiento`
--

INSERT INTO `funcionasiento` (`IdFuncion`, `IdAsiento`, `EstadoAsiento`)
VALUES (1, 1, 'Ocupado'),
       (1, 2, 'Ocupado'),
       (1, 3, 'Disponible'),
       (1, 4, 'Disponible'),
       (1, 5, 'Disponible'),
       (1, 6, 'Disponible'),
       (2, 7, 'Ocupado'),
       (2, 8, 'Disponible'),
       (2, 9, 'Disponible'),
       (3, 13, 'Disponible'),
       (3, 14, 'Disponible'),
       (4, 1, 'Disponible'),
       (4, 2, 'Disponible'),
       (4, 3, 'Disponible'),
       (4, 4, 'Disponible'),
       (4, 5, 'Disponible'),
       (4, 6, 'Disponible'),
       (5, 7, 'Disponible'),
       (5, 8, 'Disponible'),
       (5, 9, 'Disponible'),
       (6, 13, 'Disponible'),
       (6, 14, 'Disponible'),
       (7, 10, 'Ocupado'),
       (7, 11, 'Ocupado'),
       (7, 12, 'Ocupado'),
       (9, 10, 'Ocupado'),
       (9, 11, 'Ocupado'),
       (9, 12, 'Ocupado'),
       (10, 23, 'Ocupado'),
       (10, 24, 'Ocupado'),
       (10, 25, 'Ocupado'),
       (10, 26, 'Ocupado'),
       (10, 27, 'Ocupado'),
       (10, 28, 'Ocupado'),
       (10, 29, 'Ocupado'),
       (10, 30, 'Ocupado'),
       (11, 10, 'Disponible'),
       (11, 11, 'Disponible'),
       (11, 12, 'Disponible'),
       (12, 15, 'Disponible'),
       (12, 16, 'Disponible'),
       (14, 15, 'Disponible'),
       (14, 16, 'Disponible'),
       (16, 17, 'Disponible'),
       (16, 18, 'Disponible'),
       (17, 19, 'Disponible'),
       (17, 20, 'Disponible'),
       (18, 17, 'Disponible'),
       (18, 18, 'Disponible'),
       (19, 19, 'Disponible'),
       (19, 20, 'Disponible'),
       (20, 10, 'Disponible'),
       (20, 11, 'Disponible'),
       (20, 12, 'Disponible'),
       (21, 1, 'Disponible'),
       (21, 2, 'Disponible'),
       (21, 3, 'Disponible'),
       (21, 4, 'Disponible'),
       (21, 5, 'Disponible'),
       (21, 6, 'Disponible'),
       (22, 23, 'Disponible'),
       (22, 24, 'Disponible'),
       (22, 25, 'Disponible'),
       (22, 26, 'Disponible'),
       (22, 27, 'Disponible'),
       (22, 28, 'Disponible'),
       (22, 29, 'Disponible'),
       (22, 30, 'Disponible');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `membresia`
--

CREATE TABLE `membresia`
(
    `IdMembresia`           int(11)        NOT NULL,
    `Tipo`                  varchar(50)    NOT NULL,
    `Descripcion`           text DEFAULT NULL,
    `Costo`                 decimal(10, 2) NOT NULL,
    `DuracionMeses`         int(11)        NOT NULL,
    `BeneficiosDescripcion` text DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `membresia`
--

INSERT INTO `membresia` (`IdMembresia`, `Tipo`, `Descripcion`, `Costo`, `DuracionMeses`, `BeneficiosDescripcion`)
VALUES (1, 'Fan', 'Nivel básico con algunos descuentos.', 100.00, 12, 'Acumula puntos, precio especial martes.'),
       (2, 'Fanático', 'Nivel intermedio con más beneficios.', 250.00, 12, 'Descuentos en dulcería, preventas.'),
       (3, 'Súper Fanático', 'Nivel premium con todos los beneficios.', 500.00, 12, 'Boletos gratis y combos.'),
       (4, 'Estudiante', 'Descuento para estudiantes.', 80.00, 6, 'Precio especial L-J.'),
       (5, 'Senior', 'Beneficios >60 años.', 70.00, 12, '10% en dulcería.');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pelicula`
--

CREATE TABLE `pelicula`
(
    `IdPelicula`           int(11)      NOT NULL,
    `Titulo`               varchar(150) NOT NULL,
    `Duracion`             int(11)      NOT NULL,
    `Sinopsis`             text          DEFAULT NULL,
    `FechaEstreno`         date         NOT NULL,
    `Clasificacion`        varchar(10)  NOT NULL,
    `Idioma`               varchar(50)  NOT NULL,
    `Subtitulada`          tinyint(1)    DEFAULT 0,
    `Fotografia`           varchar(255)  DEFAULT NULL,
    `Formato`              varchar(10)  NOT NULL,
    `Estado`               varchar(20)  NOT NULL,
    `IdEstudio`            int(11)       DEFAULT NULL,
    `IdDirector`           int(11)       DEFAULT NULL,
    `IdTipoPelicula`       int(11)       DEFAULT NULL,
    `CalificacionPromedio` decimal(3, 2) DEFAULT 0.00,
    `TotalCalificaciones`  int(11)       DEFAULT 0
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `pelicula`
--

INSERT INTO `pelicula` (`IdPelicula`, `Titulo`, `Duracion`, `Sinopsis`, `FechaEstreno`, `Clasificacion`, `Idioma`,
                        `Subtitulada`, `Fotografia`, `Formato`, `Estado`, `IdEstudio`, `IdDirector`, `IdTipoPelicula`,
                        `CalificacionPromedio`, `TotalCalificaciones`)
VALUES (1, 'Roma', 135, 'Crónica de vida familiar en los 70.', '2018-11-21', 'R', 'Español', 1, 'placeholder.jpg', '2D',
        'En Cartelera', 3, 2, 3, 4.25, 2),
       (2, 'Interestelar', 169, 'Exploradores viajan por un agujero de gusano.', '2014-11-07', 'PG-13', 'Inglés', 1,
        'placeholder.jpg', 'IMAX', 'En Cartelera', 1, 3, 4, 4.75, 2),
       (3, 'Spider-Man: A Través del Spider-Verso', 140, 'Miles Morales en el Multiverso.', '2023-06-02', 'PG',
        'Inglés', 1, 'placeholder.jpg', '2D', 'En Cartelera', 2, NULL, 5, 4.95, 2),
       (4, 'Barbie', 114, 'Crisis existencial de Barbie.', '2023-07-21', 'PG-13', 'Inglés', 1, 'placeholder.jpg', '2D',
        'En Cartelera', 1, 4, 2, 3.65, 2),
       (5, 'Dune: Parte Dos', 166, 'Paul Atreides entre los Fremen.', '2024-03-01', 'PG-13', 'Inglés', 1,
        'placeholder.jpg', 'IMAX', 'En Cartelera', 1, 5, 4, 4.85, 2),
       (6, 'Parásitos', 132, 'Familia pobre se infiltra en familia rica.', '2019-05-30', 'R', 'Coreano', 1,
        'placeholder.jpg', '2D', 'En Cartelera', NULL, 6, 3, 4.65, 2),
       (7, 'Oppenheimer', 180, 'Historia de J. Robert Oppenheimer.', '2023-07-21', 'R', 'Inglés', 1, 'placeholder.jpg',
        'IMAX', 'En Cartelera', 2, 3, 3, 4.45, 2),
       (8, 'El Gato con Botas: El Último Deseo', 102, 'Gato pierde vidas.', '2022-12-21', 'PG', 'Inglés', 1,
        'placeholder.jpg', '3D', 'En Cartelera', 2, NULL, 5, 4.15, 2),
       (9, 'Wonka', 116, 'Juventud de Willy Wonka.', '2023-12-15', 'PG', 'Inglés', 1, 'placeholder.jpg', '2D',
        'En Cartelera', 1, NULL, 7, 3.80, 2),
       (10, 'La La Land', 128, 'Amor en LA entre pianista y actriz.', '2016-12-09', 'PG-13', 'Inglés', 0,
        'placeholder.jpg', '2D', 'En Cartelera', 6, NULL, 7, 4.25, 2),
       (11, 'Todo en Todas Partes al Mismo Tiempo', 139, 'Inmigrante explora universos.', '2022-03-25', 'R', 'Inglés',
        1, 'placeholder.jpg', '2D', 'En Cartelera', 6, NULL, 1, 4.95, 2),
       (12, 'El Padrino', 175, 'La saga de la familia Corleone.', '1972-03-24', 'R', 'Inglés', 1, 'placeholder.jpg',
        '2D', 'En Cartelera', 2, NULL, 3, 5.00, 2),
       (13, 'Coco', 105, 'Un niño viaja a la Tierra de los Muertos.', '2017-10-27', 'PG', 'Español', 0,
        'placeholder.jpg', '2D', 'En Cartelera', 2, NULL, 5, 4.85, 2),
       (14, 'Matrix', 136, 'Un programador descubre la realidad simulada.', '1999-03-31', 'R', 'Inglés', 1,
        'placeholder.jpg', '2D', 'En Cartelera', 1, NULL, 1, 4.65, 2),
       (15, 'Toy Story', 81, 'Juguetes cobran vida.', '1995-11-22', 'G', 'Inglés', 0, 'placeholder.jpg', '2D',
        'En Cartelera', 2, NULL, 5, 4.45, 2),
       (16, 'Forrest Gump', 142, 'Vida de un hombre a través de la historia.', '1994-07-06', 'PG-13', 'Inglés', 1,
        'placeholder.jpg', '2D', 'En Cartelera', 2, NULL, 3, 4.95, 2),
       (17, 'El Señor de los Anillos: La Comunidad del Anillo', 178,
        'Un hobbit emprende una misión épica para destruir un anillo.', '2001-12-19', 'PG-13', 'Inglés', 1,
        'placeholder.jpg', '2D', 'En Cartelera', 1, NULL, 4, 4.75, 2),
       (18, 'Pulp Fiction', 154, 'Varias historias entrelazadas del mundo criminal.', '1994-10-14', 'R', 'Inglés', 1,
        'placeholder.jpg', '2D', 'En Cartelera', NULL, NULL, 1, 4.55, 2),
       (19, 'Blade Runner 2049', 164,
        'Un nuevo blade runner descubre un secreto que podría sumir a la sociedad en el caos.', '2017-10-06', 'R',
        'Inglés', 1, 'placeholder.jpg', 'IMAX', 'En Cartelera', 1, 5, 4, 4.75, 2),
       (20, 'Interestelar (Re-estreno)', 169, 'Versión re-editada y remasterizada.', '2024-11-07', 'PG-13', 'Inglés', 1,
        'placeholder.jpg', 'IMAX', 'Próximamente', 1, 3, 4, 4.90, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `peliculaactor`
--

CREATE TABLE `peliculaactor`
(
    `IdPelicula` int(11)      NOT NULL,
    `IdActor`    int(11)      NOT NULL,
    `Personaje`  varchar(100) NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `peliculaactor`
--

INSERT INTO `peliculaactor` (`IdPelicula`, `IdActor`, `Personaje`)
VALUES (1, 1, 'Cleodegaria \"Cleo\" Gutiérrez'),
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
       (11, 14, 'Waymond Wang'),
       (12, 10, 'Don Vito Corleone'),
       (13, 1, 'Mamá Coco (voz)'),
       (14, 2, 'Neo'),
       (15, 6, 'Woody (voz)'),
       (16, 2, 'Forrest Gump'),
       (17, 10, 'Gandalf'),
       (18, 5, 'Mia Wallace'),
       (19, 6, 'K');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto`
(
    `IdProducto`  int(11)        NOT NULL,
    `Nombre`      varchar(100)   NOT NULL,
    `Descripcion` text    DEFAULT NULL,
    `Precio`      decimal(10, 2) NOT NULL,
    `Stock`       int(11)        NOT NULL,
    `Estado`      varchar(20)    NOT NULL,
    `IdCategoria` int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`IdProducto`, `Nombre`, `Descripcion`, `Precio`, `Stock`, `Estado`, `IdCategoria`)
VALUES (1, 'Refresco Grande', '946ml', 45.00, 184, 'Disponible', 1),
       (2, 'Palomitas Grandes Mantequilla', 'Recien hechas', 70.00, 128, 'Disponible', 2),
       (3, 'Nachos con Queso y Jalapeños', 'Totopos con queso', 65.00, 84, 'Disponible', 4),
       (4, 'Combo Pareja', '2 Refrescos + Palomitas', 150.00, 46, 'Disponible', 5),
       (5, 'Agua Embotellada 600ml', 'Agua natural', 20.00, 298, 'Disponible', 1),
       (6, 'Chocolate Grande', 'Barra importada', 35.00, 80, 'Disponible', 3),
       (7, 'Palomitas Caramelizadas Chicas', 'Caramelo', 55.00, 70, 'Agotado', 2),
       (8, 'Hot Dog Clásico', 'Salchicha de pavo', 50.00, 90, 'Disponible', 4),
       (9, 'Combo Nachos', 'Nachos + Refresco mediano', 100.00, 58, 'Disponible', 5),
       (10, 'Helado de Vainilla (vaso)', 'Helado cremoso', 40.00, 50, 'Disponible', 6);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `promocion`
--

CREATE TABLE `promocion`
(
    `IdPromocion` int(11)       NOT NULL,
    `Nombre`      varchar(100)  NOT NULL,
    `Descripcion` text DEFAULT NULL,
    `FechaInicio` date          NOT NULL,
    `FechaFin`    date          NOT NULL,
    `Descuento`   decimal(5, 2) NOT NULL,
    `CodigoPromo` varchar(20)   NOT NULL,
    `Estado`      varchar(20)   NOT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `promocion`
--

INSERT INTO `promocion` (`IdPromocion`, `Nombre`, `Descripcion`, `FechaInicio`, `FechaFin`, `Descuento`, `CodigoPromo`,
                         `Estado`)
VALUES (1, 'Martes 2x1', 'Boletos 2x1 martes', '2025-01-01', '2025-12-31', 0.50, 'MARTES2X1', 'Activa'),
       (2, 'Combo Estreno', '10% en combo estreno', '2025-05-01', '2025-07-31', 0.10, 'COMBOESTRENO10', 'Activa'),
       (3, 'Miércoles de Estreno', '20% en estreno miércoles', '2025-02-01', '2025-11-30', 0.20, 'MIERCOLES20',
        'Activa'),
       (4, 'Combo Familiar', '15% en dulcería familiar', '2025-04-01', '2025-06-30', 0.15, 'FAMILIA15', 'Activa'),
       (5, 'Promo Verano Pasado', 'Descuento verano 2024', '2024-07-01', '2024-08-31', 0.25, 'VERANO24', 'Expirada');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `sala`
--

CREATE TABLE `sala`
(
    `IdSala`    int(11)     NOT NULL,
    `Numero`    int(11)     NOT NULL,
    `Capacidad` int(11)     NOT NULL,
    `TipoSala`  varchar(20) NOT NULL,
    `Estado`    varchar(20) NOT NULL,
    `IdCine`    int(11) DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `sala`
--

INSERT INTO `sala` (`IdSala`, `Numero`, `Capacidad`, `TipoSala`, `Estado`, `IdCine`)
VALUES (1, 1, 120, 'Tradicional', 'Disponible', 1),
       (2, 2, 80, 'VIP', 'Disponible', 1),
       (3, 1, 200, 'IMAX', 'Disponible', 2),
       (4, 5, 100, 'Tradicional', 'Mantenimiento', 2),
       (5, 3, 150, 'Tradicional', 'Disponible', 1),
       (6, 4, 90, 'VIP', 'Disponible', 1),
       (7, 2, 180, 'Tradicional', 'Disponible', 2),
       (8, 6, 70, 'Junior', 'Disponible', 2),
       (9, 1, 160, 'IMAX', 'Disponible', 3),
       (10, 2, 100, 'VIP', 'Disponible', 3),
       (11, 3, 120, 'Tradicional', 'Mantenimiento', 3),
       (12, 1, 140, 'Tradicional', 'Disponible', 4),
       (13, 2, 60, 'Platino', 'Disponible', 4);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipopelicula`
--

CREATE TABLE `tipopelicula`
(
    `IdTipoPelicula` int(11)     NOT NULL,
    `Nombre`         varchar(50) NOT NULL,
    `Descripcion`    text DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tipopelicula`
--

INSERT INTO `tipopelicula` (`IdTipoPelicula`, `Nombre`, `Descripcion`)
VALUES (1, 'Acción', 'Secuencias de riesgo y efectos especiales.'),
       (2, 'Comedia', 'Provoquen risa y entretenimiento.'),
       (3, 'Drama', 'Conflictos emocionales y desarrollo.'),
       (4, 'Ciencia Ficción', 'Tecnología y viajes espaciales.'),
       (5, 'Animación', 'Técnicas de animación.'),
       (6, 'Terror', 'Provocar miedo y tensión.'),
       (7, 'Musical', 'Canciones y bailes.'),
       (8, 'Documental', 'Basadas en hechos reales.');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `venta`
--

CREATE TABLE `venta`
(
    `IdVenta`     int(11)        NOT NULL,
    `Fecha`       datetime       NOT NULL DEFAULT current_timestamp(),
    `IdCliente`   int(11)                 DEFAULT NULL,
    `Total`       decimal(10, 2) NOT NULL,
    `MetodoPago`  varchar(50)    NOT NULL,
    `Estado`      varchar(20)    NOT NULL,
    `Facturado`   tinyint(1)              DEFAULT 0,
    `IdEmpleado`  int(11)                 DEFAULT NULL,
    `IdPromocion` int(11)                 DEFAULT NULL
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `venta`
--

INSERT INTO `venta` (`IdVenta`, `Fecha`, `IdCliente`, `Total`, `MetodoPago`, `Estado`, `Facturado`, `IdEmpleado`,
                     `IdPromocion`)
VALUES (1, '2025-06-01 09:00:00', 1, 150.00, 'Tarjeta', 'Completado', 0, 1, NULL),
       (2, '2025-06-01 10:00:00', 2, 200.00, 'Efectivo', 'Completado', 0, 3, 2),
       (3, '2025-06-01 11:00:00', NULL, 95.00, 'Efectivo', 'Completado', 0, 1, NULL),
       (4, '2025-06-02 06:42:47', NULL, 645.00, 'Efectivo', 'Completado', 0, NULL, NULL),
       (5, '2025-06-02 06:47:41', NULL, 160.00, 'Efectivo', 'Completado', 0, NULL, NULL),
       (6, '2025-06-02 06:58:50', NULL, 160.00, 'Efectivo', 'Completado', 0, NULL, NULL),
       (7, '2025-06-02 06:59:14', NULL, 80.00, 'Efectivo', 'Completado', 0, NULL, NULL),
       (8, '2025-06-02 07:00:05', NULL, 80.00, 'Efectivo', 'Completado', 0, NULL, NULL),
       (9, '2025-06-02 07:16:49', NULL, 80.00, 'Efectivo', 'Completado', 0, NULL, NULL),
       (10, '2025-06-02 07:23:01', NULL, 75.00, 'Efectivo', 'Completado', 0, NULL, 1),
       (11, '2025-06-02 07:23:53', 10, 240.00, 'Efectivo', 'Completado', 0, NULL, 1);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `actor`
--
ALTER TABLE `actor`
    ADD PRIMARY KEY (`IdActor`);

--
-- Indices de la tabla `asiento`
--
ALTER TABLE `asiento`
    ADD PRIMARY KEY (`IdAsiento`),
    ADD KEY `IdSala` (`IdSala`);

--
-- Indices de la tabla `boleto`
--
ALTER TABLE `boleto`
    ADD PRIMARY KEY (`IdBoleto`),
    ADD KEY `IdFuncion` (`IdFuncion`),
    ADD KEY `IdCliente` (`IdCliente`),
    ADD KEY `IdAsiento` (`IdAsiento`),
    ADD KEY `IdVenta` (`IdVenta`);

--
-- Indices de la tabla `calificacionpelicula`
--
ALTER TABLE `calificacionpelicula`
    ADD PRIMARY KEY (`IdCalificacion`),
    ADD UNIQUE KEY `unique_cliente_pelicula` (`IdCliente`, `IdPelicula`),
    ADD KEY `IdPelicula` (`IdPelicula`);

--
-- Indices de la tabla `categoriaproducto`
--
ALTER TABLE `categoriaproducto`
    ADD PRIMARY KEY (`IdCategoria`);

--
-- Indices de la tabla `cine`
--
ALTER TABLE `cine`
    ADD PRIMARY KEY (`IdCine`);

--
-- Indices de la tabla `cliente`
--
ALTER TABLE `cliente`
    ADD PRIMARY KEY (`IdCliente`),
    ADD UNIQUE KEY `Email` (`Email`),
    ADD KEY `IdMembresia` (`IdMembresia`);

--
-- Indices de la tabla `detalleventa`
--
ALTER TABLE `detalleventa`
    ADD PRIMARY KEY (`IdDetalleVenta`),
    ADD KEY `IdVenta` (`IdVenta`),
    ADD KEY `IdProducto` (`IdProducto`);

--
-- Indices de la tabla `director`
--
ALTER TABLE `director`
    ADD PRIMARY KEY (`IdDirector`);

--
-- Indices de la tabla `empleado`
--
ALTER TABLE `empleado`
    ADD PRIMARY KEY (`IdEmpleado`),
    ADD KEY `IdCine` (`IdCine`);

--
-- Indices de la tabla `estudio`
--
ALTER TABLE `estudio`
    ADD PRIMARY KEY (`IdEstudio`);

--
-- Indices de la tabla `funcion`
--
ALTER TABLE `funcion`
    ADD PRIMARY KEY (`IdFuncion`),
    ADD KEY `IdPelicula` (`IdPelicula`),
    ADD KEY `IdSala` (`IdSala`);

--
-- Indices de la tabla `funcionasiento`
--
ALTER TABLE `funcionasiento`
    ADD PRIMARY KEY (`IdFuncion`, `IdAsiento`),
    ADD KEY `IdAsiento` (`IdAsiento`);

--
-- Indices de la tabla `membresia`
--
ALTER TABLE `membresia`
    ADD PRIMARY KEY (`IdMembresia`);

--
-- Indices de la tabla `pelicula`
--
ALTER TABLE `pelicula`
    ADD PRIMARY KEY (`IdPelicula`),
    ADD KEY `IdEstudio` (`IdEstudio`),
    ADD KEY `IdDirector` (`IdDirector`),
    ADD KEY `IdTipoPelicula` (`IdTipoPelicula`);

--
-- Indices de la tabla `peliculaactor`
--
ALTER TABLE `peliculaactor`
    ADD PRIMARY KEY (`IdPelicula`, `IdActor`),
    ADD KEY `IdActor` (`IdActor`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
    ADD PRIMARY KEY (`IdProducto`),
    ADD KEY `IdCategoria` (`IdCategoria`);

--
-- Indices de la tabla `promocion`
--
ALTER TABLE `promocion`
    ADD PRIMARY KEY (`IdPromocion`),
    ADD UNIQUE KEY `CodigoPromo` (`CodigoPromo`);

--
-- Indices de la tabla `sala`
--
ALTER TABLE `sala`
    ADD PRIMARY KEY (`IdSala`),
    ADD KEY `IdCine` (`IdCine`);

--
-- Indices de la tabla `tipopelicula`
--
ALTER TABLE `tipopelicula`
    ADD PRIMARY KEY (`IdTipoPelicula`),
    ADD UNIQUE KEY `Nombre` (`Nombre`);

--
-- Indices de la tabla `venta`
--
ALTER TABLE `venta`
    ADD PRIMARY KEY (`IdVenta`),
    ADD KEY `IdEmpleado` (`IdEmpleado`),
    ADD KEY `IdPromocion` (`IdPromocion`),
    ADD KEY `FK_Venta_Cliente` (`IdCliente`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `actor`
--
ALTER TABLE `actor`
    MODIFY `IdActor` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 15;

--
-- AUTO_INCREMENT de la tabla `asiento`
--
ALTER TABLE `asiento`
    MODIFY `IdAsiento` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 31;

--
-- AUTO_INCREMENT de la tabla `boleto`
--
ALTER TABLE `boleto`
    MODIFY `IdBoleto` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 16;

--
-- AUTO_INCREMENT de la tabla `calificacionpelicula`
--
ALTER TABLE `calificacionpelicula`
    MODIFY `IdCalificacion` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 40;

--
-- AUTO_INCREMENT de la tabla `categoriaproducto`
--
ALTER TABLE `categoriaproducto`
    MODIFY `IdCategoria` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 7;

--
-- AUTO_INCREMENT de la tabla `cine`
--
ALTER TABLE `cine`
    MODIFY `IdCine` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 5;

--
-- AUTO_INCREMENT de la tabla `cliente`
--
ALTER TABLE `cliente`
    MODIFY `IdCliente` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 11;

--
-- AUTO_INCREMENT de la tabla `detalleventa`
--
ALTER TABLE `detalleventa`
    MODIFY `IdDetalleVenta` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 9;

--
-- AUTO_INCREMENT de la tabla `director`
--
ALTER TABLE `director`
    MODIFY `IdDirector` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 7;

--
-- AUTO_INCREMENT de la tabla `empleado`
--
ALTER TABLE `empleado`
    MODIFY `IdEmpleado` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 9;

--
-- AUTO_INCREMENT de la tabla `estudio`
--
ALTER TABLE `estudio`
    MODIFY `IdEstudio` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 7;

--
-- AUTO_INCREMENT de la tabla `funcion`
--
ALTER TABLE `funcion`
    MODIFY `IdFuncion` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 23;

--
-- AUTO_INCREMENT de la tabla `membresia`
--
ALTER TABLE `membresia`
    MODIFY `IdMembresia` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 6;

--
-- AUTO_INCREMENT de la tabla `pelicula`
--
ALTER TABLE `pelicula`
    MODIFY `IdPelicula` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 21;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
    MODIFY `IdProducto` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 11;

--
-- AUTO_INCREMENT de la tabla `promocion`
--
ALTER TABLE `promocion`
    MODIFY `IdPromocion` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 6;

--
-- AUTO_INCREMENT de la tabla `sala`
--
ALTER TABLE `sala`
    MODIFY `IdSala` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 14;

--
-- AUTO_INCREMENT de la tabla `tipopelicula`
--
ALTER TABLE `tipopelicula`
    MODIFY `IdTipoPelicula` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 9;

--
-- AUTO_INCREMENT de la tabla `venta`
--
ALTER TABLE `venta`
    MODIFY `IdVenta` int(11) NOT NULL AUTO_INCREMENT,
    AUTO_INCREMENT = 12;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `asiento`
--
ALTER TABLE `asiento`
    ADD CONSTRAINT `asiento_ibfk_1` FOREIGN KEY (`IdSala`) REFERENCES `sala` (`IdSala`) ON DELETE CASCADE;

--
-- Filtros para la tabla `boleto`
--
ALTER TABLE `boleto`
    ADD CONSTRAINT `boleto_ibfk_1` FOREIGN KEY (`IdFuncion`) REFERENCES `funcion` (`IdFuncion`) ON DELETE CASCADE,
    ADD CONSTRAINT `boleto_ibfk_2` FOREIGN KEY (`IdCliente`) REFERENCES `cliente` (`IdCliente`) ON DELETE SET NULL,
    ADD CONSTRAINT `boleto_ibfk_3` FOREIGN KEY (`IdAsiento`) REFERENCES `asiento` (`IdAsiento`) ON DELETE SET NULL,
    ADD CONSTRAINT `boleto_ibfk_4` FOREIGN KEY (`IdVenta`) REFERENCES `venta` (`IdVenta`) ON DELETE CASCADE;

--
-- Filtros para la tabla `calificacionpelicula`
--
ALTER TABLE `calificacionpelicula`
    ADD CONSTRAINT `calificacionpelicula_ibfk_1` FOREIGN KEY (`IdPelicula`) REFERENCES `pelicula` (`IdPelicula`) ON DELETE CASCADE,
    ADD CONSTRAINT `calificacionpelicula_ibfk_2` FOREIGN KEY (`IdCliente`) REFERENCES `cliente` (`IdCliente`) ON DELETE CASCADE;

--
-- Filtros para la tabla `cliente`
--
ALTER TABLE `cliente`
    ADD CONSTRAINT `cliente_ibfk_1` FOREIGN KEY (`IdMembresia`) REFERENCES `membresia` (`IdMembresia`);

--
-- Filtros para la tabla `detalleventa`
--
ALTER TABLE `detalleventa`
    ADD CONSTRAINT `detalleventa_ibfk_1` FOREIGN KEY (`IdVenta`) REFERENCES `venta` (`IdVenta`) ON DELETE CASCADE,
    ADD CONSTRAINT `detalleventa_ibfk_2` FOREIGN KEY (`IdProducto`) REFERENCES `producto` (`IdProducto`);

--
-- Filtros para la tabla `empleado`
--
ALTER TABLE `empleado`
    ADD CONSTRAINT `empleado_ibfk_1` FOREIGN KEY (`IdCine`) REFERENCES `cine` (`IdCine`);

--
-- Filtros para la tabla `funcion`
--
ALTER TABLE `funcion`
    ADD CONSTRAINT `funcion_ibfk_1` FOREIGN KEY (`IdPelicula`) REFERENCES `pelicula` (`IdPelicula`) ON DELETE CASCADE,
    ADD CONSTRAINT `funcion_ibfk_2` FOREIGN KEY (`IdSala`) REFERENCES `sala` (`IdSala`) ON DELETE CASCADE;

--
-- Filtros para la tabla `funcionasiento`
--
ALTER TABLE `funcionasiento`
    ADD CONSTRAINT `funcionasiento_ibfk_1` FOREIGN KEY (`IdFuncion`) REFERENCES `funcion` (`IdFuncion`) ON DELETE CASCADE,
    ADD CONSTRAINT `funcionasiento_ibfk_2` FOREIGN KEY (`IdAsiento`) REFERENCES `asiento` (`IdAsiento`) ON DELETE CASCADE;

--
-- Filtros para la tabla `pelicula`
--
ALTER TABLE `pelicula`
    ADD CONSTRAINT `pelicula_ibfk_1` FOREIGN KEY (`IdEstudio`) REFERENCES `estudio` (`IdEstudio`),
    ADD CONSTRAINT `pelicula_ibfk_2` FOREIGN KEY (`IdDirector`) REFERENCES `director` (`IdDirector`),
    ADD CONSTRAINT `pelicula_ibfk_3` FOREIGN KEY (`IdTipoPelicula`) REFERENCES `tipopelicula` (`IdTipoPelicula`);

--
-- Filtros para la tabla `peliculaactor`
--
ALTER TABLE `peliculaactor`
    ADD CONSTRAINT `peliculaactor_ibfk_1` FOREIGN KEY (`IdPelicula`) REFERENCES `pelicula` (`IdPelicula`) ON DELETE CASCADE,
    ADD CONSTRAINT `peliculaactor_ibfk_2` FOREIGN KEY (`IdActor`) REFERENCES `actor` (`IdActor`) ON DELETE CASCADE;

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
    ADD CONSTRAINT `producto_ibfk_1` FOREIGN KEY (`IdCategoria`) REFERENCES `categoriaproducto` (`IdCategoria`);

--
-- Filtros para la tabla `sala`
--
ALTER TABLE `sala`
    ADD CONSTRAINT `sala_ibfk_1` FOREIGN KEY (`IdCine`) REFERENCES `cine` (`IdCine`) ON DELETE CASCADE;

--
-- Filtros para la tabla `venta`
--
ALTER TABLE `venta`
    ADD CONSTRAINT `FK_Venta_Cliente` FOREIGN KEY (`IdCliente`) REFERENCES `cliente` (`IdCliente`) ON DELETE SET NULL,
    ADD CONSTRAINT `venta_ibfk_1` FOREIGN KEY (`IdEmpleado`) REFERENCES `empleado` (`IdEmpleado`),
    ADD CONSTRAINT `venta_ibfk_2` FOREIGN KEY (`IdPromocion`) REFERENCES `promocion` (`IdPromocion`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
