package c.plus.plan.common.utils;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import c.plus.plan.common.R;

public class AudioBtnUtils {
     private SoundPool.Builder builder;
     private SoundPool soundpool;
     private int soundId;

     public AudioBtnUtils(Context context) {
         builder = new SoundPool.Builder();
         //AudioAttributes是一个封装音频各种属性的方法
         AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
         //设置音频流的合适的属性
         attrBuilder.setLegacyStreamType(AudioManager.STREAM_SYSTEM);
         soundpool = builder.build();
         soundId = soundpool.load(context, R.raw.btn_sound, 1);
         //是否加载完成的监听
         soundpool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
             //加载完毕后再播放
             soundpool.play(soundId, 1f, 1f, 0, 0, 1);
         });
     }
}

