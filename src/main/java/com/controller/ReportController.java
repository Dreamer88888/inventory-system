package com.controller;

import com.dto.TransactionInReportDto;
import com.dto.TransactionInRequestDto;
import com.dto.TransactionOutReportDto;
import com.entity.StokReport;
import com.service.ExportPdfServiceImpl;
import com.service.StokReportService;
import com.service.TransactionInService;
import com.service.TransactionOutService;
import com.util.GlobalFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ExportPdfServiceImpl exportPdfService;

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

            List<Integer> stoks = stokReport.getStokBarang();

            model.addAttribute("reportDate", dateString);
            model.addAttribute("kodeBarangs", stokReport.getKodeBarang());
            model.addAttribute("namaBarangs", stokReport.getNamaBarang());
            model.addAttribute("namaVendors", stokReport.getNamaVendor());
            model.addAttribute("stokBarangs", stokReport.getStokBarang());
            model.addAttribute("sum", stoks.stream().collect(Collectors.summingInt(Integer::intValue)));

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

    @GetMapping("/stok/delete/{id}")
    public String deleteStokReport(@PathVariable Long id) {
        if (stokReportService.deleteStokReport(id)) {
            return "redirect:/report/stok";
        } else {
            log.info("GAGAL DELETE");
            return "redirect:/report/stok";
        }

    }

    @GetMapping("/stok/download/{id}")
    public void downloadStokReport(@PathVariable Long id, HttpServletResponse response) throws IOException {
        StokReport stokReport = stokReportService.findById(id);
        if (stokReport != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String dateString = sdf.format(stokReport.getDate());
            List<Integer> stoks = stokReport.getStokBarang();

            Map<String, Object> data = new HashMap<>();
            data.put("reportDate", dateString);
            data.put("kodeBarangs", stokReport.getKodeBarang());
            data.put("namaBarangs", stokReport.getNamaBarang());
            data.put("namaVendors", stokReport.getNamaVendor());
            data.put("stokBarangs", stokReport.getStokBarang());
            data.put("sum", stoks.stream().collect(Collectors.summingInt(Integer::intValue)));

            ByteArrayInputStream exportedReport = exportPdfService.exportReportPdf("laporan-stok-pdf", data);

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=stok_report_" + dateString + ".pdf");
            IOUtils.copy(exportedReport, response.getOutputStream());
        }


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

            List<Integer> quantities = transactionInReportDtos.stream().map(transactionInReportDto -> {
                return transactionInReportDto.getQuantity();
            }).collect(Collectors.toList());

            model.addAttribute("sum", quantities.stream().collect(Collectors.summingInt(Integer::intValue)));
            model.addAttribute("reports", transactionInReportDtos);
            model.addAttribute("from", transactionInRequestDto.getFrom());
            model.addAttribute("to", transactionInRequestDto.getTo());
            model.addAttribute("transactionInReqDto", transactionInRequestDto);

            return "laporan-barang-masuk";
        } else {
            model.addAttribute("transactionInRequestDto", transactionInRequestDto);

            return "laporan-barang-masuk";
        }

    }

    @GetMapping("/in/download/{from}/{to}")
    public void downloadTransactionInReport(@PathVariable("from") String from, @PathVariable("to") String to, HttpServletResponse response) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");

        Date dateFrom = sdf.parse(from);
        Date dateTo = sdf.parse(to);

        List<TransactionInReportDto> transactionInReportDtos = transactionInService
                .getTransactionInReport(dateFrom, dateTo);

        List<Integer> quantities = transactionInReportDtos.stream().map(transactionInReportDto -> {
            return transactionInReportDto.getQuantity();
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("sum", quantities.stream().collect(Collectors.summingInt(Integer::intValue)));
        data.put("reports", transactionInReportDtos);
        data.put("from", from);
        data.put("to", to);

        ByteArrayInputStream exportedReport = exportPdfService.exportReportPdf("laporan-barang-masuk-pdf", data);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=transaction_in_report_" + from + "_" + to + ".pdf");
        IOUtils.copy(exportedReport, response.getOutputStream());
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

            List<Integer> quantities = transactionOutReportDtos.stream().map(transactionInReportDto -> {
                return transactionInReportDto.getQuantity();
            }).collect(Collectors.toList());

            model.addAttribute("sum", quantities.stream().collect(Collectors.summingInt(Integer::intValue)));
            model.addAttribute("reports", transactionOutReportDtos);
            model.addAttribute("transactionInReqDto", transactionInRequestDto);
            model.addAttribute("from", transactionInRequestDto.getFrom());
            model.addAttribute("to", transactionInRequestDto.getTo());

            return "laporan-barang-keluar";
        } else {
            model.addAttribute("transactionInRequestDto", transactionInRequestDto);

            return "laporan-barang-keluar";
        }
    }

    @GetMapping("/out/download/{from}/{to}")
    public void downloadTransactionOutReport(@PathVariable("from") String from, @PathVariable("to") String to, HttpServletResponse response) throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");

        Date dateFrom = sdf.parse(from);
        Date dateTo = sdf.parse(to);

        log.info(dateFrom +" " + dateTo);
        List<TransactionOutReportDto> transactionOutReportDtos = transactionOutService
                .getTransactionOutReport(dateFrom, dateTo);
        log.info(transactionOutReportDtos.toString());

        List<Integer> quantities = transactionOutReportDtos.stream().map(transactionInReportDto -> {
            return transactionInReportDto.getQuantity();
        }).collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("sum", quantities.stream().collect(Collectors.summingInt(Integer::intValue)));
        data.put("reports", transactionOutReportDtos);
        data.put("from", from);
        data.put("to", to);

        ByteArrayInputStream exportedReport = exportPdfService.exportReportPdf("laporan-barang-keluar-pdf", data);

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=transaction_in_report_" + from + "_" + to + ".pdf");
        IOUtils.copy(exportedReport, response.getOutputStream());
    }

}
