package br.com.ifpe.intelifones.util.exception;

public class CompradorException extends RuntimeException {

    public static final String MSG_NOME_OBRIGATORIO = "O nome do comprador é obrigatório";
    public static final String MSG_NOME_MINIMO_CARACTERES = "O nome deve ter pelo menos 3 caracteres";
    public static final String MSG_EMAIL_OBRIGATORIO = "O e-mail é obrigatório";
    public static final String MSG_EMAIL_INVALIDO = "E-mail inválido";
    public static final String MSG_EMAIL_JA_EXISTE = "Este e-mail já está cadastrado";
    public static final String MSG_SENHA_MINIMO_CARACTERES = "A senha deve ter pelo menos 6 caracteres";
    public static final String MSG_COMPRADOR_NAO_ENCONTRADO = "Comprador com ID %d não encontrado";
    public static final String MSG_COMPRADOR_NAO_ENCONTRADO_POR_EMAIL = "Comprador com e-mail '%s' não encontrado";

    public CompradorException(String message) {
        super(message);
    }
}