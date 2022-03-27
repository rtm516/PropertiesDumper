package com.rtm516.propertiesdumper;

import android.os.Build;

import java.util.Locale;

// From https://gitlab.com/AuroraOSS/AuroraStore/-/blob/b34e14e13a8b4dbbe9f1303b166e3a6b65e85e97/app/src/main/java/com/aurora/extensions/Platform.kt
public class PlatformExtension {
    static Boolean isHuawei() {
        return Build.MANUFACTURER.toLowerCase(Locale.getDefault()).contains("huawei")
            || Build.HARDWARE.toLowerCase(Locale.getDefault()).contains("kirin")
            || Build.HARDWARE.toLowerCase(Locale.getDefault()).contains("hi3");
    }
}
