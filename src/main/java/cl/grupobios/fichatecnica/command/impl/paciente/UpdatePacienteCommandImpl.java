package cl.grupobios.fichatecnica.command.impl.paciente;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.exceptions.BusinessException;
import cl.grupobios.fichatecnica.exceptions.ResourceNotFoundException;
import cl.grupobios.fichatecnica.exceptions.ValidationException;
import cl.grupobios.fichatecnica.models.Genero;
import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.models.TipoSangre;
import cl.grupobios.fichatecnica.repositories.IGeneroRepository;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;
import cl.grupobios.fichatecnica.repositories.ITipoSangreRepository;

public class UpdatePacienteCommandImpl implements Command<Paciente> {
    private final Long id;
    private final Paciente paciente;
    private final IPacienteRepository pacienteRepository;
    private final IGeneroRepository generoRepository;
    private final ITipoSangreRepository tipoSangreRepository;

    public UpdatePacienteCommandImpl(Long id, Paciente paciente, IPacienteRepository pacienteRepository, IGeneroRepository generoRepository, ITipoSangreRepository tipoSangreRepository) {
        this.id = id;
        this.paciente = paciente;
        this.pacienteRepository = pacienteRepository;
        this.generoRepository = generoRepository;
        this.tipoSangreRepository = tipoSangreRepository;
    }

    @Override
    public Paciente execute() {
        try {
            Paciente pacienteExiste = validateAndGetId();
            updatePacienteFields(pacienteExiste);
            processGenero(pacienteExiste);
            processTipoSangre(pacienteExiste);

            return savePacientes(pacienteExiste);
        } catch (Exception e) {
            throw new ValidationException("Error en poder actualizar los datos: " + e.getMessage());
        }
    }

    // metodo privado que valida el id del paciente si es que existe dicho id
    private Paciente validateAndGetId(){
        if(id <= 0) {
            throw new ValidationException("ID de paciente inválido");
        }

        return pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
    }

    private void updatePacienteFields(Paciente pacienteExistente) {
        if(paciente.getNombrePaciente() != null) {
            pacienteExistente.setNombrePaciente(paciente.getNombrePaciente());
        }

        if(paciente.getRutPaciente() != null) {
            pacienteExistente.setRutPaciente(paciente.getRutPaciente());
        }

        if(paciente.getCorreoPaciente() != null) {
            pacienteExistente.setCorreoPaciente(paciente.getCorreoPaciente());
        }

        if (paciente.getFechaNacimiento() != null) {
            pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
        }

        if (paciente.getGenero() != null) {
            pacienteExistente.setGenero(paciente.getGenero());
        }

        if (paciente.getTipoSangre() != null) {
            pacienteExistente.setTipoSangre(paciente.getTipoSangre());
        }
    }

    private void processGenero(Paciente pacienteExistente) {
        if (paciente.getGenero() != null && paciente.getGenero().getId() != 0) {
            Genero generos = generoRepository.findById(paciente.getGenero().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genero no encontrada"));

            pacienteExistente.setGenero(generos);
        }
    }

    private void processTipoSangre(Paciente pacienteExistente) {
        if (paciente.getTipoSangre() != null && paciente.getTipoSangre().getId() != 0) {
            TipoSangre tipoSangre = tipoSangreRepository.findById(paciente.getTipoSangre().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genero no encontrada"));

            pacienteExistente.setTipoSangre(tipoSangre);
        }
    }

    private Paciente savePacientes(Paciente paciente) {
        try {
            return pacienteRepository.save(paciente);
        } catch (Exception e) {
            throw new BusinessException("Violación de integridad de datos: " + e.getMessage());
        }
    }
    
}
