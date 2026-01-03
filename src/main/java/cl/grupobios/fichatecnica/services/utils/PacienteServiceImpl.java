package cl.grupobios.fichatecnica.services.utils;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;
import cl.grupobios.fichatecnica.services.IPacienteService;
import cl.grupobios.fichatecnica.utils.GeneratorCodigo;
import cl.grupobios.fichatecnica.utils.ReporteUtils;
@Service
public class PacienteServiceImpl implements IPacienteService {

    @Autowired
    private ReporteUtils reporteUtils;

    @Autowired
    private IPacienteRepository pacienteRepository;

    public PacienteServiceImpl(IPacienteRepository pacienteRepository){
        this.pacienteRepository = pacienteRepository;
    }

    // obtenemos la lista de los pacientes
    @Override
    public Set<Paciente> obtenerPacientes() {
        // utilizamos findAll para obtener todos los pacientes registrados en la base de datos
        return new LinkedHashSet<>(pacienteRepository.findAll());
    }

    // este override obtiene un paciente por su ID
    @Override
    public Optional<Paciente> obtenerPacientePorId(Long idPaciente) {
        return pacienteRepository.findByIdAndActivoTrue(idPaciente);
    }

    // registramos un paciente en el sistema
    @Override
    public Paciente agregarPaciente(Paciente paciente) {
        
        // Verificar si el RUT ya existe
        if (existePacientePorRut(paciente.getRutPaciente())) {
            throw new RuntimeException("Ya existe un paciente con el RUT: " + paciente.getRutPaciente());
        }
        
        // Verificar si el correo ya existe (si aplica)
        if (paciente.getCorreoPaciente() != null && 
            existePacientePorCorreo(paciente.getCorreoPaciente())) {
            throw new RuntimeException("Ya existe un paciente con el correo: " + paciente.getCorreoPaciente());
        }

        String numeroPaciente = generarNumeroPacienteUnico();
        paciente.setNroPaciente(numeroPaciente);
        paciente.setActivo(true); // por defecto el paciente se registra como activo
        
        try {
            // 4. Intentar guardar
            return pacienteRepository.save(paciente);
        } catch (DataIntegrityViolationException e) {
            // 5. Manejar error de restricción única
            if (e.getMessage().contains("UKGD287WRMSYB7ACCI6U2WXOMU9")) {
                throw new RuntimeException("Error de duplicado: Verifique que el RUT, número o correo no estén registrados");
            }
            throw new RuntimeException("Error al guardar el paciente: " + e.getMessage(), e);
        }
    }

    // actualizamos un paciente existente buscando su id
    @Override
    public Paciente actualizarPaciente(Long idPaciente, Paciente paciente) {
        Paciente pacienteExistente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // Verificar si el RUT cambió y si ya existe
        if (!pacienteExistente.getRutPaciente().equals(paciente.getRutPaciente()) &&
            pacienteRepository.existsByRutPaciente(paciente.getRutPaciente())) {
            throw new RuntimeException("Ya existe otro paciente con el RUT: " + paciente.getRutPaciente());
        }
        
        // Verificar si el correo cambió y si ya existe
        if (paciente.getCorreoPaciente() != null && 
            !pacienteExistente.getCorreoPaciente().equals(paciente.getCorreoPaciente()) &&
            pacienteRepository.existsByCorreoPaciente(paciente.getCorreoPaciente())) {
            throw new RuntimeException("Ya existe otro paciente con el correo: " + paciente.getCorreoPaciente());
        }

        pacienteExistente.setNombrePaciente(paciente.getNombrePaciente());
        pacienteExistente.setCorreoPaciente(paciente.getCorreoPaciente());
        pacienteExistente.setRutPaciente(paciente.getRutPaciente());
        pacienteExistente.setFechaNacimiento(paciente.getFechaNacimiento());
        pacienteExistente.setGenero(paciente.getGenero());
        pacienteExistente.setTipoSangre(paciente.getTipoSangre());

        // la edad del paciente no se actualiza ya que depende de la fecha de nacimiento 
        // el nro del paciente no se toca ya que es unico y aleatorio
        // el estado de activo tampoco se actualiza en este metodo

        try {
            return pacienteRepository.save(pacienteExistente);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Error al actualizar: Verifique que los datos únicos no estén duplicados", e);
        }
    }

    // este método para eliminar un paciete es un soft delete (eliminacion logica)
    @Override
    public void eliminarPaciente(Long idPaciente) {
        Paciente pacienteExistente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        // eliminación logica: aquí lo que hacemos es cambiar el estado del paciente a inactivo
        pacienteExistente.setActivo(false);
        pacienteRepository.save(pacienteExistente);

        /*
         * en el JSON ahora aparecera como eliminado, pero en la base de datos seguira existiendo
         * si se desea eliminar fisicamente de la base de datos, solo de debe cambiar la siguiente linea:
         * pacienteRepository.delete(pacienteExistente);
        */
    }

    private String generarNumeroPacienteUnico() {
        String numeroPaciente;
        boolean existe;

        do{
            // generamos el numero de paciente aleatorio
            numeroPaciente = GeneratorCodigo.generarCodigoAleatorio();

            // Realizamos una verificación para saber si el numero existe en la base de datos de un paciente
            existe = pacienteRepository.existsByNroPaciente(numeroPaciente);

        } while(existe); // repetimos el proceso hasta que se genere un numero unico

        return numeroPaciente;
    }

    /**
     * Genera la ficha técnica de un paciente en formato PDF
     * @param idPaciente ID del paciente
     * @return byte array con el PDF generado
    */
    @Override
    public byte[] generarFichaTecnicaPdf(Long idPaciente) {
        // verificamos que el paciente existe
        Optional<Paciente> paciente = obtenerPacientePorId(idPaciente);

        if (!paciente.isPresent()) {
            throw new RuntimeException("Paciente no encontrado con ID: " + idPaciente);
        }

        // generamos el archivo PDF
        return reporteUtils.generarFichaTecnicaPaciente(idPaciente);
    }
    


    private boolean existePacientePorRut(String rut) {
        return pacienteRepository.existsByRutPaciente(rut);
    }
    
    private boolean existePacientePorCorreo(String correo) {
        return pacienteRepository.existsByCorreoPaciente(correo);
    }
}
