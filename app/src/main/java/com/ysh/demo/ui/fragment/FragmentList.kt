package com.ysh.demo.ui.fragment

import android.os.Bundle
import com.drake.brv.annotaion.AnimationType
import com.drake.brv.utils.divider
import com.drake.brv.utils.models
import com.drake.brv.utils.setup
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleFragment
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.bean.ListBean
import com.ysh.demo.databinding.FragmentListBinding
import com.ysh.demo.extend.visible
import com.ysh.demo.view.TitleView
import me.hgj.jetpackmvvm.ext.nav

class FragmentList : SimpleFragment<SimpleViewModel, FragmentListBinding>() {
    override fun layoutId(): Int {
        return R.layout.fragment_list
    }

    override fun handleEvent(msg: Message) {

    }

    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        initTitle()
        initRefresh()
        initRecyclerView()
    }

    private fun initTitle() {
        mDatabind.title.apply {
            ivBack.visible()
            setCallback(object : TitleView.ClickCallback {
                override fun onBack() {
                    val directions = FragmentListDirections.actionFragmentListToFragmentFirst()
                    nav().navigate(directions)
                }

                override fun onRight() {

                }
            })
        }

    }

    private fun initRefresh() {
        mDatabind.refresh.onRefresh {

        }
        mDatabind.refresh.onLoadMore { }
    }

    private fun initRecyclerView() {
        mDatabind.recycler.divider {
            setDivider(10, true)
        }.setup {
            addType<ListBean>(R.layout.layout_list_item)
        }.apply {
            animationEnabled = true
            setAnimation(AnimationType.SLIDE_RIGHT)
        }
    }

    override fun lazyLoadData() {
        mDatabind.recycler.models = arrayListOf(ListBean(), ListBean(), ListBean(), ListBean(),
            ListBean(), ListBean(), ListBean(), ListBean(), ListBean(), ListBean(), ListBean(),
            ListBean(), ListBean())
    }
}