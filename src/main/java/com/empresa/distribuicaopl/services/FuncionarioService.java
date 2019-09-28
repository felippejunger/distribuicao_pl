package com.empresa.distribuicaopl.services;


import com.empresa.distribuicaopl.models.Cargos.Analista;
import com.empresa.distribuicaopl.models.Cargos.Auxiliar;
import com.empresa.distribuicaopl.models.Cargos.Diretor;
import com.empresa.distribuicaopl.models.Departamentos.*;
import com.empresa.distribuicaopl.models.Funcionario;
import com.empresa.distribuicaopl.repositories.FuncionarioRepository;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FuncionarioService {

    private ExecutorService executorService;

    @PostConstruct
    private void create() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void process(String body) {
        // no result operation
        executorService.submit(() -> processaCriacao(body));
    }


    @PreDestroy
    private void destroy() {
        executorService.shutdown();
    }

    public static Map<String, String> mapFuncionarios;
    static {
        mapFuncionarios = new HashMap<>();
        mapFuncionarios.put("diretoria", Diretoria.class.getName());
        mapFuncionarios.put("contabilidade", Contabilidade.class.getName());
        mapFuncionarios.put("financeiro", Financeiro.class.getName());
        mapFuncionarios.put("relacionamento com o cliente", RelacionamentoCliente.class.getName());
        mapFuncionarios.put("serviços gerais", ServicosGerais.class.getName());
        mapFuncionarios.put("tecnologia", Tecnologia.class.getName());

    }

    @Autowired
    FuncionarioRepository funcionarioRepository;

    public void processaCriacao(String body){

//        if(this.validaSchemaCriacaoFuncionario(body)){
//
//        }

        List<Funcionario> funcionarios = this.PopulaListaFuncionarios(body);

        System.out.println(funcionarios);

        funcionarioRepository.saveAll(funcionarios);

        System.out.println("Salvo!");
//            funcionarioRepository.findById()
        JSONObject json = new JSONObject().put("records", new JSONArray());


    }

    private List<Funcionario> PopulaListaFuncionarios(String body){
        List<Funcionario> funcionarios = new ArrayList<>();
        JSONArray arrayFuncionarios = new JSONArray(body);
        arrayFuncionarios.forEach( (item) ->{

            System.out.println(item);
            JSONObject j = new JSONObject(item.toString());

            System.out.println(j);

            Funcionario funcionario = new Funcionario();
            funcionario.setCargo(j.getString("cargo"));
            funcionario.setMatricula(j.getString("matricula"));
            funcionario.setSalario_bruto(j.getDouble("salario_bruto"));
            funcionario.setNome(j.getString("nome"));
            funcionario.setDataAdmissao(LocalDate.parse(j.getString("data_de_admissao")));
            funcionario.setValorParticipacao(0.0);

            funcionario.setDepartamento( new RelacionamentoCliente() );

            funcionarios.add(funcionario);
        });

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
