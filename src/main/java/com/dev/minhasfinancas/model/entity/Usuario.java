package com.dev.minhasfinancas.model.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(schema = "financas", name = "usuario")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String email;

    private String senha;


}
