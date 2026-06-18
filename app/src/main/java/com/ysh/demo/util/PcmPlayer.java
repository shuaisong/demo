package com.ysh.demo.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import com.blankj.utilcode.util.ToastUtils;

import java.io.FileInputStream;

public class PcmPlayer {

    private static final int SAMPLE_RATE = 16000; // 采样率
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_OUT_MONO; // 声道配置
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT; // 采样精度
    public PlayState state = PlayState.IDLE;
    private AudioTrack audioTrack;
    private FileInputStream fileInputStream;
    private Thread thread;

    private PlayCallBack playCallBack;

    public void setPlayCallBack(PlayCallBack playCallBack) {
        this.playCallBack = playCallBack;
    }

    public void playPcm(String pcmFilePath) {
        if (state == PlayState.PLAYING) return;
        thread = new Thread(() -> {
            int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize, AudioTrack.MODE_STREAM);

            byte[] buffer = new byte[bufferSize];
            try {
                fileInputStream = new FileInputStream(pcmFilePath);
                audioTrack.play();
                state = PlayState.PLAYING;
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    if (audioTrack != null) {
                        audioTrack.write(buffer, 0, bytesRead);
                        if (playCallBack != null) {
                            playCallBack.onData(buffer);
                        }
                    }
                }
                audioTrack.stop();
                audioTrack.release();
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                ToastUtils.showShort("播放失败");
            }
            state = PlayState.FINISH;
        });
        thread.start();
    }

    public void stopPlayer() {
        try {
            thread.interrupt();
            audioTrack.stop();
            audioTrack.release();
            fileInputStream.close();
            state = PlayState.FINISH;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        if (state == PlayState.PAUSE) {
            state = PlayState.PLAYING;
        }
    }

    public void pause() {
        state = PlayState.PAUSE;
    }

    public enum PlayState {
        /**
         * 空闲状态
         */
        IDLE,
        /**
         * 播放中
         */
        PLAYING,
        /**
         * 暂停中
         */
        PAUSE,

        /**
         * 播放结束
         */
        FINISH
    }

    public interface PlayCallBack {

        void onData(byte[] bytes);
    }
}