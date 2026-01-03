package cl.grupobios.fichatecnica.dtos;

import java.time.LocalDate;

public class PacienteDTO {
    private Long idPaciente;
    private String nombrePaciente;
    private String rutPaciente;
    private String correoPaciente;
    private LocalDate fechaNacimiento;
    private String nroPaciente;
    private Boolean activo;
    private String generoNombre;
    private String tipoSangreNombre;

    // Constructores
    public PacienteDTO() {}

    public PacienteDTO(Long idPaciente, String nombrePaciente, String rutPaciente, 
                      String correoPaciente, LocalDate fechaNacimiento, 
                      String nroPaciente, Boolean activo, 
                      String generoNombre, String tipoSangreNombre) {
        this.idPaciente = idPaciente;
        this.nombrePaciente = nombrePaciente;
        this.rutPaciente = rutPaciente;
        this.correoPaciente = correoPaciente;
        this.fechaNacimiento = fechaNacimiento;
        this.nroPaciente = nroPaciente;
        this.activo = activo;
        this.generoNombre = generoNombre;
        this.tipoSangreNombre = tipoSangreNombre;
    }

    public Long getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(Long idPaciente) {
        this.idPaciente = idPaciente;
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

    public String getNroPaciente() {
        return nroPaciente;
    }

    public void setNroPaciente(String nroPaciente) {
        this.nroPaciente = nroPaciente;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getGeneroNombre() {
        return generoNombre;
    }

    public void setGeneroNombre(String generoNombre) {
        this.generoNombre = generoNombre;
    }

    public String getTipoSangreNombre() {
        return tipoSangreNombre;
    }

    public void setTipoSangreNombre(String tipoSangreNombre) {
        this.tipoSangreNombre = tipoSangreNombre;
    }

    
}
