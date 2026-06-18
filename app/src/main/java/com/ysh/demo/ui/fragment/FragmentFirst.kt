package com.ysh.demo.ui.fragment

import android.os.Bundle
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleFragment
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.databinding.FragmentFirstBinding
import com.ysh.demo.extend.init
import com.ysh.demo.extend.initMain
import com.ysh.demo.extend.interceptLongClick

class FragmentFirst : SimpleFragment<SimpleViewModel, FragmentFirstBinding>() {

    override fun layoutId(): Int {
        return R.layout.fragment_first
    }

    override fun handleEvent(msg: Message) {
    }

    override fun createObserver() {

    }

    override fun dismissLoading() {
    }

    override fun initView(savedInstanceState: Bundle?) {
        //初始化viewpager2
        mDatabind.mainViewpager.initMain(this)
        //初始化 bottomBar
        mDatabind.bottomNavigationView.init {
            when (it) {
                R.id.home -> mDatabind.mainViewpager.setCurrentItem(0, false)
                R.id.mine -> mDatabind.mainViewpager.setCurrentItem(1, false)
            }
        }
        mDatabind.bottomNavigationView.interceptLongClick(R.id.home, R.id.mine)
    }


    override fun lazyLoadData() {
    }

}