package com.empresa.distribuicaopl.models.Departamentos;

public abstract class Departamento {
    public String getNome() {
        return nome;
    }

    protected void setNome(String nome) {
        this.nome = nome;
    }

    protected String nome;

}
