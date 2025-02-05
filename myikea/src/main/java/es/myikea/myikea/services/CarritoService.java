package es.myikea.myikea.services;

import es.myikea.myikea.models.CarritoItem;
import es.myikea.myikea.models.Pedido;
import es.myikea.myikea.models.Producto;
import es.myikea.myikea.models.User;
import es.myikea.myikea.repositories.CarritoRepository;
import es.myikea.myikea.repositories.PedidoRepository;
import es.myikea.myikea.repositories.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public CarritoService(CarritoRepository carritoRepository, PedidoRepository pedidoRepository, ProductoRepository productoRepository) {
        this.carritoRepository = carritoRepository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    // Obtener productos del carrito que no han sido finalizados
    public List<CarritoItem> obtenerProductosCarrito(User user) {
        return carritoRepository.findByUserAndNotFinalizado(user);
    }



    public void agregarProductoAlCarrito(User user, Producto producto) {
        if (producto.getStock() <= 0) {
            throw new IllegalStateException("No hay suficiente stock para " + producto.getNombre());
        }

        // 🔽 Reducimos el stock antes de agregarlo al carrito
        producto.setStock(producto.getStock() - 1);
        productoRepository.save(producto);  // ✅ Guardamos el producto con el nuevo stock

        // 🔽 Crear un nuevo CarritoItem y asegurarse de que tenga una cantidad válida
        CarritoItem nuevoItem = new CarritoItem();
        nuevoItem.setUser(user);
        nuevoItem.setProducto(producto);
        nuevoItem.setCantidad(1);  // ✅ IMPORTANTE: Establecemos una cantidad de 1
        nuevoItem.setFinalizado(false);

        carritoRepository.save(nuevoItem); // ✅ Guardamos el producto en el carrito
    }








    // Eliminar producto del carrito
    public void eliminarProductoDelCarrito(User user, Long productoId) {
        List<CarritoItem> items = carritoRepository.findByUser(user);

        CarritoItem itemAEliminar = items.stream()
                .filter(item -> item.getProducto().getId() == productoId) // Usando `==` en vez de `.equals()`
                .findFirst()
                .orElse(null);

        if (itemAEliminar != null) {
            carritoRepository.delete(itemAEliminar);
        }
    }



    // Vaciar carrito
    public void vaciarCarrito(User user) {
        carritoRepository.deleteByUser(user);
    }

    @Transactional
    public void finalizarPedido(User usuario) {
        List<CarritoItem> itemsDelCarrito = carritoRepository.findByUserAndNotFinalizado(usuario);

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setTotal(calcularPrecioTotal(itemsDelCarrito));
        pedido.setFecha(LocalDateTime.now());

        for (CarritoItem item : itemsDelCarrito) {



            item.setPedido(pedido);
            item.setFinalizado(true);
            carritoRepository.save(item);
        }

        pedido.setItems(new ArrayList<>(itemsDelCarrito));
        pedidoRepository.save(pedido);
    }


    // Calcular total del pedido
    public double calcularPrecioTotal(List<CarritoItem> items) {
        return items.stream()
                .mapToDouble(item -> item.getProducto().getPrecio() * item.getCantidad())
                .sum();
    }
}
