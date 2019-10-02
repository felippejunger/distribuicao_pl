package com.empresa.distribuicaopl.controllers;

import com.empresa.distribuicaopl.models.Funcionario;
import com.empresa.distribuicaopl.services.FuncionarioService;
import com.empresa.distribuicaopl.utils.Utils;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;


import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/rest/v1/funcionario")
public class FuncionarioController {

    @Autowired
    FuncionarioService funcionarioService;

    @CacheEvict(value = "myCache", allEntries = true)
    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> cria(@RequestBody String body) {
        try{
            System.out.println("Processando request para criacao de usuario...");

            JSONObject obj = new JSONObject();
            obj.put("dados", new JSONArray(body));

            //Validacao basica de schema do json recebido
            if(funcionarioService.validaSchemaCriacaoFuncionario(obj)){

                funcionarioService.processaCriacao(body);
                System.out.println("Processando com sucesso.");
                return new ResponseEntity<String>(Utils.buscaRetornoComHATEOAS("Request aceito! Os funcionarios estarao disponiveis em instantes."), HttpStatus.ACCEPTED);
            }
            else{
                return new ResponseEntity<String>("Problema com o request passado!", HttpStatus.BAD_REQUEST);
            }
        }
        catch (ValidationException e){
            System.out.println("Problema ao validar o json de criacao de funcionarios: "+e.getMessage());
            return new ResponseEntity<String>(Utils.buscaMsgRetorno(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>(Utils.buscaMsgRetorno("Problema ao processar a requisicao!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CacheEvict(value = "myCache", allEntries = true)
    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json")
    public @ResponseBody
    ResponseEntity<String> apaga(){

        try{
            System.out.println("Processando request para apagar todos os funcionarios...");
            funcionarioService.processaDelete();
            System.out.println("Processo finalizado.");
            return new ResponseEntity<String>(Utils.buscaMsgRetorno("Processado com sucesso!"), HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>(Utils.buscaMsgRetorno("Problema ao processar a requisicao!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/participacao", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> calcParticipacao(@RequestBody String body){

        try{

            System.out.println("Processando requisicao para calcular a participacao dos funcionarios...");

            JSONObject bodyJson = new JSONObject(body);

            if(funcionarioService.validaValorADistribuir(bodyJson)){

                List<Funcionario> funcionarios = funcionarioService.buscaTodosOsFuncionarios(funcionarioService.temProcessamentoPendente());
                String resposta = funcionarioService.processaCalculoParticipacao(bodyJson.getDouble("valor_distribuicao"), funcionarios);

                System.out.println("Processado com sucesso!");

                return new ResponseEntity<String>(resposta, HttpStatus.OK);
            }
            return new ResponseEntity<String>("Problema com request passado!", HttpStatus.BAD_REQUEST);
        }
        catch (HttpClientErrorException e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>(Utils.buscaMsgRetorno(e.getMessage()), e.getStatusCode());
        }
        catch (Exception e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>(Utils.buscaMsgRetorno("Problema ao processar a requisicao!"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
