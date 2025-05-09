package com.mutz.spoof

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
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
        
        // Bagian hook untuk Build.* fields telah dihapus
        // Semua operasi spoofing sekarang dilakukan di native code (.so)
        
        XposedBridge.log("SpoofModule: Menggunakan hook native saja, hook Java/Kotlin dihapus")
    }
}