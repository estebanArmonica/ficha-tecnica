package cl.grupobios.fichatecnica.command.impl.tipoSangre;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.TipoSangre;

public class ListIdTipoSangreCommandImpl implements Command<TipoSangre>{

    private final Long id;

    public ListIdTipoSangreCommandImpl(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    @Override
    public TipoSangre execute() {
        throw new UnsupportedOperationException("Este comando debe ser procesado por un CommandHandler");
    }
    
}
