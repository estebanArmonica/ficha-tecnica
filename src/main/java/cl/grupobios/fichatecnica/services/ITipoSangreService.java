package cl.grupobios.fichatecnica.services;

import java.util.Set;

import cl.grupobios.fichatecnica.models.TipoSangre;

public interface ITipoSangreService {
    
    // agregamos un tipo de sangre
    TipoSangre agregarTipoSangre(TipoSangre tipoSangre);

    // obtenemos un tipo de sangre por su id
    TipoSangre obtenerTipoSangrePorId(Long id);

    // listamos todos los tipos de sangre
    Set<TipoSangre> listarTiposSangre();
}
