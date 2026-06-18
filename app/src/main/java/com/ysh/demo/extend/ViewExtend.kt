package com.ysh.demo.extend

import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.coroutineScope
import com.blankj.utilcode.util.ToastUtils
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ysh.demo.ui.fragment.FragmentHome
import com.ysh.demo.ui.fragment.MineFragment

fun ViewPager2.init(fragment: Fragment, fragments: ArrayList<Fragment>,
        isUserInputEnabled: Boolean = true): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = isUserInputEnabled
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int) = fragments[position]
        override fun getItemCount() = fragments.size
    }
    return this
}

fun ViewPager2.initMain(fragment: Fragment): ViewPager2 {
    //是否可滑动
    this.isUserInputEnabled = false
    this.offscreenPageLimit = 2
    //设置适配器
    adapter = object : FragmentStateAdapter(fragment) {
        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    FragmentHome()
                }
                1 -> {
                    MineFragment()
                }
                else -> {
                    FragmentHome()
                }
            }
        }

        override fun getItemCount() = 2
    }
    return this
}

/**
 * 拦截BottomNavigation长按事件 防止长按时出现Toast
 * @param ids IntArray
 */
fun BottomNavigationView.interceptLongClick(vararg ids: Int) {
    val bottomNavigationMenuView: ViewGroup = (this.getChildAt(0) as ViewGroup)
    for (index in ids.indices) {
        bottomNavigationMenuView.getChildAt(index).findViewById<View>(
            ids[index]).setOnLongClickListener {
            true
        }
    }
}

fun BottomNavigationView.init(navigationItemSelectedAction: (Int) -> Unit): BottomNavigationView {
    setOnNavigationItemSelectedListener {
        navigationItemSelectedAction.invoke(it.itemId)
        true
    }
    return this
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun String.toast() {
    ToastUtils.showShort(this)
}

fun CharSequence.toast() {
    ToastUtils.showShort(this)
}

fun TextView.isNotEmpty(msg: String = ""): Boolean {
    if (text.isEmpty()) {
        if (msg.isNotEmpty()) {
            msg.toast()
        } else hint.toString().toast()
        return false
    }
    return true
}

fun TextView.isEmpty(msg: String = ""): Boolean {
    if (text.isEmpty()) {
        if (msg.isNotEmpty()) {
            msg.toast()
        } else hint.toString().toast()
        return true
    }
    return false
}

public object ViewExtend {
    fun AppCompatActivity.lifecycleScope(): LifecycleCoroutineScope {
        return lifecycle.coroutineScope
    }
}
