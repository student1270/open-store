package ru.gb.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.gb.model.OrderItem;
import ru.gb.model.Order;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder(Order order);

}