# Changelog - BookPoint Chile

## [1.1.0] - 2026-06-30 (Actualización para API Gateway)

### Fixed
- **Parent POM**: Removido `catalogo-service` (no existe), agregado `gateway-service`
- **Gateway POM**: Cambiado para usar `spring-boot-starter-parent` directamente (no hereda de `bookpoint-parent`), agregado Spring Cloud BOM 2024.0.0, agregado versión para `springdoc-openapi-starter-webmvc-ui`
- **Duplicate SQL Key**: Fixed duplicate `server` key in gateway's `application.yml`
- **Removed Invalid Files**: Deleted notebook files with hyphens in names (invalid Java files) and the extra `cart-service` directory
- **Gateway Configuration**:
  - Updated `gateway-service/src/main/resources/application.yml` to exclude DataSource/JPA auto-configuration
  - Added routes for both `/api/cart/**` and `/api/carrito/**` for compatibility

### Added
- **API Gateway Service**: Complete `gateway-service` module running on port 8080
- **Global Logging Filter**: Added to all requests/responses
- **Actuator Endpoints**: Enabled `/actuator/health`, `/actuator/gateway`, `/actuator/info`
- **Compression**: Enabled response compression for better performance
- **Updated Documentation**:
  - `DOCUMENTACION_APIS_POSTMAN.txt`: All endpoints updated to use only `http://localhost:8080`, added gateway guide, variable `{{base_url}}` for Postman
  - Fixed catalog-service route (now part of inventario-service)
  - Added notes about security and headers added by the gateway

### Changed
- **Gateway Routes**: All microservice routes now go through the gateway on port 8080

## [1.0.0] - 2026-06-30 (Versión inicial con microservicios)
- Initial multi-module project setup
- All microservices with basic CRUD operations
- Database schema and initial data
