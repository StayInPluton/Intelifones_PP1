package br.com.ifpe.intelifones.model.vendedor;

import br.com.ifpe.intelifones.util.exception.VendedorException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendedorService {

    @Autowired
    private VendedorRepository repository;

    @Transactional
    public Vendedor save(Vendedor vendedor) {

        // Validações
        if (vendedor.getNome() == null || vendedor.getNome().trim().isEmpty()) {
            throw new VendedorException(VendedorException.MSG_NOME_OBRIGATORIO);
        }

        if (vendedor.getNome().length() < 3) {
            throw new VendedorException(VendedorException.MSG_NOME_MINIMO_CARACTERES);
        }

        if (vendedor.getEmail() == null || vendedor.getEmail().trim().isEmpty()) {
            throw new VendedorException(VendedorException.MSG_EMAIL_OBRIGATORIO);
        }

        if (!vendedor.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new VendedorException(VendedorException.MSG_EMAIL_INVALIDO);
        }

        if (repository.existsByEmailIgnoreCase(vendedor.getEmail())) {
            throw new VendedorException(VendedorException.MSG_EMAIL_JA_EXISTE);
        }

        if (vendedor.getCpfCnpj() == null || vendedor.getCpfCnpj().trim().isEmpty()) {
            throw new VendedorException(VendedorException.MSG_CPF_CNPJ_OBRIGATORIO);
        }

        if (repository.existsByCpfCnpj(vendedor.getCpfCnpj())) {
            throw new VendedorException(VendedorException.MSG_CPF_CNPJ_JA_EXISTE);
        }

        if (vendedor.getSenha() == null || vendedor.getSenha().length() < 6) {
            throw new VendedorException(VendedorException.MSG_SENHA_MINIMO_CARACTERES);
        }

        vendedor.setHabilitado(Boolean.TRUE);
        vendedor.setAtivo(true);

        return repository.save(vendedor);
    }

    public List<Vendedor> listarTodos() {
        return repository.findAll();
    }

    public List<Vendedor> listarVendedoresAtivos() {
        return repository.findVendedoresAtivos();
    }

    public Vendedor obterPorID(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new VendedorException(
                        String.format(VendedorException.MSG_VENDEDOR_NAO_ENCONTRADO, id)));
    }

    public Vendedor obterPorEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new VendedorException(
                        String.format(VendedorException.MSG_VENDEDOR_NAO_ENCONTRADO_POR_EMAIL, email)));
    }

    @Transactional
    public Vendedor update(Long id, Vendedor vendedorAlterado) {

        Vendedor vendedor = obterPorID(id);

        if (vendedorAlterado.getNome() != null && !vendedorAlterado.getNome().trim().isEmpty()) {
            if (vendedorAlterado.getNome().length() < 3) {
                throw new VendedorException(VendedorException.MSG_NOME_MINIMO_CARACTERES);
            }
            vendedor.setNome(vendedorAlterado.getNome());
        }

        if (vendedorAlterado.getTelefone() != null) {
            vendedor.setTelefone(vendedorAlterado.getTelefone());
        }

        if (vendedorAlterado.getSenha() != null && vendedorAlterado.getSenha().length() >= 6) {
            vendedor.setSenha(vendedorAlterado.getSenha());
        }

        if (vendedorAlterado.getAtivo() != null) {
            vendedor.setAtivo(vendedorAlterado.getAtivo());
        }

        vendedor.setHabilitado(Boolean.TRUE);

        return repository.save(vendedor);
    }

    @Transactional
    public void delete(Long id) {
        Vendedor vendedor = obterPorID(id);
        vendedor.setHabilitado(Boolean.FALSE);
        vendedor.setAtivo(false);
        repository.save(vendedor);
    }
}