package com.rtm516.propertiesdumper;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.FeatureInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

// From https://gitlab.com/AuroraOSS/AuroraStore/-/blob/b34e14e13a8b4dbbe9f1303b166e3a6b65e85e97/app/src/main/java/com/aurora/store/data/providers/NativeDeviceInfoProvider.kt
public class NativeDeviceInfoProvider extends ContextWrapper {
    public NativeDeviceInfoProvider(Context base) {
        super(base);
    }

    public Properties getNativeDeviceProperties() {
        Properties properties = new Properties();

        properties.setProperty("UserReadableName", Build.DEVICE + "-default");
        properties.setProperty("Build.HARDWARE", Build.HARDWARE);
        properties.setProperty("Build.RADIO", Build.getRadioVersion() != null ? Build.getRadioVersion() : "unknown");
        properties.setProperty("Build.FINGERPRINT", Build.FINGERPRINT);
        properties.setProperty("Build.BRAND", Build.BRAND);
        properties.setProperty("Build.DEVICE", Build.DEVICE);
        properties.setProperty("Build.VERSION.SDK_INT", String.valueOf(Build.VERSION.SDK_INT));
        properties.setProperty("Build.VERSION.RELEASE", Build.VERSION.RELEASE);
        properties.setProperty("Build.MODEL", Build.MODEL);
        properties.setProperty("Build.MANUFACTURER", Build.MANUFACTURER);
        properties.setProperty("Build.PRODUCT", Build.PRODUCT);
        properties.setProperty("Build.ID", Build.ID);
        properties.setProperty("Build.BOOTLOADER", Build.BOOTLOADER);

        Configuration config = getApplicationContext().getResources().getConfiguration();
        properties.setProperty("TouchScreen", String.valueOf(config.touchscreen));
        properties.setProperty("Keyboard", String.valueOf(config.keyboard));
        properties.setProperty("Navigation", String.valueOf(config.navigation));
        properties.setProperty("ScreenLayout", String.valueOf(config.screenLayout & 15));
        properties.setProperty("HasHardKeyboard", String.valueOf(config.keyboard == Configuration.KEYBOARD_QWERTY));
        properties.setProperty("HasFiveWayNavigation", String.valueOf(config.navigation == Configuration.NAVIGATIONHIDDEN_YES));

        //Display Metrics
        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        properties.setProperty("Screen.Density", String.valueOf(metrics.densityDpi));
        properties.setProperty("Screen.Width", String.valueOf(metrics.widthPixels));
        properties.setProperty("Screen.Height", String.valueOf(metrics.heightPixels));


        //Supported Platforms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            properties.setProperty("Platforms", TextUtils.join(",", Build.SUPPORTED_ABIS));
        } else {
            List<String> platform = new ArrayList<>();
            if (!TextUtils.isEmpty(Build.CPU_ABI)) {
                platform.add(Build.CPU_ABI);
            }
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                platform.add(Build.CPU_ABI2);
            }

            properties.setProperty("Platforms", TextUtils.join(",", platform));
        }
        //Supported Features
        properties.setProperty("Features", TextUtils.join(",", getFeatures()));
        //Shared Locales
        properties.setProperty("Locales", TextUtils.join(",", getLocales()));
        //Shared Libraries
        properties.setProperty("SharedLibraries", TextUtils.join(",", getSharedLibraries()));
        //GL Extensions
        ActivityManager activityManager = (ActivityManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        properties.setProperty("GL.Version", String.valueOf(activityManager.getDeviceConfigurationInfo().reqGlEsVersion));
        properties.setProperty("GL.Extensions", TextUtils.join(",", EglExtensionProvider.eglExtensions));

        //Google Related Props
        NativeGsfVersionProvider gsfVersionProvider = new NativeGsfVersionProvider(getApplicationContext());
        properties.setProperty("Client", "android-google");
        properties.setProperty("GSF.version", String.valueOf(gsfVersionProvider.getGsfVersionCode(true)));
        properties.setProperty("Vending.version", String.valueOf(gsfVersionProvider.getVendingVersionCode(true)));
        properties.setProperty("Vending.versionString", gsfVersionProvider.getVendingVersionString(true));

        //MISC
        properties.setProperty("Roaming", "mobile-notroaming");
        properties.setProperty("TimeZone", "UTC-10");

        //Telephony (USA 3650 AT&T)
        properties.setProperty("CellOperator", "310");
        properties.setProperty("SimOperator", "38");

        if (PlatformExtension.isHuawei())
            stripHuaweiProperties(properties);

        return properties;
    }

    private List<String> getFeatures() {
        List<String> featureStringList = new ArrayList<>();
        try {
            FeatureInfo[] availableFeatures = getApplicationContext().getPackageManager().getSystemAvailableFeatures();
            for (FeatureInfo feature : availableFeatures) {
                if (!feature.name.isEmpty()) {
                    featureStringList.add(feature.name);
                }
            }
        } catch (Exception ignored) {

        }
        return featureStringList;
    }

    private List<String> getLocales() {
        List<String> localeList = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            localeList.addAll(Arrays.asList(getApplicationContext().getAssets().getLocales()));
        } else {
            for (Locale locale : Locale.getAvailableLocales()) {
                localeList.add(locale.toString());
            }
        }
        List<String> locales = new ArrayList<>();
        for (String locale : localeList) {
            if (TextUtils.isEmpty(locale)) {
                continue;
            }
            locales.add(locale.replace("-", "_"));
        }
        return locales;
    }

    private List<String> getSharedLibraries() {
        String[] systemSharedLibraryNames = getApplicationContext().getPackageManager().getSystemSharedLibraryNames();
        List<String> libraries = new ArrayList<>();
        try {
            if (systemSharedLibraryNames != null) {
                libraries.addAll(Arrays.asList(systemSharedLibraryNames));
            }
        } catch (Exception ignored) {

        }
        return libraries;
    }

    private Properties stripHuaweiProperties(Properties properties) {
        //Add i-Phoney properties
        properties.setProperty("Build.HARDWARE", "unknown");
        properties.setProperty("Build.BOOTLOADER", "unknown");
        properties.setProperty("Build.BRAND", "PassionFruit");
        properties.setProperty("Build.DEVICE", "ProPlus5GFold");
        properties.setProperty("Build.MODEL", "iPhoney");
        properties.setProperty("Build.MANUFACTURER", "PassionFruit");
        properties.setProperty("Build.PRODUCT", "iPhoney_24");
        properties.setProperty("Build.ID", "ABC.123");
        return properties;
    }

}
