package cl.grupobios.fichatecnica.command.impl.genero;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.Genero;

public class CreateGeneroCommandImpl implements Command<Genero>{
    private final Genero genero;

    public CreateGeneroCommandImpl(Genero genero) {
        this.genero = genero;
    }

    @Override
    public Genero execute() {
        return genero;
    }
    
}
