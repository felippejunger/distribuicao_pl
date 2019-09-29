package com.empresa.distribuicaopl.models;

public enum Peso{

    PESO_1(1), PESO_2(2), PESO_3(3), PESO_4(4),PESO_5(5);

    private int valor;

    Peso(int valor){
        this.valor = valor;
    }

    int getValor(){
        return this.valor;
    }
}
