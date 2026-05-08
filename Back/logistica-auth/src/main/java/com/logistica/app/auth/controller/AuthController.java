package com.logistica.app.auth.controller;
import com.logistica.app.auth.model.AuthDTOs.*;
import com.logistica.app.auth.model.Rol;
import com.logistica.app.auth.model.Usuario;
import com.logistica.app.auth.repository.RolRepository;
import com.logistica.app.auth.repository.UsuarioRepository;
import com.logistica.app.auth.service.JwtUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController @RequestMapping("/auth")
public class AuthController {
    @Autowired 
    private AuthenticationManager authManager;
    
    @Autowired 
    private UsuarioRepository usuarioRepo;
    
    @Autowired 
    private RolRepository rolRepo;
    
    @Autowired 
    private PasswordEncoder encoder;
    
    @Autowired 
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtUtils.generateJwtToken(auth);
        UserDetails ud = (UserDetails) auth.getPrincipal();
        List<String> roles = ud.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        Usuario u = usuarioRepo.findByUsername(ud.getUsername()).orElseThrow();
        return ResponseEntity.ok(new JwtResponse(jwt, u.getId(), ud.getUsername(), u.getEmail(), roles));
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegisterRequest req) {
        if (usuarioRepo.existsByUsername(req.getUsername())) return ResponseEntity.badRequest().body(new MessageResponse("Username ya en uso"));
        if (usuarioRepo.existsByEmail(req.getEmail())) return ResponseEntity.badRequest().body(new MessageResponse("Email ya registrado"));
        Usuario u = new Usuario(req.getUsername(), req.getEmail(), encoder.encode(req.getPassword()));
        Set<String> rolesReq = req.getRoles();
        Set<Rol> roles = new HashSet<>();
        if (rolesReq == null || rolesReq.isEmpty()) {
            roles.add(rolRepo.findByNombre(Rol.NombreRol.ROLE_VENDEDOR).orElseThrow());
        } else {
            rolesReq.forEach(r -> {
                switch(r.toUpperCase()) {
                    case "ADMIN","ROLE_ADMIN" -> roles.add(rolRepo.findByNombre(Rol.NombreRol.ROLE_ADMIN).orElseThrow());
                    case "COMPRADOR","ROLE_COMPRADOR" -> roles.add(rolRepo.findByNombre(Rol.NombreRol.ROLE_COMPRADOR).orElseThrow());
                    default -> roles.add(rolRepo.findByNombre(Rol.NombreRol.ROLE_VENDEDOR).orElseThrow());
                }
            });
        }
        u.setRoles(roles); usuarioRepo.save(u);
        return ResponseEntity.ok(new MessageResponse("Usuario registrado: " + req.getUsername()));
    }
}