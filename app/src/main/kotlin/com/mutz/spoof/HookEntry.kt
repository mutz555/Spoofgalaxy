package com.mutz.spoof

import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook

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
            "BOARD" to "kalama",
            "HARDWARE" to "qcom",
            "DISPLAY" to "UP1A.231005.007",
            "ID" to "UP1A.231005.007",
            "TAGS" to "release-keys",
            "TYPE" to "user",
            "USER" to "dpi",
            "HOST" to "21DJB"
        )

        // Spoof Build fields
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

        // Spoof Android version
        try {
            XposedHelpers.setStaticIntField(Build.VERSION::class.java, "SDK_INT", 34)
            XposedHelpers.setStaticObjectField(Build.VERSION::class.java, "RELEASE", "14")
            XposedBridge.log("SpoofModule: Set SDK_INT to 34 and RELEASE to 14")
        } catch (e: Throwable) {
            XposedBridge.log("SpoofModule: Failed to spoof Build.VERSION – ${e.message}")
        }

        // SOC spoof for Android 12+
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val socModelField = XposedHelpers.findField(Build::class.java, "SOC_MODEL")
                socModelField.isAccessible = true
                socModelField.set(null, "SM8650")

                val socManufacturerField = XposedHelpers.findField(Build::class.java, "SOC_MANUFACTURER")
                socManufacturerField.isAccessible = true
                socManufacturerField.set(null, "Qualcomm Technologies, Inc.")

                XposedBridge.log("SpoofModule: Set SOC_MODEL to SM8650 and SOC_MANUFACTURER to Qualcomm Technologies, Inc.")
            }
        } catch (e: Throwable) {
            XposedBridge.log("SpoofModule: Failed to spoof SOC fields – ${e.message}")
        }

        // Hook SystemProperties.get
        try {
            val sysPropClass = XposedHelpers.findClass("android.os.SystemProperties", lpparam.classLoader)

            XposedHelpers.findAndHookMethod(sysPropClass, "get", String::class.java, String::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val key = param.args[0] as String
                        val spoofed = mapOf(
                            // Begin: Inserted from system.prop.txt
                            "ro.product.brand" to "samsung",
                            "ro.product.manufacturer" to "samsung",
                            "ro.product.device" to "e3q",
                            "ro.product.model" to "SM-S928U1",
                            "ro.product.odm.model" to "SM-S928U1",
                            "ro.product.vendor.model" to "SM-S928U1",
                            "ro.product.system.model" to "SM-S928U1",
                            "ro.product.system_ext.model" to "SM-S928U1",
                            "ro.product.marketname" to "Galaxy S24 Ultra US Unlocked",
                            "ro.product.name" to "Galaxy S24 Ultra US Unlocked",
                            "debug.tracing.block_touch_buffer" to "1",
                            "debug.touchscreen.latency.scale" to "0.5",
                            "debug.touchscreen.input_delay" to "10",
                            "debug.touchscreen.speed_mode" to "1",
                            "debug.touchscreen.latency_scale" to "0.5",
                            "debug.performance.animations" to "false",
                            "debug.touchscreen.ultra_fast" to "1",
                            "persist.sys.ui.config" to "multiTouch",
                            "ro.config.touchscreen.max.fingers" to "10",
                            "persist.sys.touchscreen.sensitivity" to "100",
                            "debug.touchscreen.timing" to "1",
                            "persist.sys.touchscreen.test_mode" to "0",
                            "debug.touchscreen.enable_tslinux" to "1",
                            "persist.sys.touchscreen.downgrade_mode" to "0",
                            "dev.pm.dyn_samplingrate" to "true",
                            "persist.sys.anim.scale" to "0",
                            "persist.sys.window_animation_scale" to "0",
                            "persist.sys.transition_animation_scale" to "0",
                            "persist.sys.input_delay" to "5",
                            "persist.sys.touchscreen.optimization" to "true",
                            "persist.sys.touchscreen.boost" to "1",
                            "debug.vsync.max_fps" to "120",
                            "debug.cpu.performance_mode" to "1",
                            "debug.cpu.performance_boost" to "true",
                            "debug.cpu_perf_event_sampling" to "true",
                            "debug.cpu_perf_event_sampling_rate" to "250",
                            "debug.cpu_perf_event_max_sample_rate" to "250",
                            "debug.snapdragon.fps_boost" to "1",
                            "debug.snapdragon.performance_mode" to "1",
                            "debug.memory.optimization" to "1",
                            "debug.memory_perf_event_sampling" to "true",
                            "debug.fps" to "120",
                            "debug.fps.limit" to "120",
                            "debug.frame_rate.max" to "120",
                            "debug.frame_rate.min" to "120",
                            "debug.frame_rate_smoothing" to "true",
                            "debug.frame_rate.vsync" to "0",
                            "debug.frame_rate.adaptive" to "1",
                            "debug.fps.target" to "120",
                            "debug.fps.max" to "120",
                            "debug.fps.min" to "120",
                            "debug.fps.control" to "false",
                            "debug.fps.optimization" to "true",
                            "debug.thermal.refresh_rate" to "120",
                            "debug.thermal.refresh_rate_limit" to "120",
                            "debug.thermal.dynamic_control" to "false",
                            "debug.thermal.max_fps" to "120",
                            "debug.thermal.min_fps" to "120",
                            "debug.renderthread.framerate" to "120",
                            "debug.render.fps" to "120",
                            "debug.render.detail_fps" to "120",
                            "debug.render.smooth_fps" to "1",
                            "sys.fps_unlock_allowed" to "120",
                            "sys.fps.default" to "120",
                            "sys.fps.override" to "120",
                            "sys.fps.min" to "120",
                            "sys.fps.max" to "120",
                            "sys.fps.dynamic_range" to "120",
                            "sys.thermal.refresh_rate" to "120",
                            "sys.thermal.max_fps" to "120",
                            "sys.thermal.performance_mode" to "1",
                            "sys.thermal.cooling_mode" to "1",
                            "sys.touch.sampling_rate" to "250",
                            "sys.touchscreen.max_sampling_rate" to "250",
                            "sys.touchscreen.min_sampling_rate" to "250",
                            "sys.touchscreen.sampling_resolution" to "250",
                            "sys.display.refresh_rate" to "120",
                            "sys.display.dynamic_refresh_rate" to "false",
                            "sys.display.refresh_rate_limit" to "120",
                            "sys.touch.refresh_rate" to "120",
                            "sys.touch.max_sampling_rate" to "250",
                            "sys.touch.min_sampling_rate" to "250",
                            "sys.touch.sensitivity" to "1",
                            "sys.touch.haptic_feedback" to "true",
                            "persist.sys.fps_limit" to "120",
                            "persist.sys.max_fps" to "120",
                            "persist.sys.frame_rate_smoothing" to "true",
                            "persist.sys.frame_rate.vsync" to "0",
                            "persist.sys.frame_rate.adaptive" to "1",
                            "persist.sys.thermal.refresh_rate" to "120",
                            "persist.sys.thermal.max_refresh_rate" to "120",
                            "persist.sys.thermal.performance_boost" to "true",
                            "persist.sys.thermal.performance_mode" to "1",
                            "persist.sys.touch.sampling_rate" to "120",
                            "persist.sys.touchscreen.max_sampling_rate" to "250",
                            "persist.sys.touchscreen.min_sampling_rate" to "250",
                            "persist.sys.display.dynamic_refresh_rate" to "false",
                            "persist.sys.display.max_refresh_rate" to "120",
                            "persist.sys.display.refresh_rate_override" to "120",
                            "persist.sys.touch.refresh_rate" to "250",
                            "persist.sys.touch.sampling_rate" to "250",
                            "persist.sys.touch.sensitivity" to "1",
                            "persist.sys.touch.sampling_resample_rate" to "250",
                            "persist.sys.touchscreen.sampling_rate" to "250",
                            "persist.sys.display.refresh_rate" to "120",
                            "persist.game.minfps" to "120",
                            "persist.game.maxfps" to "120",
                            "debug.fps.cap" to "120",
                            "debug.gpu.fps.cap" to "120",
                            "debug.frame_rate.cap" to "120",
                            "debug.frame_rate.max" to "120",
                            "debug.display.refresh_rate.cap" to "60",
                            "debug.display.refresh_rate.limit" to "90",
                            "debug.touch.sampling_rate.cap" to "250",
                            "debug.touchscreen.sampling_rate.cap" to "250",
                            "debug.gpu.performance_cap" to "80",
                            "debug.cpu.performance_cap" to "75",
                            "debug.gpu.overclock" to "1",
                            "debug.gpu.hw_acceleration" to "1",
                            "debug.gpu.ui_render" to "1",
                            "debug.gpu.ui_refresh_rate" to "120",
                            "debug.gpu.performance_mode" to "1",
                            "debug.gpu.refresh_rate" to "120",
                            "debug.gpu.fps_boost" to "1",
                            "debug.renderthread.framerate" to "120",
                            "debug.hwui.fps_divisor" to "-1",
                            "debug.hwui.force_refresh_rate" to "120",
                            "debug.hwui.refresh_rate_forced" to "120",
                            "debug.hwui.profile.maxframes" to "120",
                            "debug.hwui.min_fps" to "120",
                            "debug.hwui.frame_rate_multiple" to "120",
                            "debug.hwui.max_frames" to "120",
                            "debug.hwui.detail_fps" to "120",
                            "debug.hwui.animation_fps" to "120",
                            "debug.touch.refresh_rate" to "250",
                            "debug.touch.sync" to "false",
                            "debug.input.touch_sampling_rate" to "250",
                            "debug.input.touch_refresh_rate" to "120",
                            "debug.input.touch_sensitivity" to "1",
                            "debug.touch.sampling_rate" to "250",
                            "debug.touch.resample_rate" to "250",
                            "debug.touchscreen.sampling_rate" to "250",
                            "debug.touchscreen.max_sampling_rate" to "250",
                            "debug.touchscreen.min_sampling_rate" to "250",
                            "debug.touch.sampling_rate_max" to "250",
                            "debug.touch.sampling_rate_min" to "250",
                            "debug.touch.sampling_event_rate" to "250",
                            "debug.touch.sample_interval" to "2ms",
                            "debug.egl.render_fps" to "120",
                            "debug.egl.refresh_rate" to "120",
                            "debug.egl.fps" to "120",
                            "debug.egl.framerate" to "120",
                            "debug.egl.sampling_rate" to "250",
                            "debug.egl.detail_fps" to "120",
                            "debug.sf.fps" to "120",
                            "debug.sf.frame_rate" to "120",
                            "debug.sf.hw_rotation_fps_divisor" to "-1",
                            "debug.sf.120hz_mode" to "1",
                            "debug.sf.hwui.fps" to "120",
                            "debug.sf.hwui.framerate" to "120",
                            "debug.sf.hwui.refresh_rate" to "120",
                            "debug.sf.hwui.sampling_rate" to "250",
                            "debug.sf.hwui.render_fps" to "120",
                            "debug.sf.hwui.update_rate" to "120",
                            "debug.sf.hwui.detail_fps" to "120",
                            "debug.sf.hwui_refresh_rate" to "120",
                            "debug.sf.hwui_max_fps" to "120",
                            "debug.sf.hwui_min_fps" to "120",
                            "debug.sf.hwui_force_refresh_rate" to "120",
                            "debug.sf.hwui_frame_rate" to "120",
                            "debug.sf.hwui_frame_rate_multiple" to "120",
                            "debug.sf.hwui_fps_boost" to "120",
                            "debug.sf.hwui_fps_throttle" to "120",
                            "debug.sf.frame_rate_multiple_fences" to "120",
                            "debug.sf_frame_rate_multiple_fences" to "120",
                            "debug.sf.frame_rate_multiple_threshold" to "120",
                            "debug.sf.late_vsync_threshold" to "120",
                            "debug.gr.render_fps" to "120",
                            "debug.gr.fps" to "120",
                            "debug.gr.framerate" to "120",
                            "debug.gr.refresh_rate" to "120",
                            "debug.gr.sampling_rate" to "250",
                            "debug.gr.render_fps" to "120",
                            "debug.gr.update_rate" to "120",
                            "debug.gr.detail_fps" to "120",
                            "debug.display.min_refresh_rate" to "120",
                            "debug.display.peak_refresh_rate" to "120",
                            "debug.display.refresh_rate_min" to "120",
                            "debug.display.refresh_rate_max" to "120",
                            "debug.display.refresh_rate_for_fps_boost" to "120",
                            "debug.display.max_refresh_rate" to "120",
                            "debug.display.allow_non_native_refresh_rate_override" to "0",
                            "debug.display.refresh_rate" to "120",
                            "debug.display.dynamic_refresh_rate" to "false",
                            "debug.javafx.animation.fullspeed" to "1",
                            "debug.javafx.animation.framerate" to "120",
                            "debug.mediatek.high_frame_rate_sf_set_big_core_fps_threshold" to "120",
                            "debug.OVRManager.cpuLevel" to "2",
                            "debug.OVRManager.gpuLevel" to "2",
                            "debug.OVRPlugin.systemDisplayFrequency" to "120",
                            "debug.frame_rate.target" to "120",
                            "debug.frame_rate.sync" to "false",
                            "debug.frame_rate.limit" to "true",
                            "debug.frame_rate.dynamic_scaling" to "false",
                            "debug.frame_rate.frame_pacing" to "true",
                            "debug.frame_rate.gpu_tuning" to "true",
                            "debug.frame_rate_sample_rate" to "250",
                            "debug.frame_rate_max_sample_rate" to "250",
                            "debug.frame_rate_dynamic_sample" to "false",
                            "debug.frame_rate_event_sampling" to "true",
                            "debug.perf_event_max_sample_rate" to "250",
                            "debug.perf_event_sample_rate" to "250",
                            "debug.perf_event_sampling_enabled" to "true",
                            "debug.perf_event_max_buffer_size" to "4096",
                            "debug.perf_event_enable_all" to "true",
                            "debug.perf_event_enable_vsync" to "false",
                            "debug.refresh_rate.view_override" to "1",
                            "debug.refresh_rate.min_fps" to "120",
                            "debug.refresh_rate.max_fps" to "120",
                            "debug.refresh_rate.dynamic" to "false",
                            "debug.refresh_rate.max_fps_lock" to "120",
                            "debug.refresh_rate.min_fps_lock" to "120",
                            "debug.refresh_rate.motion_smoothing" to "true",
                            "debug.refresh_rate.vsync" to "false",
                            "debug.refresh_rate.gpu_sync" to "false",
                            "debug.refresh_rate.vsync_offset" to "0",
                            "debug.refresh_rate.cpu_sync" to "false",
                            "debug.redroid.fps" to "120",
                            "debug.stagefright.fps" to "120",
                            "debug.fps.render.fast" to "1",
                            "debug.game.fps_limit" to "120",
                            "debug.game.framebuffer_rate" to "120",
                            "debug.game.frame_rate_smoothing" to "1",
                            "debug.game.enable_vsync" to "false",
                            "debug.game.max_fps" to "120",
                            "debug.game.force_gpu_rendering" to "true",
                            "debug.game.enable_advanced_graphics" to "true",
                            "debug.game.high_performance_mode" to "true",
                            "debug.game.use_custom_rendering" to "true",
                            "debug.game.disable_thermal_throttling" to "true",
                            "debug.game.optimize_gpu" to "true",
                            "debug.display.dynamic_refresh_rate" to "false",
                            "debug.display.fixed_refresh_rate" to "120",
                            "ro.soc.manufacturer" to "Qualcomm",
                            "ro.hardware.chipname" to "SM8650-AC",
                            "ro.chipname" to "SM8650-AC",
                            "ro.soc.model" to "SM8650",
                            "ro.vendor.soc.model.external_name" to "SM8650-AC",
                            "ro.vendor.soc.model.part_name" to "SM8650-AC",
                            "debug.sf.showupdates" to "0",
                            "debug.sf.showcpu" to "0",
                            "debug.sf.showbackground" to "0",
                            "debug.sf.showfps" to "0",
                            "ro.fps_enable" to "0",
                            "ro.fps.capsmin" to "120",
                            "ro.fps.capsmax" to "120",
                            "cpu.fps" to "auto",
                            "gpu.fps" to "auto",
                            "boot.fps" to "60",
                            "shutdown.fps" to "60"
                            // End: Inserted from system.prop.txt
                        )
                        spoofed[key]?.let {
                            param.result = it
                            XposedBridge.log("SpoofModule: SystemProperties.get($key) -> $it")
                        }
                    }
                })
        } catch (e: Throwable) {
            XposedBridge.log("SpoofModule: Failed to hook SystemProperties.get – ${e.message}")
        }
    }
}