package com.service;

import com.entity.Barang;
import com.entity.StokReport;
import com.repository.StokReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StokReportService {

    @Autowired
    private StokReportRepository stokReportRepository;

    @Autowired
    private BarangService barangService;

    public List<StokReport> findAll() {
        return stokReportRepository.findAll();
    }

    public StokReport findById(Long id) {
        Optional<StokReport> stokReport = stokReportRepository.findById(id);

        if (stokReport.isPresent()) {
            return stokReport.get();
        } else {
            log.error("Stok Report dengan Id: " + id + " tidak ditemukan.");

            return null;
        }
    }

    public StokReport addStokReport() {
        List<Barang> barangs = barangService.findAll();

        List<String> kodeBarang = new ArrayList<>();
        List<String> namaBarang = new ArrayList<>();
        List<String> namaVendor = new ArrayList<>();
        List<Integer> stokBarang = new ArrayList<>();

        StokReport stokReport = new StokReport();

        stokReport.setDate(new Date());

        for (int i = 0; i < barangs.size(); i++) {
            kodeBarang.add(barangs.get(i).getKode());
            namaBarang.add(barangs.get(i).getNama());
            namaVendor.add(barangs.get(i).getNamaVendor());
            stokBarang.add(barangs.get(i).getStok());
        }

        stokReport.setKodeBarang(kodeBarang);
        stokReport.setNamaBarang(namaBarang);
        stokReport.setNamaVendor(namaVendor);
        stokReport.setStokBarang(stokBarang);

        return stokReportRepository.save(stokReport);
    }

}
