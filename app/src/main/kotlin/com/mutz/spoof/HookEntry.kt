package com.mutz.spoof

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XposedBridge

class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        XposedBridge.log("SpoofModule: Hooked into ${lpparam.packageName}")

        val spoofProps = mapOf(
            "BRAND" to "samsung",
            "MANUFACTURER" to "samsung",
            "DEVICE" to "dm3q",
            "PRODUCT" to "dm3qxx",
            "MODEL" to "SM-S928B",
            "FINGERPRINT" to "samsung/dm3qxx/dm3q:14/UP1A.231005.007/S928BXXU1AXB5:user/release-keys",
            "BOARD" to "kalama"
        )

        spoofProps.forEach { (field, value) ->
            try {
                val buildField = XposedHelpers.findField(Build::class.java, field)
                buildField.isAccessible = true
                buildField.set(null, value)
                XposedBridge.log("SpoofModule: Set Build.$field to $value")
            } catch (e: Throwable) {
                XposedBridge.log("SpoofModule: Failed to set Build.$field – ${e.message}")
            }
        }

        try {
            XposedHelpers.setStaticIntField(Build.VERSION::class.java, "SDK_INT", 34)
            XposedHelpers.setStaticObjectField(Build.VERSION::class.java, "RELEASE", "14")
            XposedBridge.log("SpoofModule: Set SDK_INT to 34 and RELEASE to 14")
        } catch (e: Throwable) {
            XposedBridge.log("SpoofModule: Failed to spoof Build.VERSION – ${e.message}")
        }
    }
}