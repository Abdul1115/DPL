package es.myikea.myikea.repositories;

import es.myikea.myikea.models.Pedido;
import es.myikea.myikea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByUsuario(User usuario);
}
