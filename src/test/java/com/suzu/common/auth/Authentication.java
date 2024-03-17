package com.suzu.common.auth;

import com.suzu.common.APIBase;
import com.suzu.dataprovider.response.AccountInfoModel;
import com.suzu.services.squad4.login.AuthenServices;
import io.restassured.response.Response;

public class Authentication extends APIBase {


    /**
     * Login
     *
     * @param accountInfo : The information of this testing account
     */
    public AccountInfoModel getAccessToken(AccountInfo accountInfo, String baseToken, String code, String companyId) {
        Response response = new AuthenServices().getAccessToken(baseToken, code, companyId);

        // Update the account info
        AccountInfoModel accountInfoModel = convertResponseToObject(response, AccountInfoModel.class);
        accountInfo.setAccessToken(accountInfoModel.getAccessToken());
        accountInfo.setToken(accountInfoModel.getToken());

        var accountInfoVo = accountInfoModel.getAccountInfoVo();
        accountInfo.setUserName(accountInfoVo.getEmail());
        accountInfo.setPassword(accountInfoVo.getPassword());
        accountInfo.setActiveCode(accountInfoVo.getActiveCode());
        accountInfo.setPhone(accountInfoVo.getPhone());
        accountInfo.setSsoUserId(accountInfoVo.getSsoUserId());
        return accountInfoModel;
    }
}
