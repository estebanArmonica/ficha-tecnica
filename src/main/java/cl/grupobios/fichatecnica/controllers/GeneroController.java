package cl.grupobios.fichatecnica.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.grupobios.fichatecnica.command.handler.GeneroCommandHandler;
import cl.grupobios.fichatecnica.command.impl.genero.CreateGeneroCommandImpl;
import cl.grupobios.fichatecnica.command.impl.genero.ListGeneroCommandImpl;
import cl.grupobios.fichatecnica.command.impl.genero.ListIdGeneroCommandImpl;
import cl.grupobios.fichatecnica.models.Genero;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("api/v1/generos/")
@Tag(name = "Generos", description = "Controlador de géneros")
public class GeneroController {
    
    private final GeneroCommandHandler commandHandler;

    @Autowired
    public GeneroController(GeneroCommandHandler commandHandler){
        this.commandHandler = commandHandler;
    }

    /*
     * Este es un endpoint GET el cual devuelve todos los generos registrados en la base de datos
    */
   @GetMapping("listar")
   @Operation(
        summary = "Listado de todos los generos registrados",
        description = "Devuelve un listado con todos los generos regustrados en la base de datos",
        tags = {"Generos"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Listado de generos obtenidos exitosamente",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Genero.class)
                )
            ),
            @ApiResponse(
                responseCode = "204",
                description = "Listado de genero sin respuesta"
            )
        }
   )
   public ResponseEntity<?> obtenerTodo() {
        ListGeneroCommandImpl command = new ListGeneroCommandImpl();
        return ResponseEntity.ok(commandHandler.handler(command));
   }

   @GetMapping("lista/{id}")
   @Operation(
        summary = "Obtenemos un genero por el id",
        description = "Devuelve a travéz de su id un genero en especifico",
        tags = {"Generos"},
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Genero encontrado",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Genero.class)
                )
            ),
            @ApiResponse(
                responseCode = "404",
                description = "Genero no encontrado"
            )
        }
   )
   public ResponseEntity<Genero> obtenerPorId(@PathVariable Long id) {
        ListIdGeneroCommandImpl command = new ListIdGeneroCommandImpl(id);
        
        Genero genero = commandHandler.handler(command);
        if (genero == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(genero);
   }

   @PostMapping("create")
   @Operation(
        summary = "Registramos un genero en el sistema",
        description = "Registramos un genero que no este en el sistema",
        tags = {"Generos"},
        responses = {
            @ApiResponse(
                responseCode = "201",
                description = "Registracion del genero fue un exito",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = Genero.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Error validación (e.g. campo nombre genero vacío)"
            ),
            @ApiResponse(
                responseCode = "409",
                description = "Conflict (e.g. El genero ya existe)"
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Error interno del servidor"
            )
        }
   )
   public ResponseEntity<Genero> agregarGenero(@Valid @RequestBody Genero genero) {
        CreateGeneroCommandImpl command = new CreateGeneroCommandImpl(genero);
        Genero generoCreado = commandHandler.handler(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(generoCreado);
   }   
}
