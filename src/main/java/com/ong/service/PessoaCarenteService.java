package com.ong.service;

import com.ong.model.PessoaCarente;
import com.ong.repository.PessoaCarenteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PessoaCarenteService {

    private final PessoaCarenteRepository pessoaCarenteRepository;

    public PessoaCarenteService(PessoaCarenteRepository pessoaCarenteRepository) {
        this.pessoaCarenteRepository = pessoaCarenteRepository;
    }

    public List<PessoaCarente> listarTodos() {
        return pessoaCarenteRepository.findAll();
    }

    public PessoaCarente buscarPorId(Long id) {
        return pessoaCarenteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pessoa não encontrada com id: " + id));
    }

    public PessoaCarente salvar(PessoaCarente pessoa) {
        return pessoaCarenteRepository.save(pessoa);
    }

    public void deletar(Long id) {
        pessoaCarenteRepository.deleteById(id);
    }

    public long contarTotal() {
        return pessoaCarenteRepository.count();
    }

    public long contarRecebemAuxilio() {
        return pessoaCarenteRepository.countByRecebeAuxilioTrue();
    }

    public long contarNaoRecebemAuxilio() {
        return pessoaCarenteRepository.countByRecebeAuxilioFalse();
    }

    public BigDecimal calcularMediaRenda() {
        List<PessoaCarente> pessoas = pessoaCarenteRepository.findAll();
        if (pessoas.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal soma = pessoas.stream()
                .map(PessoaCarente::getRendaMensal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return soma.divide(BigDecimal.valueOf(pessoas.size()), 2, RoundingMode.HALF_UP);
    }

}
