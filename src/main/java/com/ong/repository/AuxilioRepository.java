package com.ong.repository;

import com.ong.model.Auxilio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuxilioRepository extends JpaRepository<Auxilio, Long> {

    List<Auxilio> findByPessoaId(Long pessoaId);

}
