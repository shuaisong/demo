package com.ysh.demo.ui.fragment

import android.os.Bundle
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleFragment
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.databinding.FragmentHomeBinding
import com.ysh.demo.event.EventMessage
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class FragmentHome : SimpleFragment<SimpleViewModel, FragmentHomeBinding>() {

    override fun layoutId(): Int {
        return R.layout.fragment_home
    }

    override fun lazyLoadData() {
    }

    override fun handleEvent(msg: Message) {
    }

    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(msg: EventMessage) {

    }

}