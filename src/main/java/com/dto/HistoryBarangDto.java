package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryBarangDto {

    private Long id;
    private String kode;
    private Date date;
    private String description;
    private String nomorSuratJalan;
    private Integer in;
    private Integer out;
    private Integer stok;

}
