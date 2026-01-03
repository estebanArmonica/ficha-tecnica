package cl.grupobios.fichatecnica.models;

import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;

@Data
@Entity
@Table(name = "pacientes")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_paciente")
    @SequenceGenerator(
        name = "seq_paciente",
        sequenceName = "seq_paciente",
        allocationSize = 1,
        initialValue = 1
    )
    @Column(name = "id_paciente")
    private Long id;

    @Column(name = "nro_paciente", length = 20, nullable = false)
    private String nroPaciente;

    @Column(name = "nom_paciente", length = 35, nullable = false)
    private String nombrePaciente;

    @Column(name = "rut_paciente", length = 12, nullable = false)
    private String rutPaciente;

    @Column(name = "correo", length = 100, nullable = false, unique = true)
    private String correoPaciente;

    @Column(name = "fech_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /*
     * Trasient: Indica que este campo no se mapeará a una columna de la base de datos.
     * JsonInclude: Indica que este campo se incluirá en la serialización JSON incluso si es nulo.
    */ 
    @Transient
    @JsonInclude
    private Integer edadPaciente;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    // relaciones una a muchos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "genero_id", nullable = false)
    private Genero genero;

    // relacion uno a uno
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_sangre_id", nullable = false)
    private TipoSangre tipoSangre;

    public Paciente(final Long id, final String nroPaciente, final String nombrePaciente, final String rutPaciente, final String correoPaciente,
            final LocalDate fechaNacimiento, final Integer edadPaciente, final Genero genero, final TipoSangre tipoSangre) {
        this.id = id;
        this.nroPaciente = nroPaciente;
        this.nombrePaciente = nombrePaciente;
        this.rutPaciente = rutPaciente;
        this.correoPaciente = correoPaciente;
        this.fechaNacimiento = fechaNacimiento;
        this.edadPaciente = calcularEdad(); // método privado para calcular la edad de un paciente.
        this.genero = genero;
        this.tipoSangre = tipoSangre;
        this.activo = true; // por defecto un paciente se crea como activo
    }
        public Paciente(){}

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }

    public String getNroPaciente() { 
        return nroPaciente; 
    }

    public void setNroPaciente(String nroPaciente) { 
        this.nroPaciente = nroPaciente;
    }

    public String getNombrePaciente() { 
        return nombrePaciente; 
    }
    
    public void setNombrePaciente(String nombrePaciente) { 
        this.nombrePaciente = nombrePaciente; 
    }

    public String getRutPaciente() { 
        return rutPaciente; 
    }

    public void setRutPaciente(String rutPaciente) { 
        this.rutPaciente = rutPaciente; 
    }

    public String getCorreoPaciente() { 
        return correoPaciente; 
    }

    public void setCorreoPaciente(String correoPaciente) { 
        this.correoPaciente = correoPaciente; 
    }

    public LocalDate getFechaNacimiento() { 
        return fechaNacimiento; 
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) { 
        this.fechaNacimiento = fechaNacimiento; 
    }

    @JsonProperty("edadPaciente")
    public Integer getEdadPaciente() { 
        if (this.edadPaciente == null) {
            this.edadPaciente = calcularEdad();
        }
        return this.edadPaciente;  
    }
    
    public void setEdadPaciente(Integer edadPaciente) { 
        this.edadPaciente = edadPaciente; 
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Genero getGenero() { 
        return genero; 
    }

    public void setGenero(Genero genero) { 
        this.genero = genero; 
    }

    public TipoSangre getTipoSangre() { 
        return tipoSangre; 
    }

    public void setTipoSangre(TipoSangre tipoSangre) { 
        this.tipoSangre = tipoSangre; 
    }

    @Override
    public String toString() {
        return "cl.grupobios.fichatecnica.models.Paciente [ id= " + id + " ]";
    }

    /*
     * Creamos un método privado llamado calcularEdad el cual retorna un entero.
     * Este método calcula la edad del paciente basándose en su fecha de nacimiento y la fecha actual. 
    */
    private Integer calcularEdad(){
        // hacemos una condición por si la fecha de nacimiento es nula
        if (this.fechaNacimiento == null){
            return 0; // en caso de ser nula, retornamos 0
        }

        // retornamos la diferencia en años entre la fecha de nacimiento y la fecha actual
        return Period.between(this.fechaNacimiento, LocalDate.now()).getYears();
    }
}
