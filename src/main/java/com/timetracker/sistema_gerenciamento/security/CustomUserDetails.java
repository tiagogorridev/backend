package com.timetracker.sistema_gerenciamento.security;

import com.timetracker.sistema_gerenciamento.model.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Usar o perfil do usuário em vez de hardcoded "ROLE_USER"
        String perfil = usuario.getPerfil();
        if (perfil == null || perfil.isEmpty()) {
            perfil = "USER"; // Valor padrão
        }

        // Adicionar prefixo ROLE_ se não estiver presente
        String roleName = perfil.startsWith("ROLE_") ? perfil : "ROLE_" + perfil;
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return usuario.getSenha();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}