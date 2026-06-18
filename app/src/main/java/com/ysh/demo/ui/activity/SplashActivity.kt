package com.ysh.demo.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.blankj.utilcode.util.BarUtils
import com.ysh.demo.MainApplication.Companion.mainApplication
import com.ysh.demo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

@SuppressLint("CustomSplashScreen") class SplashActivity : AppCompatActivity() {

    private var mCoroutineScope: CoroutineScope? = null

    // 数据
    private var mKeepOnAtomicBool = AtomicBoolean(true)
    override fun onCreate(savedInstanceState: Bundle?) {
        mainApplication.onCreate()
        //设置状态栏颜色为透明
        BarUtils.transparentStatusBar(this)
        //设置状态栏为亮色
        BarUtils.setStatusBarLightMode(this, true)
        super.onCreate(savedInstanceState)
        val mSplashScreen = installSplashScreen()
        // 每次UI绘制前，会判断 Splash 是否有必要继续展示在屏幕上；直到不再满足条件时，隐藏Splash。
        mSplashScreen.setKeepOnScreenCondition() {
            mKeepOnAtomicBool.get()
        }
        // Splash展示完毕的监听方法
        mSplashScreen.setOnExitAnimationListener {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
            overridePendingTransition(R.anim.right_in, R.anim.left_out)
            it.view.postDelayed({
                it.remove()
            }, 1100)
        }
        mCoroutineScope = CoroutineScope(Job() + Dispatchers.Main)
        mCoroutineScope!!.launch(Dispatchers.IO) {
            delay(500)
            mKeepOnAtomicBool.compareAndSet(true, false)
            this@SplashActivity.window.decorView.postInvalidate()
        }
        initData()
    }

    private fun enter() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
        overridePendingTransition(R.anim.right_in, R.anim.left_out)
    }

    private fun initData() {
    }

    override fun onDestroy() {
        super.onDestroy()
        mCoroutineScope?.cancel()
    }

}