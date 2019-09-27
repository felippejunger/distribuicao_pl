package com.empresa.distribuicaopl.services;


import com.empresa.distribuicaopl.models.Cargos.Analista;
import com.empresa.distribuicaopl.models.Funcionario;
import com.empresa.distribuicaopl.repositories.FuncionarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class FuncionarioService {

    @Autowired
    FuncionarioRepository funcionarioRepository;

    public String processaCriacao(String body) throws IOException {

//        if(this.validaSchemaCriacaoFuncionario(body)){
//
//        }

        List<Funcionario> funcionarios = this.PopulaListaFuncionarios(body);

        System.out.println(funcionarios);


//        Funcionario funcionario = new Analista();
//        funcionarioRepository.save(funcionario);

//            funcionarioRepository.findById()
        JSONObject json = new JSONObject().put("records", new JSONArray());

        return "";
    }

    private List<Funcionario> PopulaListaFuncionarios(String body) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<Funcionario> funcionarios = Arrays.asList(mapper.readValue(body, Funcionario[].class));

        return funcionarios;
    }

    private boolean validaSchemaCriacaoFuncionario(JSONObject body){

        InputStream inputStream = getClass().getResourceAsStream("/schema.json");
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(body);

        return true;
    }

}
