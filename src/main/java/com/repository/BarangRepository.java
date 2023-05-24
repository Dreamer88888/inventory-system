package com.repository;

import com.entity.Barang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BarangRepository extends JpaRepository<Barang, String> {
    Optional<Barang> findByKode(String kode);

    long deleteByKode(String kode);
}
