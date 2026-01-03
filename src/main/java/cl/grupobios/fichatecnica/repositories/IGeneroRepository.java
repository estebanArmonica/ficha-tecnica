package cl.grupobios.fichatecnica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.grupobios.fichatecnica.models.Genero;

@Repository
public interface IGeneroRepository extends JpaRepository<Genero, Long> {
    
}
