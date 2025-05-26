-- Asegúrate de estar usando la base de datos correcta
USE CinemaSystem;

-- Para manejar caracteres especiales como acentos correctamente
SET NAMES utf8mb4;

-- Desactivar temporalmente la revisión de claves foráneas puede ser útil
-- SET FOREIGN_KEY_CHECKS=0;

-- Add more Cines
INSERT INTO Cine (IdCine, Nombre, Direccion, Ciudad, Estado, CodigoPostal, Telefono, HoraApertura, HoraCierre) VALUES
(3, 'Cinépolis Centro Magno', 'Av. Vallarta 2425, Arcos Vallarta', 'Guadalajara', 'Jalisco', '44130', '3336157777', '10:30:00', '00:30:00'),
(4, 'Cinemex Paseo San Pedro', 'Av. José Vasconcelos 402, Del Valle', 'San Pedro Garza García', 'Nuevo León', '66220', '8183351234', '11:00:00', '00:00:00');

-- Add more Estudios
INSERT INTO Estudio (IdEstudio, Nombre, Pais, Descripcion) VALUES
(4, 'Paramount Pictures', 'Estados Unidos', 'Uno de los estudios más antiguos de Hollywood.'),
(5, 'Sony Pictures', 'Estados Unidos', 'Parte de Sony Entertainment Inc.'),
(6, 'A24', 'Estados Unidos', 'Conocido por películas independientes y aclamadas por la crítica.');

-- Add more Directores
INSERT INTO Director (IdDirector, Nombre, Nacionalidad, FechaNacimiento) VALUES
(4, 'Greta Gerwig', 'Estadounidense', '1983-08-04'),
(5, 'Denis Villeneuve', 'Canadiense', '1967-10-03'),
(6, 'Bong Joon-ho', 'Surcoreano', '1969-09-14');

-- Add more TiposPelicula (Assuming the existing ones are sufficient for now, but you could add 'Terror', 'Musical', 'Documental' etc.)
-- Example:
INSERT INTO TipoPelicula (IdTipoPelicula, Nombre, Descripcion) VALUES
(6, 'Terror', 'Películas diseñadas para provocar miedo, tensión y horror.'),
(7, 'Musical', 'Películas donde las canciones y bailes son parte fundamental de la narrativa.'),
(8, 'Documental', 'Películas basadas en hechos reales con propósito informativo o educativo.');


-- Add more Peliculas
INSERT INTO Pelicula (IdPelicula, Titulo, Duracion, Sinopsis, FechaEstreno, Clasificacion, Idioma, Subtitulada, Fotografia, Formato, Estado, IdEstudio, IdDirector, IdTipoPelicula) VALUES
(4, 'Barbie', 114, 'Barbie sufre una crisis que la lleva a cuestionarse su mundo y su existencia.', '2023-07-21', 'PG-13', 'Inglés', TRUE, 'posters/barbie.jpg', '2D', 'En Cartelera', 1, 4, 2),
(5, 'Dune: Parte Dos', 166, 'Paul Atreides se une a los Fremen y comienza un viaje espiritual y marcial para convertirse en Muad''Dib.', '2024-03-01', 'PG-13', 'Inglés', TRUE, 'posters/dune2.jpg', 'IMAX', 'Estreno', 1, 5, 4),
(6, 'Parásitos', 132, 'Una familia pobre se infiltra ingeniosamente en la vida de una familia rica con consecuencias imprevistas.', '2019-05-30', 'R', 'Coreano', TRUE, 'posters/parasitos.jpg', '2D', 'En Cartelera', NULL, 6, 3), -- Assuming independent or smaller studio not listed
(7, 'Oppenheimer', 180, 'La historia del científico J. Robert Oppenheimer y su papel en el desarrollo de la bomba atómica.', '2023-07-21', 'R', 'Inglés', TRUE, 'posters/oppenheimer.jpg', 'IMAX', 'En Cartelera', 2, 3, 3),
(8, 'El Gato con Botas: El Último Deseo', 102, 'El Gato con Botas descubre que su pasión por la aventura le ha pasado factura: ha consumido ocho de sus nueve vidas.', '2022-12-21', 'PG', 'Inglés', TRUE, 'posters/gato_con_botas2.jpg', '3D', 'En Cartelera', 2, NULL, 5),
(9, 'Wonka', 116, 'La historia de cómo Willy Wonka pasó de ser un joven inventor a un excéntrico chocolatero.', '2023-12-15', 'PG', 'Inglés', TRUE, 'posters/wonka.jpg', '2D', 'En Cartelera', 1, NULL, 7),
(10, 'La La Land', 128, 'Un pianista de jazz y una aspirante a actriz se enamoran mientras persiguen sus sueños en Los Ángeles.', '2016-12-09', 'PG-13', 'Inglés', FALSE, 'posters/lalaland.jpg', '2D', 'En Cartelera', 6, NULL, 7),
(11, 'Todo en Todas Partes al Mismo Tiempo', 139, 'Una inmigrante china se ve envuelta en una aventura salvaje en la que solo ella puede salvar el mundo explorando otros universos.', '2022-03-25', 'R', 'Inglés', TRUE, 'posters/everything_everywhere.jpg', '2D', 'En Cartelera', 6, NULL, 1);


