package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCompradorOrderByDataPedidoDesc(Usuario comprador);

    // Pedidos aguardando pagamento cujo prazo já expirou (para o scheduler)
    List<Pedido> findByStatusAndExpiraEmBefore(StatusPedido status, LocalDateTime agora);
}