package com.suzu.services.squad4.login;

import com.suzu.common.APIBase;
import com.suzu.controller.store.login.AccountModel;
import com.suzu.dataprovider.FailureResponseModel;
import com.suzu.dataprovider.response.AccountInfoModel;
import com.suzu.constants.FrameworkConst;
import com.suzu.constants.HTTPMethod;
import com.suzu.services.APIPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

import static com.suzu.constants.FailureHandling.CONTINUE_ON_FAILURE;
import static com.suzu.constants.FailureHandling.STOP_ON_FAILURE;
import static com.suzu.constants.FrameworkConst.DATA_NOT_FOUND;
import static org.hamcrest.Matchers.*;

public class AuthenServices extends APIBase {
    public Response getAccessToken(String baseToken, String code, String companyId) {
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("accessToken", baseToken);
        reqParams.put("code", code);
        reqParams.put("companyId", companyId);

        RequestSpecification request = createDefaultRequest(APIPath.API_ACCOUNT_TOKEN, false, "https://" + FrameworkConst.API_FE_HOST).params(reqParams);
        return callRestAPI(HTTPMethod.GET, request);
    }


    /* Call API Get Access Token */
    public Response getAccessToken(AccountModel accountModel) {
        HashMap<String, String> repParams = (HashMap) accountModel.getQueryParams().getValue();
        return getAccessToken(repParams.get("accessToken"), repParams.get("code"), repParams.get("companyId"));
    }

    public void verifyResponse(AccountModel accountModel, Response response) {
        verifyHTTPStatusCodeAndTime(response, accountModel.getResCode(), accountModel.getResTime());

        int expResCode = Integer.parseInt(String.valueOf(accountModel.getResCode().getValue()));
        String expErrCode = String.valueOf(accountModel.getErrorCode().getValue());
        switch (expResCode) {
            case 200:
                String token = extractResponseDataToString(response, "token", DATA_NOT_FOUND).toString();
                if (!token.equalsIgnoreCase(DATA_NOT_FOUND) && expErrCode.isEmpty()) {
                    AccountInfoModel expExp = convertRequestDataToObject(accountModel.getTestResult().getValue(), AccountInfoModel.class);
                    AccountInfoModel actRes = convertResponseToObject(response, AccountInfoModel.class);
                    assertThatCondition(actRes, hasProperty("token"), CONTINUE_ON_FAILURE, "Verify [token] is exist");
                    assertThatCondition(actRes, hasProperty("hash"), CONTINUE_ON_FAILURE, "Verify [hash] is exist");
                    assertThatCondition(actRes, hasProperty("accessToken"), CONTINUE_ON_FAILURE, "Verify [accessToken] is exist");
                    assertThatCondition(actRes, hasProperty("refreshToken"), CONTINUE_ON_FAILURE, "Verify [refreshToken] is exist");
                    assertThatCondition(actRes, hasProperty("expiresIn"), CONTINUE_ON_FAILURE, "Verify [expiresIn] is exist");
                    assertThatCondition(actRes, hasProperty("maxItem", equalTo(0)), CONTINUE_ON_FAILURE, "Verify [maxItem] = 0");
                    assertEqualCondition(actRes.getAccountInfoVo(), expExp.getAccountInfoVo(), CONTINUE_ON_FAILURE, "Verify Account Info Vo");
                } else {
                    assertThatCondition(token, equalToIgnoringCase(DATA_NOT_FOUND), STOP_ON_FAILURE, "Verify [token] isn't exist");
                    FailureResponseModel expExp = convertRequestDataToObject(accountModel.getTestResult().getValue(), FailureResponseModel.class);
                    FailureResponseModel actRes = convertResponseToObject(response, FailureResponseModel.class);
                    assertEqualCondition(actRes, expExp, CONTINUE_ON_FAILURE, "Verify Account Info Vo");
                }
                break;
            case 400:
                break;
            default:
                break;
        }
    }
}
