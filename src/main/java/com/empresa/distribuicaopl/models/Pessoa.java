package com.empresa.distribuicaopl.models;

public abstract class Pessoa {
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    protected String nome;
}
