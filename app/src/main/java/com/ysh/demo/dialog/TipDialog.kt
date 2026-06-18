package com.ysh.demo.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.ysh.demo.R
import com.ysh.demo.databinding.DialogTipBinding
import me.jessyan.autosize.utils.AutoSizeUtils
import razerdp.basepopup.BasePopupWindow

open class TipDialog(context: Context) : BasePopupWindow(context) {
    protected val binding: DialogTipBinding

    init {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_tip, null,
            false)
        contentView = binding.root
        width = AutoSizeUtils.dp2px(context, 260f)
        popupGravity = Gravity.CENTER
    }


    fun setMsg(msg: String) {
        binding.tvTip.text = msg
    }

    fun setTextColor(@ColorRes color: Int) {
        binding.tvTip.setTextColor(ContextCompat.getColor(context, color))
    }

    //isBackPressEnable 设置返回按键是否可用
    override fun onBackPressed(): Boolean {
        findViewById<View>(R.id.button_cancel).performClick()
        return super.onBackPressed()
    }
}