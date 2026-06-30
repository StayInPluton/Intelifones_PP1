package br.com.ifpe.intelifones.model.usuario;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String senha;

    @Column(length = 20)
    private String telefone;

    @Column(length = 14)
    private String cpf;

    @Column(name = "imagem")
    private String imagem;

    // REMOVIDO: String endereco (campo único e sem estrutura)
    // Endereços agora ficam na tabela Endereco (relação separada)
    // Use GET /api/usuarios/enderecos para gerenciar

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    // --- Recuperação de senha ---
    @Column(length = 64)
    @JsonIgnore
    private String tokenRecuperacaoSenha;

    @Column
    @JsonIgnore
    private LocalDateTime tokenExpiracao;

    // --- UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return ativo; }

    // Helper para validar token de recuperação
    public boolean tokenValido(String token) {
        return token != null
                && token.equals(this.tokenRecuperacaoSenha)
                && this.tokenExpiracao != null
                && LocalDateTime.now().isBefore(this.tokenExpiracao);
    }

    public void limparToken() {
        this.tokenRecuperacaoSenha = null;
        this.tokenExpiracao = null;
    }
}
