package com.suzu.datadriven;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DataModel {
    public String devName;         // DEV Name
    public String description;     // Mô tả trên website
    public Object value;           // Value

    public DataModel(DataModel model) {
        this.devName = model.getDevName();
        this.description = model.getDescription();
        this.value = model.getValue();
    }
}
