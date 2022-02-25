package com.dev.minhasfinancas.service;

import com.dev.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.dev.minhasfinancas.exceptions.RegraNegocioException;
import com.dev.minhasfinancas.model.entity.Usuario;
import com.dev.minhasfinancas.model.repository.UsuarioRepository;
import com.dev.minhasfinancas.service.UsuarioService;
import com.dev.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;

    @Test(expected = Test.None.class)
    public void deveSalvarUmUsuario() {
        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1l)
                .nome("nome")
                .email("email@email.com")
                .senha("senha").build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //acao
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //verificao
        Assertions.assertThat(usuarioSalvo).isNotNull();
        Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
        Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
        Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
        Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");

    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
        //cenario
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //acao
        org.junit.jupiter.api.Assertions
                .assertThrows(RegraNegocioException.class, () -> service.salvarUsuario(usuario) ) ;

        //verificacao
        Mockito.verify( repository, Mockito.never() ).save(usuario);
    }

    @Test(expected = Test.None.class)
    public void deveAutenticarUsuarioComSucesso(){
        String email = "email@email.com";
        String senha  = "senha";
        Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Usuario resultado = service.autenticar(email, senha);
        Assertions.assertThat(resultado).isNotNull();
    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastrado(){
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());


        Throwable ex = Assertions.catchThrowable(() ->  service.autenticar("email@email.com", "senha"));

        Assertions.assertThat(ex).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Usuário não Existe!");
    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater(){
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));


        Throwable ex = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "123"));

        Assertions.assertThat(ex).isInstanceOf(ErroAutenticacaoException.class).hasMessage("Senha Inválida");
    }

    @Test(expected = Test.None.class)
    public void deveValidarEmail(){
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
        service.validarEmail("email@email.com");


    }

    @Test(expected = RegraNegocioException.class)
    public void deveLancarRegraDeNegocioExceptionAoValidarEmailJaExistente(){
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        service.validarEmail("email@email.com");


    }
}
