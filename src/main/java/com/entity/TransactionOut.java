package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "tbl_transaction_out")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionOut {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String kodeBarang;
    private Date date;
    private Integer stok;
    private String nomorSuratJalan;
    private String namaCustomer;
    private Integer adjustmentStok;

}
