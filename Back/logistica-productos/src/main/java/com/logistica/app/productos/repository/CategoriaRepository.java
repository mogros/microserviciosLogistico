package com.logistica.app.productos.repository;
import com.logistica.app.productos.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {}