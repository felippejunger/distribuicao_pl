package com.empresa.distribuicaopl.models;

import com.empresa.distribuicaopl.models.Departamentos.*;
import com.empresa.distribuicaopl.utils.DoubleUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

public class Participacao {

    private double valorParticipacao;

    public double getValorParticipacao() {
        return DoubleUtils.buscaValorFomatado(valorParticipacao,2);
    }

    public void setValorParticipacao(double valorParticipacao) {
        this.valorParticipacao = valorParticipacao;
    }

    public double calculaParticipacao(double salarioBruto, Peso pesoTempoAdmissao, Peso pesoAreaAtuacao, Peso pesoFaixaSalarial){

        double fator1 = ( salarioBruto * pesoTempoAdmissao.getValor() );
        double fator2 = ( salarioBruto * pesoAreaAtuacao.getValor() );

        double participacao = ( ( fator1 + fator2 ) / pesoFaixaSalarial.getValor() ) * 12;

        return DoubleUtils.buscaValorFomatado(participacao,2);
    }

    public Peso calculaPesoTempoAdmissao(LocalDate dataAdmissao, LocalDate hoje){

        Peso pesoPorTempo;

        Period periodo = Period.between ( dataAdmissao , hoje );

        int tempoDeAdmissao = periodo.getYears();

        /** PESO_1 Até 1 ano de casa **/
        if(tempoDeAdmissao < 1){
            pesoPorTempo = Peso.PESO_1;

        /** PESO_2 Mais de um ano e menos de 3 **/
        }else if(tempoDeAdmissao >= 1 && tempoDeAdmissao < 3){
            pesoPorTempo = Peso.PESO_2;

        /** PESO_3 Acima de 3 anos e menos de 8 anos**/
        }else if(tempoDeAdmissao >= 3 && tempoDeAdmissao < 8){
            pesoPorTempo = Peso.PESO_3;

        /** PESO_5 Mais 8 anos de casa **/
        }else{
            pesoPorTempo = Peso.PESO_5;
        }

        return pesoPorTempo;
    }

    public Peso calculaPesoPorArea(Departamento areaDeAtuacao){

        Peso pesoPorArea;

        if(areaDeAtuacao.getClass().equals(Diretoria.class)){
            pesoPorArea = Peso.PESO_1;

        }else if(areaDeAtuacao.getClass().equals(Contabilidade.class) ||
                areaDeAtuacao.getClass().equals(Financeiro.class) ||
                areaDeAtuacao.getClass().equals(Tecnologia.class)){
            pesoPorArea = Peso.PESO_2;

        }else if(areaDeAtuacao.getClass().equals(ServicosGerais.class)){
            pesoPorArea = Peso.PESO_3;
        }else if(areaDeAtuacao.getClass().equals(RelacionamentoCliente.class)){

            pesoPorArea = Peso.PESO_5;
        }else {

            pesoPorArea = Peso.PESO_1;
        }

        return pesoPorArea;
    }

    public Peso calculaPesoFaixaSalarial(double salarioFuncionario, double salarioMinimo){

        Peso pesoFaixaSalarial;

        double oitoSalariosMinimos = 8 * salarioMinimo;
        double cincoSalariosMinimos = 5 * salarioMinimo;
        double tresSalariosMinimos = 3 * salarioMinimo;

        /** FUNCIONARIOS QUE GANHAM MAIS DE 8 SALARIOS MINIMOS**/
        if(salarioFuncionario > oitoSalariosMinimos){
            pesoFaixaSalarial = Peso.PESO_5;

            /** FUNCIONARIOS QUE GANHAM MAIS DE 5 E MENOS DE 8 SALARIOS MINIMOS**/
        }else if( (salarioFuncionario > cincoSalariosMinimos) && ( salarioFuncionario <= oitoSalariosMinimos )){
            pesoFaixaSalarial = Peso.PESO_3;

            /** FUNCIONARIOS QUE GANHAM MAIS DE 3 E MENOS DE 5 SALARIOS MINIMOS**/
        }else if(salarioFuncionario > tresSalariosMinimos && salarioFuncionario <= cincoSalariosMinimos){
            pesoFaixaSalarial = Peso.PESO_2;

            /** FUNCIONARIOS QUE GANHAM MENOS DE 3 SALARIOS MINIMOS**/
        }else{
            pesoFaixaSalarial = Peso.PESO_1;
        }

        return pesoFaixaSalarial;
    }

    public Peso buscaPesoEstagiario(){
        return Peso.PESO_1;
    }
}
