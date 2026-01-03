package cl.grupobios.fichatecnica.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.command.factory.PacienteCommandFactory;
import cl.grupobios.fichatecnica.exceptions.ConcurrencyException;
import cl.grupobios.fichatecnica.exceptions.ResourceNotFoundException;
import cl.grupobios.fichatecnica.exceptions.ValidationException;
import cl.grupobios.fichatecnica.models.Paciente;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/pacientes/")
@Tag(name = "Pacientes", description = "Controlador de pacientes")
public class PacienteController {

    private final ObjectMapper objectMapper;
    private final PacienteCommandFactory commandFactory;

    @Autowired
    public PacienteController(ObjectMapper objectMapper, PacienteCommandFactory commandFactory) {
        this.objectMapper = objectMapper;
        this.commandFactory = commandFactory;
    }

    /*
     * Retorna un listado en json de todos los pacientes regisrados en el sistema
     * (GET)
     */
    @GetMapping("listar")
    @Operation(summary = "Listado completo de todos los pacientes registrados", description = "Devuelve un listado de todos los pacientes registrados", tags = {
            "Pacientes" }, responses = {
                    @ApiResponse(responseCode = "200", description = "Listado de pacientes obtenidos sin problemas", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Paciente.class))),
                    @ApiResponse(responseCode = "204", description = "Sin respuesta del listado")
            })
    public ResponseEntity<List<Paciente>> obtenerTodo() {
        try {

            // creamos un command y llamamos al factory para traernos el metodo de listado
            // completo (findAll)
            Command<List<Paciente>> command = commandFactory.obtenerTodosLosPacientes();

            // creamos una lista de todos los pacientes para el command
            List<Paciente> pacientes = command.execute();

            // retornamos la lista de pacientes
            return ResponseEntity.ok(pacientes);
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    /*
     * Retorna en json un paciente regisrados en el sistema a travéz de la busqueda
     * de si ID (GET/id)
     */
    @GetMapping("listar/{id}")
    @Operation(summary = "Listamos a un paciente en especifico por el id", description = "Devuelve un solo paciente buscado por su id", tags = {
            "Pacientes" }, responses = {
                    @ApiResponse(responseCode = "200", description = "Busqueda del paciente sin problemas", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Paciente.class))),
                    @ApiResponse(responseCode = "204", description = "Sin respuesta del listado")
            })
    public ResponseEntity<?> buscarPorId(@PathVariable("id") Long id) {
        try {
            Command<Optional<Paciente>> command = commandFactory.buscarPorId(id);
            Optional<Paciente> pacienteOptional = command.execute(); // <-- EJECUTAR el command
            
            if (pacienteOptional.isPresent()) {
                return ResponseEntity.ok(pacienteOptional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // registramos un paciente en el sistema (POST)
    @PostMapping("create")
    @Operation(summary = "Registramos un nuevo paciente en el sistema", description = "Devuelve un nuevo registro del paciente", tags = {
            "Pacientes" }, responses = {
                    @ApiResponse(responseCode = "201", description = "Registro del paciente creado sin problemas", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Paciente.class))),
                    @ApiResponse(responseCode = "400", description = "Error de validación"),
                    @ApiResponse(responseCode = "409", description = "Conflict, (e.g. El paciente ya está registrado)"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    public ResponseEntity<Paciente> registrarPaciente(@RequestParam("paciente") String pacienteJson) throws Exception {
        try {
            Paciente paciente = objectMapper.readValue(pacienteJson, Paciente.class);
            Command<Paciente> createCommand = commandFactory.createPacienteCommand(paciente);

            // registramos los datos
            Paciente nuevoPaciente = createCommand.execute();

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPaciente);
        } catch (ValidationException ex) {
            System.out.println("BAD REQUEST: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (ResourceNotFoundException ex) {
            System.out.println("NOT FOUND: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ConcurrencyException ex) {
            System.out.println("CONFLICT: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception ex) {
            System.out.println("INTERNAL SERVER ERROR (Exception): " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // actualizamos un paciente registrado y existente en el sistema (PUT/id)
    @PutMapping("update/{id}")
    @Operation(summary = "Actualizamos un paciente existente en el sistema", description = "Devuelve una actualización de un paciente que ya existe en el sistema", tags = {
            "Pacientes" }, responses = {
                    @ApiResponse(responseCode = "201", description = "Registro del paciente creado sin problemas", content = @Content(mediaType = "application/json", schema = @Schema(type = "array", implementation = Paciente.class))),
                    @ApiResponse(responseCode = "400", description = "Error de validación"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    public ResponseEntity<Paciente> actualizarPaciente(@PathVariable("id") Long id,
            @RequestParam("paciente") String pacienteJson) throws Exception {
        try {
            Paciente paciente = objectMapper.readValue(pacienteJson, Paciente.class);
            Command<Paciente> updateCommand = commandFactory.updatePacienteCommand(id, paciente);

            // registramos los datos para actualizar el paciente
            Paciente nuevoPaciente = updateCommand.execute();

            return ResponseEntity.status(HttpStatus.OK).body(nuevoPaciente);
        } catch (ValidationException ex) {
            System.out.println("BAD REQUEST: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (ResourceNotFoundException ex) {
            System.out.println("NOT FOUND: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (ConcurrencyException ex) {
            System.out.println("CONFLICT: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        } catch (Exception ex) {
            System.out.println("INTERNAL SERVER ERROR (Exception): " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("delete/{id}")
    @Operation(summary = "Eliminación lógica (soft delete) de un paciente", description = "Realiza una eliminación lógica cambiando el estado activo a false", tags = {
            "Pacientes" }, responses = {
                    @ApiResponse(responseCode = "200", description = "Paciente desactivado exitosamente"),
                    @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            })
    public ResponseEntity<Void> eliminarPaciente(@PathVariable("id") Long id) {
        try {
            Command<Void> deleteCommand = commandFactory.eliminarPacienteCommand(id);
            deleteCommand.execute();
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException ex) {
            System.out.println("NOT FOUND: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            System.out.println("INTERNAL SERVER ERROR: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "{id}/ficha-tecnia/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @Operation(
        summary = "Generar ficha técnica en PDF",
        description = "Genera y descarga la ficha técnica de un paciente en formato PDF"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "PDF generado exitosamente",
                     content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "404", description = "Paciente no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error al generar el PDF")
    })
    public ResponseEntity<Resource> generarFichaTecnicaPDF(
            @Parameter(description = "ID del paciente", required = true, example = "1")
            @PathVariable Long id) {
        
        try {
            // verificamos que el paciente exista
            Command<Optional<Paciente>> buscarCommand = commandFactory.buscarPorId(id);

            Optional<Paciente> pacOptional = buscarCommand.execute();

            if(!pacOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // generamos el PDF usando el archivo jrxml
            Command<byte[]> pdfCommand = commandFactory.generarFichaTecnicaPDFCommand(id);

            byte[] pdfBytes = pdfCommand.execute();

            // creamos el nombre del archio con el nombre del paciente
            String nombrePaciente = pacOptional.get().getNombrePaciente().replace(" ", "_");
            String nombreArchivo = String.format("Ficha_Tecnica_%s_%d.pdf", nombrePaciente, id);

            // creamos el recurso
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);

            // retornamos el respuesta final
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + nombreArchivo + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(resource);
                    
        } catch (ResourceNotFoundException ex) {
            System.out.println("NOT FOUND: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception ex) {
            System.out.println("INTERNAL SERVER ERROR: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
