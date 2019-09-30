package com.empresa.distribuicaopl.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class DoubleUtils {

    public static double buscaValorFomatado(double valor, int casaDecimais){
        BigDecimal bd = new BigDecimal(valor).setScale(casaDecimais, RoundingMode.DOWN);
        return bd.doubleValue();

    }
}
