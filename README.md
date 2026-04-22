# Gasolineras en Ruta 

A web application that helps drivers find the cheapest petrol stations along their route. The user enters an origin and a destination, and the system displays nearby petrol stations with their updated prices, automatically identifying the most affordable one.

Price data is retrieved daily from the official source of the Spanish Ministry for Ecological Transition (MITECO).

---

## Technologies

- Java 21 + Spring Boot
- PostgreSQL 16 + PostGIS
- React + Leaflet
- OpenRouteService API (free)
- MITECO API — Spanish Government (free)

---

## Features

- Route search between origin and destination with automatic geocoding
- Petrol station filtering within a 3 km radius of the calculated route
- Interactive map with colour-coded markers based on relative price range
- Automatic identification of the cheapest petrol station on the route
- Full detail of each petrol station on marker click
- Brand filter using partial text search
- Daily data update from the official source with one-download-per-day control
- Display of the last update date and time

---

## Prerequisites

- Java 21
- PostgreSQL 16 with PostGIS extension installed, running on port 5434
- Node.js 18 or higher
- A free OpenRouteService API key (register at https://openrouteservice.org)

---

## Setup

### 1. Clone the repository

```bash
git clone https://github.com/clauuzambrana/gasolineras.git
cd gasolineras
```

### 2. Create the database

Open pgAdmin or any PostgreSQL client and run:

```sql
CREATE DATABASE gasolineras_db;
```

Then connect to that database and run:

```sql
CREATE EXTENSION postgis;
```

### 3. Configure the backend

Open the file:
src/main/resources/application.properties

And set the following values with your own credentials:

```properties
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5434/gasolineras_db
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

openrouteservice.apikey=YOUR_API_KEY
```

---

## Running the Project

### Backend

From the project root folder:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw spring-boot:run
```

Alternatively, open `GasolinerasApplication.java` in VS Code and click the **Run** button above the `main` method.

The server starts at `http://localhost:8080`.

### Initial data load

The first time you run the project, and whenever you want to update prices, send this request to download data from the government source. You can use the `test/api.http` file included in the project or any REST client:
POST http://localhost:8080/api/actualizar

The system will download data for over 12,000 petrol stations across Spain. The process takes between 15 and 30 seconds. From that point on, the endpoint checks whether a download has already been performed that day and skips it if not needed.

### Frontend

```bash
cd frontend
npm install
npm start
```

The application opens automatically at `http://localhost:3000`.

---

## Usage

1. Enter an origin and a destination in the left panel form.
2. Optionally type a brand name in the filter field (e.g. `repsol` or `bp`).
3. Click **Buscar ruta**.
4. The map displays the route and nearby petrol stations with colour-coded markers based on their relative price within the route.
5. The side panel shows the cheapest petrol station found.
6. Click any marker to view the full details of that petrol station.

---

## Project Structure
gasolineras/
├── src/
│   └── main/java/com/claudia/gasolineras/
│       ├── config/
│       ├── controller/
│       ├── entity/
│       ├── repository/
│       ├── service/
│       └── GasolinerasApplication.java
├── frontend/
│   └── src/
│       ├── components/
│       │   ├── MapaRuta.js
│       │   └── PanelLateral.js
│       └── App.js
├── test/
│   └── api.http
└── pom.xml

---

## Notes

This project requires local infrastructure (PostgreSQL with PostGIS, Node.js and a valid OpenRouteService API key) and cannot be packaged as a standalone executable. Please follow the setup instructions above carefully before running the application.
