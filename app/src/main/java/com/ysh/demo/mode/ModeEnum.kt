package com.ysh.demo.mode

import com.ysh.demo.R


enum class ModeEnum(val value: Int, val lab: String,val icon: Int, val direction: Int) {
    Nest(0, "机场总览", R.mipmap.ic_launcher, R.id.action_fragmentFirst_to_fragmentList), Uav(0,
        "无人机总览", R.mipmap.ic_launcher, R.id.action_fragmentFirst_to_fragmentList),
    Case(2, "案件处理", R.mipmap.ic_launcher, R.id.action_fragmentFirst_to_fragmentB)
}