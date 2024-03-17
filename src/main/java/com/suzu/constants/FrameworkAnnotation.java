package com.suzu.constants;

import javax.annotation.Nullable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for all project. It provides more information for execution results
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FrameworkAnnotation {
    public CategoryType[] category();

    public AuthorType[] author();

    @Nullable
    public AuthorType[] reviewer();

    @Nullable
    public AuthorType[] modifier() default {};

    @Nullable
    public TCLevel[] tcLevel() default {};            // For management
}
