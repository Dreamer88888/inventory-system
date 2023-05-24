package com.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class TransactionOutReportDto {

    private Long id;
    private String kodeBarang;
    private String namaBarang;
    private String namaCustomer;
    private Date date;
    private Integer quantity;

}