-- Add more Actores
INSERT INTO Actor (IdActor, Nombre, Nacionalidad, FechaNacimiento) VALUES
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


-- Add more PeliculaActor
INSERT INTO PeliculaActor (IdPelicula, IdActor, Personaje) VALUES
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

-- Add more Salas
INSERT INTO Sala (IdSala, Numero, Capacidad, TipoSala, Estado, IdCine) VALUES
(5, 3, 150, 'Tradicional', 'Disponible', 1), -- Cinépolis Las Américas
(6, 4, 90, 'VIP', 'Disponible', 1),         -- Cinépolis Las Américas
(7, 2, 180, 'Tradicional', 'Disponible', 2), -- Cinemex Parque Delta
(8, 6, 70, 'Junior', 'Disponible', 2),      -- Cinemex Parque Delta
(9, 1, 160, 'IMAX', 'Disponible', 3),       -- Cinépolis Centro Magno
(10, 2, 100, 'VIP', 'Disponible', 3),       -- Cinépolis Centro Magno
(11, 3, 120, 'Tradicional', 'Mantenimiento', 3), -- Cinépolis Centro Magno
(12, 1, 140, 'Tradicional', 'Disponible', 4), -- Cinemex Paseo San Pedro
(13, 2, 60, 'Platino', 'Disponible', 4);    -- Cinemex Paseo San Pedro

-- Add more Asientos (Sample for new Salas)
-- Sala 5 (IdSala=5, Cinepolis Las Americas)
INSERT INTO Asiento (IdAsiento, Fila, Numero, TipoAsiento, Estado, IdSala) VALUES
(8, 'A', 1, 'Normal', 'Disponible', 5),
(9, 'A', 2, 'Normal', 'Disponible', 5),
(10, 'G', 8, 'Preferencial', 'Reparacion', 5);
-- Sala 9 (IdSala=9, Cinepolis Centro Magno, IMAX)
INSERT INTO Asiento (IdAsiento, Fila, Numero, TipoAsiento, Estado, IdSala) VALUES
(11, 'B', 5, 'IMAX Normal', 'Disponible', 9),
(12, 'B', 6, 'IMAX Normal', 'Disponible', 9),
(13, 'J', 10, 'IMAX Preferente', 'Bloqueado', 9);
-- Sala 13 (IdSala=13, Cinemex Paseo San Pedro, Platino)
INSERT INTO Asiento (IdAsiento, Fila, Numero, TipoAsiento, Estado, IdSala) VALUES
(14, 'A', 1, 'Platino Individual', 'Disponible', 13),
(15, 'A', 2, 'Platino Individual', 'Disponible', 13),
(16, 'C', 3, 'Platino Doble', 'Disponible', 13);


