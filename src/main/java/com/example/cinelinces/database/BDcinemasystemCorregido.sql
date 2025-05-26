DROP DATABASE IF EXISTS CinemaSystem;

CREATE DATABASE IF NOT EXISTS CinemaSystem;
USE CinemaSystem;

-- Tabla Cine
CREATE TABLE Cine (
    IdCine INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Direccion VARCHAR(200) NOT NULL,
    Ciudad VARCHAR(100) NOT NULL,
    Estado VARCHAR(100) NOT NULL,
    CodigoPostal VARCHAR(10) NOT NULL,
    Telefono VARCHAR(20) NOT NULL,
    HoraApertura TIME NOT NULL,
    HoraCierre TIME NOT NULL
);

-- Tabla Estudio
CREATE TABLE Estudio (
    IdEstudio INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Pais VARCHAR(100) NOT NULL,
    Descripcion TEXT
);

-- Tabla Director
CREATE TABLE Director (
    IdDirector INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Nacionalidad VARCHAR(100) NOT NULL,
    FechaNacimiento DATE
);

-- Tabla TipoPelicula (Añadida por el usuario)
CREATE TABLE TipoPelicula (
    IdTipoPelicula INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL UNIQUE,
    Descripcion TEXT
);

-- Tabla Pelicula (Modificada para incluir Fotografia e IdTipoPelicula)
CREATE TABLE Pelicula (
    IdPelicula INT AUTO_INCREMENT PRIMARY KEY,
    Titulo VARCHAR(150) NOT NULL,
    Duracion INT NOT NULL, -- Duración en minutos
    Sinopsis TEXT,
    FechaEstreno DATE NOT NULL,
    Clasificacion VARCHAR(10) NOT NULL, -- Ej: PG-13, R, G
    Idioma VARCHAR(50) NOT NULL,
    Subtitulada BOOLEAN DEFAULT FALSE,
    Fotografia VARCHAR(255), -- URL o ruta a la imagen (Añadido por el usuario)
    Formato VARCHAR(10) NOT NULL, -- Ej: 2D, 3D, IMAX
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Estreno', 'En cartelera', 'Proximamente'
    IdEstudio INT,
    IdDirector INT,
    IdTipoPelicula INT, -- (Añadido por el usuario)
    FOREIGN KEY (IdEstudio) REFERENCES Estudio(IdEstudio),
    FOREIGN KEY (IdDirector) REFERENCES Director(IdDirector),
    FOREIGN KEY (IdTipoPelicula) REFERENCES TipoPelicula(IdTipoPelicula) -- (Añadido por el usuario)
);

-- Tabla Actor
CREATE TABLE Actor (
    IdActor INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Nacionalidad VARCHAR(100) NOT NULL,
    FechaNacimiento DATE
);

-- Tabla PeliculaActor (Relación muchos a muchos)
CREATE TABLE PeliculaActor (
    IdPelicula INT,
    IdActor INT,
    Personaje VARCHAR(100) NOT NULL,
    PRIMARY KEY (IdPelicula, IdActor),
    FOREIGN KEY (IdPelicula) REFERENCES Pelicula(IdPelicula),
    FOREIGN KEY (IdActor) REFERENCES Actor(IdActor)
);

-- Tabla Sala
CREATE TABLE Sala (
    IdSala INT AUTO_INCREMENT PRIMARY KEY,
    Numero INT NOT NULL,
    Capacidad INT NOT NULL,
    TipoSala VARCHAR(20) NOT NULL, -- Ej: 'Normal', 'VIP', '3D'
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Disponible', 'Mantenimiento'
    IdCine INT,
    FOREIGN KEY (IdCine) REFERENCES Cine(IdCine)
);

-- Tabla Asiento
CREATE TABLE Asiento (
    IdAsiento INT AUTO_INCREMENT PRIMARY KEY,
    Fila CHAR(2) NOT NULL,
    Numero INT NOT NULL,
    TipoAsiento VARCHAR(20) NOT NULL, -- Ej: 'Normal', 'Preferencial', 'Doble'
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Disponible', 'Reparacion', 'Bloqueado'
    IdSala INT,
    FOREIGN KEY (IdSala) REFERENCES Sala(IdSala)
);

