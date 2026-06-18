package com.ysh.demo.ui.fragment

import android.os.Bundle
import android.view.View
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleFragment
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.databinding.FragmentMineBinding

class MineFragment : SimpleFragment<SimpleViewModel, FragmentMineBinding>(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.fragment_mine
    }

    override fun lazyLoadData() {
    }


    override fun handleEvent(msg: Message) {

    }

    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
    }

    override fun onClick(v: View) {
    }

}