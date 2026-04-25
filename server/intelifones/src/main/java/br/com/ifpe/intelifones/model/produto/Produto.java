package br.com.ifpe.intelifones.model.produto;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.intelifones.util.entity.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(nullable = false)
   private String nome;

   @Column
   private String descricao;

   @Column(nullable = false)
   private Double preco;

   @Column(nullable = false)
   private Boolean usado;

   @Column(nullable = false)
   private String estadoConservacao; // novo, seminovo, etc

   @Column(nullable = false)
   private Boolean ativo;

   @ManyToOne
   @JoinColumn(name = "categoria_id", nullable = true)
   private CategoriaProduto categoria;

}