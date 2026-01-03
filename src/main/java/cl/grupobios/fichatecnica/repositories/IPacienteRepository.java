package cl.grupobios.fichatecnica.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cl.grupobios.fichatecnica.models.Paciente;

@Repository
public interface IPacienteRepository extends JpaRepository<Paciente, Long> {
    
    // Solo busca pacientes activos
    List<Paciente> findByActivoTrue();
    
    // Busca paciente por ID solo si está activo
    Optional<Paciente> findByIdAndActivoTrue(Long id);
    
    // Verificar si existe un paciente activo con ese número
    boolean existsByNroPacienteAndActivoTrue(String nroPaciente);
    
    // Métodos para buscar incluyendo inactivos (este es opcional pero estara en el codigo en caso de ser necesario)
    boolean existsByNroPaciente(String nroPaciente);


    // Buscar todos con relaciones
    @Query("SELECT DISTINCT p FROM Paciente p " +
           "LEFT JOIN FETCH p.genero " +
           "LEFT JOIN FETCH p.tipoSangre " +
           "WHERE p.activo = true")
    List<Paciente> findAllWithRelations();
    
    // Buscar por ID con relaciones
    @Query("SELECT p FROM Paciente p " +
           "LEFT JOIN FETCH p.genero " +
           "LEFT JOIN FETCH p.tipoSangre " +
           "WHERE p.id = :id AND p.activo = true")
    Optional<Paciente> findByIdWithRelations(@Param("id") Long id);

    // métodos para verificar duplicados
    boolean existsByRutPaciente(String rutPaciente);
    boolean existsByCorreoPaciente(String correoPaciente);

}
