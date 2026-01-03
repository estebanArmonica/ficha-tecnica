package cl.grupobios.fichatecnica.command.handler;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.grupobios.fichatecnica.command.impl.tipoSangre.CreateTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.ListIdTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.command.impl.tipoSangre.ListTipoSangreCommandImpl;
import cl.grupobios.fichatecnica.models.TipoSangre;
import cl.grupobios.fichatecnica.services.ITipoSangreService;

@Service
@Transactional
public class TipoSangreCommandHandler {
    private ITipoSangreService tipoSangreService;

    @Autowired
    public TipoSangreCommandHandler(ITipoSangreService tipoSangreService) {
        this.tipoSangreService = tipoSangreService;
    }

    // listamos todos los tipos de sangres disponibles (GET)
    @Transactional(readOnly = true)
    public Set<TipoSangre> handler(ListTipoSangreCommandImpl command) {
        return tipoSangreService.listarTiposSangre();
    }

    // buscamos un tipo de sangre por el id (GET/id)
    @Transactional(readOnly = true)
    public TipoSangre handler(ListIdTipoSangreCommandImpl command) {
        return tipoSangreService.obtenerTipoSangrePorId(command.getId());
    }

    // registramos un tipo de sangre (POST)
    public TipoSangre handler(CreateTipoSangreCommandImpl command) {
        // realizamos validacion de datos
        if(command.execute().getNombreTipoSangre() == null){
            throw new IllegalArgumentException("El nombre del tipo de sangre es requerido");
        }

        return tipoSangreService.agregarTipoSangre(command.execute());
    }
}
