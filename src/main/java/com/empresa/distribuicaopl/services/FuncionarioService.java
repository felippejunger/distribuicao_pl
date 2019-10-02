package com.empresa.distribuicaopl.services;


import com.empresa.distribuicaopl.models.Departamentos.*;
import com.empresa.distribuicaopl.models.Funcionario;
import com.empresa.distribuicaopl.models.Participacao;
import com.empresa.distribuicaopl.models.Peso;
import com.empresa.distribuicaopl.models.Salario;
import com.empresa.distribuicaopl.repositories.FuncionarioRepository;
import com.empresa.distribuicaopl.utils.DoubleUtils;
import com.empresa.distribuicaopl.utils.StringUtils;
import com.empresa.distribuicaopl.utils.Utils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class FuncionarioService {

    private ExecutorService executorService;

    private final String FLAG_PROCESSANDO = "processando";

    @PostConstruct
    private void create() {
        executorService = Executors.newSingleThreadExecutor();
    }

    public void processaCriacao(String body) {
        // no result operation
        executorService.submit(() -> criaFuncionarios(body));
    }

    public void processaDelete(){
        executorService.submit(() -> apagaFuncionarios());
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PreDestroy
    private void destroy() {
        executorService.shutdown();
    }

    @Autowired
    FuncionarioRepository funcionarioRepository;

    public void criaFuncionarios(String body){

        List<Funcionario> funcionarios = this.PopulaListaFuncionariosFromJSON(body);

        redisTemplate.opsForValue().set(this.FLAG_PROCESSANDO,"true");

        funcionarioRepository.saveAll(funcionarios);

        redisTemplate.opsForValue().getOperations().delete(this.FLAG_PROCESSANDO);

        System.out.println("Registros salvos com sucesso!");
    }

    private List<Funcionario> PopulaListaFuncionariosFromJSON(String body){
        List<Funcionario> funcionarios = new ArrayList<>();
        JSONArray arrayFuncionarios = new JSONArray(body);

        arrayFuncionarios.forEach( (item) ->{

            JSONObject j = new JSONObject(item.toString());
            Funcionario funcionario = new Funcionario();
            funcionario.setCargo(j.getString("cargo"));
            funcionario.setMatricula(j.getString("matricula"));
            funcionario.setSalario_bruto(j.getDouble("salario_bruto"));
            funcionario.setNome(j.getString("nome"));
            funcionario.setDataAdmissao(LocalDate.parse(j.getString("data_de_admissao")));
            String departamento = StringUtils.removerAcentos(j.getString("area").toLowerCase());
            funcionario.setDepartamento(Utils.buscaDepartamentoInstance(departamento));


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

//    public String calcParticipacaoFuncionarios(double valorADistribuir, List<Funcionario> funcionarios){
//
//        return this.processaCalculoParticipacao(valorADistribuir, funcionarios);
//
//    }

    public boolean validaValorADistribuir(JSONObject body){
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

    public String processaCalculoParticipacao(double valorMaxADistribuir, List<Funcionario> funcionarios){

        double accValorADistribuir = 0.0;

        if(temProcessamentoPendente() ){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Funcionarios ainda nao cadastrados. Tente novamente em instantes...");
        }

        if(funcionarios.size() == 0){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND,"Funcionarios nao cadastrados!");
        }

        for (Funcionario funcionario : funcionarios) {

            Participacao participacao = new Participacao();
            /**CALCULA PESO - TEMPO ADMISSAO**/
            Peso pesoTempoAdmissao = participacao.calculaPesoTempoAdmissao(funcionario.getDataAdmissao(),LocalDate.now());

            /**CALCULA PESO - AREA ATUACAO**/
            Peso pesoAreaAtuacao = participacao.calculaPesoPorArea(funcionario.getDepartamento());

            /**CALCULA PESO - FAIXA SALARIAL**/
            Peso pesoFaixaSalarial = funcionario.eEstagiario() ? participacao.buscaPesoEstagiario() :
                    participacao.calculaPesoFaixaSalarial(funcionario.getSalario_bruto(), Salario.SALARIO_MINIMO);


            double participacaoFuncionario = participacao.calculaParticipacao(funcionario.getSalario_bruto(),
                                                                pesoTempoAdmissao,pesoAreaAtuacao,pesoFaixaSalarial);

            participacao.setValorParticipacao(participacaoFuncionario);
            funcionario.setParticipacao(participacao);

            accValorADistribuir += participacaoFuncionario;

        }

        if(accValorADistribuir <= valorMaxADistribuir){
            return this.buscaRetornoParticipacao(funcionarios, accValorADistribuir, valorMaxADistribuir).toString();
        }
        else {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"Valor maximo a distribuir inferior ao necessario!");
        }
    }


    @Cacheable(value = "myCache", condition = "!#temPendente", unless="#result.size() == 0")
    public List<Funcionario> buscaTodosOsFuncionarios(boolean temPendente){

        return (List<Funcionario>) funcionarioRepository.findAll();
    }

    public boolean temProcessamentoPendente(){
        return redisTemplate.hasKey(this.FLAG_PROCESSANDO);
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
