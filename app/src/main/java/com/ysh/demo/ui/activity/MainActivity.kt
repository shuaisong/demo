package com.ysh.demo.ui.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.ToastUtils
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleActivity
import com.ysh.demo.databinding.ActivityMainBinding
import com.ysh.demo.event.EventMessage
import com.ysh.demo.viewmodel.MainViewModel
import me.hgj.jetpackmvvm.network.manager.NetState
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 项目主页Activity
 */
class MainActivity : SimpleActivity<MainViewModel, ActivityMainBinding>() {
    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    var exitTime = 0L
    override fun initView(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        transparentStatusBar()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val nav = Navigation.findNavController(this@MainActivity, R.id.host_fragment)
                if (nav.currentDestination != null && nav.currentDestination!!.id != R.id.fragmentFirst) {
                    //如果当前界面不是主页，那么直接调用返回即可
                    nav.navigateUp()
                } else {
                    //是主页
                    if (System.currentTimeMillis() - exitTime > 2000) {
                        ToastUtils.showShort("再按一次退出程序")
                        exitTime = System.currentTimeMillis()
                    } else {
                        finish()
                    }
                }
            }
        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(msg: EventMessage) {

    }

    override fun initData() {
        initSdk()
        getPermission()
    }

    private fun initSdk() {

    }


    override fun handleEvent(msg: Message) {
    }

    override fun createObserver() {

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /**
     *  在Activity/Fragment中如果想监听网络变化
     */
    override fun onNetworkStateChanged(netState: NetState) {
        super.onNetworkStateChanged(netState)
        LogUtils.d(GsonUtils.toJson(netState))
    }

    private fun getPermission() {

    }

}