-- Tabla Funcion
CREATE TABLE Funcion (
    IdFuncion INT AUTO_INCREMENT PRIMARY KEY,
    FechaHora DATETIME NOT NULL,
    Precio DECIMAL(10,2) NOT NULL,
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Programada', 'En Venta', 'Cancelada'
    IdPelicula INT,
    IdSala INT,
    FOREIGN KEY (IdPelicula) REFERENCES Pelicula(IdPelicula),
    FOREIGN KEY (IdSala) REFERENCES Sala(IdSala)
);

-- Tabla Membresia
CREATE TABLE Membresia (
    IdMembresia INT AUTO_INCREMENT PRIMARY KEY,
    Tipo VARCHAR(50) NOT NULL,
    Descripcion TEXT,
    Costo DECIMAL(10,2) NOT NULL,
    DuracionMeses INT NOT NULL,
    BeneficiosDescripcion TEXT
);

-- Tabla Cliente
CREATE TABLE Cliente (
    IdCliente INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Telefono VARCHAR(20) NOT NULL,
    FechaNacimiento DATE NOT NULL,
    FechaRegistro DATETIME NOT NULL,
    IdMembresia INT,
    FOREIGN KEY (IdMembresia) REFERENCES Membresia(IdMembresia)
);

-- Tabla Empleado
CREATE TABLE Empleado (
    IdEmpleado INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Puesto VARCHAR(50) NOT NULL,
    FechaContratacion DATE NOT NULL,
    Salario DECIMAL(10,2) NOT NULL,
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Activo', 'Inactivo'
    IdCine INT,
    FOREIGN KEY (IdCine) REFERENCES Cine(IdCine)
);

-- Tabla Promocion
CREATE TABLE Promocion (
    IdPromocion INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    FechaInicio DATE NOT NULL,
    FechaFin DATE NOT NULL,
    Descuento DECIMAL(5,2) NOT NULL, -- Podría ser % (ej: 0.10 para 10%) o un monto fijo.
    CodigoPromo VARCHAR(20) UNIQUE NOT NULL,
    Estado VARCHAR(20) NOT NULL -- Ej: 'Activa', 'Inactiva', 'Expirada'
);

-- Tabla Venta (Modificada para incluir IdPromocion)
CREATE TABLE Venta (
    IdVenta INT AUTO_INCREMENT PRIMARY KEY,
    Fecha DATETIME NOT NULL,
    Total DECIMAL(10,2) NOT NULL,
    MetodoPago VARCHAR(50) NOT NULL,
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Completada', 'Cancelada'
    Facturado BOOLEAN DEFAULT FALSE,
    IdEmpleado INT,
    IdPromocion INT, -- Columna sugerida para registrar la promoción aplicada
    FOREIGN KEY (IdEmpleado) REFERENCES Empleado(IdEmpleado),
    FOREIGN KEY (IdPromocion) REFERENCES Promocion(IdPromocion) -- FK para la promoción
);

-- Tabla Boleto
CREATE TABLE Boleto (
    IdBoleto INT AUTO_INCREMENT PRIMARY KEY,
    PrecioFinal DECIMAL(10,2) NOT NULL, -- Precio después de cualquier descuento
    FechaCompra DATETIME NOT NULL,
    CodigoQR VARCHAR(200), -- Podría ser un hash o identificador único para el QR
    IdFuncion INT,
    IdCliente INT, -- Puede ser NULL si la venta es anónima
    IdAsiento INT,
    IdVenta INT,
    FOREIGN KEY (IdFuncion) REFERENCES Funcion(IdFuncion),
    FOREIGN KEY (IdCliente) REFERENCES Cliente(IdCliente),
    FOREIGN KEY (IdAsiento) REFERENCES Asiento(IdAsiento),
    FOREIGN KEY (IdVenta) REFERENCES Venta(IdVenta)
);

