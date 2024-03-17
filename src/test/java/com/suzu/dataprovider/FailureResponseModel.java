package com.suzu.dataprovider;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class FailureResponseModel {
    String code;
    boolean success;
    String message;
    Object data;

    @EqualsAndHashCode.Exclude
    String time;
}
