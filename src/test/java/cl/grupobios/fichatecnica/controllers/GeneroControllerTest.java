package cl.grupobios.fichatecnica.controllers;

import cl.grupobios.fichatecnica.command.handler.GeneroCommandHandler;
import cl.grupobios.fichatecnica.command.impl.genero.CreateGeneroCommandImpl;
import cl.grupobios.fichatecnica.command.impl.genero.ListGeneroCommandImpl;
import cl.grupobios.fichatecnica.command.impl.genero.ListIdGeneroCommandImpl;
import cl.grupobios.fichatecnica.models.Genero;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test unitario para GeneroController")
class GeneroControllerTest {
    
    private MockMvc mockMvc;

    @Mock
    private GeneroCommandHandler commandHandler;

    @InjectMocks
    private GeneroController generoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(generoController).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Tests para GET /api/v1/generos/listar")
    class ObtenerTodosLosGenerosTests {

        @Test
        @DisplayName("Debería retornar Set de géneros con estado 200 OK")
        void deberiaRetornarListaDeGeneros() throws Exception {
            // Arrange
            Set<Genero> generos = new HashSet<>();
            generos.add(new Genero(1L, "Masculino", "M"));
            generos.add(new Genero(2L, "Femenino", "F"));
            
            when(commandHandler.handler(any(ListGeneroCommandImpl.class))).thenReturn(generos);

            // Act & Assert
            mockMvc.perform(get("/api/v1/generos/listar")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));

            verify(commandHandler, times(1)).handler(any(ListGeneroCommandImpl.class));
        }

        @Test
        @DisplayName("Debería retornar Set vacío con estado 200 OK")
        void deberiaRetornarSetVacio() throws Exception {
            // Arrange
            Set<Genero> generos = new HashSet<>();
            when(commandHandler.handler(any(ListGeneroCommandImpl.class))).thenReturn(generos);

            // Act & Assert
            mockMvc.perform(get("/api/v1/generos/listar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(commandHandler, times(1)).handler(any(ListGeneroCommandImpl.class));
        }
    }

    @Nested
    @DisplayName("Tests para GET /api/v1/generos/lista/{id}")
    class ObtenerGeneroPorIdTests {
        
        @Test
        @DisplayName("Debería retornar género por ID con estado 200 OK")
        void deberiaRetornarGeneroPorId() throws Exception {
            // Arrange
            Genero genero = new Genero(1L, "Masculino", "M");
            when(commandHandler.handler(any(ListIdGeneroCommandImpl.class))).thenReturn(genero);

            // Act & Assert
            mockMvc.perform(get("/api/v1/generos/lista/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreGenero").value("Masculino"))
                    .andExpect(jsonPath("$.sigla").value("M"));

            verify(commandHandler, times(1)).handler(any(ListIdGeneroCommandImpl.class));
        }

        @Test
        @DisplayName("Debería retornar 404 cuando género no existe")
        void deberiaRetornarNotFoundCuandoGeneroNoExiste() throws Exception {
            // Arrange
            when(commandHandler.handler(any(ListIdGeneroCommandImpl.class))).thenReturn(null);

            // Act & Assert - El controller ahora devuelve 404 cuando el género no existe
            mockMvc.perform(get("/api/v1/generos/lista/{id}", 99L))
                    .andExpect(status().isNotFound()); // CAMBIADO de isOk() a isNotFound()

            verify(commandHandler, times(1)).handler(any(ListIdGeneroCommandImpl.class));
    }
}

    @Nested
    @DisplayName("Tests para POST /api/v1/generos/create")
    class CrearGeneroTests {
        
        @Test
        @DisplayName("Debería crear género exitosamente con estado 201 Created")
        void deberiaCrearGeneroExitosamente() throws Exception {
            // Arrange
            Genero nuevoGenero = new Genero(null, "Otro", "O");
            Genero generoCreado = new Genero(4L, "Otro", "O");

            when(commandHandler.handler(any(CreateGeneroCommandImpl.class))).thenReturn(generoCreado);
            
            // Act & Assert
            mockMvc.perform(post("/api/v1/generos/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(nuevoGenero)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(4))
                    .andExpect(jsonPath("$.nombreGenero").value("Otro"))
                    .andExpect(jsonPath("$.sigla").value("O"));

            verify(commandHandler, times(1)).handler(any(CreateGeneroCommandImpl.class));
        }

        @Test
        @DisplayName("Debería crear género incluso con nombre vacío (sin validaciones)")
        void deberiaCrearGeneroConNombreVacio() throws Exception {
            // Arrange
            Genero generoConNombreVacio = new Genero(null, "", "M");
            Genero generoCreado = new Genero(5L, "", "M");

            when(commandHandler.handler(any(CreateGeneroCommandImpl.class))).thenReturn(generoCreado);
            
            // Act & Assert - SIN validaciones, debería crear igual
            mockMvc.perform(post("/api/v1/generos/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(generoConNombreVacio)))
                    .andExpect(status().isCreated());

            verify(commandHandler, times(1)).handler(any(CreateGeneroCommandImpl.class));
        }
    }
}