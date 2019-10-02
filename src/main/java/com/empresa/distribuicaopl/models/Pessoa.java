package com.empresa.distribuicaopl.models;

import java.io.Serializable;

public abstract class Pessoa implements Serializable {
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    protected String nome;
}
