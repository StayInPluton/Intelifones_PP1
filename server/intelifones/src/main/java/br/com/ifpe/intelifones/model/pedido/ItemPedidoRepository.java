package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.model.usuario.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedido(Pedido pedido);

    List<ItemPedido> findByProduto_Vendedor_Id(Long vendedorId);

    // EntityGraph evita N+1: traz produto e pedido junto na mesma query
    @EntityGraph(attributePaths = {"produto", "pedido"})
    List<ItemPedido> findByPedido_CompradorOrderByPedido_DataFinalizacaoDesc(Usuario comprador);
}