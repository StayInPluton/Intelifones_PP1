package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.model.usuario.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Histórico ordenado do mais recente para o mais antigo
    List<Pedido> findByCompradorOrderByDataFinalizacaoDesc(Usuario comprador);

    List<Pedido> findByComprador(Usuario comprador);
}
