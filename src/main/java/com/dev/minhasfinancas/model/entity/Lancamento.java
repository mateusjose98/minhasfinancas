package com.dev.minhasfinancas.model.entity;



import com.dev.minhasfinancas.model.enums.StatusLancamento;
import com.dev.minhasfinancas.model.enums.TipoLancamento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(schema = "financas", name = "lancamento")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lancamento {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    private Integer mes;

    private Integer ano;

    @ManyToOne @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    private BigDecimal valor;

    @Column(name = "data_cadastro")
    @Convert(converter = Jsr310JpaConverters.LocalDateConverter.class)
    private LocalDate dataCadastro;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoLancamento tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private StatusLancamento status;



}
