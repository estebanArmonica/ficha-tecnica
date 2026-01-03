package cl.grupobios.fichatecnica.services.utils;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.grupobios.fichatecnica.models.Genero;
import cl.grupobios.fichatecnica.repositories.IGeneroRepository;
import cl.grupobios.fichatecnica.services.IGeneroService;

@Service
public class GeneroServiceImpl implements IGeneroService {

    @Autowired
    private IGeneroRepository generoRepository;

    public GeneroServiceImpl(IGeneroRepository generoRepository) {
        this.generoRepository = generoRepository;
    }

    @Override
    public Genero agregarGenero(Genero genero) {
        return generoRepository.save(genero);
    }

    @Override
    public Set<Genero> listarGeneros() {
        return new LinkedHashSet<>(generoRepository.findAll());
    }

    @Override
    public Genero obtenerGeneroPorId(Long generoId) {
        return generoRepository.findById(generoId).get();
    }
    
}
