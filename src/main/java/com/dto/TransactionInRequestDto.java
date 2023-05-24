package com.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@NoArgsConstructor
public class TransactionInRequestDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal dari ... tidak boleh kosong")
    @PastOrPresent(message = "Tanggal dari ... tidak boleh lebih dari hari ini")
    private Date from;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Tanggal sampai ... tidak boleh kosong")
    @PastOrPresent(message = "Tanggal sampai ... tidak boleh lebih dari hari ini")
    private Date to;

}
