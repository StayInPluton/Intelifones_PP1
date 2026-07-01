package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.api.pedido.FinalizarCompraRequest;
import br.com.ifpe.intelifones.model.carrinho.Carrinho;
import br.com.ifpe.intelifones.model.carrinho.CarrinhoRepository;
import br.com.ifpe.intelifones.model.carrinho.ItemCarrinho;
import br.com.ifpe.intelifones.model.carrinho.ItemCarrinhoRepository;
import br.com.ifpe.intelifones.model.produto.Produto;
import br.com.ifpe.intelifones.model.produto.ProdutoRepository;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioRepository;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PedidoService {

    private static final long MINUTOS_RESERVA = 30;

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    @Transactional
    public Pedido finalizarCompra(Long usuarioId, FinalizarCompraRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new BusinessException("Carrinho não encontrado. Adicione produtos antes de finalizar."));

        List<ItemCarrinho> itensCarrinho = itemCarrinhoRepository.findByCarrinho(carrinho);
        if (itensCarrinho.isEmpty()) {
            throw new BusinessException("Carrinho vazio. Adicione produtos antes de finalizar.");
        }

        double valorFrete = request.getValorFrete() != null ? request.getValorFrete() : 0.0;
        if (valorFrete < 0) throw new BusinessException("Valor de frete inválido");

        LocalDateTime agora = LocalDateTime.now();

        Pedido pedido = Pedido.builder()
                .comprador(usuario)
                .status(StatusPedido.AGUARDANDO_PAGAMENTO)
                .dataPedido(agora)
                .expiraEm(agora.plusMinutes(MINUTOS_RESERVA))
                .valorTotal(0.0)
                .valorFrete(valorFrete)
                .endereco(request.getEndereco())
                .cep(request.getCep())
                .numero(request.getNumero())
                .complemento(request.getComplemento())
                .telefoneContato(request.getTelefoneContato())
                .formaPagamento(request.getFormaPagamento())
                .build();

        pedido = pedidoRepository.save(pedido);
        double subtotal = 0.0;

        for (ItemCarrinho item : itensCarrinho) {
            Produto produto = item.getProduto();

            if (!produto.getAtivo())
                throw new BusinessException("Produto indisponível: " + produto.getNome());

            if (produto.getQuantidade() < item.getQuantidade())
                throw new BusinessException("Estoque insuficiente para: " + produto.getNome()
                        + ". Disponível: " + produto.getQuantidade());

            itemPedidoRepository.save(ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(item.getQuantidade())
                    .precoUnitario(produto.getPreco())
                    .build());

            subtotal += produto.getPreco() * item.getQuantidade();
            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setValorTotal(subtotal + valorFrete);
        pedidoRepository.save(pedido);
        itemCarrinhoRepository.deleteAll(itensCarrinho);

        return pedido;
    }

    /**
     * Confirma o pagamento: AGUARDANDO_PAGAMENTO → PAGO.
     * Pedido CANCELADO ou já PAGO gera erro.
     */
    @Transactional
    public Pedido confirmarPagamento(Long pedidoId, Long usuarioId) {
        Pedido pedido = buscarPedidoDoUsuario(pedidoId, usuarioId);

        if (pedido.getStatus() == StatusPedido.CANCELADO)
            throw new BusinessException("Pedido cancelado (reserva expirada ou cancelamento manual). Não pode mais ser pago.");

        if (pedido.getStatus() == StatusPedido.PAGO)
            throw new BusinessException("Pedido já está pago.");

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO)
            throw new BusinessException("Status inválido para confirmação: " + pedido.getStatus());

        pedido.setStatus(StatusPedido.PAGO);
        pedido.setDataFinalizacao(LocalDateTime.now());
        pedido.setExpiraEm(null);
        return pedidoRepository.save(pedido);
    }

    /**
     * Chamado pelo scheduler a cada minuto.
     * Expira pedidos AGUARDANDO_PAGAMENTO cujo expiraEm já passou,
     * devolve o estoque e cancela.
     */
    @Transactional
    public int expirarPedidosVencidos() {
        List<Pedido> vencidos = pedidoRepository
                .findByStatusAndExpiraEmBefore(StatusPedido.AGUARDANDO_PAGAMENTO, LocalDateTime.now());

        for (Pedido pedido : vencidos) {
            itemPedidoRepository.findByPedido(pedido).forEach(item -> {
                Produto p = item.getProduto();
                p.setQuantidade(p.getQuantidade() + item.getQuantidade());
                produtoRepository.save(p);
            });
            pedido.setStatus(StatusPedido.CANCELADO);
            pedidoRepository.save(pedido);
            log.info("Pedido #{} expirado — estoque devolvido", pedido.getId());
        }

        return vencidos.size();
    }

    public List<Pedido> listarHistoricoCompras(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        return pedidoRepository.findByCompradorOrderByDataPedidoDesc(usuario);
    }

    public List<ItemPedido> listarItensDoPedido(Long pedidoId, Long usuarioId) {
        return itemPedidoRepository.findByPedido(buscarPedidoDoUsuario(pedidoId, usuarioId));
    }

    public List<ItemPedido> listarVendasDoVendedor(Long vendedorId) {
        return itemPedidoRepository.findByProduto_Vendedor_Id(vendedorId);
    }

    /**
     * Cancela pedido manualmente.
     * REGRA: pedido PAGO não pode ser cancelado por esta via.
     */
    @Transactional
    public Pedido cancelarPedido(Long pedidoId, Long usuarioId) {
        Pedido pedido = buscarPedidoDoUsuario(pedidoId, usuarioId);

        if (pedido.getStatus() == StatusPedido.PAGO)
            throw new BusinessException("Pedido já pago não pode ser cancelado. Entre em contato com o suporte.");

        if (pedido.getStatus() == StatusPedido.ENVIADO || pedido.getStatus() == StatusPedido.ENTREGUE)
            throw new BusinessException("Não é possível cancelar pedido já " + pedido.getStatus().name().toLowerCase());

        if (pedido.getStatus() == StatusPedido.CANCELADO)
            throw new BusinessException("Pedido já está cancelado");

        itemPedidoRepository.findByPedido(pedido).forEach(item -> {
            Produto p = item.getProduto();
            p.setQuantidade(p.getQuantidade() + item.getQuantidade());
            produtoRepository.save(p);
        });

        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }

    private Pedido buscarPedidoDoUsuario(Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));
        if (!pedido.getComprador().getId().equals(usuarioId))
            throw new BusinessException("Acesso negado a este pedido");
        return pedido;
    }
}