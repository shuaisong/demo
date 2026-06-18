package com.ysh.demo

import android.app.Application
import android.content.Context
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.LogUtils
import com.drake.brv.utils.BRV
import com.drake.statelayout.StateConfig
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.tencent.mmkv.MMKV
import com.ysh.demo.util.CrashHandler
import java.io.File

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        mainApplication = this
        initRefresh()
        MMKV.initialize(this)
        initBrv()

        CrashHandler.init(this)

        initLog()
    }

    private fun initBrv() {
        BRV.modelId = BR.model
        StateConfig.emptyLayout = R.layout.layout_empty
    }

    private fun initRefresh() {
        SmartRefreshLayout.setDefaultRefreshInitializer { _: Context?, layout: RefreshLayout ->
            layout.setPrimaryColorsId(android.R.color.transparent)
            layout.setHeaderHeight(35f)
        }
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, _: RefreshLayout? ->
            MaterialHeader(context)
        }
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, _: RefreshLayout? ->
            ClassicsFooter(context)
        }
    }


    private fun initLog() {
        LogUtils.getConfig().setDir(File(externalCacheDir, "log")).setLog2FileSwitch(true).setSaveDays(12).addFileExtraHead("UniqueDeviceId",
            DeviceUtils.getUniqueDeviceId()).filePrefix = "log"
    }

    companion object {
        @JvmStatic lateinit var mainApplication: MainApplication

        @JvmStatic val instance: Application
            get() = mainApplication
    }
}