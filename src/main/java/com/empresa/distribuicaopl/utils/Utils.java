package com.empresa.distribuicaopl.utils;

import org.json.JSONObject;

public class Utils {

    public static String buscaMsgRetorno(String mensagem){
        return new JSONObject().put("mensagem", mensagem).toString();
    }
}
