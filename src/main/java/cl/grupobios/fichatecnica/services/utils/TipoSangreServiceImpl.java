package cl.grupobios.fichatecnica.services.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.grupobios.fichatecnica.models.TipoSangre;
import cl.grupobios.fichatecnica.repositories.ITipoSangreRepository;
import cl.grupobios.fichatecnica.services.ITipoSangreService;

@Service
public class TipoSangreServiceImpl implements ITipoSangreService{

    @Autowired
    private ITipoSangreRepository tipoSangreRepository;

    public TipoSangreServiceImpl(ITipoSangreRepository tipoSangreRepository) {
        this.tipoSangreRepository = tipoSangreRepository;
    }

    @Override
    public TipoSangre agregarTipoSangre(TipoSangre tipoSangre) {
        return tipoSangreRepository.save(tipoSangre); 
    }

    @Override
    public TipoSangre obtenerTipoSangrePorId(Long id) {
        return tipoSangreRepository.findById(id).get();
    }

    @Override
    public Set<TipoSangre> listarTiposSangre() {
        return new LinkedHashSet<>(tipoSangreRepository.findAll());
    }
    
}
