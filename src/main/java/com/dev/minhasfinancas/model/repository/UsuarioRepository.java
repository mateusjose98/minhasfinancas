package com.dev.minhasfinancas.model.repository;

import com.dev.minhasfinancas.model.entity.Usuario;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByNome(String nome);
    boolean existsByEmail(String email);
}
