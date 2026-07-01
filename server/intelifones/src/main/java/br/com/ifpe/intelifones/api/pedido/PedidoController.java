package br.com.ifpe.intelifones.api.pedido;

import br.com.ifpe.intelifones.model.pedido.ItemPedido;
import br.com.ifpe.intelifones.model.pedido.Pedido;
import br.com.ifpe.intelifones.model.pedido.PedidoService;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Finalização, pagamento, histórico e cancelamento")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.buscarPorEmail(auth.getName());
        return usuario.getId();
    }

    @Operation(summary = "Finalizar compra — reserva estoque por 30 min, aguardando pagamento")
    @PostMapping("/finalizar")
    public ResponseEntity<Pedido> finalizarCompra(@Valid @RequestBody FinalizarCompraRequest request) {
        return new ResponseEntity<>(pedidoService.finalizarCompra(getUsuarioLogadoId(), request), HttpStatus.CREATED);
    }

    @Operation(summary = "Confirmar pagamento (simulado) — pedido passa de AGUARDANDO_PAGAMENTO para PAGO")
    @PostMapping("/{pedidoId}/confirmar-pagamento")
    public ResponseEntity<Pedido> confirmarPagamento(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.confirmarPagamento(pedidoId, getUsuarioLogadoId()));
    }

    @Operation(summary = "Histórico de pedidos (mais recente primeiro)")
    @GetMapping("/historico")
    public ResponseEntity<List<Pedido>> historico() {
        return ResponseEntity.ok(pedidoService.listarHistoricoCompras(getUsuarioLogadoId()));
    }

    @Operation(summary = "Itens de um pedido específico")
    @GetMapping("/{pedidoId}/itens")
    public ResponseEntity<List<ItemPedido>> itensDoPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.listarItensDoPedido(pedidoId, getUsuarioLogadoId()));
    }

    @Operation(summary = "Cancelar pedido (não funciona se já PAGO)")
    @PatchMapping("/{pedidoId}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(pedidoId, getUsuarioLogadoId()));
    }

    @Operation(summary = "Vendas do vendedor logado")
    @GetMapping("/vendas")
    public ResponseEntity<List<ItemPedido>> minhasVendas() {
        return ResponseEntity.ok(pedidoService.listarVendasDoVendedor(getUsuarioLogadoId()));
    }
}