package cl.grupobios.fichatecnica.command.impl.genero;

import java.util.Set;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.Genero;

public class ListGeneroCommandImpl implements Command<Set<Genero>> {

    public ListGeneroCommandImpl(){}

    @Override
    public Set<Genero> execute() {
        throw new UnsupportedOperationException("Este comando debe ser procesado por un CommandHandler");
    }
}
