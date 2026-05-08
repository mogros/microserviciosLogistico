package com.logistica.app.productos.controller;
import com.logistica.commons.controller.CommonController;
import com.logistica.app.productos.entity.Categoria;
import com.logistica.app.productos.service.CategoriaService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
@RestController @RequestMapping("/categorias")
public class CategoriaController extends CommonController<Categoria,CategoriaService>{}