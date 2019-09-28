package com.empresa.distribuicaopl.controllers;

import com.empresa.distribuicaopl.services.FuncionarioService;
import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/rest/v1/funcionario")
public class FuncionarioController {

    @Autowired
    FuncionarioService funcionarioService;

    @RequestMapping(method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> cria(@RequestBody String body) {
        try{
            System.out.println("Processando request para criacao de usuario...");

            funcionarioService.process(body);

            System.out.println("Processando com sucesso.");

            return new ResponseEntity<String>("", HttpStatus.ACCEPTED);
        }
        catch (ValidationException e){
            System.out.println(e.getCausingExceptions()+" - "+e.getViolatedSchema());
            return new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
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


    @RequestMapping(method = RequestMethod.DELETE, produces = "application/json", consumes = "application/json")
    public @ResponseBody
    ResponseEntity<String> apaga(@PathVariable String matricula){
        return new ResponseEntity<String>("", HttpStatus.OK);
    }

}
