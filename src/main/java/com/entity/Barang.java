package com.entity;

import com.validator.KodeBarangConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_barang")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Barang {

    @Id
    @NotBlank(message = "Kode barang tidak boleh kosong")
    private String kode;

    @NotBlank(message = "Nama barang tidak boleh kosong")
    private String nama;

    @NotBlank(message = "Nama vendor tidak boleh kosong")
    private String namaVendor;

    private Integer stok;

}
