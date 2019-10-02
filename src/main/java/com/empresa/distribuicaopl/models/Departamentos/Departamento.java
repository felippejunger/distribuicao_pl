package com.empresa.distribuicaopl.models.Departamentos;

import java.io.Serializable;

public abstract class Departamento implements Serializable {
    public String getNome() {
        return nome;
    }

    protected void setNome(String nome) {
        this.nome = nome;
    }

    protected String nome;

}
