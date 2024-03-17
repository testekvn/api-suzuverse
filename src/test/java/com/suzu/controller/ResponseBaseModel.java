package com.suzu.controller;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResponseBaseModel {
    Integer opStatus;
    Integer httpStatusCode;
    String dbpErrCode;
    String dbpErrMsg;
    String errorDetails;
}
