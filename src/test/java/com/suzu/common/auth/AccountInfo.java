package com.suzu.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfo {
    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String phone;
    private int ssoUserId;
    private String activeCode;
    private String baseToken;
    private String accessToken;
    private String token;
}
