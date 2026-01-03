package cl.grupobios.fichatecnica.command.impl.paciente;

import cl.grupobios.fichatecnica.command.Command;
import cl.grupobios.fichatecnica.exceptions.ConcurrencyException;
import cl.grupobios.fichatecnica.exceptions.ResourceNotFoundException;
import cl.grupobios.fichatecnica.models.Genero;
import cl.grupobios.fichatecnica.models.Paciente;
import cl.grupobios.fichatecnica.models.TipoSangre;
import cl.grupobios.fichatecnica.repositories.IGeneroRepository;
import cl.grupobios.fichatecnica.repositories.IPacienteRepository;
import cl.grupobios.fichatecnica.repositories.ITipoSangreRepository;
import cl.grupobios.fichatecnica.utils.GeneratorCodigo;
import jakarta.validation.ValidationException;

public class CreatePacienteCommandImpl implements Command<Paciente> {
    private final Paciente paciente;
    private IPacienteRepository pacienteRepository;
    private IGeneroRepository generoRepository;
    private ITipoSangreRepository tipoSangreRepository;

    public CreatePacienteCommandImpl(Paciente paciente, IPacienteRepository pacienteRepository, IGeneroRepository generoRepository, ITipoSangreRepository tipoSangreRepository){
        this.paciente = paciente;
        this.pacienteRepository = pacienteRepository;
        this.generoRepository = generoRepository;
        this.tipoSangreRepository = tipoSangreRepository;
    }


    @Override
    public Paciente execute() {
        try {
            validatePaciente();
            handlerExistePaciente();
            processGenero();
            processTipoSangre();
            generarNumeroPaciente();

            return savePaciente();
        } catch (Exception e) {
            throw new ValidationException("Error al cargar los datos: " + e.getMessage());
        }
    }

    // validamos los datos del paciente
    private void validatePaciente() {
        if(paciente.getNombrePaciente() == null || paciente.getNombrePaciente().trim().isEmpty()){
            throw new ValidationException("El nombre del paciente es requerido");
        }

        if(paciente.getRutPaciente() == null || paciente.getRutPaciente().trim().isEmpty()){
            throw new ValidationException("El RUT del paciente es requerido");
        }

        if(paciente.getCorreoPaciente() == null || paciente.getCorreoPaciente().trim().isEmpty()){
            throw new ValidationException("El correo del paciente es requerido");
        }
    }

    // creamos un handler para verificar que el paciente existe
    private void handlerExistePaciente() {
        if(paciente.getId()  != null && paciente.getId() != 0) {
            Paciente pacienteExistente = pacienteRepository.findById(paciente.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

            paciente.setActivo(pacienteExistente.isActivo());
        }
    }
    
    /*
     * Procesamos el genero del paciente, la buscamos por id
     * para verificar que existe antes de agregarlo al registro de pacientes
     * asi mismo lo realizamos con el tipo de sangre
    */
   private void processGenero(){
        if(paciente.getGenero() != null && paciente.getGenero().getId() != null) {
            Genero generos = generoRepository.findById(paciente.getGenero().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Genero no encontrado"));

            paciente.setGenero(generos);
        }
   }

   private void processTipoSangre(){
        if(paciente.getTipoSangre() != null && paciente.getTipoSangre().getId() != null) {
            TipoSangre tipoSangre = tipoSangreRepository.findById(paciente.getTipoSangre().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de sangre no encontrado"));
            
            paciente.setTipoSangre(tipoSangre);
        }
   }

   // Método para generar número de paciente único
   private void generarNumeroPaciente() {
        String numeroPaciente;
        boolean existe;

        do {
            // generamos el numero de paciente aleatorio
            numeroPaciente = GeneratorCodigo.generarCodigoAleatorio();

            // verificamos si el numero existe en la base de datos
            existe = pacienteRepository.existsByNroPaciente(numeroPaciente);

        } while(existe); // repetimos hasta generar un número único

        paciente.setNroPaciente(numeroPaciente);
   }


   // guardamos los datos al registrar un paciente
   private Paciente savePaciente(){
        try {
            return pacienteRepository.save(paciente);
        } catch (Exception e) {
            throw new ConcurrencyException("El paciente fue modificado por otra transacción");
        }
   }
}
