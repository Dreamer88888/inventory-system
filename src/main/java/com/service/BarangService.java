package com.service;

import com.entity.Barang;
import com.repository.BarangRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BarangService {

    @Autowired
    private BarangRepository barangRepository;

    public List<Barang> findAll() {
        return barangRepository.findAll();
    }

    public Optional<Barang> findByKode(String kode) {
        return barangRepository.findByKode(kode);
    }

    public Barang addBarang(Barang barang) {
        barang.setStok(0);
        return barangRepository.save(barang);
    }

    public Barang updateBarang(Barang barang) {
        Optional<Barang> barangFromDB = barangRepository.findByKode(barang.getKode());

        if (barangFromDB.isPresent()) {
            return barangRepository.save(barang);
        } else {
            log.error("Barang dengan kode " + barang.getKode() + " tidak ada di Database.");
            return null;
        }
    }

    public Boolean deleteBarang(String kode) {
        Optional<Barang> barangFromDB = barangRepository.findByKode(kode);
        if (barangFromDB.isPresent()) {
            log.info(barangFromDB.toString());
            long deletedItems = barangRepository.deleteByKode(kode);
            log.info(String.valueOf(deletedItems));
            return true;
        } else {
            return false;
        }
    }

}
