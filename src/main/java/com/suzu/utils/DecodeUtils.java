package com.suzu.utils;

import java.util.Base64;

public final class DecodeUtils {

    public static String getDecodedString(String encodedString) {
        return new String(Base64.getDecoder().decode(encodedString.getBytes()));
    }
}
