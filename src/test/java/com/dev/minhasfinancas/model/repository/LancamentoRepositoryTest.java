package com.dev.minhasfinancas.model.repository;
import static org.assertj.core.api.Assertions.assertThat;
import com.dev.minhasfinancas.model.entity.Lancamento;
import com.dev.minhasfinancas.model.enums.StatusLancamento;
import com.dev.minhasfinancas.model.enums.TipoLancamento;
import com.dev.minhasfinancas.model.repository.LancamentoRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {

    @Autowired
    LancamentoRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveBuscarUmLancamentoPorId() {
        Lancamento lancamento = criarEPersistirLancamento();

        Optional<Lancamento> lancamentoEncontrado = repository.findById(lancamento.getId());

        assertThat(lancamentoEncontrado.isPresent()).isTrue();
    }

    @Test
    public void deveAtualizarUmLancamento(){
        Lancamento lancamento = criarEPersistirLancamento();
        lancamento.setAno(2021);
        lancamento.setDescricao("Nova descrição para atualização");

        repository.save(lancamento);

        Lancamento lancamentoAtual = entityManager.find(Lancamento.class, lancamento.getId());

        Assertions.assertThat(lancamentoAtual.getAno()).isEqualTo(2021);
        Assertions.assertThat(lancamentoAtual.getDescricao()).isEqualTo("Nova descrição para atualização");

    }

    public Lancamento criarEPersistirLancamento() {
        Lancamento lancamento = lancamentoFactory();
        return entityManager.persist(lancamento);
    }


    @Test
    public void deveSalvarUmLancamentoQuandoDadosCorretamentePreenchidos(){
        Lancamento l = lancamentoFactory();
        l = repository.save(l);
        Assertions.assertThat(l.getId()).isNotNull();
    }

    @Test
    public void deveDeletarUmLancamentoExistente(){
        Lancamento l = lancamentoFactory();
        entityManager.persist(l);
        entityManager.flush();
        l = entityManager.find(Lancamento.class, l.getId());

        repository.delete(l);

        Lancamento lancamentoInexistente = entityManager.find(Lancamento.class, l.getId());
        Assertions.assertThat(lancamentoInexistente).isNull();


    }

    public static Lancamento lancamentoFactory() {
       return  Lancamento.builder()
                .ano(2019)
                .mes(1)
                .descricao("Lançamento qualquer")
                .valor(BigDecimal.valueOf(10))
                .tipo(TipoLancamento.RECEITA)
                .status(StatusLancamento.PENDENTE)
                .dataCadastro(LocalDate.now())
                .build();
    }
}
