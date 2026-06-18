package com.ysh.demo.util;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.blankj.utilcode.util.LogUtils;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Utility class for playing sounds.
 */
public final class AudioUtil {

    private static final float MIN_RATIO = 0.3f;
    private static MediaPlayer player;

    private AudioUtil() {
        // Util class
    }

    /**
     * Plays a sound.
     *
     * @param context A context object.
     * @param resID   The resource ID of the sound to play.
     */
    public static void playSound(Context context, int resID) {
        playSound(context, resID, false);
    }

    /**
     * Plays a sound.
     *
     * @param context        A context object.
     * @param resID          The resource ID of the sound to play.
     * @param ignoreWhenBusy If set to true, will do nothing if a sound is already being played.
     */
    public synchronized static void playSound(Context context, int resID, boolean ignoreWhenBusy) {
        try {
            if (player != null) {
                if (player.isPlaying()) {
                    if (ignoreWhenBusy) {
                        return;
                    }
                    player.stop();
                }
                player.release();
                player = null;
            }

            player = MediaPlayer.create(context, resID);
            player.setOnCompletionListener(mp -> {
                if (player != null) {
                    player.release();
                    player = null;
                }
            });
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            final float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            final float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            float volumeRatio = audioCurrentVolume / audioMaxVolume;
            if (volumeRatio < MIN_RATIO) {
                volumeRatio = MIN_RATIO;
            }
            player.setVolume(volumeRatio, volumeRatio);
            player.start();
        } catch (Exception e) {
            LogUtils.d("PlaySound", e.getMessage());
        }
    }

    /**
     * Plays a sound in the background.
     *
     * @param context A context object.
     * @param resID   The resource ID of the sound to play.
     */
    public static Disposable playSoundInBackground(final Context context, final int resID) {
        return Observable.just(true)
                .subscribeOn(Schedulers.io())
                .subscribe(aBoolean -> playSound(context, resID),
                        e -> LogUtils.d("PlaySound", e.getMessage()));
    }
}
