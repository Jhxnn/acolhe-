package com.ong.controller;

import com.ong.model.PessoaCarente;
import com.ong.service.PessoaCarenteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pessoas")
public class PessoaCarenteController {

    private final PessoaCarenteService pessoaCarenteService;

    public PessoaCarenteController(PessoaCarenteService pessoaCarenteService) {
        this.pessoaCarenteService = pessoaCarenteService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pessoas", pessoaCarenteService.listarTodos());
        return "listar-pessoas";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("pessoa", new PessoaCarente());
        return "form-pessoa";
    }

    @PostMapping
    public String salvar(@Valid PessoaCarente pessoa, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pessoa", pessoa);
            return "form-pessoa";
        }
        pessoaCarenteService.salvar(pessoa);
        return "redirect:/pessoas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("pessoa", pessoaCarenteService.buscarPorId(id));
        return "form-pessoa";
    }

    @PostMapping("/editar/{id}")
    public String atualizar(@PathVariable Long id, @Valid PessoaCarente pessoa, BindingResult bindingResult, Model model) {
        pessoa.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("pessoa", pessoa);
            return "form-pessoa";
        }
        PessoaCarente existente = pessoaCarenteService.buscarPorId(id);
        pessoa.setDataCadastro(existente.getDataCadastro());
        pessoaCarenteService.salvar(pessoa);
        return "redirect:/pessoas";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        pessoaCarenteService.deletar(id);
        return "redirect:/pessoas";
    }

}
