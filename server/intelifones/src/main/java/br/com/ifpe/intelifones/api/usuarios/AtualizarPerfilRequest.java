package br.com.ifpe.intelifones.api.usuarios;

import lombok.Data;

@Data
public class AtualizarPerfilRequest {
    private String nome;
    private String telefone;
    private String cpf;
    // REMOVIDO: String endereco — use POST /api/usuarios/enderecos para gerenciar endereços
}
