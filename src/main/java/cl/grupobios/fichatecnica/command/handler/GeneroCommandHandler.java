package cl.grupobios.fichatecnica.command.handler;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.grupobios.fichatecnica.command.impl.genero.CreateGeneroCommandImpl;
import cl.grupobios.fichatecnica.command.impl.genero.ListGeneroCommandImpl;
import cl.grupobios.fichatecnica.command.impl.genero.ListIdGeneroCommandImpl;
import cl.grupobios.fichatecnica.models.Genero;
import cl.grupobios.fichatecnica.services.IGeneroService;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class GeneroCommandHandler {
    private IGeneroService generoService;
    
    @Autowired
    public GeneroCommandHandler(IGeneroService generoService) {
        this.generoService = generoService;
    }

    // listamos todos los generos (GET)
    @Transactional(readOnly = true)
    public Set<Genero> handler(ListGeneroCommandImpl command) {
        return generoService.listarGeneros();
    }

    // buscamos un genero por el id (GET/id)
    @Transactional(readOnly = true)
    public Genero handler(ListIdGeneroCommandImpl command) {
        return generoService.obtenerGeneroPorId(command.getId());
    }

    // registramos un genero (POST)
    public Genero handler(CreateGeneroCommandImpl command) {
        // realizamos validaciones de datos
        if(command.execute().getNombreGenero() == null) {
            throw new IllegalArgumentException("El nombre del genero es requerido");
        }

        if(command.execute().getSigla() == null) {
            throw new IllegalArgumentException("La sigla del genero es requerido");
        }

        return generoService.agregarGenero(command.execute());
    }

}
