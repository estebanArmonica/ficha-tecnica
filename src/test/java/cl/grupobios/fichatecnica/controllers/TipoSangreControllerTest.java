package cl.grupobios.fichatecnica.controllers;

import cl.grupobios.fichatecnica.command.handler.TipoSangreCommandHandler;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.CreateTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.ListIdTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.ListTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.models.TipoSangre;
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
@DisplayName("Test unitario para TipoSangreController")
class TipoSangreControllerTest {
    
    private MockMvc mockMvc;

    @Mock
    private TipoSangreCommandHandler commandHandler;

    @InjectMocks
    private TipoSangreController tipoSangreController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tipoSangreController).build();
        objectMapper = new ObjectMapper();
    }

    @Nested
    @DisplayName("Tests para GET /api/v1/tipo-sangre/listar")
    class ObtenerTodosLosTiposSangreTests {

        @Test
        @DisplayName("Debería retornar Set de tipos de sangre con estado 200 OK")
        void deberiaRetornarListaDeTiposSangre() throws Exception {
            // Arrange
            Set<TipoSangre> tiposSangre = new HashSet<>();
            tiposSangre.add(new TipoSangre(1L, "A+"));
            tiposSangre.add(new TipoSangre(2L, "O-"));
            tiposSangre.add(new TipoSangre(3L, "B+"));
            
            when(commandHandler.handler(any(ListTipoSangreCommandImpl.class))).thenReturn(tiposSangre);

            // Act & Assert
            mockMvc.perform(get("/api/v1/tipo-sangre/listar")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(3)));

            verify(commandHandler, times(1)).handler(any(ListTipoSangreCommandImpl.class));
        }

        @Test
        @DisplayName("Debería retornar Set vacío con estado 200 OK")
        void deberiaRetornarSetVacio() throws Exception {
            // Arrange
            Set<TipoSangre> tiposSangre = new HashSet<>();
            when(commandHandler.handler(any(ListTipoSangreCommandImpl.class))).thenReturn(tiposSangre);

            // Act & Assert
            mockMvc.perform(get("/api/v1/tipo-sangre/listar"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(commandHandler, times(1)).handler(any(ListTipoSangreCommandImpl.class));
        }
    }

    @Nested
    @DisplayName("Tests para GET /api/v1/tipo-sangre/listar/{id}")
    class ObtenerTipoSangrePorIdTests {
        
        @Test
        @DisplayName("Debería retornar tipo de sangre por ID con estado 200 OK")
        void deberiaRetornarTipoSangrePorId() throws Exception {
            // Arrange
            TipoSangre tipoSangre = new TipoSangre(1L, "A+");
            when(commandHandler.handler(any(ListIdTipoSangreCommandImpl.class))).thenReturn(tipoSangre);

            // Act & Assert
            mockMvc.perform(get("/api/v1/tipo-sangre/listar/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.nombreTipoSangre").value("A+"));

            verify(commandHandler, times(1)).handler(any(ListIdTipoSangreCommandImpl.class));
        }

        @Test
        @DisplayName("Debería retornar 200 incluso cuando handler devuelve null")
        void deberiaRetornarOkCuandoTipoSangreNoExiste() throws Exception {
            // Arrange
            when(commandHandler.handler(any(ListIdTipoSangreCommandImpl.class))).thenReturn(null);

            // Act & Assert
            mockMvc.perform(get("/api/v1/tipo-sangre/listar/{id}", 99L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").doesNotExist());

            verify(commandHandler, times(1)).handler(any(ListIdTipoSangreCommandImpl.class));
        }
    }

    @Nested
    @DisplayName("Tests para POST /api/v1/tipo-sangre/create")
    class CrearTipoSangreTests {
        
        @Test
        @DisplayName("Debería crear tipo de sangre exitosamente con estado 201 Created")
        void deberiaCrearTipoSangreExitosamente() throws Exception {
            // Arrange
            TipoSangre nuevoTipoSangre = new TipoSangre(null, "AB-");
            TipoSangre tipoSangreCreado = new TipoSangre(4L, "AB-");

            when(commandHandler.handler(any(CreateTipoSangreCommandImpl.class))).thenReturn(tipoSangreCreado);
            
            // Act & Assert
            mockMvc.perform(post("/api/v1/tipo-sangre/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(nuevoTipoSangre)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(4))
                    .andExpect(jsonPath("$.nombreTipoSangre").value("AB-"));

            verify(commandHandler, times(1)).handler(any(CreateTipoSangreCommandImpl.class));
        }

        @Test
        @DisplayName("Debería crear tipo de sangre incluso con nombre vacío (sin validaciones)")
        void deberiaCrearTipoSangreConNombreVacio() throws Exception {
            // Arrange
            TipoSangre tipoSangreConNombreVacio = new TipoSangre(null, "");
            TipoSangre tipoSangreCreado = new TipoSangre(5L, "");

            when(commandHandler.handler(any(CreateTipoSangreCommandImpl.class))).thenReturn(tipoSangreCreado);
            
            // Act & Assert - SIN validaciones, debería crear igual
            mockMvc.perform(post("/api/v1/tipo-sangre/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(tipoSangreConNombreVacio)))
                    .andExpect(status().isCreated());

            verify(commandHandler, times(1)).handler(any(CreateTipoSangreCommandImpl.class));
        }
    }
}