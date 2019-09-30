package com.empresa.distribuicaopl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.empresa.distribuicaopl.models.Departamentos.*;
import com.empresa.distribuicaopl.models.Participacao;
import com.empresa.distribuicaopl.models.Peso;
import com.empresa.distribuicaopl.models.Salario;
import com.empresa.distribuicaopl.utils.DoubleUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

class CalculoParticipacaoTest {

    private final Participacao participacao = new Participacao();

    @Test
    @DisplayName("Cálculo de peso por área de atuação")
    void calculoPesoPorArea() {
        assertEquals(Peso.PESO_1, participacao.calculaPesoPorArea(new Diretoria()));
        assertEquals(Peso.PESO_2, participacao.calculaPesoPorArea(new Contabilidade()));
        assertEquals(Peso.PESO_2, participacao.calculaPesoPorArea(new Financeiro()));
        assertEquals(Peso.PESO_2, participacao.calculaPesoPorArea(new Tecnologia()));
        assertEquals(Peso.PESO_3, participacao.calculaPesoPorArea(new ServicosGerais()));
        assertEquals(Peso.PESO_5, participacao.calculaPesoPorArea(new RelacionamentoCliente()));
    }

    @Test
    @DisplayName("Cálculo de peso por tempo de admissão")
    void calculoPesoTempoAdmissao(){

        LocalDate hoje = LocalDate.of(2019, Month.OCTOBER, 1); // 2019-10-01

        assertEquals(Peso.PESO_1, participacao.calculaPesoTempoAdmissao(LocalDate.of(2019,Month.APRIL,1),hoje), "Menos de um ano de casa tem que ter Peso 1");
        assertEquals(Peso.PESO_2, participacao.calculaPesoTempoAdmissao(LocalDate.of(2018,Month.OCTOBER,1),hoje), "Um ano ou mais e menos que 3 tem que ter peso 2");
        assertEquals(Peso.PESO_2, participacao.calculaPesoTempoAdmissao(LocalDate.of(2018,Month.SEPTEMBER,1),hoje), "Mais de um ano e menos que 3 tem que ter peso 2");
        assertEquals(Peso.PESO_2, participacao.calculaPesoTempoAdmissao(LocalDate.of(2016,Month.DECEMBER,1),hoje), "Mais de um ano e menos que 3 tem que ter peso 2");
        assertEquals(Peso.PESO_3, participacao.calculaPesoTempoAdmissao(LocalDate.of(2016,Month.OCTOBER,1),hoje), "Mais de um tres e menos que 8 tem que ter peso 3");
        assertEquals(Peso.PESO_3, participacao.calculaPesoTempoAdmissao(LocalDate.of(2011,Month.DECEMBER,1),hoje), "Menos de um ano de casa tem que ter Peso 1");
        assertEquals(Peso.PESO_5, participacao.calculaPesoTempoAdmissao(LocalDate.of(2011,Month.OCTOBER,1),hoje), "Mais de 8 anos peso 5");
        assertEquals(Peso.PESO_5, participacao.calculaPesoTempoAdmissao(LocalDate.of(2000,Month.APRIL,21),hoje), "Mais de 8 anos peso 5");
    }


    @Test
    @DisplayName("Cálculo de peso por faixa salarial")
    void calculoPesoFaixaSalarial(){

        double salarioMinimo = Salario.SALARIO_MINIMO;

        assertEquals(Peso.PESO_5,participacao.calculaPesoFaixaSalarial((8 * salarioMinimo) + 0.01, salarioMinimo), "Acima de 8x o salario minimo, peso 5");
        assertEquals(Peso.PESO_3,participacao.calculaPesoFaixaSalarial(8 * salarioMinimo, salarioMinimo), "Acima de 5x e abaixo de 8x o salario minimo, peso 3");
        assertEquals(Peso.PESO_3,participacao.calculaPesoFaixaSalarial(6 * salarioMinimo, salarioMinimo), "Acima de 5x e abaixo de 8x o salario minimo, peso 3");
        assertEquals(Peso.PESO_3,participacao.calculaPesoFaixaSalarial((5 * salarioMinimo) + 0.01, salarioMinimo), "Acima de 5x e abaixo de 8x o salario minimo, peso 3");
        assertEquals(Peso.PESO_2,participacao.calculaPesoFaixaSalarial(5 * salarioMinimo, salarioMinimo), "Acima de 3x e abaixo de 5x o salario minimo, peso 2");
        assertEquals(Peso.PESO_2,participacao.calculaPesoFaixaSalarial(4 * salarioMinimo, salarioMinimo), "Acima de 3x e abaixo de 5x o salario minimo, peso 2");
        assertEquals(Peso.PESO_2,participacao.calculaPesoFaixaSalarial((3 * salarioMinimo) + 0.01, salarioMinimo), "Acima de 3x e abaixo de 5x o salario minimo, peso 2");
        assertEquals(Peso.PESO_1,participacao.calculaPesoFaixaSalarial(3 * salarioMinimo, salarioMinimo), "Até 3x salario minimo, peso 1");
        assertEquals(Peso.PESO_1,participacao.calculaPesoFaixaSalarial(2 * salarioMinimo, salarioMinimo), "Até 3x salario minimo, peso 1");
        assertEquals(Peso.PESO_1,participacao.calculaPesoFaixaSalarial( salarioMinimo, salarioMinimo), "Até 3x salario minimo, peso 1");
    }

