package cl.grupobios.fichatecnica.command.impl.tipoSangre;

import java.util.Set;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.TipoSangre;

public class ListTipoSangreCommandImpl implements Command<Set<TipoSangre>>{

    public ListTipoSangreCommandImpl() {}

    @Override
    public Set<TipoSangre> execute() {
        throw new UnsupportedOperationException("Este comando debe ser procesado por un CommandHandler");
    }
    
}
