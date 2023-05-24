package com.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class TransactionInDto {

    @NotEmpty(message = "Kode barang tidak boleh kosong")
    private String kodeBarang;

    @NotEmpty(message = "Nomor surat jalan tidak boleh kosong")
    private String nomorSuratJalan;

    @NotNull(message = "Stok tidak boleh kosong")
    private Integer stok;

}
