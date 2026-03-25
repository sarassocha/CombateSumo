-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 25-03-2026 a las 11:16:14
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `sumo_db`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rikishi`
--

CREATE TABLE `rikishi` (
  `nombre` varchar(100) NOT NULL,
  `peso` int(11) NOT NULL,
  `altura` int(11) NOT NULL,
  `victorias` int(11) NOT NULL DEFAULT 0,
  `kimarites` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rikishi`
--

INSERT INTO `rikishi` (`nombre`, `peso`, `altura`, `victorias`, `kimarites`) VALUES
('Hakuho', 155, 192, 0, 'Oshidashi,Yorikiri,Hatakikomi,Uwatenage'),
('Asashoryu', 148, 184, 0, 'Tsukidashi,Oshitaoshi,Yorikiri,Shitatenage'),
('Takanohana', 160, 185, 0, 'Oshidashi,Yorikiri,Uwatenage,Kotenage'),
('Akebono', 233, 203, 0, 'Oshidashi,Tsukidashi,Yorikiri,Oshitaoshi'),
('Musashimaru', 235, 192, 0, 'Yorikiri,Oshidashi,Uwatenage,Yoritaoshi'),
('Konishiki', 287, 184, 0, 'Oshidashi,Yorikiri,Tsukidashi,Hatakikomi');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `rikishi`
--
ALTER TABLE `rikishi`
  ADD PRIMARY KEY (`nombre`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
