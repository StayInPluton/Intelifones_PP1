package br.com.ifpe.intelifones.api.auth;

import br.com.ifpe.intelifones.config.JwtService;
import br.com.ifpe.intelifones.model.usuario.Usuario;
import br.com.ifpe.intelifones.model.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"*"})
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login, registro e recuperação de senha")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final UsuarioService usuarioService;

    @Operation(summary = "Login de usuário")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getSenha()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtService.generateToken(userDetails);
        Usuario usuario = usuarioService.buscarPorEmail(request.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .role(usuario.getRole().name())
                .build();
    }

    @Operation(summary = "Registro de novo usuário (VENDEDOR ou COMPRADOR)")
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        Usuario usuario = usuarioService.registrar(request);

        UserDetails userDetails = userDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .role(usuario.getRole().name())
                .build();
    }

    @Operation(summary = "Solicitar recuperação de senha (envia e-mail com link)")
    @PostMapping("/recuperar-senha")
    public ResponseEntity<Map<String, String>> recuperarSenha(@RequestBody Map<String, String> body) {
        usuarioService.solicitarRecuperacaoSenha(body.get("email"));
        // Sempre 200 para não revelar se o e-mail existe
        return ResponseEntity.ok(Map.of("mensagem",
                "Se este e-mail estiver cadastrado, você receberá as instruções em breve."));
    }

    @Operation(summary = "Redefinir senha usando token do e-mail")
    @PostMapping("/resetar-senha")
    public ResponseEntity<Map<String, String>> resetarSenha(@RequestBody ResetarSenhaRequest request) {
        usuarioService.resetarSenha(request.getToken(), request.getNovaSenha());
        return ResponseEntity.ok(Map.of("mensagem", "Senha alterada com sucesso!"));
    }
}
