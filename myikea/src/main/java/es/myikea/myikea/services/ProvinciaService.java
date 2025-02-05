package es.myikea.myikea.services;

import es.myikea.myikea.models.Provincia;
import es.myikea.myikea.repositories.ProvinciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinciaService {

    private final ProvinciaRepository provinciaRepository;

    @Autowired
    public ProvinciaService(ProvinciaRepository provinciaRepository) {
        this.provinciaRepository = provinciaRepository;
    }

    public List<Provincia> obtenerTodasLasProvincias() {
        return provinciaRepository.findAll();
    }
}

