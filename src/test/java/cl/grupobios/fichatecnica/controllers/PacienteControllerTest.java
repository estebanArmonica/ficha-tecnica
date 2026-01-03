package cl.grupobios.fichatecnica.controllers;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.command.factory.PacienteCommandFactory;
import cl.grupobios.fichatecnica.exceptions.ConcurrencyException;
import cl.grupobios.fichatecnica.exceptions.ResourceNotFoundException;
import cl.grupobios.fichatecnica.exceptions.ValidationException;
import cl.grupobios.fichatecnica.models.Genero;
import cl.grupobios.fichatecnica.models.Paciente;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test unitario para PacienteController")
class PacienteControllerTest {
    
    private MockMvc mockMvc;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private PacienteCommandFactory commandFactory;

    @InjectMocks
    private PacienteController pacienteController;

    private Paciente paciente1;
    private Paciente paciente2;
    private Genero genero;
    private TipoSangre tipoSangre;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pacienteController).build();
        
        // Datos de prueba
        genero = new Genero(1L, "Masculino", "M");
        tipoSangre = new TipoSangre(1L, "A+");
        
        paciente1 = new Paciente(
            1L, 
            "PAC-001", 
            "Juan Pérez", 
            "12345678-9", 
            "juan@email.com",
            LocalDate.of(1990, 5, 15),
            33,
            genero,
            tipoSangre
        );
        
        paciente2 = new Paciente(
            2L, 
            "PAC-002", 
            "María García", 
            "98765432-1", 
            "maria@email.com",
            LocalDate.of(1985, 10, 20),
            38,
            genero,
            tipoSangre
        );
    }

    @Nested
    @DisplayName("Tests para GET /api/v1/pacientes/listar")
    class ObtenerTodosLosPacientesTests {

        @Test
        @DisplayName("Debería retornar lista de pacientes con estado 200 OK")
        void deberiaRetornarListaDePacientes() throws Exception {
            // Arrange
            List<Paciente> pacientes = Arrays.asList(paciente1, paciente2);
            
            // Mock del comportamiento del command
            when(commandFactory.obtenerTodosLosPacientes()).thenReturn(() -> pacientes);

            // Act & Assert
            mockMvc.perform(get("/api/v1/pacientes/listar")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].nombrePaciente").value("Juan Pérez"))
                    .andExpect(jsonPath("$[1].nombrePaciente").value("María García"));

            verify(commandFactory, times(1)).obtenerTodosLosPacientes();
        }

        @Test
        @DisplayName("Debería retornar 204 No Content cuando hay excepción")
        void deberiaRetornarNoContentCuandoHayExcepcion() throws Exception {
            // Arrange
            when(commandFactory.obtenerTodosLosPacientes())
                    .thenReturn(() -> { throw new RuntimeException("Error en la base de datos"); });

            // Act & Assert
            mockMvc.perform(get("/api/v1/pacientes/listar"))
                    .andExpect(status().isNoContent());

            verify(commandFactory, times(1)).obtenerTodosLosPacientes();
        }
    }

    @Nested
    @DisplayName("Tests para GET /api/v1/pacientes/listar/{id}")
    class BuscarPacientePorIdTests {
        
        @Test
        @DisplayName("Debería retornar paciente por ID con estado 200 OK")
        void deberiaRetornarPacientePorId() throws Exception {
            // Arrange
            when(commandFactory.buscarPorId(1L))
                    .thenReturn(() -> Optional.of(paciente1));

            // Act & Assert
            mockMvc.perform(get("/api/v1/pacientes/listar/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nombrePaciente").value("Juan Pérez"));

            verify(commandFactory, times(1)).buscarPorId(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando paciente no existe")
        void deberiaRetornarNotFoundCuandoPacienteNoExiste() throws Exception {
            // Arrange
            when(commandFactory.buscarPorId(99L))
                    .thenReturn(Optional::empty); // Retorna Optional vacío

            // Act & Assert
            mockMvc.perform(get("/api/v1/pacientes/listar/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(commandFactory, times(1)).buscarPorId(99L);
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request cuando hay excepción")
        void deberiaRetornarBadRequestCuandoHayExcepcion() throws Exception {
            // Arrange
            when(commandFactory.buscarPorId(99L))
                    .thenReturn(() -> { throw new RuntimeException("Error"); });

            // Act & Assert
            mockMvc.perform(get("/api/v1/pacientes/listar/{id}", 99L))
                    .andExpect(status().isBadRequest());

            verify(commandFactory, times(1)).buscarPorId(99L);
        }
    }

    @Nested
    @DisplayName("Tests para POST /api/v1/pacientes/create")
    class RegistrarPacienteTests {
        
        @Test
        @DisplayName("Debería registrar paciente exitosamente con estado 201 Created")
        void deberiaRegistrarPacienteExitosamente() throws Exception {
            // Arrange
            String pacienteJson = "{\"nombrePaciente\":\"Carlos López\"}";
            
            Paciente nuevoPaciente = new Paciente(
                3L, 
                "PAC-003", 
                "Carlos López", 
                "11222333-4", 
                "carlos@email.com",
                LocalDate.of(1992, 3, 10),
                31,
                genero,
                tipoSangre
            );
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(nuevoPaciente);
            when(commandFactory.createPacienteCommand(any(Paciente.class)))
                    .thenReturn(() -> nuevoPaciente);

            // Act & Assert
            mockMvc.perform(post("/api/v1/pacientes/create")
                    .param("paciente", pacienteJson))
                    .andExpect(status().isCreated());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).createPacienteCommand(any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request por ValidationException")
        void deberiaRetornarBadRequestPorValidationException() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.createPacienteCommand(any(Paciente.class)))
                    .thenReturn(() -> { throw new ValidationException("Datos inválidos"); });

            // Act & Assert
            mockMvc.perform(post("/api/v1/pacientes/create")
                    .param("paciente", pacienteJson))
                    .andExpect(status().isBadRequest());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).createPacienteCommand(any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found por ResourceNotFoundException")
        void deberiaRetornarNotFoundPorResourceNotFoundException() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.createPacienteCommand(any(Paciente.class)))
                    .thenReturn(() -> { throw new ResourceNotFoundException("Recurso no encontrado"); });

            // Act & Assert
            mockMvc.perform(post("/api/v1/pacientes/create")
                    .param("paciente", pacienteJson))
                    .andExpect(status().isNotFound());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).createPacienteCommand(any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 409 Conflict por ConcurrencyException")
        void deberiaRetornarConflictPorConcurrencyException() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.createPacienteCommand(any(Paciente.class)))
                    .thenReturn(() -> { throw new ConcurrencyException("Conflicto de concurrencia"); });

            // Act & Assert
            mockMvc.perform(post("/api/v1/pacientes/create")
                    .param("paciente", pacienteJson))
                    .andExpect(status().isConflict());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).createPacienteCommand(any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 500 Internal Server Error por excepción general")
        void deberiaRetornarInternalServerErrorPorExcepcionGeneral() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.createPacienteCommand(any(Paciente.class)))
                    .thenReturn(() -> { throw new RuntimeException("Error interno"); });

            // Act & Assert
            mockMvc.perform(post("/api/v1/pacientes/create")
                    .param("paciente", pacienteJson))
                    .andExpect(status().isInternalServerError());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).createPacienteCommand(any(Paciente.class));
        }
    }

    @Nested
    @DisplayName("Tests para PUT /api/v1/pacientes/update/{id}")
    class ActualizarPacienteTests {
        
        @Test
        @DisplayName("Debería actualizar paciente exitosamente con estado 200 OK")
        void deberiaActualizarPacienteExitosamente() throws Exception {
            // Arrange
            String pacienteJson = "{\"nombrePaciente\":\"Juan Pérez Actualizado\"}";
            
            Paciente pacienteActualizado = new Paciente(
                1L, 
                "PAC-001", 
                "Juan Pérez Actualizado", 
                "12345678-9", 
                "juan.nuevo@email.com",
                LocalDate.of(1990, 5, 15),
                33,
                genero,
                tipoSangre
            );
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(pacienteActualizado);
            when(commandFactory.updatePacienteCommand(eq(1L), any(Paciente.class)))
                    .thenReturn(() -> pacienteActualizado);

            // Act & Assert
            mockMvc.perform(put("/api/v1/pacientes/update/{id}", 1L)
                    .param("paciente", pacienteJson))
                    .andExpect(status().isOk());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).updatePacienteCommand(eq(1L), any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 400 Bad Request por validación fallida")
        void deberiaRetornarBadRequestPorValidacionFallida() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.updatePacienteCommand(eq(1L), any(Paciente.class)))
                    .thenReturn(() -> { throw new ValidationException("Datos inválidos"); });

            // Act & Assert
            mockMvc.perform(put("/api/v1/pacientes/update/{id}", 1L)
                    .param("paciente", pacienteJson))
                    .andExpect(status().isBadRequest());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).updatePacienteCommand(eq(1L), any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found por paciente no encontrado")
        void deberiaRetornarNotFoundPorPacienteNoEncontrado() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.updatePacienteCommand(eq(1L), any(Paciente.class)))
                    .thenReturn(() -> { throw new ResourceNotFoundException("Paciente no encontrado"); });

            // Act & Assert
            mockMvc.perform(put("/api/v1/pacientes/update/{id}", 1L)
                    .param("paciente", pacienteJson))
                    .andExpect(status().isNotFound());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).updatePacienteCommand(eq(1L), any(Paciente.class));
        }

        @Test
        @DisplayName("Debería retornar 409 Conflict por ConcurrencyException")
        void deberiaRetornarConflictPorConcurrencyException() throws Exception {
            // Arrange
            String pacienteJson = "{}";
            Paciente paciente = new Paciente();
            
            when(objectMapper.readValue(eq(pacienteJson), eq(Paciente.class))).thenReturn(paciente);
            when(commandFactory.updatePacienteCommand(eq(1L), any(Paciente.class)))
                    .thenReturn(() -> { throw new ConcurrencyException("Conflicto de concurrencia"); });

            // Act & Assert
            mockMvc.perform(put("/api/v1/pacientes/update/{id}", 1L)
                    .param("paciente", pacienteJson))
                    .andExpect(status().isConflict());

            verify(objectMapper, times(1)).readValue(eq(pacienteJson), eq(Paciente.class));
            verify(commandFactory, times(1)).updatePacienteCommand(eq(1L), any(Paciente.class));
        }
    }

    @Nested
    @DisplayName("Tests para DELETE /api/v1/pacientes/delete/{id} (Soft Delete)")
    class EliminarPacienteTests {
        
        @Test
        @DisplayName("Debería realizar soft delete exitosamente con estado 200 OK")
        void deberiaEliminarPacienteExitosamente() throws Exception {
            // Arrange
            when(commandFactory.eliminarPacienteCommand(1L))
                    .thenReturn(() -> null);

            // Act & Assert
            mockMvc.perform(delete("/api/v1/pacientes/delete/{id}", 1L))
                    .andExpect(status().isOk());

            verify(commandFactory, times(1)).eliminarPacienteCommand(1L);
        }

        @Test
        @DisplayName("Debería retornar 404 Not Found cuando paciente no existe")
        void deberiaRetornarNotFoundCuandoPacienteNoExiste() throws Exception {
            // Arrange
            when(commandFactory.eliminarPacienteCommand(99L))
                    .thenReturn(() -> { throw new ResourceNotFoundException("Paciente no encontrado"); });

            // Act & Assert
            mockMvc.perform(delete("/api/v1/pacientes/delete/{id}", 99L))
                    .andExpect(status().isNotFound());

            verify(commandFactory, times(1)).eliminarPacienteCommand(99L);
        }

        @Test
        @DisplayName("Debería retornar 500 Internal Server Error por excepción general")
        void deberiaRetornarInternalServerErrorPorExcepcionGeneral() throws Exception {
            // Arrange
            when(commandFactory.eliminarPacienteCommand(1L))
                    .thenReturn(() -> { throw new RuntimeException("Error en la base de datos"); });

            // Act & Assert
            mockMvc.perform(delete("/api/v1/pacientes/delete/{id}", 1L))
                    .andExpect(status().isInternalServerError());

            verify(commandFactory, times(1)).eliminarPacienteCommand(1L);
        }
    }
}