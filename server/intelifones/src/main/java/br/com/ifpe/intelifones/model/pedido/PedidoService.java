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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;

    @Transactional
    public Pedido finalizarCompra(Long usuarioId, FinalizarCompraRequest request) {

        // 1. Buscar usuário
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // 2. Buscar carrinho do usuário
        Carrinho carrinho = carrinhoRepository.findByUsuario(usuario)
                .orElseThrow(() -> new BusinessException("Carrinho não encontrado. Adicione produtos antes de finalizar."));

        // 3. Buscar itens do carrinho
        List<ItemCarrinho> itensCarrinho = itemCarrinhoRepository.findByCarrinho(carrinho);

        if (itensCarrinho.isEmpty()) {
            throw new BusinessException("Carrinho vazio. Adicione produtos antes de finalizar.");
        }

        // 4. Validar frete
        double valorFrete = request.getValorFrete() != null ? request.getValorFrete() : 0.0;
        if (valorFrete < 0) {
            throw new BusinessException("Valor de frete inválido");
        }

        // 5. Criar o pedido com dataPedido e dataFinalizacao
        LocalDateTime agora = LocalDateTime.now();

        Pedido pedido = Pedido.builder()
                .comprador(usuario)
                .status(StatusPedido.PAGO)
                .dataPedido(agora)
                .dataFinalizacao(agora)   // ← Data/hora exata da compra (pedido do companheiro)
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

        // 6. Processar itens: validar estoque, criar ItemPedido, diminuir estoque
        double subtotal = 0.0;

        for (ItemCarrinho item : itensCarrinho) {
            Produto produto = item.getProduto();

            if (!produto.getAtivo()) {
                throw new BusinessException("Produto indisponível: " + produto.getNome());
            }

            if (produto.getQuantidade() < item.getQuantidade()) {
                throw new BusinessException(
                        "Estoque insuficiente para: " + produto.getNome()
                        + ". Disponível: " + produto.getQuantidade()
                        + ", solicitado: " + item.getQuantidade());
            }

            ItemPedido itemPedido = ItemPedido.builder()
                    .pedido(pedido)
                    .produto(produto)
                    .quantidade(item.getQuantidade())
                    .precoUnitario(produto.getPreco())   // snapshot do preço no momento da compra
                    .build();

            itemPedidoRepository.save(itemPedido);

            subtotal += produto.getPreco() * item.getQuantidade();

            // Diminuir estoque
            produto.setQuantidade(produto.getQuantidade() - item.getQuantidade());
            produtoRepository.save(produto);
        }

        // 7. Calcular total = subtotal dos itens + frete
        pedido.setValorTotal(subtotal + valorFrete);
        pedidoRepository.save(pedido);

        // 8. Limpar carrinho após finalizar
        itemCarrinhoRepository.deleteAll(itensCarrinho);

        return pedido;
    }

    /**
     * Histórico de pedidos do comprador (retorna os pedidos, não apenas os itens).
     */
     public List<HistoricoItemDTO> listarHistoricoCompras(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
 
        return itemPedidoRepository
                .findByPedido_CompradorOrderByPedido_DataFinalizacaoDesc(usuario)
                .stream()
                .map(item -> new HistoricoItemDTO(
                        item.getId(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        new ProdutoResumoDTO(item.getProduto()),
                        new PedidoResumoDTO(
                                item.getPedido().getId(),
                                item.getPedido().getStatus(),
                                item.getPedido().getValorTotal(),
                                item.getPedido().getValorFrete(),
                                item.getPedido().getDataFinalizacao()
                        )
                ))
                .toList();
    }
 

    /**
     * Histórico detalhado de um pedido específico com seus itens.
     */
    public List<ItemPedido> listarItensDoPedido(Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));

        if (!pedido.getComprador().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado a este pedido");
        }

        return itemPedidoRepository.findByPedido(pedido);
    }

    /**
     * Vendas do vendedor logado.
     */
    public List<ItemPedido> listarVendasDoVendedor(Long vendedorId) {
        return itemPedidoRepository.findByProduto_Vendedor_Id(vendedorId);
    }

    /**
     * Cancela um pedido (apenas se ainda estiver como PAGO/PENDENTE).
     */
    @Transactional
    public Pedido cancelarPedido(Long pedidoId, Long usuarioId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));

        if (!pedido.getComprador().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado a este pedido");
        }

        if (pedido.getStatus() == StatusPedido.ENVIADO || pedido.getStatus() == StatusPedido.ENTREGUE) {
            throw new BusinessException("Não é possível cancelar um pedido já " + pedido.getStatus().name().toLowerCase());
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new BusinessException("Pedido já está cancelado");
        }

        // Repor estoque
        List<ItemPedido> itens = itemPedidoRepository.findByPedido(pedido);
        for (ItemPedido item : itens) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        return pedidoRepository.save(pedido);
    }
}
