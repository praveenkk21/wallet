package com.WalletProject.WalletProject.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/*
 @Column(unique = true,nullable = false)
    private String email;

    @Column(unique = true,nullable = false)
    private String contactNo;

    private UserType userType;

    private String authority;//comma separated

    private String password;
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserRequestDto {

    @NotBlank(message = "Email shall be provided")
    private String email;

    @NotBlank(message = "contactNo shall be provided")
    private String contactNo;

    @NotBlank(message = "name shall be provided")
    private String name;

    @NotBlank(message = "password shall be provided")
    private String password;

    private String authority;
}
