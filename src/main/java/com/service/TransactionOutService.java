package com.service;

import com.dto.TransactionOutReportDto;
import com.entity.Barang;
import com.entity.TransactionOut;
import com.repository.TransactionOutRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionOutService {

    @Autowired
    private TransactionOutRepository transactionOutRepository;

    @Autowired
    private BarangService barangService;

    public List<TransactionOut> findAll() {
        return transactionOutRepository.findAll();
    }

    public TransactionOut findById(Long id) {
        Optional<TransactionOut> transactionOutFromDB = transactionOutRepository.findById(id);

        if (transactionOutFromDB.isPresent()) {
            return transactionOutFromDB.get();
        } else {
            return null;
        }
    }

    public List<TransactionOut> findByKodeBarang(String kodeBarang) {
        return transactionOutRepository.findByKodeBarang(kodeBarang);
    }

    @Transactional
    public TransactionOut addTransactionOut(TransactionOut transactionOut) {
        Barang barangFromDB = barangService.findByKode(transactionOut.getKodeBarang()).get();

        if (barangFromDB != null) {
            Integer currentStock = barangFromDB.getStok() - transactionOut.getStok();
            if (currentStock >= 0) {
                barangFromDB.setStok(currentStock);
                barangService.updateBarang(barangFromDB);

                transactionOut.setAdjustmentStok(currentStock);

                return transactionOutRepository.save(transactionOut);
            } else {
                log.error("Stok kurang dari 0");

                return null;
            }
        } else {
            log.error("Transaksi Masuk gagal dengan Kode Barang: " + transactionOut.getKodeBarang());

            return null;
        }
    }

    public TransactionOut updateTransactionIn(TransactionOut transactionIn) {
        Optional<TransactionOut> transactionOutFromDB = transactionOutRepository.findById(transactionIn.getId());

        if (!transactionOutFromDB.isPresent()) {
            transactionOutFromDB.get().setStok(transactionIn.getStok());
            return transactionOutRepository.save(transactionOutFromDB.get());
        } else {
            log.error("Transaction In dengan Id: " + transactionIn.getId() + " tidak ditemukan.");
            return null;
        }
    }

    public List<TransactionOutReportDto> getTransactionOutReport(Date from, Date to) {
        List<TransactionOutReportDto> transactionOutReportDtos = transactionOutRepository
                .findByDateBetweenOrEquals(from, to).stream().map(transactionOut -> {
                    Barang barang = barangService.findByKode(transactionOut.getKodeBarang()).get();
                    return new TransactionOutReportDto(transactionOut.getId(), barang.getKode(), barang.getNama(),
                            transactionOut.getNamaCustomer(), transactionOut.getDate(), transactionOut.getStok());
                }).collect(Collectors.toList());

        return transactionOutReportDtos;
    }

    public Boolean deleteByKodeBarang(String kodeBarang) {
        List<TransactionOut> transactionOuts = findByKodeBarang(kodeBarang);

        for (TransactionOut transactionOut : transactionOuts) {
            deleteTransactionOut(transactionOut);
        }

        return true;
    }

    @Transactional
    public void deleteTransactionOut(TransactionOut transactionOut) {
        Barang barang = barangService.findByKode(transactionOut.getKodeBarang()).get();
        if (barang != null) {
            Integer currentStok = barang.getStok();
            barang.setStok(currentStok + transactionOut.getStok());
            barangService.updateBarang(barang);
            transactionOutRepository.delete(transactionOut);
        } else {
            log.error("Delete transaksi keluar gagal, dengan id " + transactionOut.getKodeBarang());
        }

    }

}
