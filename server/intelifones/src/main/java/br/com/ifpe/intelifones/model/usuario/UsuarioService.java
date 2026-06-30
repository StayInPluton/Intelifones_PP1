package br.com.ifpe.intelifones.model.usuario;

import br.com.ifpe.intelifones.api.auth.RegisterRequest;
import br.com.ifpe.intelifones.api.usuarios.AtualizarPerfilRequest;
import br.com.ifpe.intelifones.model.email.EmailService;
import br.com.ifpe.intelifones.util.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EnderecoRepository enderecoRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    // -------------------------------------------------------------------------
    // REGISTRO
    // -------------------------------------------------------------------------

    @Transactional
    public Usuario registrar(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado!");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome())
                .email(request.getEmail())
                .senha(passwordEncoder.encode(request.getSenha()))
                .telefone(request.getTelefone())
                .cpf(request.getCpf())
                .role(request.getRole())
                .ativo(true)
                .build();

        usuario = usuarioRepository.save(usuario);

        // E-mail de boas-vindas em background (não bloqueia a resposta)
        emailService.enviarBoasVindas(usuario.getEmail(), usuario.getNome(), usuario.getRole().name());

        return usuario;
    }

    // -------------------------------------------------------------------------
    // BUSCA
    // -------------------------------------------------------------------------

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado: " + email));
    }

    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado com ID: " + id));
    }

    // -------------------------------------------------------------------------
    // ATUALIZAÇÃO DE PERFIL
    // -------------------------------------------------------------------------

    @Transactional
    public Usuario atualizarPerfil(Long usuarioId, AtualizarPerfilRequest request) {
        Usuario usuario = buscarPorId(usuarioId);

        if (request.getNome() != null && !request.getNome().trim().isEmpty()) {
            usuario.setNome(request.getNome());
        }
        if (request.getTelefone() != null) {
            usuario.setTelefone(request.getTelefone());
        }
        if (request.getCpf() != null) {
            usuario.setCpf(request.getCpf());
        }

        return usuarioRepository.save(usuario);
    }

    // -------------------------------------------------------------------------
    // UPLOAD DE IMAGEM DE PERFIL
    // -------------------------------------------------------------------------

    public Usuario atualizarImagem(Long usuarioId, MultipartFile arquivo) {
        Usuario usuario = buscarPorId(usuarioId);

        try {
            String nomeArquivo = UUID.randomUUID() + "_" + arquivo.getOriginalFilename();
            Path pastaUploads = Paths.get("uploads", "usuarios");
            Files.createDirectories(pastaUploads);
            Path caminho = pastaUploads.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminho, StandardCopyOption.REPLACE_EXISTING);
            usuario.setImagem(nomeArquivo);
            return usuarioRepository.save(usuario);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar imagem");
        }
    }

    // -------------------------------------------------------------------------
    // ENDEREÇOS ESTRUTURADOS
    // -------------------------------------------------------------------------

    public List<Endereco> listarEnderecos(Long usuarioId) {
        return enderecoRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Endereco adicionarEndereco(Long usuarioId, Endereco endereco) {
        Usuario usuario = buscarPorId(usuarioId);
        endereco.setUsuario(usuario);

        // Primeiro endereço vira principal automaticamente
        boolean primeiroEndereco = enderecoRepository.countByUsuarioId(usuarioId) == 0;
        endereco.setPrincipal(primeiroEndereco);

        return enderecoRepository.save(endereco);
    }

    @Transactional
    public Endereco atualizarEndereco(Long usuarioId, Long enderecoId, Endereco dados) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado a este endereço");
        }

        endereco.setLogradouro(dados.getLogradouro());
        endereco.setNumero(dados.getNumero());
        endereco.setComplemento(dados.getComplemento());
        endereco.setBairro(dados.getBairro());
        endereco.setCidade(dados.getCidade());
        endereco.setUf(dados.getUf());
        endereco.setCep(dados.getCep());

        return enderecoRepository.save(endereco);
    }

    @Transactional
    public void definirEnderecoPrincipal(Long usuarioId, Long enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado a este endereço");
        }

        enderecoRepository.desativarTodosPrincipais(usuarioId);
        endereco.setPrincipal(true);
        enderecoRepository.save(endereco);
    }

    @Transactional
    public void removerEndereco(Long usuarioId, Long enderecoId) {
        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuarioId)) {
            throw new BusinessException("Acesso negado a este endereço");
        }

        boolean eraPrincipal = Boolean.TRUE.equals(endereco.getPrincipal());
        enderecoRepository.delete(endereco);

        // Se era o principal, promove o próximo disponível
        if (eraPrincipal) {
            enderecoRepository.findByUsuarioId(usuarioId)
                    .stream().findFirst().ifPresent(proximo -> {
                        proximo.setPrincipal(true);
                        enderecoRepository.save(proximo);
                    });
        }
    }

    /**
     * Retorna o endereço principal formatado como String para uso no cálculo de frete.
     * Integra com GET /api/frete/calcular?endereco=...
     */
    public String obterEnderecoEntregaPrincipal(Long usuarioId) {
        return enderecoRepository.findByUsuarioIdAndPrincipalTrue(usuarioId)
                .map(Endereco::toEnderecoCompleto)
                .orElseThrow(() -> new BusinessException(
                        "Nenhum endereço principal cadastrado. Adicione um endereço em /api/usuarios/enderecos"));
    }

    // -------------------------------------------------------------------------
    // RECUPERAÇÃO DE SENHA
    // -------------------------------------------------------------------------

    @Transactional
    public void solicitarRecuperacaoSenha(String email) {
        // Sempre retorna sucesso para não vazar quais e-mails existem (segurança)
        usuarioRepository.findByEmail(email).ifPresent(usuario -> {
            String token = gerarToken();
            usuario.setTokenRecuperacaoSenha(token);
            usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1));
            usuarioRepository.save(usuario);
            emailService.enviarRecuperacaoSenha(email, usuario.getNome(), token);
        });
    }

    @Transactional
    public void resetarSenha(String token, String novaSenha) {
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new BusinessException("A senha deve ter pelo menos 6 caracteres");
        }

        Usuario usuario = usuarioRepository.findByTokenRecuperacaoSenha(token)
                .orElseThrow(() -> new BusinessException("Token inválido ou expirado"));

        if (!usuario.tokenValido(token)) {
            throw new BusinessException("Token expirado. Solicite um novo link de recuperação.");
        }

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        usuario.limparToken();
        usuarioRepository.save(usuario);
    }

    // -------------------------------------------------------------------------
    // INTERNO
    // -------------------------------------------------------------------------

    private String gerarToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