-- Tabla CategoriaProducto
CREATE TABLE CategoriaProducto (
    IdCategoria INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Descripcion TEXT
);

-- Tabla Producto (Ej: Palomitas, Refrescos)
CREATE TABLE Producto (
    IdProducto INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    Precio DECIMAL(10,2) NOT NULL,
    Stock INT NOT NULL,
    Estado VARCHAR(20) NOT NULL, -- Ej: 'Disponible', 'Agotado'
    IdCategoria INT,
    FOREIGN KEY (IdCategoria) REFERENCES CategoriaProducto(IdCategoria)
);

-- Tabla DetalleVenta (Para productos vendidos en una Venta)
CREATE TABLE DetalleVenta (
    IdDetalleVenta INT AUTO_INCREMENT PRIMARY KEY,
    Cantidad INT NOT NULL,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2) NOT NULL, -- Cantidad * PrecioUnitario
    IdVenta INT,
    IdProducto INT,
    FOREIGN KEY (IdVenta) REFERENCES Venta(IdVenta),
    FOREIGN KEY (IdProducto) REFERENCES Producto(IdProducto)
);

-- Asegúrate de estar usando la base de datos correcta
USE CinemaSystem;

-- Para manejar caracteres especiales como acentos correctamente
SET NAMES utf8mb4;

-- Desactivar temporalmente la revisión de claves foráneas puede ser útil si el orden no es perfecto,
-- aunque intentaré mantener el orden correcto. Si estás seguro del orden, no es estrictamente necesario.
-- SET FOREIGN_KEY_CHECKS=0;

-- Tabla: Cine
INSERT INTO Cine (IdCine, Nombre, Direccion, Ciudad, Estado, CodigoPostal, Telefono, HoraApertura, HoraCierre) VALUES
(1, 'Cinépolis Las Américas', 'Av. las Américas 1500, Plaza Las Américas', 'Morelia', 'Michoacán', '58270', '4431234567', '10:00:00', '23:59:00'),
(2, 'Cinemex Parque Delta', 'Av. Cuauhtémoc 462, Narvarte Poniente', 'Ciudad de México', 'CDMX', '03020', '5598765432', '11:00:00', '01:00:00');

-- Tabla: Estudio
INSERT INTO Estudio (IdEstudio, Nombre, Pais, Descripcion) VALUES
(1, 'Warner Bros. Pictures', 'Estados Unidos', 'Productora y distribuidora de cine y televisión.'),
(2, 'Universal Pictures', 'Estados Unidos', 'Estudio cinematográfico estadounidense, filial de NBCUniversal.'),
(3, 'Videocine', 'México', 'Empresa mexicana de producción y distribución de películas.');

-- Tabla: Director
INSERT INTO Director (IdDirector, Nombre, Nacionalidad, FechaNacimiento) VALUES
(1, 'Guillermo del Toro', 'Mexicana', '1964-10-09'),
(2, 'Alfonso Cuarón', 'Mexicana', '1961-11-28'),
(3, 'Christopher Nolan', 'Británico-Estadounidense', '1970-07-30');

-- Tabla: TipoPelicula
INSERT INTO TipoPelicula (IdTipoPelicula, Nombre, Descripcion) VALUES
(1, 'Acción', 'Películas con secuencias de riesgo, persecuciones y efectos especiales.'),
(2, 'Comedia', 'Películas diseñadas para provocar risa y entretenimiento.'),
(3, 'Drama', 'Películas enfocadas en conflictos emocionales y desarrollo de personajes.'),
(4, 'Ciencia Ficción', 'Películas que exploran conceptos futuristas, tecnología y viajes espaciales.'),
(5, 'Animación', 'Películas creadas principalmente mediante técnicas de animación.');

