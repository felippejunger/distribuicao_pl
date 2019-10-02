package com.empresa.distribuicaopl;

import com.empresa.distribuicaopl.models.Departamentos.*;
import com.empresa.distribuicaopl.models.Participacao;
import com.empresa.distribuicaopl.models.Peso;
import com.empresa.distribuicaopl.models.Salario;
import com.empresa.distribuicaopl.utils.DoubleUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.gen5.api.DisplayName;

import java.time.LocalDate;
import java.time.Month;

public class TestParticipacao {

    private final Participacao participacao = new Participacao();

    @Test
    @DisplayName("Cálculo de peso por área de atuação")
    public void calculoPesoPorArea() {
        Assert.assertEquals(Peso.PESO_1, participacao.calculaPesoPorArea(new Diretoria()));
        Assert.assertEquals(Peso.PESO_2, participacao.calculaPesoPorArea(new Contabilidade()));
        Assert.assertEquals(Peso.PESO_2, participacao.calculaPesoPorArea(new Financeiro()));
        Assert.assertEquals(Peso.PESO_2, participacao.calculaPesoPorArea(new Tecnologia()));
        Assert.assertEquals(Peso.PESO_3, participacao.calculaPesoPorArea(new ServicosGerais()));
        Assert.assertEquals(Peso.PESO_5, participacao.calculaPesoPorArea(new RelacionamentoCliente()));
    }

    @Test
    @DisplayName("Cálculo de peso por tempo de admissão")
    public void calculoPesoTempoAdmissao(){

        LocalDate hoje = LocalDate.of(2019, Month.OCTOBER, 1); // 2019-10-01

        Assert.assertEquals(Peso.PESO_1, participacao.calculaPesoTempoAdmissao(LocalDate.of(2019,Month.APRIL,1),hoje));
        Assert.assertEquals(Peso.PESO_2, participacao.calculaPesoTempoAdmissao(LocalDate.of(2018,Month.OCTOBER,1),hoje));
        Assert.assertEquals(Peso.PESO_2, participacao.calculaPesoTempoAdmissao(LocalDate.of(2018,Month.SEPTEMBER,1),hoje));
        Assert.assertEquals(Peso.PESO_2, participacao.calculaPesoTempoAdmissao(LocalDate.of(2016,Month.DECEMBER,1),hoje));
        Assert.assertEquals(Peso.PESO_3, participacao.calculaPesoTempoAdmissao(LocalDate.of(2016,Month.OCTOBER,1),hoje));
        Assert.assertEquals(Peso.PESO_3, participacao.calculaPesoTempoAdmissao(LocalDate.of(2011,Month.DECEMBER,1),hoje));
        Assert.assertEquals(Peso.PESO_5, participacao.calculaPesoTempoAdmissao(LocalDate.of(2011,Month.OCTOBER,1),hoje));
        Assert.assertEquals(Peso.PESO_5, participacao.calculaPesoTempoAdmissao(LocalDate.of(2000,Month.APRIL,21),hoje));
    }


    @Test
    @DisplayName("Cálculo de peso por faixa salarial")
    public void calculoPesoFaixaSalarial(){

        double salarioMinimo = Salario.SALARIO_MINIMO;

        Assert.assertEquals(Peso.PESO_5,participacao.calculaPesoFaixaSalarial((8 * salarioMinimo) + 0.01, salarioMinimo));
        Assert.assertEquals(Peso.PESO_3,participacao.calculaPesoFaixaSalarial(8 * salarioMinimo, salarioMinimo));
        Assert.assertEquals(Peso.PESO_3,participacao.calculaPesoFaixaSalarial(6 * salarioMinimo, salarioMinimo));
        Assert.assertEquals(Peso.PESO_3,participacao.calculaPesoFaixaSalarial((5 * salarioMinimo) + 0.01, salarioMinimo));
        Assert.assertEquals(Peso.PESO_2,participacao.calculaPesoFaixaSalarial(5 * salarioMinimo, salarioMinimo));
        Assert.assertEquals(Peso.PESO_2,participacao.calculaPesoFaixaSalarial(4 * salarioMinimo, salarioMinimo));
        Assert.assertEquals(Peso.PESO_2,participacao.calculaPesoFaixaSalarial((3 * salarioMinimo) + 0.01, salarioMinimo));
        Assert.assertEquals(Peso.PESO_1,participacao.calculaPesoFaixaSalarial(3 * salarioMinimo, salarioMinimo));
        Assert.assertEquals(Peso.PESO_1,participacao.calculaPesoFaixaSalarial(2 * salarioMinimo, salarioMinimo));
        Assert.assertEquals(Peso.PESO_1,participacao.calculaPesoFaixaSalarial( salarioMinimo, salarioMinimo));
    }

