package com.empresa.distribuicaopl.controllers;

import com.empresa.distribuicaopl.services.FuncionarioService;
import org.everit.json.schema.ValidationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import sun.misc.Cache;

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
            //TODO jsonschema
            JSONObject obj = new JSONObject();
            obj.put("dados", new JSONArray(body));

            if(funcionarioService.validaSchemaCriacaoFuncionario(obj)){

                funcionarioService.process(body);
                System.out.println("Processando com sucesso.");
                return new ResponseEntity<String>("", HttpStatus.ACCEPTED);
            }
            else{
                return new ResponseEntity<String>("", HttpStatus.BAD_REQUEST);
            }

        }
        catch (ValidationException e){
            System.out.println(e.getCausingExceptions()+" - "+e.getViolatedSchema());
            return new ResponseEntity<String>("", HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(method = RequestMethod.PUT, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> atualiza(@PathVariable String matricula){
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> busca(@PathVariable String matricula){
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

    @CacheEvict(value = "myCache", allEntries = true)
    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> apaga(){

        try{
            System.out.println("Processando request para apagar todos os funcionarios...");
            funcionarioService.processDelete();
            System.out.println("Processo finalizado.");
            return new ResponseEntity<String>("", HttpStatus.OK);
        }
        catch (Exception e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(path = "/participacao", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> calParticipacao(@RequestBody String body){

        try{

            System.out.println("Processando requisicao para calcular a participacao dos funcionarios...");
            String resposta = funcionarioService.CalcParticipacaoFuncionarios(body);
            System.out.println("Processado");
            return new ResponseEntity<String>(resposta, HttpStatus.ACCEPTED);
        }
        catch (HttpClientErrorException e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>(e.getMessage(), e.getStatusCode());
        }
        catch (Exception e){
            System.out.println("Problema ao processar a requisicao. Motivo: "+e.getMessage());
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
