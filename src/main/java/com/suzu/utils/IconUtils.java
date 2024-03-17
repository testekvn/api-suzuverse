package com.suzu.utils;


import com.suzu.constants.FrameworkConst;

public final class IconUtils {

    private IconUtils() {
    }

    public static String getOSIcon() {
        String operationSystem = PlatformManager.getOSInfo().toLowerCase();
        if (operationSystem.contains("win")) {
            return FrameworkConst.ICON_OS_WINDOWS;
        } else if (operationSystem.contains("nix") || operationSystem.contains("nux") || operationSystem.contains("aix")) {
            return FrameworkConst.ICON_OS_LINUX;
        } else if (operationSystem.contains("mac")) {
            return FrameworkConst.ICON_OS_MAC;
        }
        return operationSystem;
    }
}
