package com.WalletProject.WalletProject.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true,nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String contactNo;

    @Column(nullable = false)
    private String name;

    private UserType userType;

    private String authority;//comma separated

    private String password;

    //private String username;

    private Boolean isAccountNonExpired;

    private Boolean isAccountNonLocked;

    private Boolean isCredentialsNonExpired;

    private Boolean isEnabled;

    public User(User user) {
        name=user.getName();
        password=user.getPassword();
        authority= user.getAuthority();
        contactNo=user.getContactNo();
        email=user.getEmail();
        isAccountNonExpired=user.isAccountNonExpired();
        isAccountNonLocked=user.isAccountNonLocked();
        isCredentialsNonExpired=user.isCredentialsNonExpired();
        isEnabled=user.isEnabled();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (authority == null || authority.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")); // Default role
        }

        return Arrays.stream(authority.split(","))
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim().toUpperCase())) // Ensure "ROLE_" prefix
                .toList();
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
