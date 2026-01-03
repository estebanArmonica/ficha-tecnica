package cl.grupobios.fichatecnica.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.grupobios.fichatecnica.command.handler.TipoSangreCommandHandler;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.CreateTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.ListIdTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.ListTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.models.TipoSangre;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/v1/tipo-sangre/")
@Tag(name = "TipoSangre", description = "Controlador de tipo de sangre")
public class TipoSangreController {
    
    private final TipoSangreCommandHandler commandHandler;

    @Autowired
    public TipoSangreController(TipoSangreCommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @GetMapping("listar")
    @Operation(
        summary = "Listado de todos los tipos de sangres registrados",
        description = "Devuelve un listado completo de los tipos de sangres",
        tags = {"TipoSangre"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista de tipos de sangres exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = TipoSangre.class)
                )
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Sin respuesta del listado de tipos de sangre"
            )
        }
    )
    public ResponseEntity<?> obtenerTodo() {
        ListTipoSangreCommandImpl command = new ListTipoSangreCommandImpl();
        return ResponseEntity.ok(commandHandler.handler(command));
    }

    @GetMapping("listar/{id}")
    @Operation(
        summary = "Lista un solo tipo de sangre por el id",
        description = "Devuelve un solo tipo de sangre buscado por el id",
        tags = {"TipoSangre"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Lista del tipo de sangre especifico exitoso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = TipoSangre.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "El tipo de sangre no fue encontrado"
            )
        }
    )
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        ListIdTipoSangreCommandImpl command = new ListIdTipoSangreCommandImpl(id);
        return ResponseEntity.ok(commandHandler.handler(command));
    }

    @PostMapping("create")
    @Operation(
        summary = "Registra un tipo de sangre en el sistema",
        description = "Devuelve una registración de un nuevo tipo de sangre",
        tags = {"TipoSangre"},
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Registración del tipo de sangre fue exitoso",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = TipoSangre.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error de validación (e.g. campo nombre de tipo de sangre no puede estár vácia)"
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Conflict (e.g. El tipo de sangre ya existe)"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
    )
    public ResponseEntity<TipoSangre> agregarTipoSangre(@Valid @RequestBody TipoSangre tipo){
        CreateTipoSangreCommandImpl command = new CreateTipoSangreCommandImpl(tipo);
        return ResponseEntity.status(HttpStatus.CREATED).body(commandHandler.handler(command));
    }
}
