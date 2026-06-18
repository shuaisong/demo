package com.ysh.demo.dialog

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.ysh.demo.R
import com.ysh.demo.databinding.CustomProgressDialogViewBinding
import me.jessyan.autosize.utils.AutoSizeUtils
import razerdp.basepopup.BasePopupWindow

class LoadingDialog: BasePopupWindow {
    private val mBinding :CustomProgressDialogViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.custom_progress_dialog_view,null,false)

    constructor(context: Context):super(context){

    }
    constructor(fragment: Fragment) : super(fragment) {}
    init {
        contentView = mBinding.root
        width = AutoSizeUtils.dp2px(context,200f)
        popupGravity = Gravity.CENTER
    }
    fun setMsg(msg:String){
        mBinding.tvTip.text = msg
    }
}