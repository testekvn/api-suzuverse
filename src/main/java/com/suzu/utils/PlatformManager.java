package com.suzu.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlatformManager {

    private static final ThreadLocal<String> platformName = new ThreadLocal<>();

    public static String getPlatformName() {
        return platformName.get();
    }

    public static void setPlatformName(String platform) {
        platformName.set(platform);
    }

    public static void unloadPlatformName() {
        platformName.remove();
    }

    public static String getOSInfo() {
        return System.getProperty("os.name").replace(" ", " ");
    }

    public static boolean isMacOS() {
        return getOSInfo().toLowerCase().contains("mac os");
    }
}
