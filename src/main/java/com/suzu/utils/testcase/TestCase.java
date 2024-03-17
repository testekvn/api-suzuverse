package com.suzu.utils.testcase;

import com.suzu.constants.AuthorType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCase {
    private String desc;
    private String id;
    private String name;
    private List<AuthorType> authorType;
    private String mtTCId;
}
