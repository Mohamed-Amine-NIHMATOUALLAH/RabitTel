package com.rabittel.usersservice.security;

import com.rabittel.usersservice.entities.User;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {


    private final User user;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().name()
                )
        );
    }


    @Override
    public String getPassword() {

        return user.getPasswordHash();
    }


    @Override
    public String getUsername() {

        return user.getEmail();
    }


    @Override
    public boolean isAccountNonExpired() {

        return true;
    }


    @Override
    public boolean isAccountNonLocked() {


        if(user.getLockedUntil() == null){
            return true;
        }


        return user.getLockedUntil()
                .isBefore(LocalDateTime.now());
    }


    @Override
    public boolean isCredentialsNonExpired() {

        return true;
    }


    @Override
    public boolean isEnabled() {

        return user.isActive();
    }


    public User getUser() {

        return user;
    }
}