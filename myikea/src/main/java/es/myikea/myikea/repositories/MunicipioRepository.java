package es.myikea.myikea.repositories;

import es.myikea.myikea.models.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Integer> {
    List<Municipio> findByProvinciaId(Integer provinciaId);
}

