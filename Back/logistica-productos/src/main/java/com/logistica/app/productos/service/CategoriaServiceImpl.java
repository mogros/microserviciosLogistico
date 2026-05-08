package com.logistica.app.productos.service;
import com.logistica.commons.service.CommonServiceImpl;
import com.logistica.app.productos.entity.Categoria;
import com.logistica.app.productos.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
@Service
public class CategoriaServiceImpl extends CommonServiceImpl<Categoria,CategoriaRepository> implements CategoriaService{}