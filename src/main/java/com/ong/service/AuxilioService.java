package com.ong.service;

import com.ong.model.Auxilio;
import com.ong.repository.AuxilioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AuxilioService {

    private final AuxilioRepository auxilioRepository;

    public AuxilioService(AuxilioRepository auxilioRepository) {
        this.auxilioRepository = auxilioRepository;
    }

    public List<Auxilio> listarTodos() {
        return auxilioRepository.findAll();
    }

    public List<Auxilio> listarPorPessoa(Long pessoaId) {
        return auxilioRepository.findByPessoaId(pessoaId);
    }

    public Auxilio salvar(Auxilio auxilio) {
        return auxilioRepository.save(auxilio);
    }

    public void deletar(Long id) {
        auxilioRepository.deleteById(id);
    }

    public long contarAuxilios() {
        return auxilioRepository.count();
    }

    public BigDecimal somarValorDistribuido() {
        return auxilioRepository.findAll().stream()
                .map(Auxilio::getValorEstimado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
