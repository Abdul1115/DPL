package es.myikea.myikea.services;

import es.myikea.myikea.models.CarritoItem;
import es.myikea.myikea.models.Pedido;
import es.myikea.myikea.models.User;
import es.myikea.myikea.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll(); // Recupera todos los pedidos
    }


    public Pedido obtenerPedidoPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
    }
}
