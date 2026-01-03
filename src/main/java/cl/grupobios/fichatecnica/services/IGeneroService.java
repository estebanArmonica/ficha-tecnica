package cl.grupobios.fichatecnica.services;

import java.util.Set;

import cl.grupobios.fichatecnica.models.Genero;

public interface IGeneroService {
    
    // agregamos un genero
    Genero agregarGenero(Genero genero);

    // listamos todos los generos
    Set<Genero> listarGeneros();

    // obtenemos un genero por su ID
    Genero obtenerGeneroPorId(Long generoId);
}
