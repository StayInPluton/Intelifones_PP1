package br.com.ifpe.intelifones.api.comprador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.ifpe.intelifones.model.comprador.Comprador;
import br.com.ifpe.intelifones.model.comprador.CompradorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/compradores")
@Tag(name = "Compradores", description = "Endpoints para gerenciamento de compradores")
public class CompradorController {

    @Autowired
    private CompradorService compradorService;

    @Operation(summary = "Criar um novo comprador", description = "Cria um novo comprador com os dados fornecidos")
    @PostMapping
    public ResponseEntity<Comprador> create(@Valid @RequestBody CompradorRequest request) {
        Comprador comprador = request.build();
        Comprador savedComprador = compradorService.save(comprador);
        return new ResponseEntity<>(savedComprador, HttpStatus.CREATED);
    }

    @Operation(summary = "Listar todos os compradores", description = "Retorna uma lista de todos os compradores cadastrados")
    @GetMapping
    public ResponseEntity<List<Comprador>> listAll() {
        List<Comprador> compradores = compradorService.listarTodos();
        return ResponseEntity.ok(compradores);
    }

    @Operation(summary = "Listar compradores ativos", description = "Retorna uma lista de compradores que estão ativos")
    @GetMapping("/ativos")
    public ResponseEntity<List<Comprador>> listAtivos() {
        List<Comprador> compradores = compradorService.listarCompradoresAtivos();
        return ResponseEntity.ok(compradores);
    }

    @Operation(summary = "Obter comprador por ID", description = "Retorna os detalhes de um comprador específico usando seu ID")
    @GetMapping("/{id}")
    public ResponseEntity<Comprador> getById(@PathVariable Long id) {
        Comprador comprador = compradorService.obterPorID(id);
        return ResponseEntity.ok(comprador);
    }

    @Operation(summary = "Obter comprador por email", description = "Retorna os detalhes de um comprador específico usando seu email")
    @GetMapping("/email/{email}")
    public ResponseEntity<Comprador> getByEmail(@PathVariable String email) {
        Comprador comprador = compradorService.obterPorEmail(email);
        return ResponseEntity.ok(comprador);
    }

    @Operation(summary = "Atualizar um comprador", description = "Atualiza os dados de um comprador existente usando seu ID")
    @PutMapping("/{id}")
    public ResponseEntity<Comprador> update(
            @PathVariable Long id,
            @Valid @RequestBody CompradorRequest request) {
        Comprador comprador = request.build();
        Comprador updatedComprador = compradorService.update(id, comprador);
        return ResponseEntity.ok(updatedComprador);
    }

    @Operation(summary = "Deletar um comprador", description = "Remove um comprador do sistema usando seu ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        compradorService.delete(id);
        return ResponseEntity.noContent().build();
    }
}