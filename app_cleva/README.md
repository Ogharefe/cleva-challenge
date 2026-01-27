# Hero Movement Challenge - Hexagonal Architecture

This project implements a hero movement simulator on a 2D grid map based on instructions provided as a string (North, South, East, Ouest). It uses **Spring Boot 3** and **Java 21**, following the **Hexagonal Architecture** (Ports and Adapters) pattern.

## Features
- Hexagonal Architecture: domain isolated from adapters and configuration
- Java records: immutable domain [Hero.java](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/domain/model/Hero.java), [GameMap.java](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/domain/model/GameMap.java)
- Map loading use case and service: [LoadMapUseCase](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/application/ports/in/LoadMapUseCase.java), [MapLoadService](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/application/service/MapLoadService.java)
- Map loading adapter: [FileMapLoader.java](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/infrastructure/adapter/out/persistence/FileMapLoader.java)
- Movement rules: stay within bounds; cannot move into walls (`#`)
- Instruction set: `N` North, `S` South, `E` East, `O` Ouest (West)
- Global exception handling for predictable HTTP error responses [GlobalExceptionHandler.java](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/infrastructure/adapter/in/web/GlobalExceptionHandler.java)

## Prerequisites
- JDK 21+
- Maven 3.8+

## Installation

- Extract the project files
- Navigate to the project root directory

## Configuration

- Default map path is configured in [application.yml](file:///c:/clevaProject/app_default/app_cleva/src/main/resources/application.yml):

```yaml
app:
  map:
    path: "C:/clevaProject/carte.txt"
```

- Place maps under `src/main/resources` for runtime and tests, or pass an explicit path
- Map tiles: `#` is a wall; any other character is traversable

## Running the Application

### Running Tests
- Runs unit and integration tests:

```bash
mvn test
```

### Running the App
- Launch the API:

```bash
mvn spring-boot:run
```

- Default port is 9099

## API

- Movement
  - Endpoint: POST `/api/hero/move` [HeroMovementController.java](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/infrastructure/adapter/in/web/HeroMovementController.java)
  - Request body:
    ```json
    {
      "startX": 3,
      "startY": 0,
      "instructions": "SSSSEEEEEENN",
      "mapPath": "carte.txt"
    }
    ```
  - If `mapPath` is empty or omitted, the service uses the configured default
  - Response body:
    ```json
    { "x": 9, "y": 2 }
    ```

- Map loading
  - Endpoint: POST `/api/map/load` [MapController.java](file:///c:/clevaProject/app_default/app_cleva/src/main/java/com/example/heromovement/infrastructure/adapter/in/web/MapController.java)
  - Request body:
    ```json
    { "path": "carte.txt" }
    ```
    or filesystem:
    ```json
    { "path": "C:\\\\clevaProject\\\\carte.txt" }
    ```
    or use configured default:
    ```json
    {}
    ```
  - Response body:
    ```json
    { "width": 20, "height": 20 }
    ```

## Path Selection Rules
- If `path` is an absolute path or contains `/` or `\\`, it is treated as a filesystem path.
- If `path` is a simple name (e.g., `carte.txt`), it is treated as a classpath resource.
- If `path` is omitted or empty, the application uses `app.map.path` from application.yml (filesystem if absolute, classpath if simple).

## Project Structure
- `domain`: Direction, Position, Hero, GameMap
- `application`: MoveHeroUseCase, HeroMovementService, LoadMapUseCase, MapLoadService
- `infrastructure`: 
  - in/web: controllers (hero, map), global exception handler
  - out/persistence: file map loader
- `resources`: application.yml, map files (carte.txt)
- `test`: JUnit tests and test resources

## Troubleshooting
- Map path issues:
  - Use classpath-relative names like `carte.txt`
  - Or pass filesystem path (the loader falls back to reading files directly)
- Common exceptions:
  - InvalidInstructionException: unknown instruction character
  - InvalidCoordinateException: start out of map bounds
  - InvalidMapException: empty or malformed map content
  - MapLoadingException: file not found or I/O error

## Examples
- Run a movement:

```bash
curl -X POST http://localhost:9099/api/hero/move \
  -H "Content-Type: application/json" \
  -d '{ "startX": 3, "startY": 0, "instructions": "SSSSEEEEEENN", "mapPath": "carte.txt" }'
```

Returns:

```json
{ "x": 9, "y": 2 }
```

- Load a map (classpath):

```bash
curl -X POST http://localhost:9099/api/map/load \
  -H "Content-Type: application/json" \
  -d '{ "path": "carte.txt" }'
```

- Load a map (filesystem):

```bash
curl -X POST http://localhost:9099/api/map/load \
  -H "Content-Type: application/json" \
  -d '{ "path": "C:\\\\clevaProject\\\\carte.txt" }'
```