-- Tabla: Pelicula
INSERT INTO Pelicula (IdPelicula, Titulo, Duracion, Sinopsis, FechaEstreno, Clasificacion, Idioma, Subtitulada, Fotografia, Formato, Estado, IdEstudio, IdDirector, IdTipoPelicula) VALUES
(1, 'Roma', 135, 'Crónica de un año turbulento en la vida de una familia de clase media en la Ciudad de México de los años 70.', '2018-11-21', 'R', 'Español', TRUE, 'posters/roma.jpg', '2D', 'En Cartelera', 3, 2, 3),
(2, 'Interestelar', 169, 'Un equipo de exploradores viaja a través de un agujero de gusano en el espacio en un intento por asegurar la supervivencia de la humanidad.', '2014-11-07', 'PG-13', 'Inglés', TRUE, 'posters/interestelar.jpg', 'IMAX', 'En Cartelera', 1, 3, 4),
(3, 'Spider-Man: A Través del Spider-Verso', 140, 'Miles Morales se embarca en una aventura épica que transportará al simpático Spiderman de Brooklyn a través del Multiverso.', '2023-06-02', 'PG', 'Inglés', TRUE, 'posters/spiderman_spiderverse.jpg', '2D', 'Estreno', 2, NULL, 5); -- Director podría ser un equipo, aquí NULL como ejemplo

-- Tabla: Actor
INSERT INTO Actor (IdActor, Nombre, Nacionalidad, FechaNacimiento) VALUES
(1, 'Yalitza Aparicio', 'Mexicana', '1993-12-11'),
(2, 'Matthew McConaughey', 'Estadounidense', '1969-11-04'),
(3, 'Anne Hathaway', 'Estadounidense', '1982-11-12'),
(4, 'Shameik Moore', 'Estadounidense', '1995-05-04');

-- Tabla: PeliculaActor
INSERT INTO PeliculaActor (IdPelicula, IdActor, Personaje) VALUES
(1, 1, 'Cleodegaria "Cleo" Gutiérrez'),
(2, 2, 'Joseph Cooper'),
(2, 3, 'Amelia Brand'),
(3, 4, 'Miles Morales (voz)');

-- Tabla: Sala
INSERT INTO Sala (IdSala, Numero, Capacidad, TipoSala, Estado, IdCine) VALUES
(1, 1, 120, 'Tradicional', 'Disponible', 1),
(2, 2, 80, 'VIP', 'Disponible', 1),
(3, 1, 200, 'IMAX', 'Disponible', 2),
(4, 5, 100, 'Tradicional', 'Mantenimiento', 2);

-- Tabla: Asiento (Ejemplos para Sala 1 y Sala 3)
-- Sala 1 (IdSala=1)
INSERT INTO Asiento (IdAsiento, Fila, Numero, TipoAsiento, Estado, IdSala) VALUES
(1, 'A', 1, 'Normal', 'Disponible', 1),
(2, 'A', 2, 'Normal', 'Disponible', 1),
(3, 'F', 5, 'Preferencial', 'Disponible', 1),
(4, 'F', 6, 'Preferencial', 'Disponible', 1);
-- Sala 3 (IdSala=3)
INSERT INTO Asiento (IdAsiento, Fila, Numero, TipoAsiento, Estado, IdSala) VALUES
(5, 'C', 10, 'IMAX Normal', 'Disponible', 3),
(6, 'C', 11, 'IMAX Normal', 'Disponible', 3),
(7, 'H', 7, 'IMAX Preferente', 'Disponible', 3);

-- Tabla: Funcion (Usando fechas y horas actuales o futuras como ejemplo)
INSERT INTO Funcion (IdFuncion, FechaHora, Precio, Estado, IdPelicula, IdSala) VALUES
(1, '2025-05-25 16:00:00', 85.00, 'En Venta', 1, 1), -- Roma en Cinépolis Las Américas, Sala 1
(2, '2025-05-25 19:00:00', 120.00, 'En Venta', 2, 3), -- Interestelar en Cinemex Parque Delta, Sala IMAX
(3, '2025-05-26 17:30:00', 90.00, 'Programada', 3, 1), -- Spider-Man en Cinépolis Las Américas, Sala 1
(4, '2025-05-25 20:30:00', 85.00, 'En Venta', 1, 1); -- Roma, otra función

