package com.logistica.app.auth.config;
import com.logistica.app.auth.model.Rol;
import com.logistica.app.auth.model.Usuario;
import com.logistica.app.auth.repository.RolRepository;
import com.logistica.app.auth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    @Autowired private RolRepository rolRepo;
    @Autowired private UsuarioRepository usuarioRepo;
    @Autowired private PasswordEncoder encoder;

    @Override public void run(String... args) {
        for (Rol.NombreRol nombre : Rol.NombreRol.values()) {
            if (rolRepo.findByNombre(nombre).isEmpty()) { rolRepo.save(new Rol(nombre)); log.info("Rol creado: {}", nombre); }
        }
        if (!usuarioRepo.existsByUsername("admin")) {
            Rol rolAdmin = rolRepo.findByNombre(Rol.NombreRol.ROLE_ADMIN).orElseThrow();
            Usuario admin = new Usuario("admin","admin@logistica.com",encoder.encode("Admin123!"));
            admin.setRoles(Set.of(rolAdmin));
            usuarioRepo.save(admin);
            log.info("Usuario admin creado");
        }
    }
}