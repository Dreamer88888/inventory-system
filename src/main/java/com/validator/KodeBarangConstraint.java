package com.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = KodeBarangValidator.class)
public @interface KodeBarangConstraint {

    String message() default "Kode barang sudah ada, gunakan kode barang lain";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
