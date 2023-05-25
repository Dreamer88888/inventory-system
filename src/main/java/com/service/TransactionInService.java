package com.service;

import com.dto.TransactionInReportDto;
import com.entity.Barang;
import com.entity.TransactionIn;
import com.entity.TransactionOut;
import com.repository.TransactionInRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionInService {

    @Autowired
    private TransactionInRepository transactionInRepository;

    @Autowired
    private BarangService barangService;

    public List<TransactionIn> findAll() {
        return transactionInRepository.findAll();
    }

    public TransactionIn findById(Long id) {
        Optional<TransactionIn> transactionInFromDB = transactionInRepository.findById(id);

        if (transactionInFromDB.isPresent()) {
            return transactionInFromDB.get();
        } else {
            return null;
        }
    }

    public List<TransactionIn> findByKodeBarang(String kodeBarang) {
        return transactionInRepository.findByKodeBarang(kodeBarang);
    }

    @Transactional
    public TransactionIn addTransactionIn(TransactionIn transactionIn) {
        Barang barangFromDB = barangService.findByKode(transactionIn.getKodeBarang()).get();

        if (barangFromDB != null) {
            Integer currentStock = barangFromDB.getStok() + transactionIn.getStok();
            barangFromDB.setStok(currentStock);
            barangService.updateBarang(barangFromDB);

            transactionIn.setAdjustmentStok(currentStock);

            return transactionInRepository.save(transactionIn);
        } else {
            log.error("Transaksi Masuk gagal dengan Kode Barang: " + transactionIn.getKodeBarang());

            return null;
        }
    }

    public TransactionIn updateTransactionIn(TransactionIn transactionIn) {
        Optional<TransactionIn> transactionInFromDB = transactionInRepository.findById(transactionIn.getId());

        if (!transactionInFromDB.isPresent()) {
            transactionInFromDB.get().setStok(transactionIn.getStok());
            return transactionInRepository.save(transactionInFromDB.get());
        } else {
            log.error("Transaction In dengan Id: " + transactionIn.getId() + " tidak ditemukan.");
            return null;
        }
    }

    public List<TransactionInReportDto> getTransactionInReport(Date from, Date to) {
        List<TransactionInReportDto> transactionInReportDtos = transactionInRepository
                .findByDateBetweenOrEquals(from, to).stream().map(transactionIn -> {
                    log.info(transactionIn.toString());
                    Barang barang = barangService.findByKode(transactionIn.getKodeBarang()).get();
                    return new TransactionInReportDto(transactionIn.getId(), barang.getKode(), barang.getNama(),
                            barang.getNamaVendor(),
                            transactionIn.getDate(), transactionIn.getStok());
                }).collect(Collectors.toList());

        return transactionInReportDtos;
    }

    public Boolean deleteByKodeBarang(String kodeBarang) {
        List<TransactionIn> transactionIns = findByKodeBarang(kodeBarang);

        for (TransactionIn transactionIn : transactionIns) {
            deleteTransactionIn(transactionIn);
        }

        return true;
    }

    @Transactional
    public void deleteTransactionIn(TransactionIn transactionIn) {
        Barang barang = barangService.findByKode(transactionIn.getKodeBarang()).get();

        if (barang != null) {
            Integer currentStok = barang.getStok();
            barang.setStok(currentStok - transactionIn.getStok());
            barangService.updateBarang(barang);
            transactionInRepository.delete(transactionIn);
        } else {
            log.error("Delete transaksi keluar gagal, dengan id " + transactionIn.getKodeBarang());
        }
    }

}
