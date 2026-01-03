package cl.grupobios.fichatecnica.command.factory.utils;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.command.factory.PacienteCommandFactory;
import cl.grupobios.fichatecnica.command.impl.paciente.CreatePacienteCommandImpl;
import cl.grupobios.fichatecnica.command.impl.paciente.DeletePacienteCommandImpl;
import cl.grupobios.fichatecnica.command.impl.paciente.GenerateFichaTecnicaPdfCommandImpl;
import cl.grupobios.fichatecnica.command.impl.paciente.ListIdPacienteCommandImpl;
import cl.grupobios.fichatecnica.command.impl.paciente.ListPacienteCommandImpl;
import cl.grupobios.fichatecnica.command.impl.paciente.UpdatePacienteCommandImpl;
import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.repositories.IGeneroRepository;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;
import cl.grupobios.fichatecnica.repositories.ITipoSangreRepository;
import cl.grupobios.fichatecnica.utils.ReporteUtils;

@Component
public class PacienteCommandFactoryImpl implements PacienteCommandFactory {
    private final IPacienteRepository pacienteRepository;
    private final IGeneroRepository generoRepository;
    private final ITipoSangreRepository tipoSangreRepository;
    private final ReporteUtils reporteUtils;

    public PacienteCommandFactoryImpl(
            IPacienteRepository pacienteRepository, 
            IGeneroRepository generoRepository, 
            ITipoSangreRepository tipoSangreRepository,
            ReporteUtils reporteUtils) { 
        this.pacienteRepository = pacienteRepository;
        this.generoRepository = generoRepository;
        this.tipoSangreRepository = tipoSangreRepository;
        this.reporteUtils = reporteUtils;
    }

    // registramos un paciente
    @Override
    public Command<Paciente> createPacienteCommand(Paciente paciente) {
        return new CreatePacienteCommandImpl(paciente, pacienteRepository, generoRepository, tipoSangreRepository);
    }

    // actualizamos un paciente existente
    @Override
    public Command<Paciente> updatePacienteCommand(Long id, Paciente paciente) {
        return new UpdatePacienteCommandImpl(id, paciente, pacienteRepository, generoRepository, tipoSangreRepository);
    }

    // listamos todos los pacientes registrados
    @Override
    public Command<List<Paciente>> obtenerTodosLosPacientes() {
        return new ListPacienteCommandImpl(pacienteRepository);
    }

    // listamos por el id de un paciente
    @Override
    public Command<Optional<Paciente>> buscarPorId(Long id) {
        return new ListIdPacienteCommandImpl(id, pacienteRepository);
    }

    // eliminamos a un paciente (soft delete)
    @Override
    public Command<Void> eliminarPacienteCommand(Long id) {
        return new DeletePacienteCommandImpl(id, pacienteRepository);
    }

    @Override
    public Command<byte[]> generarFichaTecnicaPDFCommand(Long id) {
        return new GenerateFichaTecnicaPdfCommandImpl(id, reporteUtils);
    }
    
}
