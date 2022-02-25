package com.dev.minhasfinancas.model.repository;

import com.dev.minhasfinancas.model.entity.Usuario;
import com.dev.minhasfinancas.model.repository.UsuarioRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@DataJpaTest
@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UsuarioRepositoryTest {


    @Autowired
    TestEntityManager entityManager;

    @Autowired
    UsuarioRepository repository;

    @Test
    public void deveVerificarExistenciaDeUmEmail(){
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        boolean result = repository.existsByEmail("email@hotmail.com");
        Assertions.assertThat(result).isTrue();

    }

    @Test
    public void deveRetornarFalsoQuandoNaoExistirUsuarioCadastradoComEmail(){

        boolean result = repository.existsByEmail("email@hotmail.com");

        Assertions.assertThat(result).isFalse();

    }

    @Test
    public void devePersistirUsuarioNoBanco(){
        Usuario usuario = criarUsuario();
        Usuario usuarioSalvo = repository.save(usuario);
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);
        Assertions.assertThat(repository.findByEmail("email@hotmail.com").isPresent()).isTrue();

    }

    @Test
    public void deveRetornarVazioQuandoEmailNaoExiste(){
        Assertions.assertThat(repository.findByEmail("email@hotmail.com").isPresent()).isFalse();

    }

    public static Usuario criarUsuario(){
         return Usuario.builder().nome("usuario")
                .email("email@hotmail.com")
                .senha("senha")
                .build();
    }
}
