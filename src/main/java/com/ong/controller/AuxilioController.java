package com.ong.controller;

import com.ong.model.Auxilio;
import com.ong.model.TipoAuxilio;
import com.ong.service.AuxilioService;
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
@RequestMapping("/pessoas/{id}/auxilios")
public class AuxilioController {

    private final AuxilioService auxilioService;
    private final PessoaCarenteService pessoaCarenteService;

    public AuxilioController(AuxilioService auxilioService, PessoaCarenteService pessoaCarenteService) {
        this.auxilioService = auxilioService;
        this.pessoaCarenteService = pessoaCarenteService;
    }

    @GetMapping
    public String listar(@PathVariable Long id, Model model) {
        model.addAttribute("pessoa", pessoaCarenteService.buscarPorId(id));
        model.addAttribute("auxilios", auxilioService.listarPorPessoa(id));
        return "listar-auxilios";
    }

    @GetMapping("/novo")
    public String novo(@PathVariable Long id, Model model) {
        model.addAttribute("pessoa", pessoaCarenteService.buscarPorId(id));
        model.addAttribute("auxilio", new Auxilio());
        model.addAttribute("tiposAuxilio", TipoAuxilio.values());
        return "form-auxilio";
    }

    @PostMapping
    public String salvar(@PathVariable Long id, @Valid Auxilio auxilio, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pessoa", pessoaCarenteService.buscarPorId(id));
            model.addAttribute("tiposAuxilio", TipoAuxilio.values());
            return "form-auxilio";
        }
        auxilio.setPessoa(pessoaCarenteService.buscarPorId(id));
        auxilioService.salvar(auxilio);
        return "redirect:/pessoas/" + id + "/auxilios";
    }

    @GetMapping("/excluir/{auxilioId}")
    public String excluir(@PathVariable Long id, @PathVariable Long auxilioId) {
        auxilioService.deletar(auxilioId);
        return "redirect:/pessoas/" + id + "/auxilios";
    }

}
