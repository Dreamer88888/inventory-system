package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tbl_stok_report")
@Data
@NoArgsConstructor
public class StokReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date date;

    private List<String> kodeBarang;
    private List<String> namaBarang;
    private List<String> namaVendor;
    private List<Integer> stokBarang;

}