-- Add more Funciones
INSERT INTO Funcion (IdFuncion, FechaHora, Precio, Estado, IdPelicula, IdSala) VALUES
(5, '2025-05-26 18:00:00', 95.00, 'En Venta', 4, 5),    -- Barbie, Cinépolis Las Américas, Sala Tradicional
(6, '2025-05-26 21:00:00', 150.00, 'En Venta', 5, 9),   -- Dune 2, Cinépolis Centro Magno, Sala IMAX
(7, '2025-05-27 15:00:00', 80.00, 'Programada', 6, 7),  -- Parásitos, Cinemex Parque Delta, Sala Tradicional
(8, '2025-05-24 20:00:00', 130.00, 'Pasada', 7, 3),     -- Oppenheimer, Cinemex Parque Delta, Sala IMAX (Función pasada)
(9, '2025-05-28 16:30:00', 100.00, 'En Venta', 8, 6),   -- Gato con Botas, Cinépolis Las Américas, Sala VIP
(10, '2025-05-29 19:45:00', 90.00, 'En Venta', 9, 12),  -- Wonka, Cinemex Paseo San Pedro, Sala Tradicional
(11, '2025-05-20 17:00:00', 75.00, 'Cancelada', 1, 2),  -- Roma, Cinépolis Las Américas, Sala VIP (Cancelada)
(12, '2025-06-01 20:15:00', 160.00, 'Próximamente en Venta', 5, 3), -- Dune 2, otra función en Cinemex Parque Delta
(13, '2025-05-27 17:00:00', 110.00, 'En Venta', 10, 10), -- La La Land, Cinépolis Centro Magno, Sala VIP
(14, '2025-05-28 20:30:00', 95.00, 'En Venta', 11, 5);   -- Todo en Todas Partes, Cinépolis Las Américas, Sala Tradicional


-- Add more Membresias
INSERT INTO Membresia (IdMembresia, Tipo, Descripcion, Costo, DuracionMeses, BeneficiosDescripcion) VALUES
(4, 'Estudiante', 'Descuentos especiales para estudiantes con credencial vigente.', 80.00, 6, 'Precio especial de lunes a jueves, descuento en combo individual.'),
(5, 'Senior', 'Beneficios para adultos mayores de 60 años.', 70.00, 12, 'Precio especial todos los días, 10% en dulcería.');

-- Add more Clientes
INSERT INTO Cliente (IdCliente, Nombre, Apellido, Email, Telefono, FechaNacimiento, FechaRegistro, IdMembresia) VALUES
(4, 'Luis', 'Ramírez', 'luis.ram@example.com', '3312345678', '1999-04-15', '2025-02-10 11:00:00', 4),
(5, 'Ana', 'García', 'ana.garcia@example.com', '5567890123', '1960-07-30', '2024-12-01 09:30:00', 5),
(6, 'Jorge', 'Torres', 'jorge.t@example.com', '4429876543', '1985-12-01', '2025-03-01 17:45:00', 1),
(7, 'Laura', 'Pérez', 'laura.p@example.com', '8119876500', '2003-06-20', '2025-04-22 12:10:00', NULL);


-- Add more Empleados
INSERT INTO Empleado (IdEmpleado, Nombre, Apellido, Puesto, FechaContratacion, Salario, Estado, IdCine) VALUES
(4, 'Laura', 'Sánchez', 'Staff Dulcería', '2024-03-01', 6200.00, 'Activo', 1),
(5, 'Miguel', 'Hernández', 'Proyeccionista', '2023-08-15', 8000.00, 'Activo', 2),
(6, 'Claudia', 'Vargas', 'Gerente de Sucursal', '2023-11-01', 16000.00, 'Activo', 3),
(7, 'Roberto', 'Díaz', 'Taquillero', '2024-01-10', 6800.00, 'Activo', 4),
(8, 'Fernanda', 'Castillo', 'Limpieza', '2023-09-01', 5500.00, 'Inactivo', 2);


-- Add more Promociones
INSERT INTO Promocion (IdPromocion, Nombre, Descripcion, FechaInicio, FechaFin, Descuento, CodigoPromo, Estado) VALUES
(3, 'Miércoles de Estreno', '20% de descuento en boleto para películas de estreno los miércoles.', '2025-02-01', '2025-11-30', 0.20, 'MIERCOLES20', 'Activa'),
(4, 'Combo Familiar', 'Palomitas grandes + 4 refrescos medianos por precio especial.', '2025-04-01', '2025-06-30', 0.15, 'FAMILIA15', 'Activa'), -- 15% sobre el precio normal de los items por separado
(5, 'Promo Verano Pasado', 'Descuento especial durante verano 2024.', '2024-07-01', '2024-08-31', 0.25, 'VERANO24', 'Expirada');

