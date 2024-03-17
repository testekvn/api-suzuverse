package com.suzu.constants;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
//@LinkAnnotation(type = "tms")

public @interface TFSLink {
    String value();
}
