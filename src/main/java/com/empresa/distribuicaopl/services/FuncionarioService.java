package com.empresa.distribuicaopl.services;


import com.empresa.distribuicaopl.models.Departamentos.*;
import com.empresa.distribuicaopl.models.Funcionario;
import com.empresa.distribuicaopl.models.Participacao;
import com.empresa.distribuicaopl.models.Peso;
import com.empresa.distribuicaopl.models.Salario;
import com.empresa.distribuicaopl.repositories.FuncionarioRepository;
import com.empresa.distribuicaopl.utils.DoubleUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
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

    public void processDelete(){
        executorService.submit(() -> apagaFuncionarios());
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

        List<Funcionario> funcionarios = this.PopulaListaFuncionariosFromJSON(body);

        funcionarioRepository.saveAll(funcionarios);

        System.out.println("Registros salvos com sucesso!");
    }

    private List<Funcionario> PopulaListaFuncionariosFromJSON(String body){
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

            funcionario.setDepartamento( new RelacionamentoCliente() );

            funcionarios.add(funcionario);
        });

        return funcionarios;
    }

    public boolean validaSchemaCriacaoFuncionario(JSONObject body){

        InputStream inputStream = getClass().getResourceAsStream("/schema.json");
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        Schema schema = SchemaLoader.load(rawSchema);
        schema.validate(body);

        return true;
    }


    public void apagaFuncionarios(){

        System.out.println("Apagando funcionarios....");
        funcionarioRepository.deleteAll();
        System.out.println("Funcionarios apagados.");
    }

    public String CalcParticipacaoFuncionarios(String body){
        JSONObject bodyJson = new JSONObject(body);
        String resposta = "";
        if(this.validaValorADistribuir(bodyJson)){
            double valorADistribuir = bodyJson.getDouble("valor_distribuicao");
            resposta = this.processaCalculoParticipacao(valorADistribuir);
        }

        return resposta;
    }

    private boolean validaValorADistribuir(JSONObject body){
        if(body.has("valor_distribuicao")){

            double valorADistribuir = body.getDouble("valor_distribuicao");
            if(valorADistribuir > 0.0){
                return true;
            }
            else{
                throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Campo precisa ser maior que zero!");
            }
        }
        else{
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Campo valor_distribuicao nao encontrado!");
        }
    }

    private String processaCalculoParticipacao(double valorMaxADistribuir){

        double accValorADistribuir = 0.0;
        System.out.println("antes");
        List<Funcionario> funcionarios = this.buscaTodosOsFuncionarios();
        System.out.println("depois");

        if(funcionarios.size() == 0){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Nenhum funcionario cadastrado!");
        }

        for (Funcionario funcionario : funcionarios) {
            Participacao participacao = new Participacao();
            Peso pesoTempoAdmissao = participacao.calculaPesoTempoAdmissao(funcionario.getDataAdmissao(),LocalDate.now());
            Peso pesoAreaAtuacao = participacao.calculaPesoPorArea(funcionario.getDepartamento());
            Peso pesoFaixaSalarial = funcionario.eEstagiario() ? participacao.buscaPesoEstagiario() :
                    participacao.calculaPesoFaixaSalarial(funcionario.getSalario_bruto(), Salario.SALARIO_MINIMO);

            double participacaoFuncionario = participacao.calculaParticipacao(funcionario.getSalario_bruto(),
                                                                pesoTempoAdmissao,pesoAreaAtuacao,pesoFaixaSalarial);

            participacao.setValorParticipacao(participacaoFuncionario);
            funcionario.setParticipacao(participacao);

            accValorADistribuir += participacaoFuncionario;

        }

        if(accValorADistribuir <= valorMaxADistribuir){
            return this.buscaRetornoParticipacao(funcionarios,accValorADistribuir,valorMaxADistribuir).toString();
        }
        else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Valor maximo a distribuir inferior ao necessario!");
        }
    }


    @Cacheable(value = "myCache")
    public List<Funcionario> buscaTodosOsFuncionarios(){

        return (List<Funcionario>) funcionarioRepository.findAll();
    }

    private JSONObject buscaRetornoParticipacao(List<Funcionario> funcionarios, double accValorADistribuir, double valorMaxADistribuir){
        JSONObject retorno = new JSONObject();

        JSONArray arrayParticipacoes = new JSONArray();

        for (Funcionario funcionario : funcionarios) {
            JSONObject participacao = new JSONObject();
            participacao.put("matricula",funcionario.getMatricula());
            participacao.put("nome",funcionario.getNome());
            participacao.put("valor_da_participacao",funcionario.getParticipacao().getValorParticipacao());

            arrayParticipacoes.put(participacao);
        }

        retorno.put("participacoes", arrayParticipacoes);
        retorno.put("total_de_funcionarios",funcionarios.size());
        retorno.put("total_distribuido", DoubleUtils.buscaValorFomatado(accValorADistribuir,2));
        retorno.put("total_disponibilizado", DoubleUtils.buscaValorFomatado(valorMaxADistribuir,2));
        retorno.put("saldo_total_disponibilizado", DoubleUtils.buscaValorFomatado(valorMaxADistribuir - accValorADistribuir,2));

        return retorno;
    }

}
