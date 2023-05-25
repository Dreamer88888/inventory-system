package com.controller;

import com.dto.HistoryBarangDto;
import com.dto.SearchDto;
import com.entity.Barang;
import com.service.BarangService;
import com.service.HistoryService;
import com.service.TransactionInService;
import com.service.TransactionOutService;
import com.util.GlobalFunction;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.raw.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/data-master")
public class BarangController {

    @Autowired
    private BarangService barangService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TransactionInService transactionInService;

    @Autowired
    private TransactionOutService transactionOutService;

    @GetMapping
    public String getViewDataMaster(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        model.addAttribute("searchDto", new SearchDto());
        model.addAttribute("barangs", barangService.findAll());

        return "data-master";
    }

    @GetMapping("/add")
    public String getViewAddDataMaster(HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        model.addAttribute("barang", new Barang());

        return "tambah-barang";
    }

    @PostMapping("/add")
    public String addBarang(@Valid @ModelAttribute("barang") Barang barang, BindingResult bindingResult,
            HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);
        if (!bindingResult.hasErrors() && barangService.findByKode(barang.getKode()).isEmpty() && barangService.findByNamaAndVendor(barang.getNama(), barang.getNamaVendor()).isEmpty()) {
            Barang createdBarang = barangService.addBarang(barang);
            if (createdBarang != null) {
                return "redirect:/data-master";
            } else {
                return "redirect:/data-master/add";
            }
        } else {
            if (barangService.findByKode(barang.getKode()).isPresent()) {
                model.addAttribute("errorMessage", "Kode barang sudah terpakai, gunakan kode lain");
            }
            if (barangService.findByNamaAndVendor(barang.getNama(), barang.getNamaVendor()).isPresent()) {
                model.addAttribute("errorComposite", "Pasangan nama barang dan vendor yang sama sudah ada");
            }
            model.addAttribute("barang", barang);

            return "tambah-barang";
        }

    }

    @GetMapping("/update/{kode}")
    public String getViewUpdateDataMaster(@PathVariable String kode, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        Barang barang = barangService.findByKode(kode).get();
        if (barang != null) {
            model.addAttribute("barang", barang);

            return "edit-barang";
        } else {
            return "redirect:/data-master";
        }
    }

    @PostMapping("/update")
    public String updateBarang(@Valid @ModelAttribute("barang") Barang barang, BindingResult bindingResult,
            HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        if (!bindingResult.hasErrors()) {
            Barang barangFromDB = barangService.findByKode(barang.getKode()).get();

            if (barangFromDB != null) {
                Barang updatedBarang = barangService.updateBarang(barang);

                if (updatedBarang == null) {
                    log.error("Barang dengan Kode " + barang.getKode() + " gagal diperbarui.");
                }
                return "redirect:/data-master";
            } else {
                return "redirect:/data-master/update/" + barang.getKode();
            }
        } else {
            model.addAttribute("barang", barang);

            return "edit-barang";
        }

    }

    @GetMapping("/delete/{kode}")
    @Transactional
    public String deleteBarang(@PathVariable String kode, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        transactionInService.deleteByKodeBarang(kode);
        transactionOutService.deleteByKodeBarang(kode);

        if (barangService.deleteBarang(kode)) {
            return "redirect:/data-master";
        } else {
            log.error("Barang dengan Kode " + kode + " gagal dihapus.");

            return "redirect:/data-master";
        }
    }

    @GetMapping("/history/{kode}")
    public String historyBarang(@PathVariable String kode, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        Barang barang = barangService.findByKode(kode).get();
        List<HistoryBarangDto> historyBarangDtos = historyService.getHistoryBarang(kode);

        model.addAttribute("kodeBarang", kode);
        model.addAttribute("namaBarang", barang.getNama());
        model.addAttribute("histories", historyBarangDtos);

        return "history-barang";
    }

    @GetMapping("/history/delete/in/{id}/{kode}")
    public String deleteHistoryIn(@PathVariable("id") Long id, @PathVariable("kode") String kode, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        if (!historyService.deleteHistoryInById(id)) {
            log.error("Transaksi keluar dengan id " + id + " tidak ditemukan");
        }

        Barang barang = barangService.findByKode(kode).get();
        List<HistoryBarangDto> historyBarangDtos = historyService.getHistoryBarang(kode);

        model.addAttribute("kodeBarang", kode);
        model.addAttribute("namaBarang", barang.getNama());
        model.addAttribute("histories", historyBarangDtos);

        return "history-barang";
    }

    @GetMapping("/history/delete/out/{id}/{kode}")
    public String deleteHistoryOut(@PathVariable("id") Long id, @PathVariable("kode") String kode, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        if (!historyService.deleteHistoryOutById(id)) {
            log.error("Transaksi keluar dengan id " + id + " tidak ditemukan");
        }

        Barang barang = barangService.findByKode(kode).get();
        List<HistoryBarangDto> historyBarangDtos = historyService.getHistoryBarang(kode);

        model.addAttribute("kodeBarang", kode);
        model.addAttribute("namaBarang", barang.getNama());
        model.addAttribute("histories", historyBarangDtos);

        return "history-barang";
    }

    @PostMapping("/search")
    public String searchByKodeBarangOrNamaBarangOrNamaVendor(SearchDto searchDto, HttpServletRequest request, Model model) {
        String role = GlobalFunction.getUserRole(request);

        model.addAttribute("role", role);

        List<Barang> barangs = barangService.findByKodeOrNamaOrNamaVendorContaining(searchDto);

        model.addAttribute("barangs", barangs);
        model.addAttribute("searchDto", searchDto);

        return "data-master";

    }

}
