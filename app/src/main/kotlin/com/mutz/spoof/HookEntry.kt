package com.mutz.spoof

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookEntry : IXposedHookLoadPackage {

    // Daftar properti SoC dan device Galaxy S24 Ultra (Snapdragon 8 Gen 3)
    private val spoofedProps = mapOf(
        // Device & Brand
        "ro.product.manufacturer" to "samsung",
        "ro.product.brand" to "samsung",
        "ro.product.model" to "SM-S928B",
        "ro.product.name" to "gts9ultexx",
        "ro.product.device" to "gts9ultra",
        // Snapdragon 8 Gen 3 properties
        "ro.hardware.chipset" to "Snapdragon 8 Gen 3",
        "ro.hardware.chipname" to "SM8650-AC",
        "ro.chipname" to "SM8650-AC",
        "ro.soc.model" to "SM8650",
        "ro.soc.manufacturer" to "Qualcomm",
        "ro.vendor.soc.model.external_name" to "SM8650-AC",
        "ro.vendor.soc.model.part_name" to "SM8650-AC"
    )

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {

        try {
            val clazz = XposedHelpers.findClass("android.os.SystemProperties", lpparam.classLoader)

            // Hook get(String key)
            XposedHelpers.findAndHookMethod(clazz, "get", String::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val key = param.args[0] as? String ?: return
                    if (spoofedProps.containsKey(key)) {
                        param.result = spoofedProps[key]
                        XposedBridge.log("SpoofModule: [Java] Spoofed SystemProperties.get(\"$key\") = \"${spoofedProps[key]}\"")
                    }
                }
            })

            // Hook get(String key, String def)
            XposedHelpers.findAndHookMethod(clazz, "get", String::class.java, String::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val key = param.args[0] as? String ?: return
                    if (spoofedProps.containsKey(key)) {
                        param.result = spoofedProps[key]
                        XposedBridge.log("SpoofModule: [Java] Spoofed SystemProperties.get(\"$key\", def) = \"${spoofedProps[key]}\"")
                    }
                }
            })

            XposedBridge.log("SpoofModule: Java SystemProperties hook applied!")
        } catch (e: Throwable) {
            XposedBridge.log("SpoofModule: Failed to hook SystemProperties – ${e.message}")
        }
    }
}