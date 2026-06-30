package br.com.ifpe.intelifones.model.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void enviarBoasVindas(String email, String nome, String tipoUsuario) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(email);
        mensagem.setSubject("Bem-vindo ao Intelifones");
        mensagem.setText(
                "Olá " + nome +
                "\n\nSeu cadastro foi realizado com sucesso como " +
                tipoUsuario + ".");

        mailSender.send(mensagem);
    }

    public void enviarRecuperacaoSenha(String email, String nome, String token) {

        SimpleMailMessage mensagem = new SimpleMailMessage();

        mensagem.setTo(email);
        mensagem.setSubject("Recuperação de senha");

        mensagem.setText(
                "Olá " + nome +
                "\n\nSeu token de recuperação é:\n\n" +
                token);

        mailSender.send(mensagem);
    }
}