-- Add more Ventas
INSERT INTO Venta (IdVenta, Fecha, Total, MetodoPago, Estado, Facturado, IdEmpleado, IdPromocion) VALUES
(4, '2025-05-26 17:45:00', 190.00, 'Tarjeta', 'Completada', TRUE, 1, NULL), -- 2 boletos Barbie (2*95)
(5, '2025-05-26 20:30:00', 270.00, 'Tarjeta', 'Completada', FALSE, 7, 3), -- 2 boletos Dune 2 (2*150 = 300, -20% de 300 = 60, total 240 para boletos. + 30 de un dulce)
    -- Ajuste: Si la promo es 20% en *boleto de estreno* y hoy es miercoles, asumamos que Dune 2 califica. Precio Boleto 150. 20% de 150 = 30. Precio con desc = 120.
    -- Si son 2 boletos: 2 * 120 = 240. Si el total es 270, es 240 (boletos) + 30 (producto). OK.
(6, '2025-05-24 19:50:00', 208.00, 'Efectivo', 'Completada', FALSE, 5, NULL), -- 2 boletos Oppenheimer (2 * 130 = 260, esto era función pasada, no puede ser venta nueva. Ajustemos.)
    -- Venta 6 para una función activa: Gato con Botas (IdPelicula=8, IdFuncion=9, Precio=100)
    -- 2 boletos Gato con Botas = 200.
(7, '2025-05-20 16:30:00', 0.00, 'N/A', 'Cancelada', FALSE, 1, NULL), -- Venta asociada a función cancelada, puede ser de 0 o el reembolso
(8, '2025-05-27 14:30:00', 145.00, 'Tarjeta', 'Completada', TRUE, 6, 4); -- Combo Familiar (150) con 15% -> 127.5 + un agua (17.5)

-- Re-evaluando Venta 6
UPDATE Venta SET Total = 200.00, IdEmpleado = 4, Fecha = '2025-05-28 16:15:00' WHERE IdVenta = 6; -- 2 boletos para Gato con Botas (Funcion 9)

-- Add more Boletos
INSERT INTO Boleto (IdBoleto, PrecioFinal, FechaCompra, CodigoQR, IdFuncion, IdCliente, IdAsiento, IdVenta) VALUES
(4, 95.00, '2025-05-26 17:45:00', 'QR20250526001A1F5', 5, 6, 8, 4),  -- Boleto 1 Venta 4 (Jorge, Barbie, Asiento A1 Sala 5)
(5, 95.00, '2025-05-26 17:45:00', 'QR20250526001A2F5', 5, NULL, 9, 4),-- Boleto 2 Venta 4 (Anon, Barbie, Asiento A2 Sala 5)
(6, 120.00, '2025-05-26 20:30:00', 'QR20250526001B5F9', 6, 4, 11, 5), -- Boleto 1 Venta 5 (Luis, Dune 2, Asiento B5 Sala 9 con promo)
(7, 120.00, '2025-05-26 20:30:00', 'QR20250526001B6F9', 6, 4, 12, 5), -- Boleto 2 Venta 5 (Luis, Dune 2, Asiento B6 Sala 9 con promo)
(8, 100.00, '2025-05-28 16:15:00', 'QR20250528001XXF9', 9, 7, NULL, 6), -- Boleto 1 Venta 6 (Laura P, Gato con Botas, asiento no especificado)
(9, 100.00, '2025-05-28 16:15:00', 'QR20250528002XXF9', 9, 7, NULL, 6); -- Boleto 2 Venta 6 (Laura P, Gato con Botas, asiento no especificado)

-- Add more CategoriaProducto (Example)
INSERT INTO CategoriaProducto (IdCategoria, Nombre, Descripcion) VALUES
(6, 'Helados', 'Variedad de helados y paletas de hielo.');

-- Add more Productos
INSERT INTO Producto (IdProducto, Nombre, Descripcion, Precio, Stock, Estado, IdCategoria) VALUES
(5, 'Agua Embotellada 600ml', 'Agua purificada natural.', 20.00, 300, 'Disponible', 1),
(6, 'Chocolate Grande', 'Barra de chocolate importado.', 35.00, 80, 'Disponible', 3),
(7, 'Palomitas Caramelizadas Chicas', 'Palomitas con cubierta de caramelo, tamaño chico.', 55.00, 70, 'Agotado', 2),
(8, 'Hot Dog Clásico', 'Salchicha de pavo en pan, con aderezos.', 50.00, 90, 'Disponible', 4),
(9, 'Combo Nachos', 'Nachos con queso y jalapeños + Refresco Mediano.', 100.00, 60, 'Disponible', 5),
(10, 'Helado de Vainilla (vaso)', 'Vaso de helado cremoso sabor vainilla.', 40.00, 50, 'Disponible', 6);


