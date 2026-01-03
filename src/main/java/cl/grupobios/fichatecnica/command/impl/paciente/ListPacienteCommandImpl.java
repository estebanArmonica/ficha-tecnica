package cl.grupobios.fichatecnica.command.impl.paciente;

import java.util.List;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;

public class ListPacienteCommandImpl implements Command<List<Paciente>> {
    private final IPacienteRepository pacienteRepository;

    public ListPacienteCommandImpl(IPacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public List<Paciente> execute() {
        return pacienteRepository.findAllWithRelations();
    }
    
}
