package com.ong.controller;

import com.ong.service.AuxilioService;
import com.ong.service.PessoaCarenteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final PessoaCarenteService pessoaCarenteService;
    private final AuxilioService auxilioService;

    public HomeController(PessoaCarenteService pessoaCarenteService, AuxilioService auxilioService) {
        this.pessoaCarenteService = pessoaCarenteService;
        this.auxilioService = auxilioService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("totalPessoas", pessoaCarenteService.contarTotal());
        model.addAttribute("totalRecebemAuxilio", pessoaCarenteService.contarRecebemAuxilio());
        model.addAttribute("totalNaoRecebemAuxilio", pessoaCarenteService.contarNaoRecebemAuxilio());
        model.addAttribute("mediaRenda", pessoaCarenteService.calcularMediaRenda());
        model.addAttribute("totalAuxilios", auxilioService.contarAuxilios());
        model.addAttribute("valorTotalDistribuido", auxilioService.somarValorDistribuido());
        return "index";
    }

}
