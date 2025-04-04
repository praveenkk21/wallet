package com.WalletProject.WalletProject.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
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

    @NotBlank(message = "Email shall be provided",groups = CreateUSer.class)
    @Email(message = "Email shall be valid")
    private String email;

    @NotBlank(message = "contactNo shall be provided",groups = CreateUSer.class)
    private String contactNo;

    @NotBlank(message = "name shall be provided",groups = CreateUSer.class)
    private String name;

    @NotBlank(message = "password shall be provided", groups = CreateUSer.class)
    private String password;

    private String authority;
}
