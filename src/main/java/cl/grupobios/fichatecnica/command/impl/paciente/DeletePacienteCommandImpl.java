package cl.grupobios.fichatecnica.command.impl.paciente;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.exceptions.ResourceNotFoundException;
import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;

public class DeletePacienteCommandImpl implements Command<Void>{
    private final Long id;
    private final IPacienteRepository pacienteRepository;

    public DeletePacienteCommandImpl(Long id, IPacienteRepository pacienteRepository) {
        this.id = id;
        this.pacienteRepository = pacienteRepository;
    }


    @Override
    public Void execute() {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID de paciente inválido");
        }

        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no se encontro con el ID: " + id));

        // Soft delete: cambiamos el estado actvo a false
        pacienteExistente.setActivo(false);
        pacienteRepository.save(pacienteExistente);

        return null;
    }
    
}
