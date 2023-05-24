package com.controller;

import com.dto.TransactionInDto;
import com.entity.TransactionIn;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.service.BarangService;
import com.service.TransactionInService;
import com.util.GlobalFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/in")
public class TransactionInController {

    @Autowired
    private TransactionInService transactionInService;

    @Autowired
    private BarangService barangService;

    @GetMapping("/add")
    public String getViewTransactionIn(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        model.addAttribute("transactionInDto", new TransactionInDto());
        model.addAttribute("barangs", barangService.findAll());

        return "transaction-in";
    }

    @PostMapping("/add")
    public String addTransactionIn(@Valid @ModelAttribute("transactionInDto") TransactionInDto transactionInDto, BindingResult bindingResult, HttpServletRequest request, Model model) {
        if (!bindingResult.hasErrors()) {
            TransactionIn transactionIn = new TransactionIn();

            transactionIn.setKodeBarang(transactionInDto.getKodeBarang());
            transactionIn.setNomorSuratJalan(transactionInDto.getNomorSuratJalan());
            transactionIn.setStok(transactionInDto.getStok());
            transactionIn.setDate(new Date());

            if (transactionInService.addTransactionIn(transactionIn) != null) {
                return "redirect:/data-master";
            } else {
                return "redirect:/in/add";
            }
        } else {
            String role = GlobalFunction.getUserRole(request);

            model.addAttribute("role", role);

            model.addAttribute("transactionInDto", transactionInDto);
            model.addAttribute("barangs", barangService.findAll());

            return "transaction-in";
        }

    }

}