-- Add more DetalleVenta
-- Venta 5 (IdVenta=5) tuvo 2 boletos Dune 2 (240) y Total 270. Falta un producto de 30. (No hay producto de 30 exacto, usaré chocolate de 35 y ajusto total)
UPDATE Venta SET Total = 275.00 WHERE IdVenta = 5;
INSERT INTO DetalleVenta (IdDetalleVenta, Cantidad, PrecioUnitario, Subtotal, IdVenta, IdProducto) VALUES
(4, 1, 35.00, 35.00, 5, 6); -- 1 Chocolate Grande en Venta 5

-- Venta 8 (IdVenta=8) Total 145. Tuvo Combo Familiar (IdPromocion=4)
-- El combo familiar es un Producto (ej. IdProducto por definir) o se arma de varios?
-- Asumamos Producto "Combo Familiar" no existe como tal, sino que la promo aplica a items.
-- Si la promo "FAMILIA15" aplicó, y el total fue 145.
-- Podría ser: Palomitas Gdes (70) + 4 Refrescos Medianos (supongamos 40 c/u = 160). Total normal 230.
-- 15% de 230 = 34.5. Precio con promo = 195.5. Esto no cuadra con Venta 8.
-- Vamos a asumir que Venta 8 fue por UN Combo Nachos (100) y UNAS Palomitas Chicas (55), sin promoción. Total 155.
-- O que IdPromocion=4 se aplicó a un producto tipo Combo que costaba 170.60 aprox para dar 145 con 15% desc.
-- Simplifiquemos: Venta 8 tuvo un Combo Nachos y un Agua.
UPDATE Venta SET Total = (100 + 20), IdPromocion = NULL WHERE IdVenta = 8; -- 120
INSERT INTO DetalleVenta (IdDetalleVenta, Cantidad, PrecioUnitario, Subtotal, IdVenta, IdProducto) VALUES
(5, 1, 100.00, 100.00, 8, 9), -- 1 Combo Nachos en Venta 8
(6, 1, 20.00, 20.00, 8, 5);   -- 1 Agua Embotellada en Venta 8

-- Nueva Venta solo con productos
INSERT INTO Venta (IdVenta, Fecha, Total, MetodoPago, Estado, Facturado, IdEmpleado, IdPromocion) VALUES
(9, '2025-05-27 18:00:00', 95.00, 'Efectivo', 'Completada', FALSE, 4, NULL);
INSERT INTO DetalleVenta (IdDetalleVenta, Cantidad, PrecioUnitario, Subtotal, IdVenta, IdProducto) VALUES
(7, 1, 50.00, 50.00, 9, 8), -- 1 Hot Dog Clásico
(8, 1, 45.00, 45.00, 9, 1); -- 1 Refresco Grande

-- Venta con promo MARTES2X1
-- Necesitamos una función en martes con boletos para aplicar 2x1.
-- Funcion 1 es Roma, 85.00. Si es martes, 2 boletos costarían 85.00.
-- Asumamos que HOY es Martes 27 de Mayo 2025.
-- Funcion 13: La La Land, Precio 110.00, el 2025-05-27 (Martes)
INSERT INTO Venta (IdVenta, Fecha, Total, MetodoPago, Estado, Facturado, IdEmpleado, IdPromocion) VALUES
(10, '2025-05-27 16:30:00', 110.00, 'Tarjeta', 'Completada', FALSE, 6, 1); -- Promo MARTES2X1
INSERT INTO Boleto (IdBoleto, PrecioFinal, FechaCompra, CodigoQR, IdFuncion, IdCliente, IdAsiento, IdVenta) VALUES
(10, 110.00, '2025-05-27 16:30:00', 'QR20250527001XXF13', 13, 5, NULL, 10), -- Boleto 1 Venta 10 (Ana G, La La Land)
(11, 0.00, '2025-05-27 16:30:00', 'QR20250527002XXF13', 13, 5, NULL, 10);   -- Boleto 2 Venta 10 (Ana G, La La Land, gratis por 2x1)


-- Reactivar la revisión de claves foráneas si se desactivó
-- SET FOREIGN_KEY_CHECKS=1;