package com.rtm516.propertiesdumper;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

// From https://gitlab.com/AuroraOSS/AuroraStore/-/blob/b34e14e13a8b4dbbe9f1303b166e3a6b65e85e97/app/src/main/java/com/aurora/store/data/providers/NativeGsfVersionProvider.kt
public class NativeGsfVersionProvider {
    private final String GOOGLE_SERVICES_PACKAGE_ID = "com.google.android.gms";
    private final String GOOGLE_VENDING_PACKAGE_ID = "com.android.vending";
    private final int GOOGLE_SERVICES_VERSION_CODE = 203019037;
    private final int GOOGLE_VENDING_VERSION_CODE = 82151710;
    private final String GOOGLE_VENDING_VERSION_STRING = "21.5.17-21 [0] [PR] 326734551";

    private int gsfVersionCode = 0;
    private int vendingVersionCode = 0;
    private String vendingVersionString = "";

    private Context context;

    public NativeGsfVersionProvider(Context applicationContext) {
        this.context = applicationContext;

        init1();
        init2();
    }

    private void init1() {
        try {
            gsfVersionCode = context.getPackageManager().getPackageInfo(GOOGLE_SERVICES_PACKAGE_ID, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // com.google.android.gms not found
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(GOOGLE_VENDING_PACKAGE_ID, 0);
            vendingVersionCode = packageInfo.versionCode;
            vendingVersionString = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // com.android.vending not found
        }
    }

    private void init2() {
        try {
            gsfVersionCode = context.getPackageManager().getPackageInfo(GOOGLE_SERVICES_PACKAGE_ID, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // com.google.android.gms not found
        }
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(GOOGLE_VENDING_PACKAGE_ID, 0);
            vendingVersionCode = packageInfo.versionCode;
            vendingVersionString = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // com.android.vending not found
        }
    }

    public int getGsfVersionCode(Boolean defaultIfNotFound) {
        return (defaultIfNotFound && gsfVersionCode < GOOGLE_SERVICES_VERSION_CODE) ? GOOGLE_SERVICES_VERSION_CODE : gsfVersionCode;
    }

    public int getVendingVersionCode(Boolean defaultIfNotFound) {
        return (defaultIfNotFound && vendingVersionCode < GOOGLE_VENDING_VERSION_CODE) ? GOOGLE_VENDING_VERSION_CODE : vendingVersionCode;
    }

    public String getVendingVersionString(Boolean defaultIfNotFound) {
        return (defaultIfNotFound && vendingVersionCode < GOOGLE_VENDING_VERSION_CODE) ? GOOGLE_VENDING_VERSION_STRING : vendingVersionString;
    }
}
