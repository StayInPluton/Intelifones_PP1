package br.com.ifpe.intelifones.model.produto;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.intelifones.util.entity.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Produto")
@SQLRestriction("habilitado = true")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Produto extends EntidadeAuditavel {

   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column
   private String nome;

   @Column
   private String descricao;

   @Column
   private Double preco;

   @Column
   private Boolean usado;

   @Column
   private String estadoConservacao; // novo, seminovo, etc

   @Column
   private Boolean ativo;

   @ManyToOne
   @JoinColumn(name = "categoria_id")
   private CategoriaProduto categoria;

}