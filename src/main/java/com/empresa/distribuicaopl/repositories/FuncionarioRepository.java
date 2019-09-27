package com.empresa.distribuicaopl.repositories;

import com.empresa.distribuicaopl.models.Funcionario;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FuncionarioRepository extends CrudRepository<Funcionario, String> {}
