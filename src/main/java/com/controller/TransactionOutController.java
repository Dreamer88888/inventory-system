package com.controller;

import com.dto.TransactionOutDto;
import com.entity.Barang;
import com.entity.TransactionOut;
import com.service.BarangService;
import com.service.TransactionOutService;
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
import java.util.Optional;

@Controller
@RequestMapping("/out")
public class TransactionOutController {

    @Autowired
    private TransactionOutService transactionOutService;

    @Autowired
    private BarangService barangService;

    @GetMapping("/add")
    public String getViewTransactionOut(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        model.addAttribute("transactionOutDto", new TransactionOutDto());
        model.addAttribute("barangs", barangService.findAll());

        return "transaction-out";
    }

    @PostMapping("/add")
    public String addTransactionIn(@Valid @ModelAttribute("transactionOutDto") TransactionOutDto transactionOutDto, BindingResult bindingResult, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        if (!bindingResult.hasErrors()) {
            TransactionOut transactionOut = new TransactionOut();

            transactionOut.setKodeBarang(transactionOutDto.getKodeBarang());
            transactionOut.setNomorSuratJalan(transactionOutDto.getNomorSuratJalan());
            transactionOut.setNamaCustomer(transactionOutDto.getNamaCustomer());
            transactionOut.setStok(transactionOutDto.getStok());
            transactionOut.setDate(new Date());

            Optional<Barang> barang = barangService.findByKode(transactionOutDto.getKodeBarang());

            if (barang.get().getStok() < transactionOutDto.getStok()) {
                model.addAttribute("errorMessage", "Stok barang tidak mencukupi");
                model.addAttribute("transactionOutDto", transactionOutDto);
                model.addAttribute("barangs", barangService.findAll());

                return "transaction-out";
            } else {
                if (transactionOutService.addTransactionOut(transactionOut) != null) {
                    return "redirect:/data-master";
                } else {
                    return "redirect:/out/add";
                }
            }
        } else {
            model.addAttribute("transactionOutDto", transactionOutDto);
            model.addAttribute("barangs", barangService.findAll());

            return "transaction-out";
        }
    }

}
