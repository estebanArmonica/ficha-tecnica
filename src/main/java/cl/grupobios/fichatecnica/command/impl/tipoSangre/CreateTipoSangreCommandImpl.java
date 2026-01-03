package cl.grupobios.fichatecnica.command.impl.tipoSangre;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.TipoSangre;

public class CreateTipoSangreCommandImpl implements Command<TipoSangre> {
    private final TipoSangre tipoSangre;

    public CreateTipoSangreCommandImpl(TipoSangre tipoSangre) {
        this.tipoSangre = tipoSangre;
    }
    
    @Override
    public TipoSangre execute() {
        return tipoSangre;
    }
    
}
