package com.suzu.help;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginModel {

    public int row;

    public String tcId = "TC_ID";

    public String email = "EMAIL";

    public String password = "PASSWORD";

    public String company = "COMPANY";

    public String expectedTitle = "EXPECTED_TITLE";

    public String expectedError = "EXPECTED_ERROR";

    public String expectedUrl = "EXPECTED_URL";
}
