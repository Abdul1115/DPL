package es.myikea.myikea.services;

import es.myikea.myikea.models.Municipio;
import es.myikea.myikea.repositories.MunicipioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MunicipioService {

    private final MunicipioRepository municipioRepository;

    @Autowired
    public MunicipioService(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }


    public List<Municipio> obtenerMunicipiosPorProvinciaId(Integer provinciaId) {
        return municipioRepository.findByProvinciaId(provinciaId);
    }
}