-- Tabla: Membresia
INSERT INTO Membresia (IdMembresia, Tipo, Descripcion, Costo, DuracionMeses, BeneficiosDescripcion) VALUES
(1, 'Fan', 'Nivel básico con algunos descuentos.', 100.00, 12, 'Acumula puntos, precio especial martes.'),
(2, 'Fanático', 'Nivel intermedio con más beneficios.', 250.00, 12, 'Acumula más puntos, descuentos en dulcería, preventas seleccionadas.'),
(3, 'Súper Fanático', 'Nivel premium con todos los beneficios.', 500.00, 12, 'Boletos gratis al mes, acceso a todas las preventas, combos especiales.');

-- Tabla: Cliente
INSERT INTO Cliente (IdCliente, Nombre, Apellido, Email, Telefono, FechaNacimiento, FechaRegistro, IdMembresia) VALUES
(1, 'Mariana', 'López', 'mariana.lopez@example.com', '4435551122', '1995-08-23', '2024-03-15 10:00:00', 2),
(2, 'Carlos', 'Fuentes', 'carlos.f@example.com', '5551112233', '1988-02-10', '2025-01-20 14:30:00', NULL),
(3, 'Sofía', 'Hernández', 'sofia.hdz@example.com', '8112345678', '2001-11-05', '2023-11-01 18:00:00', 3);

-- Tabla: Empleado
INSERT INTO Empleado (IdEmpleado, Nombre, Apellido, Puesto, FechaContratacion, Salario, Estado, IdCine) VALUES
(1, 'Juan', 'Rodríguez', 'Taquillero', '2023-05-10', 6500.00, 'Activo', 1),
(2, 'Ana', 'Martínez', 'Gerente de Sucursal', '2022-01-20', 15000.00, 'Activo', 1),
(3, 'Pedro', 'Gómez', 'Staff Dulcería', '2024-02-01', 6000.00, 'Activo', 2);

-- Tabla: Promocion
INSERT INTO Promocion (IdPromocion, Nombre, Descripcion, FechaInicio, FechaFin, Descuento, CodigoPromo, Estado) VALUES
(1, 'Martes 2x1', 'Todos los martes boletos al 2x1 en funciones seleccionadas.', '2025-01-01', '2025-12-31', 0.50, 'MARTES2X1', 'Activa'), -- 50% de descuento
(2, 'Combo Estreno', '10% de descuento en combo grande al comprar boleto para estreno.', '2025-05-01', '2025-07-31', 0.10, 'COMBOESTRENO10', 'Activa'); -- 10% de descuento

-- Tabla: Venta
INSERT INTO Venta (IdVenta, Fecha, Total, MetodoPago, Estado, Facturado, IdEmpleado, IdPromocion) VALUES
(1, '2025-05-25 15:45:00', 170.00, 'Tarjeta', 'Completada', FALSE, 1, NULL), -- 2 boletos para Roma (2*85)
(2, '2025-05-25 18:30:00', 108.00, 'Efectivo', 'Completada', FALSE, 3, 2); -- 1 boleto Interestelar (120) con promoción del 10% sobre ese boleto -> 108

-- Actualización de Venta 2 para que el total cuadre con una promoción de 10% sobre un boleto de 120
-- Si la promoción es del 10% sobre el boleto, el precio del boleto es 108. Total de la venta es 108 si solo es un boleto.
-- Si la promoción es sobre un combo, el IdPromocion debería aplicarse diferente o a un detalle de venta.
-- Asumamos que la promoción IdPromocion=2 se aplica al total de la venta que incluye un boleto de estreno.
-- Venta 2 (Total: 1 Boleto de 120 + Combo con 10% desc). Para simplificar, dejemos que la promo afectó el boleto.
-- Venta 2 (IdVenta=2), Precio Boleto Interestelar=120. Si IdPromocion=2 (10% desc), entonces el boleto costó 108.
-- Si la venta es SÓLO ese boleto, Total=108. Si hay más items, el Total aumentaría.

