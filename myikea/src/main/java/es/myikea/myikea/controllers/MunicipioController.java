package es.myikea.myikea.controllers;


import es.myikea.myikea.models.Municipio;
import es.myikea.myikea.services.MunicipioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.stream.Collectors;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/municipios")
public class MunicipioController {

    private final MunicipioService municipioService;

    @Autowired
    public MunicipioController(MunicipioService municipioService) {
        this.municipioService = municipioService;
    }

    @GetMapping("/{provinciaId}")
    public ResponseEntity<List<Map<String, Object>>> obtenerMunicipiosPorProvincia(@PathVariable Integer provinciaId) {
        List<Municipio> municipios = municipioService.obtenerMunicipiosPorProvinciaId(provinciaId);

        // Crear el mapa con tipos explícitos
        List<Map<String, Object>> simpleMunicipios = municipios.stream()
                .map(municipio -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", municipio.getId());
                    map.put("nombre", municipio.getNombre());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(simpleMunicipios);
    }

}
