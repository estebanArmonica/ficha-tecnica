package cl.grupobios.fichatecnica.command.impl.genero;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.Genero;

public class ListIdGeneroCommandImpl implements Command<Genero> {

    private final Long id;

    public ListIdGeneroCommandImpl(Long id){
        this.id = id;
    }

    public Long getId(){
        return id;
    }

    @Override
    public Genero execute() {
        throw new UnsupportedOperationException("Este comando debe ser procesado por un CommandHandler");
    }
    
}
