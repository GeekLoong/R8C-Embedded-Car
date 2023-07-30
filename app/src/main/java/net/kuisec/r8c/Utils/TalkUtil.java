package net.kuisec.r8c.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * 文字转语音工具类
 */
public class TalkUtil {

    private static TextToSpeech speech;

    /**
     * 初始化语音模块
     * @param context 上下文
     */
    public static void initTalk(Context context) {
        speech = new TextToSpeech(context, i -> {
            //初始化成功就是0，初始化失败就是-1
            if (i == 0) {
                int result = speech.setLanguage(Locale.CHINESE);
                if (!(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)) {
                    speak("语音模块初始化完成");
                }
            }
        });
    }

    /**
     * 文字转语音
     * @param text 文字
     */
    public static void speak(String text) {
        if (speech != null) {
            speech.setSpeechRate(3F);
            speech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "speak");
        }
    }

}
