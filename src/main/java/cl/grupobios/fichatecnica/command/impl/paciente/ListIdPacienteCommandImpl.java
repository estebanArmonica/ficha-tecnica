package cl.grupobios.fichatecnica.command.impl.paciente;

import java.util.Optional;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;

public class ListIdPacienteCommandImpl implements Command<Optional<Paciente>> {

    private final Long id;
    private final IPacienteRepository pacienteRepository;

    public ListIdPacienteCommandImpl(Long id, IPacienteRepository pacienteRepository) {
        this.id = id;
        this.pacienteRepository = pacienteRepository;
    }

    public Long getId() {
        return id;
    }

    @Override
    public Optional<Paciente> execute() {
        if(id == null || id <= 0){
            throw new UnsupportedOperationException("ID de paciente invalido");
        }

        return pacienteRepository.findByIdWithRelations(id);
    }
}
