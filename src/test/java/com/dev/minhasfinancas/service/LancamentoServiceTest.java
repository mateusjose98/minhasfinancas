package com.dev.minhasfinancas.service;

import com.dev.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.dev.minhasfinancas.exceptions.RegraNegocioException;
import com.dev.minhasfinancas.model.entity.Lancamento;
import com.dev.minhasfinancas.model.entity.Usuario;
import com.dev.minhasfinancas.model.enums.StatusLancamento;
import com.dev.minhasfinancas.model.enums.TipoLancamento;
import com.dev.minhasfinancas.model.repository.LancamentoRepository;
import com.dev.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.dev.minhasfinancas.model.repository.UsuarioRepository;
import com.dev.minhasfinancas.service.impl.LancamentoServiceImpl;
import com.dev.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.doThrow;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {


    @SpyBean // métodos reais
    LancamentoServiceImpl service;

    @MockBean // métodos simulados
    LancamentoRepository repository;

    @Test
    public void deveSalvarUmLancamento(){

        Lancamento lancamentoParaSalvar = LancamentoRepositoryTest.lancamentoFactory();
        Mockito.doNothing().when(service).validar(lancamentoParaSalvar);

        Lancamento lancamentoSalvo = LancamentoRepositoryTest.lancamentoFactory();
        lancamentoSalvo.setId(1L); // deve haver um id pois foi "salvo" no banco
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
        Mockito.when(repository.save(lancamentoParaSalvar)).thenReturn(lancamentoSalvo);

        Lancamento lancamentoObtido = service.salvar(lancamentoParaSalvar);

        Assertions.assertThat(lancamentoObtido.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
        Assertions.assertThat(lancamentoObtido.getId()).isEqualTo(lancamentoSalvo.getId());
    }

    @Test
    public void deveFalharAoSalvarLancamento(){
        //cenário
        Lancamento lancamentoASalvar = LancamentoRepositoryTest.lancamentoFactory();
        doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);

        //execucao e verificacao
        catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class );
        verify(repository, never()).save(lancamentoASalvar);

    }

    @Test
    public void deveAtualizarUmLancamento() {
        //cenário
        Lancamento lancamentoSalvo = LancamentoRepositoryTest.lancamentoFactory();
        lancamentoSalvo.setId(1l);
        lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);

        doNothing().when(service).validar(lancamentoSalvo);

        when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);

        //execucao
        service.atualizar(lancamentoSalvo);

        //verificação
        verify(repository, times(1)).save(lancamentoSalvo);

    }

    @Test
    public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();

        //execucao e verificacao
        catchThrowableOfType( () -> service.atualizar(lancamento), NullPointerException.class );
        verify(repository, never()).save(lancamento);
    }

    @Test
    public void deveDeletarUmLancamento() {
        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();
        lancamento.setId(1l);

        //execucao
        service.deletar(lancamento);

        //verificacao
        verify( repository ).delete(lancamento);
    }

    @Test
    public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {

        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();

        //execucao
        catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class );

        //verificacao
        verify( repository, never() ).delete(lancamento);
    }


    @Test
    public void deveFiltrarLancamentos() {
        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();
        lancamento.setId(1l);

        List<Lancamento> lista = Arrays.asList(lancamento);
        when( repository.findAll(any(Example.class)) ).thenReturn(lista);

        //execucao
        List<Lancamento> resultado = service.buscar(lancamento);

        //verificacoes
        assertThat(resultado)
                .isNotEmpty()
                .hasSize(1)
                .contains(lancamento);

    }

    @Test
    public void deveAtualizarOStatusDeUmLancamento() {
        //cenário
        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();
        lancamento.setId(1l);
        lancamento.setStatus(StatusLancamento.PENDENTE);

        StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
        doReturn(lancamento).when(service).atualizar(lancamento);

        //execucao
        service.atualizarStatus(lancamento, novoStatus);

        //verificacoes
        assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
        verify(service).atualizar(lancamento);

    }

    @Test
    public void deveObterUmLancamentoPorID() {
        //cenário
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();
        lancamento.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(lancamento));

        //execucao
        Optional<Lancamento> resultado =  service.obterPorId(id);

        //verificacao
        assertThat(resultado.isPresent()).isTrue();
    }

    @Test
    public void deveREtornarVazioQuandoOLancamentoNaoExiste() {
        //cenário
        Long id = 1l;

        Lancamento lancamento = LancamentoRepositoryTest.lancamentoFactory();
        lancamento.setId(id);

        when( repository.findById(id) ).thenReturn( Optional.empty() );

        //execucao
        Optional<Lancamento> resultado =  service.obterPorId(id);

        //verificacao
        assertThat(resultado.isPresent()).isFalse();
    }

    @Test
    public void deveLancarErrosAoValidarUmLancamento() {
        Lancamento lancamento = new Lancamento();

        Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("");

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");

        lancamento.setDescricao("Salario");

        erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setAno(0);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setAno(13);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");

        lancamento.setMes(1);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(202);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");

        lancamento.setAno(2020);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        lancamento.setUsuario(new Usuario());

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");

        lancamento.getUsuario().setId(1l);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.ZERO);

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");

        lancamento.setValor(BigDecimal.valueOf(1));

        erro = catchThrowable( () -> service.validar(lancamento) );
        assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de Lançamento.");

    }

    @Test
    public void deveObterSaldoPorUsuario() {
        //cenario
        Long idUsuario = 1l;

        when( repository
                .obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO))
                .thenReturn(BigDecimal.valueOf(100));

        when( repository
                .obterSaldoPorTipoLancamentoEUsuarioEStatus(idUsuario, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO))
                .thenReturn(BigDecimal.valueOf(50));

        //execucao
        BigDecimal saldo = service.obterSaldoPorUsuario(idUsuario);

        //verificacao
        assertThat(saldo).isEqualTo(BigDecimal.valueOf(50));

    }



}
