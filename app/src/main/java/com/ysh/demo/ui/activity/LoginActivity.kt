package com.ysh.demo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import com.blankj.utilcode.util.KeyboardUtils
import com.ysh.demo.R
import com.ysh.demo.base.Constant
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleActivity
import com.ysh.demo.databinding.ActivityLoginBinding
import com.ysh.demo.extend.gone
import com.ysh.demo.extend.isEmpty
import com.ysh.demo.extend.visible
import com.ysh.demo.util.CacheUtil
import com.ysh.demo.viewmodel.IndexViewModel

class LoginActivity : SimpleActivity<IndexViewModel, ActivityLoginBinding>(), View.OnClickListener {

    override fun layoutId(): Int {
        return R.layout.activity_login
    }

    override fun initData() {

    }

    private var psdVisible = false

    override fun initView(savedInstanceState: Bundle?) {
        setStatusBarLightMode(false)
        transparentStatusBar()
        mDatabind.apply {
            login.setOnClickListener(this@LoginActivity)
            ivVisible.setOnClickListener(this@LoginActivity)
            etPassword.addTextChangedListener(afterTextChanged = {
                it?.apply {
                    if (it.isEmpty()) {
                        ivVisible.gone()
                    } else ivVisible.visible()
                }
            })
            etPassword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
                override fun onEditorAction(v: TextView?, actionId: Int,
                        event: KeyEvent?): Boolean {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        login()
                        return true
                    }
                    return false
                }
            })
        }
    }

    override fun createObserver() {
        mViewModel.userInfo.observe(this) {
            CacheUtil.setUserInfo(it)
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
            overridePendingTransition()
        }
    }


    override fun handleEvent(msg: Message) {
        if (msg.code == Constant.LOG_OUT) {
            CacheUtil.removeLogin()
        }
    }

    private fun login() {
        KeyboardUtils.hideSoftInput(this@LoginActivity)

        mDatabind.apply {
            if (etUsername.isEmpty() || etPassword.isEmpty()) {
                return
            }
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            overridePendingTransition()
            finish()
//            showLoading("正在登录中。。。")
//            mViewModel.login(etUsername.text.toString(), etPassword.text.toString(), true)
        }

    }

    override fun onClick(v: View?) {
        //账号密码登录
        mDatabind.apply {
            when (v) {
                login -> {
                    login()
                }
                ivVisible -> {
                    if (psdVisible) {
                        psdVisible = false
                        ivVisible.setImageResource(R.drawable.ic_invisible)
                        etPassword.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
                    } else {
                        psdVisible = true
                        ivVisible.setImageResource(R.drawable.ic_visible)
                        etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    }
                    etPassword.setSelection(etPassword.text.length)
                }
            }

        }
    }


}