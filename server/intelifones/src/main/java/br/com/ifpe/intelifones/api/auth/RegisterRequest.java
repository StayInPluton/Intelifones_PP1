package br.com.ifpe.intelifones.api.auth;

import br.com.ifpe.intelifones.model.usuario.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private String endereco;
    private Role role;
}