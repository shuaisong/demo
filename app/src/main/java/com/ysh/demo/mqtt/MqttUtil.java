package com.ysh.demo.mqtt;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.ysh.demo.base.ApiHelper;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Mqtt 工具类
 */
public class MqttUtil {

    private MqttUtil() {
    }

    private static MqttUtil mqttUtil;

    public static MqttUtil getInstance() {
        if (mqttUtil == null) {
            mqttUtil = new MqttUtil();
        }
        return mqttUtil;
    }

    private MqttClient client;

    private boolean activeStop = false;//主动断连
    private MsgReceiveCallBack msgReceiveCallBack;

    public void setMsgReceiveCallBack(MsgReceiveCallBack msgReceiveCallBack) {
        this.msgReceiveCallBack = msgReceiveCallBack;
    }

    public void resetIp() {
        if (client != null && client.isConnected()) {
            try {
                activeStop = true;
                client.setCallback(null);
                client.disconnect();
                client.close();
                LogUtils.e("resetIp client.close");
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        connect();
        activeStop = false;
    }

    private void connect() {
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            client = new MqttClient(ApiHelper.Companion.getMqttIp(), UUID.randomUUID().toString(), persistence);

            // MQTT 连接选项
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("admin");
            connOpts.setPassword("admin@123".toCharArray());
            connOpts.setConnectionTimeout(60);
            // 保留会话
            connOpts.setCleanSession(false);

            // 设置回调
            client.setCallback(new OnMessageCallback() {
                @Override
                public void connectionLost(@NonNull Throwable cause) {
                    super.connectionLost(cause);
                    try {
                        // Check if the client is connected
                        if (!activeStop && !client.isConnected())
                            // Try to reconnect
                            client.reconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void messageArrived(@NonNull String topic, @NonNull MqttMessage message) throws Exception {
                    super.messageArrived(topic, message);
                    try {
                        String msg = new String(message.getPayload());
                        LogUtils.v("topic:" + topic, "msg:" + msg);
                        if (msgReceiveCallBack != null)
                            msgReceiveCallBack.onMessageReceive(topic, msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void connectComplete(boolean reconnect, @Nullable String serverURI) {
                    super.connectComplete(reconnect, serverURI);
                }
            });

            // 建立连接
            new Thread(() -> {
                try {
                    client.connect(connOpts);
                } catch (MqttException e) {
                    ToastUtils.showShort("消息服务设置错误");
                }
            }).start();
        } catch (MqttException me) {
            client = null;
            ToastUtils.showShort("消息服务设置错误");
        }
    }

    private void sendMsg(JSONObject jsonObject, String topic) throws MqttException {
        String payload = jsonObject.toString(); // 消息内容
        LogUtils.d(payload);
        int qos = 0; // 服务质量等级，默认为 0（至多一次）
        boolean retained = false; // 是否要求代理服务器保留消息，默认为 false

        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        message.setRetained(retained);
        new Thread(() -> {
            try {
                if (client != null && client.isConnected()) client.publish(topic, message);
                else {
                    LogUtils.e("client == null", client == null);
                }
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public void addTopics(@NotNull ArrayList<String> topics) {
        if (client != null && client.isConnected()) {
            new Thread(() -> {
                try {
                    String[] strings = new String[topics.size()];
                    client.subscribe(topics.toArray(strings));
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    // 取消订阅
    public void removeTopic(@NotNull String topic) {
        new Thread(() -> {
            try {
                if (client != null)
                    client.unsubscribe(topic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // 取消订阅
    public void removeTopics(@NotNull List<String> topics) {
        new Thread(() -> {
            try {
                String[] strings = new String[topics.size()];
                for (int i = 0; i < topics.size(); i++) {
                    strings[i] = topics.get(i);
                }
                if (client != null) {
                    client.unsubscribe(strings);
                    topics.clear();
                }
            } catch (Exception e) {
                LogUtils.e(e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }

    public void addTopic(String topic) {
        // 订阅
        new Thread(() -> {
            try {
                client.subscribe(topic);
            } catch (MqttException e) {
                ToastUtils.showShort("订阅消息失败");
                e.printStackTrace();
            }
        }).start();
    }


    public interface MsgReceiveCallBack {
        void onMessageReceive(String topic, String msg);
    }

    public void close() {
        try {
            activeStop = true;
            if (client != null && client.isConnected()) {
                client.disconnect();
                client.close();
                client = null;
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
