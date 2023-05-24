package com.controller;

import com.dto.TransactionInReportDto;
import com.dto.TransactionInRequestDto;
import com.dto.TransactionOutReportDto;
import com.entity.StokReport;
import com.service.StokReportService;
import com.service.TransactionInService;
import com.service.TransactionOutService;
import com.util.GlobalFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Controller
@Slf4j
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private StokReportService stokReportService;

    @Autowired
    private TransactionInService transactionInService;

    @Autowired
    private TransactionOutService transactionOutService;

    @GetMapping("/stok")
    public String getViewListStokReport(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        List<StokReport> stokReports = stokReportService.findAll();

        model.addAttribute("stokReports", stokReports);

        return "daftar-laporan-stok";
    }

    @GetMapping("/stok/{id}")
    public String getViewStokReport(@PathVariable Long id, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        StokReport stokReport = stokReportService.findById(id);

        if (stokReport != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(stokReport.getDate());

            model.addAttribute("reportDate", dateString);
            model.addAttribute("kodeBarangs", stokReport.getKodeBarang());
            model.addAttribute("namaBarangs", stokReport.getNamaBarang());
            model.addAttribute("namaVendors", stokReport.getNamaVendor());
            model.addAttribute("stokBarangs", stokReport.getStokBarang());

            return "laporan-stok";
        } else {
            return "redirect:/report/stok";
        }
    }

    @GetMapping("/stok/add")
    public String addStokReport() {
        stokReportService.addStokReport();

        return "redirect:/report/stok";
    }

    @GetMapping("/in")
    public String getViewTransactionInReport(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        model.addAttribute("transactionInRequestDto", new TransactionInRequestDto());

        return "laporan-barang-masuk";
    }

    @PostMapping("/in")
    public String getViewTransactionInReportDetail(@Valid @ModelAttribute("transactionInRequestDto") TransactionInRequestDto transactionInRequestDto,
                                                   BindingResult bindingResult, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        if (!bindingResult.hasErrors()) {
            Calendar cal = Calendar.getInstance();

            cal.setTime(transactionInRequestDto.getTo());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);

            transactionInRequestDto.getTo().setTime(cal.getTimeInMillis());
            List<TransactionInReportDto> transactionInReportDtos = transactionInService
                    .getTransactionInReport(transactionInRequestDto.getFrom(), transactionInRequestDto.getTo());

            model.addAttribute("reports", transactionInReportDtos);
            model.addAttribute("transactionInReqDto", transactionInRequestDto);

            return "laporan-barang-masuk";
        } else {
            model.addAttribute("transactionInRequestDto", transactionInRequestDto);

            return "laporan-barang-masuk";
        }


    }

    @GetMapping("/out")
    public String getViewTransactionOutReport(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        model.addAttribute("transactionInRequestDto", new TransactionInRequestDto());

        return "laporan-barang-keluar";
    }

    @PostMapping("/out")
    public String getViewTransactionOutReportDetail(@Valid @ModelAttribute("transactionInRequestDto") TransactionInRequestDto transactionInRequestDto,
                                                    BindingResult bindingResult, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        if (!bindingResult.hasErrors()) {
            Calendar cal = Calendar.getInstance();

            cal.setTime(transactionInRequestDto.getTo());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);

            transactionInRequestDto.getTo().setTime(cal.getTimeInMillis());
            List<TransactionOutReportDto> transactionOutReportDtos = transactionOutService
                    .getTransactionOutReport(transactionInRequestDto.getFrom(), transactionInRequestDto.getTo());

            model.addAttribute("reports", transactionOutReportDtos);
            model.addAttribute("transactionInReqDto", transactionInRequestDto);

            return "laporan-barang-keluar";
        } else {
            model.addAttribute("transactionInRequestDto", transactionInRequestDto);

            return "laporan-barang-keluar";
        }

    }

}
