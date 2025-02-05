package es.myikea.myikea.controllers;

import es.myikea.myikea.models.Municipio;
import es.myikea.myikea.models.Producto;
import es.myikea.myikea.models.Provincia;
import es.myikea.myikea.models.User;
import es.myikea.myikea.services.MunicipioService;
import es.myikea.myikea.services.ProductoService;
import es.myikea.myikea.services.ProvinciaService;
import es.myikea.myikea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProvinciaService provinciaService;

    @Autowired
    private UserService userService;

    @Autowired
    private MunicipioService municipioService;


    @GetMapping
    public String listarProductos(Model model, Authentication authentication) {
        // Obtener los productos
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        productos.forEach(producto -> {
            String imagePath = "src/main/resources/static/images/" + producto.getImagen();
            if (producto.getImagen() == null || !Files.exists(Paths.get(imagePath))) {
                producto.setImagen("default.png"); // Imagen por defecto
            }
        });

        // Agregar la lista de productos al modelo
        model.addAttribute("productos", productos);

        // Obtener el email del usuario autenticado y verificar roles
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("email", userService.findByUsername(authentication.getName()).getEmail());

            // Verificar si el usuario tiene rol de ADMIN
            model.addAttribute("isAdmin", authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));

            // Verificar si el usuario tiene rol de MANAGER
            model.addAttribute("isManager", authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_MANAGER")));
        } else {
            model.addAttribute("email", "No autenticado");
            model.addAttribute("isAdmin", false);
            model.addAttribute("isManager", false);
        }

        return "productos"; // Nombre de la plantilla HTML
    }

    @GetMapping("/details/{id}")
    public String mostrarDetalles(@PathVariable Long id, Model model, Authentication authentication) {
        Producto producto = productoService.obtenerProductoPorId(id);

        // Asegurar que siempre tenga una imagen
        if (producto.getImagen() == null || producto.getImagen().trim().isEmpty()) {
            producto.setImagen("default.png"); // Imagen por defecto
        }

        // Agregar producto al modelo
        model.addAttribute("producto", producto);

        // Obtener usuario autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            User usuario = userService.findByUsername(authentication.getName());
            model.addAttribute("email", usuario.getEmail());

            // Verificar si el usuario tiene rol ADMIN
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("email", "No autenticado");
            model.addAttribute("isAdmin", false);
        }

        return "detalles"; // Vista de detalles
    }


    @GetMapping("/crear")
    public String mostrarFormularioCreacion(
            @RequestParam(value = "provinciaId", required = false) Integer provinciaId,
            Model model,
            Authentication authentication) {

        // Obtener todas las provincias
        List<Provincia> provincias = provinciaService.obtenerTodasLasProvincias();
        model.addAttribute("provincias", provincias);

        // Obtener los municipios solo si hay una provincia seleccionada
        List<Municipio> municipios = (provinciaId != null)
                ? municipioService.obtenerMunicipiosPorProvinciaId(provinciaId)
                : List.of();
        model.addAttribute("municipios", municipios);

        // Mantener la provincia seleccionada en el formulario
        model.addAttribute("selectedProvinciaId", provinciaId);

        // Datos del usuario autenticado
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User usuario = userService.findByUsername(username);
            model.addAttribute("email", usuario.getEmail());

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("email", "No autenticado");
            model.addAttribute("isAdmin", false);
        }

        model.addAttribute("producto", new Producto());
        return "Create"; // Nombre de la vista Thymeleaf
    }




    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto,
                                  @RequestParam("municipioId") Long municipioId) {
        try {
            Municipio municipio = new Municipio();
            municipio.setId(municipioId);
            producto.setMunicipio(municipio);

            // Procesar la imagen
            MultipartFile file = producto.getImagenFile();
            if (file != null && !file.isEmpty()) {
                String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                String uploadDir = "src/main/resources/static/images/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                try (InputStream inputStream = file.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    producto.setImagen(fileName); // Guardamos solo el nombre
                }
            } else {
                producto.setImagen("default.png");
            }

            productoService.guardarProducto(producto);
            return "redirect:/productos";
        } catch (IOException e) {
            e.printStackTrace();
            return "productos";
        }
    }


}
