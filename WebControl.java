package com.escola.eventos.controller;

import com.escola.eventos.model.Evento;
import com.escola.eventos.repository.EventoDAO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/eventos")
public class WebControl {

    @Autowired
    private EventoDAO eventoDAO;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(value = "sucesso", required = false) String sucesso,
                         @RequestParam(value = "excluido", required = false) String excluido,
                         @RequestParam(value = "erro", required = false) String erro) {
        List<Evento> eventos = eventoDAO.findAll();
        model.addAttribute("eventos", eventos);
        if (sucesso != null) {
            model.addAttribute("mensagem", "Evento salvo com sucesso!");
            model.addAttribute("tipoMensagem", "success");
        }
        if (excluido != null) {
            model.addAttribute("mensagem", "Evento excluído com sucesso!");
            model.addAttribute("tipoMensagem", "success");
        }
        if (erro != null) {
            model.addAttribute("mensagem", "Não foi possível concluir a operação. Tente novamente.");
            model.addAttribute("tipoMensagem", "danger");
        }
        return "eventos";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("evento", new Evento());
        model.addAttribute("titulo", "Novo Evento");
        model.addAttribute("acao", "/eventos/salvar");
        return "form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("evento") Evento evento,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", "Novo Evento");
            model.addAttribute("acao", "/eventos/salvar");
            model.addAttribute("mensagem", "Corrija os campos destacados antes de salvar.");
            model.addAttribute("tipoMensagem", "danger");
            return "form";
        }
        eventoDAO.save(evento);
        redirectAttributes.addAttribute("sucesso", true);
        return "redirect:/eventos";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return eventoDAO.findById(id)
                .map(evento -> {
                    model.addAttribute("evento", evento);
                    model.addAttribute("titulo", "Editar Evento");
                    model.addAttribute("acao", "/eventos/atualizar");
                    return "form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addAttribute("erro", true);
                    return "redirect:/eventos";
                });
    }

    @PostMapping("/atualizar")
    public String atualizar(@Valid @ModelAttribute("evento") Evento evento,
                            BindingResult bindingResult,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("titulo", "Editar Evento");
            model.addAttribute("acao", "/eventos/atualizar");
            model.addAttribute("mensagem", "Corrija os campos destacados antes de salvar.");
            model.addAttribute("tipoMensagem", "danger");
            return "form";
        }
        eventoDAO.save(evento);
        redirectAttributes.addAttribute("sucesso", true);
        return "redirect:/eventos";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (!eventoDAO.existsById(id)) {
            redirectAttributes.addAttribute("erro", true);
            return "redirect:/eventos";
        }
        eventoDAO.deleteById(id);
        redirectAttributes.addAttribute("excluido", true);
        return "redirect:/eventos";
    }
}
