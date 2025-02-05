package es.myikea.myikea.controllers;

import es.myikea.myikea.models.CarritoItem;
import es.myikea.myikea.models.Pedido;
import es.myikea.myikea.models.Producto;
import es.myikea.myikea.models.User;
import es.myikea.myikea.repositories.PedidoRepository;
import es.myikea.myikea.services.CarritoService;
import es.myikea.myikea.services.PedidoService;
import es.myikea.myikea.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UserService userService;
    private final PedidoRepository  pedidoRepository;

    @Autowired
    public PedidoController(PedidoService pedidoService, UserService userService, PedidoRepository pedidoRepository) {
        this.pedidoService = pedidoService;
        this.userService = userService;
        this.pedidoRepository = pedidoRepository;
    }

    @GetMapping
    public String listarPedidos(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            User usuario = userService.findByUsername(authentication.getName());

            // Obtener todos los pedidos sin importar el usuario
            List<Pedido> pedidos = pedidoService.obtenerTodosLosPedidos();

            // Validar imágenes de productos en los pedidos
            pedidos.forEach(pedido -> pedido.getItems().forEach(item -> {
                if (item.getProducto() != null) {
                    String imagePath = "src/main/resources/static/images/" + item.getProducto().getImagen();
                    if (item.getProducto().getImagen() == null || !Files.exists(Paths.get(imagePath))) {
                        item.getProducto().setImagen("descarga.png"); // Imagen por defecto
                    }
                }
            }));

            model.addAttribute("pedidos", pedidos);
            model.addAttribute("email", usuario.getEmail());

            // Verificar si el usuario tiene rol ADMIN
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("email", "No autenticado");
            model.addAttribute("isAdmin", false);
            model.addAttribute("pedidos", new ArrayList<>()); // Lista vacía si no hay autenticación
        }

        return "pedidos"; // Nombre de la vista Thymeleaf
    }


    @GetMapping("/details/{id}")
    public String detallesPedido(@PathVariable Long id, Model model, Authentication authentication) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        for (CarritoItem item : pedido.getItems()) {
            Producto producto = item.getProducto();
            if (producto != null) {
                String imagePath = "src/main/resources/static/images/" + producto.getImagen();
                if (producto.getImagen() == null || !Files.exists(Paths.get(imagePath))) {
                    producto.setImagen("default.png"); // ✅ Imagen por defecto
                }
            }
        }

        model.addAttribute("pedido", pedido);

        if (authentication != null && authentication.isAuthenticated()) {
            User usuario = userService.findByUsername(authentication.getName());
            model.addAttribute("email", usuario.getEmail());

            // Verificar si el usuario tiene rol ADMIN
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
            model.addAttribute("isAdmin", isAdmin);
        } else {
            model.addAttribute("email", "No autenticado");
            model.addAttribute("isAdmin", false);
            model.addAttribute("pedidos", new ArrayList<>()); // Lista vacía si no hay autenticación
        }

        return "pedido-detalle";
    }






}
