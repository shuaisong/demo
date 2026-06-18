package com.ysh.demo.base

import android.os.Bundle
import android.view.View
import androidx.databinding.ViewDataBinding
import com.blankj.utilcode.util.BarUtils
import com.ysh.demo.dialog.LoadingDialog
import me.hgj.jetpackmvvm.base.fragment.BaseVmDbFragment

abstract class SimpleFragment<VM : SimpleViewModel, DB : ViewDataBinding> :
    BaseVmDbFragment<VM, DB>() {
    private var dialog: LoadingDialog? = null


    var viewCreated = false
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        if (viewCreated) return
        super.onViewCreated(view, savedInstanceState)
        registerDefMsgChange()
        viewCreated = true
    }

    private fun registerDefMsgChange() {
        mViewModel.msgEvent.observeInFragment(this) {
            handleEvent(it)
        }
    }

    override fun showLoading(message: String) {
        (dialog ?: LoadingDialog(this)).apply {
            if (message.isNotEmpty()) {
                setMsg(message)
            }
        }.showPopupWindow()
    }

    override fun dismissLoading() {
        dialog?.dismiss()
    }

    protected open fun handleEvent(msg: Message) {}


    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun lazyLoadData() {

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
}