
CREATE DATABASE CinemaSystem;
USE CinemaSystem;


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


CREATE TABLE Estudio (
    IdEstudio INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Pais VARCHAR(100) NOT NULL,
    Descripcion TEXT
);


CREATE TABLE Director (
    IdDirector INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Nacionalidad VARCHAR(100) NOT NULL,
    FechaNacimiento DATE
);

---Tabla de foto

CREATE TABLE Pelicula (
    IdPelicula INT AUTO_INCREMENT PRIMARY KEY,
    Titulo VARCHAR(150) NOT NULL,
    Duracion INT NOT NULL,
    Sinopsis TEXT,
    FechaEstreno DATE NOT NULL,
    Clasificacion VARCHAR(10) NOT NULL,
    Idioma VARCHAR(50) NOT NULL,
    Subtitulada BOOLEAN DEFAULT FALSE,
    Formato VARCHAR(10) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    IdEstudio INT,
    IdDirector INT,
    FOREIGN KEY (IdEstudio) REFERENCES Estudio(IdEstudio),
    FOREIGN KEY (IdDirector) REFERENCES Director(IdDirector)
);


CREATE TABLE Actor (
    IdActor INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Nacionalidad VARCHAR(100) NOT NULL,
    FechaNacimiento DATE
);


CREATE TABLE PeliculaActor (
    IdPelicula INT,
    IdActor INT,
    Personaje VARCHAR(100) NOT NULL,
    PRIMARY KEY (IdPelicula, IdActor),
    FOREIGN KEY (IdPelicula) REFERENCES Pelicula(IdPelicula),
    FOREIGN KEY (IdActor) REFERENCES Actor(IdActor)
);


CREATE TABLE Sala (
    IdSala INT AUTO_INCREMENT PRIMARY KEY,
    Numero INT NOT NULL,
    Capacidad INT NOT NULL,
    TipoSala VARCHAR(20) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    IdCine INT,
    FOREIGN KEY (IdCine) REFERENCES Cine(IdCine)
);


CREATE TABLE Asiento (
    IdAsiento INT AUTO_INCREMENT PRIMARY KEY,
    Fila CHAR(2) NOT NULL,
    Numero INT NOT NULL,
    TipoAsiento VARCHAR(20) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    IdSala INT,
    FOREIGN KEY (IdSala) REFERENCES Sala(IdSala)
);

CREATE TABLE Funcion (
    IdFuncion INT AUTO_INCREMENT PRIMARY KEY,
    FechaHora DATETIME NOT NULL,
    Precio DECIMAL(10,2) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    IdPelicula INT,
    IdSala INT,
    FOREIGN KEY (IdPelicula) REFERENCES Pelicula(IdPelicula),
    FOREIGN KEY (IdSala) REFERENCES Sala(IdSala)
);


CREATE TABLE Membresia (
    IdMembresia INT AUTO_INCREMENT PRIMARY KEY,
    Tipo VARCHAR(50) NOT NULL,
    Descripcion TEXT,
    Costo DECIMAL(10,2) NOT NULL,
    DuracionMeses INT NOT NULL,
    BeneficiosDescripcion TEXT
);

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


CREATE TABLE Empleado (
    IdEmpleado INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Apellido VARCHAR(100) NOT NULL,
    Puesto VARCHAR(50) NOT NULL,
    FechaContratacion DATE NOT NULL,
    Salario DECIMAL(10,2) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    IdCine INT,
    FOREIGN KEY (IdCine) REFERENCES Cine(IdCine)
);


CREATE TABLE Venta (
    IdVenta INT AUTO_INCREMENT PRIMARY KEY,
    Fecha DATETIME NOT NULL,
    Total DECIMAL(10,2) NOT NULL,
    MetodoPago VARCHAR(50) NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    Facturado BOOLEAN DEFAULT FALSE,
    IdEmpleado INT,
    FOREIGN KEY (IdEmpleado) REFERENCES Empleado(IdEmpleado)
);


CREATE TABLE Boleto (
    IdBoleto INT AUTO_INCREMENT PRIMARY KEY,
    PrecioFinal DECIMAL(10,2) NOT NULL,
    FechaCompra DATETIME NOT NULL,
    CodigoQR VARCHAR(200),
    IdFuncion INT,
    IdCliente INT,
    IdAsiento INT,
    IdVenta INT,
    FOREIGN KEY (IdFuncion) REFERENCES Funcion(IdFuncion),
    FOREIGN KEY (IdCliente) REFERENCES Cliente(IdCliente),
    FOREIGN KEY (IdAsiento) REFERENCES Asiento(IdAsiento),
    FOREIGN KEY (IdVenta) REFERENCES Venta(IdVenta)
);


CREATE TABLE CategoriaProducto (
    IdCategoria INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(50) NOT NULL,
    Descripcion TEXT
);


CREATE TABLE Producto (
    IdProducto INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    Precio DECIMAL(10,2) NOT NULL,
    Stock INT NOT NULL,
    Estado VARCHAR(20) NOT NULL,
    IdCategoria INT,
    FOREIGN KEY (IdCategoria) REFERENCES CategoriaProducto(IdCategoria)
);


CREATE TABLE DetalleVenta (
    IdDetalleVenta INT AUTO_INCREMENT PRIMARY KEY,
    Cantidad INT NOT NULL,
    PrecioUnitario DECIMAL(10,2) NOT NULL,
    Subtotal DECIMAL(10,2) NOT NULL,
    IdVenta INT,
    IdProducto INT,
    FOREIGN KEY (IdVenta) REFERENCES Venta(IdVenta),
    FOREIGN KEY (IdProducto) REFERENCES Producto(IdProducto)
);


CREATE TABLE Promocion (
    IdPromocion INT AUTO_INCREMENT PRIMARY KEY,
    Nombre VARCHAR(100) NOT NULL,
    Descripcion TEXT,
    FechaInicio DATE NOT NULL,
    FechaFin DATE NOT NULL,
    Descuento DECIMAL(5,2) NOT NULL,
    CodigoPromo VARCHAR(20) UNIQUE NOT NULL,
    Estado VARCHAR(20) NOT NULL
);