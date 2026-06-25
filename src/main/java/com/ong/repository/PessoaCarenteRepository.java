package com.ong.repository;

import com.ong.model.PessoaCarente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaCarenteRepository extends JpaRepository<PessoaCarente, Long> {

    long countByRecebeAuxilioTrue();

    long countByRecebeAuxilioFalse();

}
