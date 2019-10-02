package com.empresa.distribuicaopl.utils;

import com.empresa.distribuicaopl.models.Departamentos.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {

    public static String buscaMsgRetorno(String mensagem){
        return new JSONObject().put("mensagem", mensagem).toString();
    }

    public static String buscaRetornoComHATEOAS(String mensagem){
        JSONObject obj = new JSONObject();

        JSONArray array = new JSONArray();

        JSONObject hateoas = new JSONObject();
        hateoas.put("rel","Funcionarios");
        hateoas.put("href","/rest/v1/funcionario");
        hateoas.put("type","DELETE");
        hateoas.put("desc","Usado para apagar todos os Funcionarios");

        array.put(hateoas);

        JSONObject hateoas2 = new JSONObject();
        hateoas2.put("rel","Participacao");
        hateoas2.put("href","/rest/v1/funcionario/participacao");
        hateoas2.put("type","POST");
        hateoas2.put("desc","Usado para calcular a participacao de todos os funcionarios");

        array.put(hateoas2);
        obj.put("mensagem", mensagem);
        obj.put("_links",array);

        return obj.toString();

    }

    public static Departamento buscaDepartamentoInstance(String areaAtuacao){
        switch (areaAtuacao){
            case "diretoria" : return new Diretoria();
            case "contabilidade" : return new Contabilidade();
            case "financeiro" : return new Financeiro();
            case "Tecnologia" : return new Tecnologia();
            case "servicos gerais" : return new ServicosGerais();
            case "relacionamento com o cliente" : return new RelacionamentoCliente();
            default: return new Outros();

        }
    }
}
