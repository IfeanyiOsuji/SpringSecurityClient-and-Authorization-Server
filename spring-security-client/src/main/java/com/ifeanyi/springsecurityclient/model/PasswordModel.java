package com.ifeanyi.springsecurityclient.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordModel {
    private String email;
    private String oldPassword;
    private String newPassword;
}
