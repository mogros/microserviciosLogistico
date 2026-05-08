package com.logistica.commons.controller;
import com.logistica.commons.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommonController<E, S extends CommonService<E>> {
    @Autowired protected S service;

    @GetMapping
    public ResponseEntity<?> listar() { return ResponseEntity.ok(service.findAll()); }

    @GetMapping("/pagina")
    public ResponseEntity<?> listarPagina(Pageable pageable) { return ResponseEntity.ok(service.findAllPage(pageable)); }

    @GetMapping("/{id}")
    public ResponseEntity<?> ver(@PathVariable Long id) {
        Optional<E> o = service.findById(id);
        return o.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(o.get());
    }

    @PostMapping
    public ResponseEntity<?> crear(@Validated @RequestBody E entity, BindingResult result) {
        if (result.hasErrors()) return validar(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editar(@PathVariable Long id, @Validated @RequestBody E entity, BindingResult result) {
        if (result.hasErrors()) return validar(result);
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    protected ResponseEntity<?> validar(BindingResult result) {
        Map<String,Object> errores = new HashMap<>();
        result.getFieldErrors().forEach(e -> errores.put(e.getField(), "El campo " + e.getField() + " " + e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errores);
    }
}