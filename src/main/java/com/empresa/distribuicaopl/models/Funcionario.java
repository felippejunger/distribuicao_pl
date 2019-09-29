package com.empresa.distribuicaopl.models;

import com.empresa.distribuicaopl.models.Departamentos.Departamento;
import com.empresa.distribuicaopl.utils.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.swing.text.DateFormatter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RedisHash("Funcionario")
public class Funcionario extends Pessoa implements Serializable {

    @Id
    private String matricula;
    private double salario_bruto;
    private String cargo;
    private LocalDate dataAdmissao;
    private Participacao participacao;
    private Departamento departamento;


    public Participacao getParticipacao() {
        return participacao;
    }

    public void setParticipacao(Participacao participacao) {
        this.participacao = participacao;
    }

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


    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public boolean eEstagiario(){
        return StringUtils.removerAcentos(this.getCargo().toLowerCase()).equals("estagiario");
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "[ Matricula: "+getMatricula()+"; Nome: "+getNome()+"; Area: "+getDepartamento().getNome()+
                "; cargo: "+getCargo()+"; salario bruto: "+getSalario_bruto()+"; Admissao: "+getDataAdmissao().format(formatter);
    }
}
