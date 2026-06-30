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
@Tag(name = "Pedidos", description = "Finalização de compra, histórico e cancelamento")
public class PedidoController {

    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Usuario usuario = usuarioService.buscarPorEmail(authentication.getName());
        return usuario.getId();
    }

    @Operation(summary = "Finalizar compra — transforma o carrinho em pedido",
               description = "Calcule o frete em GET /api/frete/calcular?endereco=... antes de chamar este endpoint e informe o valorFrete no body.")
    @PostMapping("/finalizar")
    public ResponseEntity<Pedido> finalizarCompra(@Valid @RequestBody FinalizarCompraRequest request) {
        Pedido pedido = pedidoService.finalizarCompra(getUsuarioLogadoId(), request);
        return new ResponseEntity<>(pedido, HttpStatus.CREATED);
    }

    @Operation(summary = "Histórico de pedidos do comprador (ordenado do mais recente)")
    @GetMapping("/historico")
    public ResponseEntity<List<Pedido>> historico() {
        return ResponseEntity.ok(pedidoService.listarHistoricoCompras(getUsuarioLogadoId()));
    }

    @Operation(summary = "Listar itens de um pedido específico")
    @GetMapping("/{pedidoId}/itens")
    public ResponseEntity<List<ItemPedido>> itensDoPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.listarItensDoPedido(pedidoId, getUsuarioLogadoId()));
    }

    @Operation(summary = "Cancelar pedido (devolve o estoque)")
    @PatchMapping("/{pedidoId}/cancelar")
    public ResponseEntity<Pedido> cancelarPedido(@PathVariable Long pedidoId) {
        return ResponseEntity.ok(pedidoService.cancelarPedido(pedidoId, getUsuarioLogadoId()));
    }

    @Operation(summary = "Listar vendas do vendedor logado")
    @GetMapping("/vendas")
    public ResponseEntity<List<ItemPedido>> minhasVendas() {
        return ResponseEntity.ok(pedidoService.listarVendasDoVendedor(getUsuarioLogadoId()));
    }
}
