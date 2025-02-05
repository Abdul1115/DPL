package es.myikea.myikea.controllers;

import es.myikea.myikea.models.CarritoItem;
import es.myikea.myikea.models.Producto;
import es.myikea.myikea.models.User;
import es.myikea.myikea.repositories.CarritoRepository;
import es.myikea.myikea.services.CarritoService;
import es.myikea.myikea.services.ProductoService;
import es.myikea.myikea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CarritoController {

    private final CarritoService carritoService;
    private final UserService userService;
    private final ProductoService productoService;
    private final CarritoRepository carritoRepository;

    @Autowired
    public CarritoController(CarritoService carritoService, UserService userService, ProductoService productoService, CarritoRepository carritoRepository) {
        this.carritoService = carritoService;
        this.userService = userService;
        this.productoService = productoService;
        this.carritoRepository = carritoRepository;
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());

            List<CarritoItem> productosEnCarrito = carritoService.obtenerProductosCarrito(user);
            double precioTotal = carritoService.calcularPrecioTotal(productosEnCarrito);

            // 🔽 Verificar imágenes y asignar "default.png" si es necesario
            for (CarritoItem item : productosEnCarrito) {
                Producto producto = item.getProducto();
                String imagePath = "src/main/resources/static/images/" + producto.getImagen();

                if (producto.getImagen() == null || !Files.exists(Paths.get(imagePath))) {
                    producto.setImagen("default.png"); // ✅ Imagen por defecto
                }
            }

            model.addAttribute("productosCarrito", productosEnCarrito);
            model.addAttribute("precioTotal", precioTotal);
            model.addAttribute("email", user.getEmail());

            System.out.println("✅ Se han encontrado " + productosEnCarrito.size() + " productos en el carrito.");
        } else {
            model.addAttribute("productosCarrito", new ArrayList<>());
            model.addAttribute("precioTotal", 0);
        }

        return "carrito";
    }



    @GetMapping("/carrito/agregar")
    public String agregarAlCarrito(@RequestParam Long productoId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());
            Producto producto = productoService.obtenerProductoPorId(productoId);
            carritoService.agregarProductoAlCarrito(user, producto);
        }
        return "redirect:/carrito";
    }





    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam Long productoId, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User user = userService.findByUsername(authentication.getName());


            carritoService.eliminarProductoDelCarrito(user, productoId);
        }
        return "redirect:/carrito";
    }


    @PostMapping("/carrito/finalizar")
    public String finalizarPedido(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User usuario = userService.findByUsername(authentication.getName());
            carritoService.finalizarPedido(usuario);
        }
        return "redirect:/pedidos";
    }
}
