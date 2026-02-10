package com.example.productos.repository;

import com.example.productos.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.ArrayList;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Producto findById(int id);
    ArrayList<Producto> findByNombre(String nombre);
    ArrayList<Producto> findByPrecio(BigDecimal precio);
}
