package cl.grupobios.fichatecnica.command.factory;

import java.util.List;
import java.util.Optional;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.models.Paciente;

public interface PacienteCommandFactory {
    
    // registramos un paciente (POST)
    Command<Paciente> createPacienteCommand(Paciente paciente);

    // actualizamos un paciente exstente (PUT)
    Command<Paciente> updatePacienteCommand(Long id, Paciente paciente);

    // listamos todos los pacientes registrados (GET)
    Command<List<Paciente>> obtenerTodosLosPacientes();

    // listamos un paciente en especifico por el id (GET/id)
    Command<Optional<Paciente>> buscarPorId(Long id);

    // borramos a un paciente (soft delete, se borra del json o soap pero no de la bd)
    Command<Void> eliminarPacienteCommand(Long id);

    /**
     * Genera la ficha técnica del paciente en PDF
     * @param id ID del paciente
     * @return Command que genera el PDF
     */
    Command<byte[]> generarFichaTecnicaPDFCommand(Long id);
}
