package com.ysh.demo.mqtt

import com.blankj.utilcode.util.LogUtils
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Mqtt 回调
 */
open class OnMessageCallback : MqttCallbackExtended {
    override fun connectionLost(cause: Throwable) {
        // 连接丢失后，一般在这里面进行重连
        LogUtils.e("连接断开，可以做重连")
    }

    @Throws(Exception::class)
    override fun messageArrived(topic: String, message: MqttMessage) {


    }

    override fun deliveryComplete(token: IMqttDeliveryToken) {
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        LogUtils.d("connectComplete---------$serverURI")
    }

}