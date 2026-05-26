package com.example.emotiondiarysystem.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 心情日签语录工具类
 * 预定义多套不同心情对应的治愈语录
 */
public class MoodQuotesUtil {

    // 心情标签定义
    public static final String MOOD_HAPPY = "happy";      // 开心
    public static final String MOOD_CALM = "calm";        // 平静
    public static final String MOOD_SAD = "sad";          // 难过
    public static final String MOOD_ANXIOUS = "anxious";  // 焦虑
    public static final String MOOD_GRATEFUL = "grateful"; // 感恩
    public static final String MOOD_ENERGIZED = "energized"; // 充满活力

    // 心情日签语录库
    private static final Map<String, String[][]> MOOD_QUOTES = new HashMap<>();

    static {
        // 开心 - 语录
        MOOD_QUOTES.put(MOOD_HAPPY, new String[][]{
            {"「快乐不是因为拥有的多，而是计较的少。」", "— 李嘉诚"},
            {"「微笑是最好的名片。」", "— 佚名"},
            {"「生活就像一面镜子，你笑它也笑。」", "— 萨克雷"},
            {"「开心是一天，不开心也是一天，为什么不开心呢？」", "— 网络语录"}
        });

        // 平静 - 语录
        MOOD_QUOTES.put(MOOD_CALM, new String[][]{
            {"「真正的平静，不是避开车马喧嚣，而是在心中修篱种菊。」", "— 林徽因"},
            {"「心静自然凉。」", "— 古语"},
            {"「淡泊以明志，宁静以致远。」", "— 诸葛亮"},
            {"「世界越喧闹，我内心越安静。」", "— 杨绛"}
        });

        // 难过 - 语录
        MOOD_QUOTES.put(MOOD_SAD, new String[][]{
            {"「难过的时候，就抬头看看天空，它那么大，一定可以包容你所有的委屈。」", "— 网络语录"},
            {"「没有不可治愈的伤痛，没有不能结束的沉沦。」", "— 肖复兴"},
            {"「黑夜无论怎样悠长，白昼总会到来。」", "— 莎士比亚"},
            {"「眼泪是心里无法诉说的言辞。」", "— 佚名"}
        });

        // 焦虑 - 语录
        MOOD_QUOTES.put(MOOD_ANXIOUS, new String[][]{
            {"「不要着急，最好的总会在最不经意的时候出现。」", "— 泰戈尔"},
            {"「深呼吸，放轻松，一切都会好起来的。」", "— 佚名"},
            {"「焦虑不会改变结果，但行动可以。」", "— 网络语录"},
            {"「活在当下，不要预支明天的烦恼。」", "— 佚名"}
        });

        // 感恩 - 语录
        MOOD_QUOTES.put(MOOD_GRATEFUL, new String[][]{
            {"「感恩的心，感谢有你。」", "— 歌曲"},
            {"「知足常乐。」", "— 古语"},
            {"「感谢生命中的每一次遇见。」", "— 佚名"},
            {"「拥有一颗感恩的心，就拥有了全世界。」", "— 网络语录"}
        });

        // 充满活力 - 语录
        MOOD_QUOTES.put(MOOD_ENERGIZED, new String[][]{
            {"「每一个不曾起舞的日子，都是对生命的辜负。」", "— 尼采"},
            {"「生命在于运动。」", "— 伏尔泰"},
            {"「年轻就是资本，活力就是希望。」", "— 佚名"},
            {"「用热情点燃生活，用行动证明自己。」", "— 网络语录"}
        });
    }

    /**
     * 根据心情标签获取随机语录
     * @param moodTag 心情标签
     * @return 语录数组 [内容, 作者]
     */
    public static String[] getRandomQuote(String moodTag) {
        String[][] quotes = MOOD_QUOTES.get(moodTag);
        if (quotes == null || quotes.length == 0) {
            // 默认返回平静语录
            quotes = MOOD_QUOTES.get(MOOD_CALM);
        }
        Random random = new Random();
        return quotes[random.nextInt(quotes.length)];
    }

    /**
     * 根据情绪类型推断心情标签
     * @param emotionType 情绪类型（积极/中性/消极）
     * @return 心情标签
     */
    public static String inferMoodTag(String emotionType) {
        if (emotionType == null) {
            return MOOD_CALM;
        }
        switch (emotionType) {
            case "积极":
                return MOOD_HAPPY;
            case "消极":
                return MOOD_SAD;
            case "中性":
            default:
                return MOOD_CALM;
        }
    }

    /**
     * 获取所有心情标签
     * @return 心情标签数组
     */
    public static String[] getAllMoodTags() {
        return new String[]{MOOD_HAPPY, MOOD_CALM, MOOD_SAD, MOOD_ANXIOUS, MOOD_GRATEFUL, MOOD_ENERGIZED};
    }
}