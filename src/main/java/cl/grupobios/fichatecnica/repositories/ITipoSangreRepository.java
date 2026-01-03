package cl.grupobios.fichatecnica.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.grupobios.fichatecnica.models.TipoSangre;

@Repository
public interface ITipoSangreRepository extends JpaRepository<TipoSangre, Long> {
    
}
