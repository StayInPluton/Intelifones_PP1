package br.com.ifpe.intelifones.model.produto;

import org.hibernate.annotations.SQLRestriction;

import br.com.ifpe.intelifones.util.entity.EntidadeAuditavel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Produto extends EntidadeAuditavel  {

   @ManyToOne
   private CategoriaProduto categoria;

  
   @Column
   private String codigo;

   @Column
   private String titulo;

   @Column
   private String descricao;

   @Column
   private Double valorUnitario;

   @Column
   private Integer tempoEntregaMinimo;

   @Column
   private Integer tempoEntregaMaximo;

}