-- Tabla: Boleto
INSERT INTO Boleto (IdBoleto, PrecioFinal, FechaCompra, CodigoQR, IdFuncion, IdCliente, IdAsiento, IdVenta) VALUES
(1, 85.00, '2025-05-25 15:45:00', 'QR20250525001A1F1', 1, 1, 1, 1), -- Boleto 1 Venta 1 (Mariana, Roma, Asiento A1)
(2, 85.00, '2025-05-25 15:45:00', 'QR20250525001A2F1', 1, 2, 1, 1), -- Boleto 2 Venta 1 (Carlos, Roma, Asiento A2)
(3, 108.00, '2025-05-25 18:30:00', 'QR20250525001C10F2', 2, NULL, 5, 2); -- Boleto Venta 2 (Anónimo, Interestelar, Asiento C10 con descuento)

-- Tabla: CategoriaProducto
INSERT INTO CategoriaProducto (IdCategoria, Nombre, Descripcion) VALUES
(1, 'Bebidas', 'Refrescos, aguas, jugos, cafés.'),
(2, 'Palomitas', 'Diferentes tamaños y sabores: mantequilla, acarameladas, light.'),
(3, 'Dulces y Chocolates', 'Variedad de golosinas.'),
(4, 'Snacks Calientes', 'Nachos, hot dogs, crepas.'),
(5, 'Combos', 'Paquetes promocionales de alimentos y bebidas.');

-- Tabla: Producto
INSERT INTO Producto (IdProducto, Nombre, Descripcion, Precio, Stock, Estado, IdCategoria) VALUES
(1, 'Refresco Grande', 'Refresco de máquina tamaño grande (946ml).', 45.00, 200, 'Disponible', 1),
(2, 'Palomitas Grandes Mantequilla', 'Palomitas recién hechas con mantequilla, tamaño grande.', 70.00, 150, 'Disponible', 2),
(3, 'Nachos con Queso y Jalapeños', 'Totopos de maíz bañados en queso cheddar tibio y rodajas de jalapeño.', 65.00, 100, 'Disponible', 4),
(4, 'Combo Pareja', '2 Refrescos Grandes + 1 Palomitas Grandes Mantequilla.', 150.00, 50, 'Disponible', 5); -- Precio del combo

-- Tabla: DetalleVenta (Productos asociados a una venta)
-- Venta 1 (IdVenta=1) no tuvo productos de dulcería en este ejemplo.
-- Venta 2 (IdVenta=2) le vamos a añadir un combo, y ajustamos el total de la VENTA.
-- Boleto Venta 2 (IdBoleto=3) fue de 108.00
-- Añadimos un Combo Pareja (IdProducto=4) de 150.00 a la Venta 2.
-- Nuevo Total para Venta 2 = 108.00 (boleto) + 150.00 (combo) = 258.00
UPDATE Venta SET Total = 258.00 WHERE IdVenta = 2;

INSERT INTO DetalleVenta (IdDetalleVenta, Cantidad, PrecioUnitario, Subtotal, IdVenta, IdProducto) VALUES
(1, 1, 150.00, 150.00, 2, 4); -- 1 Combo Pareja en Venta 2

-- Otra venta solo de dulcería
INSERT INTO Venta (IdVenta, Fecha, Total, MetodoPago, Estado, Facturado, IdEmpleado, IdPromocion) VALUES
(3, '2025-05-25 20:00:00', 115.00, 'Efectivo', 'Completada', TRUE, 3, NULL);

INSERT INTO DetalleVenta (IdDetalleVenta, Cantidad, PrecioUnitario, Subtotal, IdVenta, IdProducto) VALUES
(2, 1, 70.00, 70.00, 3, 2), -- 1 Palomitas Grandes Mantequilla
(3, 1, 45.00, 45.00, 3, 1); -- 1 Refresco Grande


-- Reactivar la revisión de claves foráneas si se desactivó
-- SET FOREIGN_KEY_CHECKS=1;