    @Test
    @DisplayName("Cálculo do peso do estagiário")
    void calculaPesoEstagiario(){

        assertEquals(Peso.PESO_1, participacao.buscaPesoEstagiario(), "Peso do estagiario e 1.");
    }

    @Test
    @DisplayName("Cálculo participação dos lucros do funcionário")
    void calculaParticipacaoLucro(){

        // ( SB * PTA ) + ( SB * PAA )
        //----------------------------  * 12 (Mêses do ano)
        //           ( PFS )

        //SB: Sário bruto
        //PTA: Peso por tempo de admissão
        //PAA: Peso por área de atuação
        //PFS: Peso por faixa salarial

        double salarioMinimo = Salario.SALARIO_MINIMO;

        /** CASOS PESO 1 | X | X **/
        assertEquals(71856.0,   participacao.calculaParticipacao(salarioMinimo * 3,          Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(35928.12,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,   Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(39920.08,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(38323.25,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(107784.0,  participacao.calculaParticipacao(salarioMinimo * 3,          Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(53892.18,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,   Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(59880.12,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(57484.87,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(95808.48,  participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(71856.24,  participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(79840.16,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(76646.5,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(143712.72, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(107784.36, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(119760.24, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(114969.74, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_1  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        /** CASOS PESO 2 | X | X **/
        assertEquals(71856.0,   participacao.calculaParticipacao(salarioMinimo *  2,         Peso.PESO_2  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(53892.18,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,  Peso.PESO_2   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(59880.0,   participacao.calculaParticipacao(salarioMinimo *  5,         Peso.PESO_2  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(57484.87,  participacao.calculaParticipacao((salarioMinimo * 8)+0.01,  Peso.PESO_2   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(95808.48,  participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(71856.24,  participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(79840.16,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(76646.5,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(119760.6, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(89820.3,   participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(99800.2,   participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(95808.12,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(167664.84, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(125748.42, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(139720.28, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(134131.37, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_2  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        /** CASOS PESO 3 | X | X **/
        assertEquals(95808.0,   participacao.calculaParticipacao(salarioMinimo *  2,         Peso.PESO_3  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(71856.24,  participacao.calculaParticipacao((salarioMinimo * 3)+0.01,  Peso.PESO_3   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(79840.0,   participacao.calculaParticipacao(salarioMinimo *  5,         Peso.PESO_3  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(76646.5,  participacao.calculaParticipacao((salarioMinimo * 8)+0.01,  Peso.PESO_3   /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(119760.6, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(89820.3,   participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(99800.2,   participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(95808.12,  participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(143712.72, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(107784.36, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(119760.24, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(114969.74, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(191616.96, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(143712.48, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(159680.32, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(153292.99, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_3  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        /** CASOS PESO 5 | X | X **/
        assertEquals(143712.0,  participacao.calculaParticipacao(salarioMinimo *  2,          Peso.PESO_5 /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(107784.36, participacao.calculaParticipacao((salarioMinimo * 3)+0.01,   Peso.PESO_5  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(119760.0,  participacao.calculaParticipacao(salarioMinimo *  5,          Peso.PESO_5 /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(114969.74, participacao.calculaParticipacao((salarioMinimo * 8)+0.01,   Peso.PESO_5  /** TEMPO **/, Peso.PESO_1/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(167664.84, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(125748.42, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(139720.28, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(134131.37, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_2/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(191616.96, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(143712.48, participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(159680.32, participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(153292.99, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_3/** AREA **/, Peso.PESO_5 /** SALARIO **/));

        assertEquals(239521.2, participacao.calculaParticipacao((salarioMinimo * 2) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_1 /** SALARIO **/));
        assertEquals(179640.6,  participacao.calculaParticipacao((salarioMinimo * 3) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_2 /** SALARIO **/));
        assertEquals(199600.4,  participacao.calculaParticipacao((salarioMinimo * 5) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_3 /** SALARIO **/));
        assertEquals(191616.24, participacao.calculaParticipacao((salarioMinimo * 8) + 0.01, Peso.PESO_5  /** TEMPO **/, Peso.PESO_5/** AREA **/, Peso.PESO_5 /** SALARIO **/));

    }

}
