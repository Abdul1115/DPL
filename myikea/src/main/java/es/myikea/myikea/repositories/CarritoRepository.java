package es.myikea.myikea.repositories;

import es.myikea.myikea.models.CarritoItem;
import es.myikea.myikea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarritoRepository extends JpaRepository<CarritoItem, Long> {

    // Obtener todos los elementos del carrito de un usuario
    List<CarritoItem> findByUser(User user);

    @Query("SELECT c FROM CarritoItem c WHERE c.user = :user AND c.finalizado = false")
    List<CarritoItem> findByUserAndNotFinalizado(@Param("user") User user);


    // Eliminar un producto específico del carrito de un usuario
    void deleteByUserAndProductoId(User user, Long productoId);

    @Modifying
    @Query("DELETE FROM CarritoItem c WHERE c.user = :user")
    void deleteByUser(@Param("user") User user);

}
