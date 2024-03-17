package com.suzu.dataprovider;

import com.suzu.controller.store.login.AccountModel;
import com.suzu.datadriven.BaseProvider;
import com.suzu.utils.configloader.JsonUtils;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;

public class AccountDataProvider extends BaseProvider {

    @DataProvider(name = "SUZU_STORE_ACCOUNT")
    public Object[][] SUZU_STORE_ACCOUNT(Method method) {
        JsonUtils jsonUtils = new JsonUtils();
        var dataList = jsonUtils.vinGetDataDrivenFromJSON(DataTestPath.DATA_ACCOUNT, method.getName());

        // Using Model Class and Data From Json file
        AccountModel model = new AccountModel();
        return updateDataModel(model, dataList);
    }

}
