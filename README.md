# Comparador de precios de gasolineras

## Descripción
Aplicación web para consultar y comparar precios de gasolineras, mostrando su ubicación en un mapa interactivo. Los datos se actualizan automáticamente a partir de la sede electrónica del Gobierno de España.

## Tecnologías
- Java + Spring Boot
- PostgreSQL + PostGIS
- Leaflet

## Funcionalidades
- Consulta de gasolineras desde el backend
- Visualización en mapa interactivo
- Actualización de datos desde fuente oficial
- Visualización de información detallada por gasolinera

## Requisitos previos:
- Java 21
- PostgreSQL 16 con PostGIS en puerto 5434
-  Node.js (versión 18 o superior)
- Base de datos gasolineras_db creada con extensión postgis

## Pasos:
1. Clonar el repositorio
2. Configurar application.properties con contraseña de PostgreSQL y API key de OpenRouteService 
3. Arrancar el backend: ejecutar GasolinerasApplication desde VS Code o con mvn spring-boot:run
4. Realizar una petición POST a http://localhost:8080/api/actualizar para cargar los datos del gobierno
5. 5. Arrancar el frontend:
   - cd frontend
   - npm install
   - npm start
6. Abrir http://localhost:3000
