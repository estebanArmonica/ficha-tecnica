package cl.grupobios.fichatecnica.services;

import java.util.Optional;
import java.util.Set;

import cl.grupobios.fichatecnica.models.Paciente;

public interface IPacienteService {

    /*
     * Obtiene la lista de los pacientes.
    */
    Set<Paciente> obtenerPacientes();

    /* 
     *  Obtiene un paciente por su ID. 
    */
   Optional<Paciente> obtenerPacientePorId(Long idPaciente);

    /* 
     * Agrega un nuevo paciente al sistema.
    */
   Paciente agregarPaciente(Paciente paciente);

    /* 
     * Actualiza la información de un paciente existente.
    */
    Paciente actualizarPaciente(Long idPaciente, Paciente paciente);

    /* 
     * Elimina un paciente del sistema por su ID.
    */
    void eliminarPaciente(Long idPaciente);

    /* 
     * Este método sirve para generar un reporte en PDF por un archivo JRXML realizado por JasperReports
    */
    byte[] generarFichaTecnicaPdf(Long idPaciente);
}
