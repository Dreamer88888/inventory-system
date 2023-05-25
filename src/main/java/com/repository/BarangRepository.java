package com.repository;

import com.entity.Barang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarangRepository extends JpaRepository<Barang, String> {
    Optional<Barang> findByKode(String kode);

    long deleteByKode(String kode);

    @Query(value = "SELECT b FROM Barang b WHERE b.kode like %:keyword% OR b.nama like %:keyword% OR b.namaVendor like %:keyword%")
    List<Barang> findByKodeOrNamaOrNamaVendorContains(String keyword);

    @Query(value = "SELECT b FROM Barang b WHERE b.nama like %:nama% AND b.namaVendor like %:namaVendor%")
    Optional<Barang> findByNamaAndVendorContains(String nama, String namaVendor);
}
