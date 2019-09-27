package com.empresa.distribuicaopl.models;

import com.empresa.distribuicaopl.models.Departamentos.Departamento;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDate;

@RedisHash("Funcionario")
public abstract class Funcionario extends Pessoa implements Serializable {

    @Id
    private String matricula;
    private double salario_bruto;
    private String cargo;
    private LocalDate dataAdmissao;
    private double valorParticipacao;
    private Departamento departamento;

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public double getSalario_bruto() {
        return salario_bruto;
    }

    public void setSalario_bruto(double salario_bruto) {
        this.salario_bruto = salario_bruto;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public LocalDate getDataAdmissao() {
        return dataAdmissao;
    }

    public void setDataAdmissao(LocalDate dataAdmissao) {
        this.dataAdmissao = dataAdmissao;
    }

    public double getValorParticipacao() {
        return valorParticipacao;
    }

    public void setValorParticipacao(double valorParticipacao) {
        this.valorParticipacao = valorParticipacao;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }


}
