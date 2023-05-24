package com.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "tbl_transaction_in")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionIn {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String kodeBarang;
    private Date date;
    private Integer stok;
    private String nomorSuratJalan;
    private Integer adjustmentStok;

}
