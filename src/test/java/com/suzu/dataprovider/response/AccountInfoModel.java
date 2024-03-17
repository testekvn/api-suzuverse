package com.suzu.dataprovider.response;

import com.suzu.dataprovider.response.sub.AccountInfoVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class AccountInfoModel {
    @EqualsAndHashCode.Exclude
    public String token;

    @EqualsAndHashCode.Exclude
    public Object hash;

    public AccountInfoVo accountInfoVo;

    @EqualsAndHashCode.Exclude
    public String accessToken;

    @EqualsAndHashCode.Exclude
    public String refreshToken;

    @EqualsAndHashCode.Exclude
    public long expiresIn;

    public int maxItem;
}



