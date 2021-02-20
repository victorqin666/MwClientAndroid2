package com.work.mw.wemeet

import android.app.Application
import android.content.Context
import com.tencent.feedback.anr.ANRReport
import com.tencent.feedback.eup.CrashReport
import com.tencent.wemeet.app.rdm.HotPatchUncaughtExceptionHandler
import com.tencent.wemeet.sdk.SdkCrashReport
import com.tencent.wemeet.sdk.VersionInfo
import com.tencent.wemeet.sdk.appSettings
import com.tencent.wemeet.sdk.appcommon.logDebug
import com.tencent.wemeet.sdk.util.DeviceUtil
import com.tencent.wemeet.sdk.wmp.PlatformExt

object RdmSDK {
    private const val TAG_FOR_NO_DEVICE_ID_VISITOR = 100

    fun init(application: Application) {
        // 初始化rash上报的配置
        val uid = getUid(
            application
        )
        CrashReport.setUserId(application, uid)
        // set rdm bugly product id
        CrashReport.setProductVersion(application, "${PlatformExt.appVersion}_${VersionInfo.BRANCH}")
        mapOf(
                "deviceName" to DeviceUtil.getDeviceName(),
                "git" to "${VersionInfo.BRANCH}_${VersionInfo.COMMIT_ID}_${VersionInfo.BUILD_ID}",
                "qci" to VersionInfo.QCI_BUILD_ID
        ).forEach {
            CrashReport.putUserData(application, it.key, it.value)
        }
        logDebug { "init CrashSDK uid=$uid" }
        ANRReport.startANRMonitor(application)

        // 真正初始化Rdm
        SdkCrashReport.initRqdHotfix(application)
        initNativeCrashReport(
            application
        )

        // 设置CrashHandler来做好保护逻辑
        // 设置UncaughtExceptionHandler必须在所有其他的UncaughtExceptionHandler设置后才能设置，否则其他的UncaughtExceptionHandler设置后会覆盖这里
        // 因为Bugly上报也使用了UncaughtExceptionHandler，所以这个语句必须在Bugly上报初始化后执行
        Thread.setDefaultUncaughtExceptionHandler(HotPatchUncaughtExceptionHandler())
    }

    private fun getUid(context: Context): String? {
        val deviceId = DeviceUtil.getDeviceId(context)
        val defaultUid = if (deviceId.isEmpty()) {
            null
        } else {
            "device_$deviceId"
        }
        return context.appSettings.getString("user_id", defaultUid)
    }

    private fun initNativeCrashReport(context: Context) {
        val tombDirectoryPath = context.getDir("tomb", Context.MODE_PRIVATE).absolutePath
        CrashReport.initNativeCrashReport(context, tombDirectoryPath, true)
    }
}
