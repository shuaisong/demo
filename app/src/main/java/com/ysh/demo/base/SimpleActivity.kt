package com.ysh.demo.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.BarUtils
import com.blankj.utilcode.util.DeviceUtils
import com.ysh.demo.R
import com.ysh.demo.dialog.LoadingDialog
import me.hgj.jetpackmvvm.base.activity.BaseVmDbActivity
import me.jessyan.autosize.internal.CustomAdapt


abstract class SimpleActivity<VM : SimpleViewModel, DB : ViewDataBinding> :
    BaseVmDbActivity<VM, DB>(), CustomAdapt {
    private var dialog: LoadingDialog? = null

    fun setStatusBarLightMode(isLightMode: Boolean) {
        BarUtils.setStatusBarLightMode(this, isLightMode)
    }


    override fun showLoading(message: String) {
        if (dialog == null) dialog = LoadingDialog(this)
        dialog!!.apply {
            if (message.isNotEmpty()) {
                setMsg(message)
            }
        }.showPopupWindow()
    }

    override fun dismissLoading() {
        dialog?.run { if (isShowing) dismiss() }
    }

    fun whiteStatusBar() {
        //设置状态栏为亮色
        BarUtils.setStatusBarLightMode(this, true)
    }

    fun transparentStatusBar() {
        //设置状态栏颜色为透明
        BarUtils.transparentStatusBar(this)
        //设置状态栏为亮色
        BarUtils.setStatusBarLightMode(this, true)
    }

    /**
     * 目标view加paddingTop
     * @param view View
     */
    fun setStatusBarHeight(view: View) {
        var paddingTop = view.paddingTop
        paddingTop += BarUtils.getStatusBarHeight()
        view.run {
            setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
        }
    }

    protected fun getOverridePendingTransitionMode(): TransitionMode {
        return TransitionMode.RIGHT
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerDefMsgChange()
        initData()
    }

    protected fun overridePendingTransition() {
        when (getOverridePendingTransitionMode()) {
            TransitionMode.RIGHT -> overridePendingTransition(R.anim.right_in, R.anim.left_out)
            TransitionMode.TOP -> overridePendingTransition(R.anim.top_in, R.anim.top_out)
            else -> {}
        }
    }

    private fun registerDefMsgChange() {
        mViewModel.msgEvent.observeInActivity(this) {
            handleEvent(it)
        }
    }

    override fun isBaseOnWidth(): Boolean {
        return true
    }

    override fun getSizeInDp(): Float {
        return if (DeviceUtils.isTablet()) {
            618f
        } else {
            375f
        }
    }

    abstract fun initData()

    abstract fun handleEvent(msg: Message)
}