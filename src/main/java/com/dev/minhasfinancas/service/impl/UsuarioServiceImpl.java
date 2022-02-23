package com.dev.minhasfinancas.service.impl;

import com.dev.minhasfinancas.exceptions.ErroAutenticacaoException;
import com.dev.minhasfinancas.exceptions.RegraNegocioException;
import com.dev.minhasfinancas.model.entity.Usuario;
import com.dev.minhasfinancas.model.repository.UsuarioRepository;
import com.dev.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {
        Usuario usuario = repository
                            .findByEmail(email)
                            .orElseThrow( () -> new ErroAutenticacaoException("Usuário não Existe!"));
        boolean senhasBatem = usuario.getSenha().equals(senha);
        if(!senhasBatem) throw new ErroAutenticacaoException("Senha Inválida");
        return usuario;
    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {
        this.validarEmail(usuario.getEmail());
        return repository.save(usuario);
    }

    @Override
    public void validarEmail(String email) {

        if(repository.existsByEmail(email)){
            throw new RegraNegocioException("Já existe um usuário cadastrado com este email");
        }

    }
}
