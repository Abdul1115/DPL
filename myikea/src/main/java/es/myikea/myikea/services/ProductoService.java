package es.myikea.myikea.services;

import es.myikea.myikea.models.Producto;
import es.myikea.myikea.repositories.ProductoRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    private static final String IMAGES_PATH = "src/main/resources/static/images/";
    private static final String DEFAULT_IMAGE = "default.png";

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // Obtener todos los productos
    public List<Producto> obtenerTodosLosProductos() {
        List<Producto> productos = productoRepository.findAll();

        productos.forEach(producto -> {
            String imagePath = IMAGES_PATH + producto.getImagen();
            if (producto.getImagen() == null || !Files.exists(Paths.get(imagePath))) {
                producto.setImagen(DEFAULT_IMAGE); // Asignar imagen por defecto
            }
        });

        return productos;
    }


    public Producto obtenerProductoPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
    }



    // Crear
    public Producto guardarProducto(Producto producto) {
        return productoRepository.save(producto);
    }







}
