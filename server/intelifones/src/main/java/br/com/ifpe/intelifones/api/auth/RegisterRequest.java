package br.com.ifpe.intelifones.api.auth;

import br.com.ifpe.intelifones.model.usuario.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
    private String senha;

    private String telefone;

    private String cpf;

    @NotNull(message = "Role é obrigatória (VENDEDOR ou COMPRADOR)")
    private Role role;

    // REMOVIDO: String endereco — adicione endereços via POST /api/usuarios/enderecos após o registro
}
