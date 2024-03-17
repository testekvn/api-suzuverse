package com.suzu.dataprovider.response.sub;

import lombok.Data;

@Data
public class Group {
    public int id;
    public int companyId;
    public String name;
    public int status;
    public int buyRestriction;
    public int sellRestriction;
    public int createRestriction;
    public int createAvatarRestriction;
    public int sellAvatarRestriction;
    public int isDeleted;
    public String createdDate;
    public String updateDate;

}
