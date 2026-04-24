package br.com.ifpe.intelifones.model.comprador;

import br.com.ifpe.intelifones.util.exception.CompradorException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompradorService {

    @Autowired
    private CompradorRepository repository;

    @Transactional
    public Comprador save(Comprador comprador) {

        if (comprador.getNome() == null || comprador.getNome().trim().isEmpty()) {
            throw new CompradorException(CompradorException.MSG_NOME_OBRIGATORIO);
        }

        if (comprador.getNome().length() < 3) {
            throw new CompradorException(CompradorException.MSG_NOME_MINIMO_CARACTERES);
        }

        if (comprador.getEmail() == null || comprador.getEmail().trim().isEmpty()) {
            throw new CompradorException(CompradorException.MSG_EMAIL_OBRIGATORIO);
        }

        if (!comprador.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new CompradorException(CompradorException.MSG_EMAIL_INVALIDO);
        }

        if (repository.existsByEmailIgnoreCase(comprador.getEmail())) {
            throw new CompradorException(CompradorException.MSG_EMAIL_JA_EXISTE);
        }

        if (comprador.getSenha() == null || comprador.getSenha().length() < 6) {
            throw new CompradorException(CompradorException.MSG_SENHA_MINIMO_CARACTERES);
        }

        comprador.setHabilitado(Boolean.TRUE);
        comprador.setAtivo(true);

        return repository.save(comprador);
    }

    public List<Comprador> listarTodos() {
        return repository.findAll();
    }

    public List<Comprador> listarCompradoresAtivos() {
        return repository.findCompradoresAtivos();
    }

    public Comprador obterPorID(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new CompradorException(
                        String.format(CompradorException.MSG_COMPRADOR_NAO_ENCONTRADO, id)));
    }

    public Comprador obterPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new CompradorException(
                        String.format(CompradorException.MSG_COMPRADOR_NAO_ENCONTRADO_POR_EMAIL, email)));
    }

    @Transactional
    public Comprador update(Long id, Comprador compradorAlterado) {

        Comprador comprador = obterPorID(id);

        if (compradorAlterado.getNome() != null && !compradorAlterado.getNome().trim().isEmpty()) {
            if (compradorAlterado.getNome().length() < 3) {
                throw new CompradorException(CompradorException.MSG_NOME_MINIMO_CARACTERES);
            }
            comprador.setNome(compradorAlterado.getNome());
        }

        if (compradorAlterado.getTelefone() != null) {
            comprador.setTelefone(compradorAlterado.getTelefone());
        }

        if (compradorAlterado.getEndereco() != null) {
            comprador.setEndereco(compradorAlterado.getEndereco());
        }

        if (compradorAlterado.getSenha() != null && compradorAlterado.getSenha().length() >= 6) {
            comprador.setSenha(compradorAlterado.getSenha());
        }

        if (compradorAlterado.getAtivo() != null) {
            comprador.setAtivo(compradorAlterado.getAtivo());
        }

        comprador.setHabilitado(Boolean.TRUE);

        return repository.save(comprador);
    }

    @Transactional
    public void delete(Long id) {
        Comprador comprador = obterPorID(id);
        comprador.setHabilitado(Boolean.FALSE);
        comprador.setAtivo(false);
        repository.save(comprador);
    }
}