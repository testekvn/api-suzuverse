package com.suzu.datadriven;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Base Model for testing data
 */
@AllArgsConstructor
@Data
@Builder
public class BaseModel {
    public DataModel testId;
    public DataModel testDesc;
    public DataModel requestBody;
    public DataModel resCode;
    public DataModel errMessage;
    public DataModel testResult;
    public DataModel queryParams;
    public DataModel pathParams;
    public DataModel errorCode;
    public DataModel resTime;

    public BaseModel() {
        testId = createDataModelObj("Id");
        testDesc = createDataModelObj("Desc");
        requestBody = createDataModelObj("RequestBody");
        resCode = createDataModelObj("ResCode");
        errMessage = createDataModelObj("ErrMessage");
        testResult = createDataModelObj("TestResult");
        queryParams = createDataModelObj("QueryParams");
        pathParams = createDataModelObj("PathParams");
        errorCode = createDataModelObj("ErrCode");
        resTime = createDataModelObj("ResTime");
    }

    public DataModel createDataModelObj(String name) {
        return DataModel.builder().devName(name).build();
    }
}
