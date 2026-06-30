package br.com.ifpe.intelifones.api.usuarios;

import br.com.ifpe.intelifones.model.usuario.Endereco;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Perfil, endereços e foto do usuário logado")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private Long getUsuarioLogadoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return usuarioService.buscarPorEmail(authentication.getName()).getId();
    }

    // -------------------------------------------------------------------------
    // PERFIL
    // -------------------------------------------------------------------------

    @Operation(summary = "Buscar dados do usuário logado")
    @GetMapping("/me")
    public ResponseEntity<Usuario> meusDados() {
        return ResponseEntity.ok(usuarioService.buscarPorId(getUsuarioLogadoId()));
    }

    @Operation(summary = "Atualizar dados do perfil (nome, telefone, CPF)")
    @PutMapping("/me")
    public ResponseEntity<Usuario> atualizarPerfil(@RequestBody AtualizarPerfilRequest request) {
        return ResponseEntity.ok(usuarioService.atualizarPerfil(getUsuarioLogadoId(), request));
    }

    @Operation(summary = "Atualizar foto de perfil")
    @PutMapping(value = "/me/imagem", consumes = "multipart/form-data")
    public ResponseEntity<Usuario> atualizarImagem(@RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.ok(usuarioService.atualizarImagem(getUsuarioLogadoId(), arquivo));
    }

    // -------------------------------------------------------------------------
    // ENDEREÇOS
    // -------------------------------------------------------------------------

    @Operation(summary = "Listar meus endereços")
    @GetMapping("/enderecos")
    public ResponseEntity<List<Endereco>> listarEnderecos() {
        return ResponseEntity.ok(usuarioService.listarEnderecos(getUsuarioLogadoId()));
    }

    @Operation(summary = "Adicionar novo endereço")
    @PostMapping("/enderecos")
    public ResponseEntity<Endereco> adicionarEndereco(@Valid @RequestBody Endereco endereco) {
        return new ResponseEntity<>(usuarioService.adicionarEndereco(getUsuarioLogadoId(), endereco), HttpStatus.CREATED);
    }

    @Operation(summary = "Atualizar endereço")
    @PutMapping("/enderecos/{enderecoId}")
    public ResponseEntity<Endereco> atualizarEndereco(@PathVariable Long enderecoId,
                                                       @Valid @RequestBody Endereco dados) {
        return ResponseEntity.ok(usuarioService.atualizarEndereco(getUsuarioLogadoId(), enderecoId, dados));
    }

    @Operation(summary = "Definir endereço como principal")
    @PatchMapping("/enderecos/{enderecoId}/principal")
    public ResponseEntity<Void> definirPrincipal(@PathVariable Long enderecoId) {
        usuarioService.definirEnderecoPrincipal(getUsuarioLogadoId(), enderecoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Remover endereço")
    @DeleteMapping("/enderecos/{enderecoId}")
    public ResponseEntity<Void> removerEndereco(@PathVariable Long enderecoId) {
        usuarioService.removerEndereco(getUsuarioLogadoId(), enderecoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obter endereço principal formatado (para cálculo de frete)")
    @GetMapping("/enderecos/principal/texto")
    public ResponseEntity<String> enderecoEntregaPrincipal() {
        return ResponseEntity.ok(usuarioService.obterEnderecoEntregaPrincipal(getUsuarioLogadoId()));
    }
}