    @Test
    @DisplayName("Cálculo do peso do estagiário")
    public void calculaPesoEstagiario(){

        Assert.assertEquals(Peso.PESO_1, participacao.buscaPesoEstagiario());
    }

    @Test
    @DisplayName("Cálculo participação dos lucros do funcionário")
    public void calculaParticipacaoLucro(){

        // ( SB * PTA ) + ( SB * PAA )
        //----------------------------  * 12 (Mêses do ano)
        //           ( PFS )

        //SB: Sário bruto
        //PTA: Peso por tempo de admissão
        //PAA: Peso por área de atuação
        //PFS: Peso por faixa salarial

        double salarioMinimo = Salario.SALARIO_MINIMO;

        /** CASOS PESO 1 | X | X **/
        Assert.assertEquals(71856.0,   participacao.calculaParticipacao(salarioMinimo * 3,          Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(35928.12,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,   Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(39920.08,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(38323.25,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);
                                                                                                                                                                                    
        Assert.assertEquals(107784.0,  participacao.calculaParticipacao(salarioMinimo * 3,          Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(53892.18,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,   Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(59880.12,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(57484.87,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(95808.48,  participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(71856.24,  participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(79840.16,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(76646.5,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(143712.72, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(107784.36, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(119760.24, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(114969.74, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        /** CASOS PESO 2 | X | X **/
        Assert.assertEquals(71856.0,   participacao.calculaParticipacao(salarioMinimo *  2,         Peso.PESO_2  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(53892.18,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,  Peso.PESO_2   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(59880.0,   participacao.calculaParticipacao(salarioMinimo *  5,         Peso.PESO_2  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(57484.87,  participacao.calculaParticipacao((salarioMinimo * 8)+0.01,  Peso.PESO_2   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(95808.48,  participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(71856.24,  participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(79840.16,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(76646.5,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(119760.6, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(89820.3,   participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(99800.2,   participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(95808.12,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(167664.84, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(125748.42, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(139720.28, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(134131.37, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        /** CASOS PESO 3 | X | X **/
        Assert.assertEquals(95808.0,   participacao.calculaParticipacao(salarioMinimo *  2,         Peso.PESO_3  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(71856.24,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,  Peso.PESO_3   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(79840.0,   participacao.calculaParticipacao(salarioMinimo *  5,         Peso.PESO_3  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(76646.5,  participacao.calculaParticipacao((salarioMinimo * 8)+0.01,  Peso.PESO_3   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(119760.6, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(89820.3,   participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(99800.2,   participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(95808.12,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(143712.72, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(107784.36, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(119760.24, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(114969.74, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(191616.96, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(143712.48, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(159680.32, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(153292.99, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        /** CASOS PESO 5 | X | X **/
        Assert.assertEquals(143712.0,  participacao.calculaParticipacao(salarioMinimo *  2,          Peso.PESO_5 /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(107784.36, participacao.calculaParticipacao((salarioMinimo * 3)+0.01,   Peso.PESO_5  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(119760.0,  participacao.calculaParticipacao(salarioMinimo *  5,          Peso.PESO_5 /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(114969.74, participacao.calculaParticipacao((salarioMinimo * 8)+0.01,   Peso.PESO_5  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(167664.84, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(125748.42, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(139720.28, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(134131.37, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(191616.96, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(143712.48, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(159680.32, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(153292.99, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

        Assert.assertEquals(239521.2, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/),0.00001);
        Assert.assertEquals(179640.6,  participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/),0.00001);
        Assert.assertEquals(199600.4,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/),0.00001);
        Assert.assertEquals(191616.24, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/),0.00001);

    }

}
