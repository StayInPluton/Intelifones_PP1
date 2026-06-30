package br.com.ifpe.intelifones.model.pedido;

import br.com.ifpe.intelifones.model.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnoreProperties({"senha", "enderecos", "tokenRecuperacaoSenha", "tokenExpiracao", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
    private Usuario comprador;

    @Column(nullable = false)
    private Double valorTotal;

    // Frete calculado via Google Maps no momento da finalização
    @Column(nullable = false)
    @Builder.Default
    private Double valorFrete = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    // Data/hora em que o pedido entrou no sistema (carrinho → pedido)
    @Column(nullable = false)
    private LocalDateTime dataPedido;

    // Data/hora em que a compra foi FINALIZADA (pagamento confirmado)
    // Pedido do seu companheiro: saber que horas foi realizada a compra
    @Column
    private LocalDateTime dataFinalizacao;

    @Column(nullable = false, length = 200)
    private String endereco;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 100)
    private String complemento;

    @Column(length = 20)
    private String telefoneContato;

    @Column(length = 20)
    private String formaPagamento;
}
