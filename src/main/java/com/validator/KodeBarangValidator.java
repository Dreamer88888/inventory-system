package com.validator;

import com.entity.Barang;
import com.repository.BarangRepository;
import com.service.BarangService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class KodeBarangValidator implements ConstraintValidator<KodeBarangConstraint, String> {

    @Autowired
    private BarangService barangService;

    @Override
    public boolean isValid(String kodeBarang, ConstraintValidatorContext constraintValidatorContext) {
        return checkIfKodeBarangUnique(kodeBarang);
    }

    private boolean checkIfKodeBarangUnique(String kodeBarang) {
        Optional<Barang> barang = barangService.findByKode(kodeBarang);

        return barang.isEmpty();
    }
}
