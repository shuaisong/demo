package com.ysh.demo.ui.fragment

import android.os.Bundle
import android.transition.TransitionInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.ysh.demo.R
import com.ysh.demo.base.Message
import com.ysh.demo.base.SimpleFragment
import com.ysh.demo.base.SimpleViewModel
import com.ysh.demo.databinding.FragmentBBinding
import me.hgj.jetpackmvvm.ext.nav

class FragmentB : SimpleFragment<SimpleViewModel, FragmentBBinding>() {

    override fun layoutId(): Int {
        return R.layout.fragment_b
    }

    override fun lazyLoadData() {

    }

    override fun handleEvent(msg: Message) {

    }

    override fun createObserver() {

    }

    override fun initView(savedInstanceState: Bundle?) {
        ViewCompat.setTransitionName(mDatabind.imageView, "image")
        postponeEnterTransition()
        postShareTransitionName()
        arguments?.let {
            val param = FragmentBArgs.fromBundle(it).param
            mDatabind.param.text = param
        }
        mDatabind.text.text = javaClass.simpleName
        mDatabind.button.apply {
            text = "back"
        }.setOnClickListener { v ->
            Navigation.findNavController(v).navigateUp()
        }
        mDatabind.imageView.setOnClickListener {
            val directions = FragmentBDirections.actionFragmentBToFragmentList()
            nav().navigate(directions)
        }
    }

    private val dialog by lazy {
        AlertDialog.Builder(requireActivity()).setTitle("提示").setMessage("测试等待。。。").create()
    }

    private fun postShareTransitionName() {
//        dialog.show()
        val transition = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.btn_trans)
        sharedElementEnterTransition = transition
        startPostponedEnterTransition()
        val requestOptions = RequestOptions().skipMemoryCache(false)
        Glide.with(mDatabind.imageView).applyDefaultRequestOptions(requestOptions).load(
            "https://copyright.bdstatic.com/vcg/creative/cc9c744cf9f7c864889c563cbdeddce6.jpg")/*.listener(object :
            RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>,
                    isFirstResource: Boolean): Boolean {
                dialog.dismiss()
                return false
            }

            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>?,
                    dataSource: DataSource, isFirstResource: Boolean): Boolean {
                mDatabind.imageView.postDelayed({
                    mDatabind.imageView.setImageDrawable(resource)
                    dialog.dismiss()
                    startPostponedEnterTransition()
                }, 1000)
                return true
            }
        })*/.into(mDatabind.imageView)

    }


}