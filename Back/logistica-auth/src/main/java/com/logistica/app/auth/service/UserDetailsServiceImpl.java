package com.logistica.app.auth.service;
import com.logistica.app.auth.model.Usuario;
import com.logistica.app.auth.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired private UsuarioRepository repo;
    @Override @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario u = repo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: "+username));
        List<GrantedAuthority> auth = u.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getNombre().name())).collect(Collectors.toList());
        return User.builder().username(u.getUsername()).password(u.getPassword()).authorities(auth).disabled(!u.isEnabled()).build();
    }
}