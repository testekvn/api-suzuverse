package com.suzu.constants;

import io.restassured.http.ContentType;

public enum ContentTypeManager {
    APPLICATION_JSON(ContentType.JSON),
    MULTIPART_FORM(ContentType.MULTIPART),
    FORM_URLENCODED(ContentType.URLENC);

    private final ContentType value;

    ContentTypeManager(ContentType contentType) {
        this.value = contentType;
    }

    public ContentType getValue() {
        return value;
    }
}
