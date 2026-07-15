# BookPoint - Guía de Pruebas Unitarias y Comunicación entre Microservicios

---

## 1. Comunicación entre Microservicios

### Herramienta utilizada: RestClient
- **No usamos Feign Client**
- **No usamos WebClient legacy**
- **Usamos Spring's RestClient (nuevo desde Spring Boot 3.2)**

### ¿Por qué RestClient?
1. API moderna y fluida
2. Mantenida oficialmente por el equipo de Spring
3. Compatible con Resilience4j para retry y circuit breaker
4. Perfecta para microservicios simples

### Ejemplo de Cliente (Carrito Service):
```java
@Component
public class ProductoClient {

    private final RestClient restClient;
    private static final String BASE_URL = "http://localhost:8083/api/productos";

    @Retry(name = "inventario-service", fallbackMethod = "fallbackObtenerProducto")
    @CircuitBreaker(name = "inventario-service", fallbackMethod = "fallbackObtenerProducto")
    public ProductoDTO obtenerProducto(Long productoId) {
        ResponseEntity<ProductoDTO> response = restClient.get()
                .uri(BASE_URL + "/{id}", productoId)
                .retrieve()
                .toEntity(ProductoDTO.class);
        return response.getBody();
    }
}
```

### Configuración de RestClient (RestClientConfig.java):
```java
@Configuration
public class RestClientConfig {
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }
}
```

### Resilience4j:
- **Retry**: Reintentos automáticos en caso de fallos transitorios (3 intentos)
- **Circuit Breaker**: Evita saturar un servicio caído
- **Fallback Methods**: Respuesta segura cuando el servicio no está disponible

---

## 2. Pruebas Unitarias con JUnit 5 y Mockito

### 2.1 Stack Tecnológico
- **JUnit 5**: Motor de pruebas
- **Mockito**: Mocking de dependencias
- **AssertJ**: Assertions fluídas y legibles
- **MockMvc**: Pruebas de controladores web
- **JaCoCo**: Medición de cobertura de código

### 2.2 Estructura de Pruebas
```
<servicio>/src/test/java/com/dfs/<servicioservice>/
├── controller/
│   └── <Xxx>ControllerTest.java
└── service/
    └── <Xxx>ServiceTest.java
```

### 2.3 Anotaciones Clave
- `@ExtendWith(MockitoExtension.class)`: Habilita Mockito
- `@Mock`: Crea un mock de una dependencia
- `@InjectMocks`: Crea una instancia de la clase a probar e inyecta los mocks
- `@Test`: Marca un método como caso de prueba
- `@DisplayName`: Nombre descriptivo para la prueba
- `@WebMvcTest`: Prueba de controlador con MockMvc
- `@MockBean`: Mockea un bean de Spring

### 2.4 Ejemplo de Prueba de Servicio
```java
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Test: crear usuario válido")
    void crearUsuario_usuarioValido_retornaUsuarioCreado() {
        // Given
        Usuario usuario = Usuario.builder().username("test").build();
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        // When
        Usuario resultado = usuarioService.crearUsuario(usuario);

        // Then
        assertThat(resultado).isNotNull();
        verify(usuarioRepository, times(1)).save(usuario);
    }
}
```

### 2.5 Ejemplo de Prueba de Controlador
```java
@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    @Test
    void listarUsuarios_retornaLista() throws Exception {
        when(usuarioService.listarUsuarios()).thenReturn(List.of(new Usuario()));

        mockMvc.perform(get("/api/auth/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
```

---

## 3. JaCoCo - Cobertura de Código

### 3.1 Configuración
El plugin JaCoCo está configurado en el `pom.xml` principal:
- **Umbral de cobertura**: 90% de líneas de código
- **Exclusiones**: Clases Application, Exception y Config
- **Fases**:
  - `prepare-agent`: Prepara el agente para medir cobertura
  - `report`: Genera el reporte HTML
  - `check`: Valida que se cumpla el umbral (falla el build si no)

### 3.2 Comandos para Ejecutar Pruebas
- **Ejecutar todas las pruebas**:
  ```bash
  ./mvnw clean test
  ```
- **Ejecutar pruebas de un microservicio específico**:
  ```bash
  ./mvnw clean test -pl auth-service
  ```
- **Ver reporte de cobertura**:
  Abrir en el navegador: `<microservicio>/target/site/jacoco/index.html`

### 3.3 ¿Qué hacer si el umbral no se cumple?
1. Identifica las partes del código no cubiertas en el reporte JaCoCo
2. Escribe pruebas adicionales
3. Vuelve a ejecutar `./mvnw test`

---

## 4. Ejecución de Microservicios
1. Inicia MySQL y crea todas las bases de datos
2. Inicia los servicios en orden:
   ```bash
   # Terminal 1
   ./mvnw spring-boot:run -pl inventario-service
   # Terminal 2
   ./mvnw spring-boot:run -pl carrito-service
   # Terminal 3
   ./mvnw spring-boot:run -pl pedidos-service
   # Etc.
   ```
3. Los microservicios se conectan usando URLs hardcodeadas (localhost:80xx)
   - Para escenario real, usar Eureka o Spring Cloud Config

---

## 5. Resumen de Servicios con Pruebas Implementadas
| Microservicio       | Service | Controller Tests | Cobertura Esperada |
|---------------------|---------|------------------|--------------------|
| auth-service        | ✅       | ✅               | 90%+               |
| carrito-service     | ✅       | ✅               | 90%+               |
| inventario-service  | ✅       | ✅               | 90%+               |
| clientes-service    | ✅       | 🟡               | 90%+               |
| (resto de servicios)| 🟡       | 🟡               | 90%+               |

✅ = Implementado, 🟡 = Pendiente (sigue la misma plantilla que auth-service)
