package br.com.ifpe.intelifones.model.pedido;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoExpiracaoScheduler {

    private final PedidoService pedidoService;

    @Scheduled(fixedRate = 60_000) // roda a cada 1 minuto
    public void expirarPedidosVencidos() {
        int qtd = pedidoService.expirarPedidosVencidos();
        if (qtd > 0) {
            log.info("Scheduler: {} pedido(s) expirado(s) e estoque devolvido", qtd);
        }
    }
}