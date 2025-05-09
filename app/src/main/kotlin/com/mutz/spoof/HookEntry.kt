package com.mutz.spoof

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class HookEntry : IXposedHookLoadPackage {
    // Deklarasi fungsi native
    external fun checkHookStatus(): Boolean
    external fun forceRefreshHook(): Boolean
    external fun dumpSystemProperties()
    
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // Load native .so
        try {
            System.loadLibrary("spoof")
            XposedBridge.log("SpoofModule: Native lib loaded")
            
            // Cek status hook native
            val hookActive = checkHookStatus()
            if (hookActive) {
                XposedBridge.log("SpoofModule: Native hook berhasil diaktifkan")
                
                // Dump properti sistem untuk debugging jika perlu
                // dumpSystemProperties()
            } else {
                XposedBridge.log("SpoofModule: Native hook belum aktif, mencoba refresh")
                val refreshResult = forceRefreshHook()
                XposedBridge.log("SpoofModule: Hasil refresh hook: ${if(refreshResult) "sukses" else "gagal"}")
            }
        } catch (e: Throwable) {
            XposedBridge.log("SpoofModule: Failed to load native lib – ${e.message}")
        }

        // Spoof Build.* fields (masih perlu karena ini Java static field)
        val spoofProps = mapOf(
            "BRAND" to "samsung",
            "MANUFACTURER" to "samsung",
            "DEVICE" to "dm3q",
            "PRODUCT" to "dm3qxx", 
            "MODEL" to "SM-S928B",
            "FINGERPRINT" to "samsung/dm3qxx/dm3q:14/UP1A.231005.007/S928BXXU1AXB5:user/release-keys",
            "BOARD" to "kalama",
            "HARDWARE" to "qcom",
            "DISPLAY" to "UP1A.231005.007",
            "ID" to "UP1A.231005.007",
            "TAGS" to "release-keys",
            "TYPE" to "user",
            "USER" to "dpi",
            "HOST" to "21DJB"
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