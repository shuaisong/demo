package com.ysh.demo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.blankj.utilcode.util.BarUtils
import com.ysh.demo.MainApplication
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleActivity
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.databinding.ActivityWelcomeBinding

class WelcomeActivity : SimpleActivity<SimpleViewModel, ActivityWelcomeBinding>() {
    override fun layoutId(): Int {
        return R.layout.activity_welcome
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MainApplication.mainApplication.onCreate()
        //设置状态栏颜色为透明
        BarUtils.transparentStatusBar(this)
        //设置状态栏为亮色
        BarUtils.setStatusBarLightMode(this, true)
        super.onCreate(savedInstanceState)
    }

    override fun initData() {
    }

    override fun handleEvent(msg: Message) {
    }

    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        findViewById<View>(R.id.welcome_image).postDelayed({
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
            overridePendingTransition()
            finish()
        }, 1000)
    }
}