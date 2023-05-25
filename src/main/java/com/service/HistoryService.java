package com.service;

import com.dto.HistoryBarangDto;
import com.entity.Barang;
import com.entity.TransactionIn;
import com.entity.TransactionOut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class HistoryService {

    @Autowired
    private BarangService barangService;

    @Autowired
    private TransactionInService transactionInService;

    @Autowired
    private TransactionOutService transactionOutService;

    public List<HistoryBarangDto> getHistoryBarang(String kode) {
        List<TransactionIn> transactionIns = transactionInService.findByKodeBarang(kode);
        List<TransactionOut> transactionOuts = transactionOutService.findByKodeBarang(kode);
        List<HistoryBarangDto> historyTransactions = new ArrayList<HistoryBarangDto>();
        Barang barang = barangService.findByKode(kode).get();

        int inIndex = 0;
        int outIndex = 0;

        while (transactionIns.size() > 0 && transactionOuts.size() > 0) {
            TransactionIn transactionIn = transactionIns.get(inIndex);
            TransactionOut transactionOut = transactionOuts.get(outIndex);

            if (transactionIn.getDate().compareTo(transactionOut.getDate()) < 0) {

                HistoryBarangDto historyBarangDto = new HistoryBarangDto();

                historyBarangDto.setId(transactionIn.getId());
                historyBarangDto.setKode(transactionIn.getKodeBarang());
                historyBarangDto.setDate(transactionIn.getDate());
                historyBarangDto.setDescription("Diterima dari " + barang.getNamaVendor());
                historyBarangDto.setNomorSuratJalan(transactionIn.getNomorSuratJalan());
                historyBarangDto.setIn(transactionIn.getStok());
                historyBarangDto.setStok(transactionIn.getAdjustmentStok());

                transactionIns.remove(inIndex);

                historyTransactions.add(historyBarangDto);
            } else {
                HistoryBarangDto historyBarangDto = new HistoryBarangDto();

                historyBarangDto.setId(transactionOut.getId());
                historyBarangDto.setKode(transactionOut.getKodeBarang());
                historyBarangDto.setDate(transactionOut.getDate());
                historyBarangDto.setDescription("Dikirim ke " + transactionOut.getNamaCustomer());
                historyBarangDto.setNomorSuratJalan(transactionOut.getNomorSuratJalan());
                historyBarangDto.setOut(transactionOut.getStok());
                historyBarangDto.setStok(transactionOut.getAdjustmentStok());

                transactionOuts.remove(outIndex);

                historyTransactions.add(historyBarangDto);
            }
        }

        for (int i = 0; i < transactionIns.size(); i++) {
            HistoryBarangDto historyBarangDto = new HistoryBarangDto();
            TransactionIn transactionIn = transactionIns.get(i);

            historyBarangDto.setId(transactionIn.getId());
            historyBarangDto.setKode(transactionIn.getKodeBarang());
            historyBarangDto.setDate(transactionIn.getDate());
            historyBarangDto.setDescription("Diterima dari " + barang.getNamaVendor());
            historyBarangDto.setNomorSuratJalan(transactionIn.getNomorSuratJalan());
            historyBarangDto.setIn(transactionIn.getStok());
            historyBarangDto.setStok(transactionIn.getAdjustmentStok());

            historyTransactions.add(historyBarangDto);
        }

        for (int i = 0; i < transactionOuts.size(); i++) {
            HistoryBarangDto historyBarangDto = new HistoryBarangDto();
            TransactionOut transactionOut = transactionOuts.get(i);

            historyBarangDto.setId(transactionOut.getId());
            historyBarangDto.setKode(transactionOut.getKodeBarang());
            historyBarangDto.setDate(transactionOut.getDate());
            historyBarangDto.setDescription("Dikirim ke " + transactionOut.getNamaCustomer());
            historyBarangDto.setNomorSuratJalan(transactionOut.getNomorSuratJalan());
            historyBarangDto.setOut(transactionOut.getStok());
            historyBarangDto.setStok(transactionOut.getAdjustmentStok());

            historyTransactions.add(historyBarangDto);
        }

        return historyTransactions;
    }

    public boolean deleteHistoryInById(Long id) {
        TransactionIn transactionIn = transactionInService.findById(id);
        transactionInService.deleteTransactionIn(transactionIn);

        return true;
    }

    public boolean deleteHistoryOutById(Long id) {
        TransactionOut transactionOut = transactionOutService.findById(id);
        transactionOutService.deleteTransactionOut(transactionOut);

        return true;
    }

}
