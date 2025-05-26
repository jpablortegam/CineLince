-- Asegúrate de que la base de datos CinemaSystem existe y está en uso
-- USE CinemaSystem;

-- Primero, si la tabla ya existe y quieres modificarla (ten cuidado si ya tienes datos):
-- ALTER TABLE Cliente
-- ADD COLUMN ContrasenaHash VARCHAR(255) NOT NULL AFTER Email; -- O donde prefieras la columna
SET FOREIGN_KEY_CHECKS=0; -- Disable foreign key checks


-- Si estás creando la tabla desde cero o re-creándola, aquí está la definición completa:
DROP TABLE IF EXISTS Cliente; -- ¡Cuidado! Esto borrará la tabla si existe.
CREATE TABLE Cliente (
    IdCliente INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    ContrasenaHash VARCHAR(255) NOT NULL, -- Contraseña hasheada
    Telefono VARCHAR(20) NOT NULL,
    FechaNacimiento DATE NOT NULL,
    FechaRegistro DATETIME NOT NULL,
    IdMembresia INT,
    FOREIGN KEY (IdMembresia) REFERENCES Membresia(IdMembresia)
);

SET FOREIGN_KEY_CHECKS=1; -- Re-enable foreign key checks


-- Add more Clientes con ContrasenaHash
-- (Recuerda reemplazar estos hashes de ejemplo con los generados por tu aplicación)
INSERT INTO Cliente (IdCliente, Nombre, Apellido, Email, ContrasenaHash, Telefono, FechaNacimiento, FechaRegistro, IdMembresia) VALUES
(1, 'Mariana', 'López', 'mariana.lopez@example.com', '$2a$10$N9qo8uLOickq.KbGMxL2Q.9.E4J.C2Q9S9zL/a3F.1aC1I.9K.zO6', '4435551122', '1995-08-23', '2024-03-15 10:00:00', 2),
(2, 'Carlos', 'Fuentes', 'carlos.f@example.com', '$2a$10$hXzL6c2mK5nB8qR7vX9wY.u7e6fS5gH4jK3lM2nO1pP0qR9sT8uV2', '5551112233', '1988-02-10', '2025-01-20 14:30:00', NULL),
(3, 'Sofía', 'Hernández', 'sofia.hdz@example.com', '$2a$10$aB1cD2eF3gH4iJ5kL6mN7.oP8qR9sT0uV1wX2yZ3A4B5C6D7E8F9G', '8112345678', '2001-11-05', '2023-11-01 18:00:00', 3),
(4, 'Luis', 'Ramírez', 'luis.ram@example.com', '$2a$10$kL9mN8oP7qR6sT5uV4wX3.yZ2A1B0C9D8E7F6G5H4I3J2K1L0M9N', '3312345678', '1999-04-15', '2025-02-10 11:00:00', 4),
(5, 'Ana', 'García', 'ana.garcia@example.com', '$2a$10$N9qo8uLOickq.KbGMxL2Q.9.E4J.C2Q9S9zL/a3F.1aC1I.9K.zO6', '5567890123', '1960-07-30', '2024-12-01 09:30:00', 5),
(6, 'Jorge', 'Torres', 'jorge.t@example.com', '$2a$10$hXzL6c2mK5nB8qR7vX9wY.u7e6fS5gH4jK3lM2nO1pP0qR9sT8uV2', '4429876543', '1985-12-01', '2025-03-01 17:45:00', 1),
(7, 'Laura', 'Pérez', 'laura.p@example.com', '$2a$10$aB1cD2eF3gH4iJ5kL6mN7.oP8qR9sT0uV1wX2yZ3A4B5C6D7E8F9G', '8119876500', '2003-06-20', '2025-04-22 12:10:00', NULL);

-- Puedes añadir más clientes aquí siguiendo el mismo formato.