package com.example.emotiondiarysystem.manager;

import android.content.Context;

import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.utils.SpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class EmotionStatManager {

    private DiaryManager diaryManager;
    private Context context;

    private static final Set<String> STOP_WORDS = new HashSet<>();
    private static final Set<String> POSITIVE_WORDS = new HashSet<>();
    private static final Set<String> NEUTRAL_WORDS = new HashSet<>();
    private static final Set<String> NEGATIVE_WORDS = new HashSet<>();

    static {
        STOP_WORDS.add("的"); STOP_WORDS.add("了"); STOP_WORDS.add("在"); STOP_WORDS.add("是");
        STOP_WORDS.add("我"); STOP_WORDS.add("有"); STOP_WORDS.add("和"); STOP_WORDS.add("就");
        STOP_WORDS.add("不"); STOP_WORDS.add("人"); STOP_WORDS.add("都"); STOP_WORDS.add("一");
        STOP_WORDS.add("一个"); STOP_WORDS.add("上"); STOP_WORDS.add("也"); STOP_WORDS.add("很");
        STOP_WORDS.add("到"); STOP_WORDS.add("说"); STOP_WORDS.add("要"); STOP_WORDS.add("去");
        STOP_WORDS.add("你"); STOP_WORDS.add("会"); STOP_WORDS.add("着"); STOP_WORDS.add("没有");
        STOP_WORDS.add("看"); STOP_WORDS.add("好"); STOP_WORDS.add("自己"); STOP_WORDS.add("这");
        STOP_WORDS.add("那"); STOP_WORDS.add("他"); STOP_WORDS.add("她"); STOP_WORDS.add("它");
        STOP_WORDS.add("们"); STOP_WORDS.add("这个"); STOP_WORDS.add("那个"); STOP_WORDS.add("什么");
        STOP_WORDS.add("怎么"); STOP_WORDS.add("为什么"); STOP_WORDS.add("因为"); STOP_WORDS.add("所以");
        STOP_WORDS.add("但是"); STOP_WORDS.add("如果"); STOP_WORDS.add("或者"); STOP_WORDS.add("然后");
        STOP_WORDS.add("还有"); STOP_WORDS.add("而且"); STOP_WORDS.add("已经"); STOP_WORDS.add("还是");
        STOP_WORDS.add("可以"); STOP_WORDS.add("能够"); STOP_WORDS.add("应该"); STOP_WORDS.add("必须");
        STOP_WORDS.add("非常"); STOP_WORDS.add("特别"); STOP_WORDS.add("比较"); STOP_WORDS.add("更加");
        STOP_WORDS.add("最"); STOP_WORDS.add("太"); STOP_WORDS.add("真"); STOP_WORDS.add("假");
        STOP_WORDS.add("大"); STOP_WORDS.add("小"); STOP_WORDS.add("多"); STOP_WORDS.add("少");
        STOP_WORDS.add("高"); STOP_WORDS.add("低"); STOP_WORDS.add("快"); STOP_WORDS.add("慢");
        STOP_WORDS.add("新"); STOP_WORDS.add("旧"); STOP_WORDS.add("今天"); STOP_WORDS.add("明天");
        STOP_WORDS.add("昨天"); STOP_WORDS.add("前天"); STOP_WORDS.add("后天"); STOP_WORDS.add("上午");
        STOP_WORDS.add("下午"); STOP_WORDS.add("晚上"); STOP_WORDS.add("早上"); STOP_WORDS.add("中午");
        STOP_WORDS.add("夜里"); STOP_WORDS.add("现在"); STOP_WORDS.add("刚才"); STOP_WORDS.add("马上");
        STOP_WORDS.add("立刻"); STOP_WORDS.add("一会儿"); STOP_WORDS.add("一下"); STOP_WORDS.add("一下下");
        STOP_WORDS.add("一点"); STOP_WORDS.add("一点点"); STOP_WORDS.add("一些"); STOP_WORDS.add("有些");
        STOP_WORDS.add("有的"); STOP_WORDS.add("所有"); STOP_WORDS.add("全部"); STOP_WORDS.add("整个");
        STOP_WORDS.add("全体"); STOP_WORDS.add("每个"); STOP_WORDS.add("每一个"); STOP_WORDS.add("各个");
        STOP_WORDS.add("各种"); STOP_WORDS.add("各种各样"); STOP_WORDS.add("各类"); STOP_WORDS.add("各类各样");
        STOP_WORDS.add("这样"); STOP_WORDS.add("那样"); STOP_WORDS.add("这么"); STOP_WORDS.add("那么");
        STOP_WORDS.add("这样的"); STOP_WORDS.add("那样的"); STOP_WORDS.add("其实"); STOP_WORDS.add("实际上");
        STOP_WORDS.add("事实上"); STOP_WORDS.add("当然"); STOP_WORDS.add("自然"); STOP_WORDS.add("显然");
        STOP_WORDS.add("明显"); STOP_WORDS.add("明显地"); STOP_WORDS.add("显然地"); STOP_WORDS.add("自然地");
        STOP_WORDS.add("当然地"); STOP_WORDS.add("其实地"); STOP_WORDS.add("实际上地"); STOP_WORDS.add("事实上地");
        STOP_WORDS.add("做"); STOP_WORDS.add("走"); STOP_WORDS.add("跑"); STOP_WORDS.add("吃");
        STOP_WORDS.add("喝"); STOP_WORDS.add("睡"); STOP_WORDS.add("玩"); STOP_WORDS.add("学");
        STOP_WORDS.add("工作");

        POSITIVE_WORDS.add("开心"); POSITIVE_WORDS.add("快乐"); POSITIVE_WORDS.add("高兴");
        POSITIVE_WORDS.add("幸福"); POSITIVE_WORDS.add("满足"); POSITIVE_WORDS.add("感恩");
        POSITIVE_WORDS.add("希望"); POSITIVE_WORDS.add("美好"); POSITIVE_WORDS.add("温暖");
        POSITIVE_WORDS.add("惊喜"); POSITIVE_WORDS.add("成功"); POSITIVE_WORDS.add("胜利");
        POSITIVE_WORDS.add("进步"); POSITIVE_WORDS.add("优秀"); POSITIVE_WORDS.add("棒");
        POSITIVE_WORDS.add("好"); POSITIVE_WORDS.add("爱"); POSITIVE_WORDS.add("喜欢");
        POSITIVE_WORDS.add("喜欢"); POSITIVE_WORDS.add("感激"); POSITIVE_WORDS.add("感动");
        POSITIVE_WORDS.add("激动"); POSITIVE_WORDS.add("兴奋"); POSITIVE_WORDS.add("愉快");
        POSITIVE_WORDS.add("舒服"); POSITIVE_WORDS.add("轻松"); POSITIVE_WORDS.add("放松");
        POSITIVE_WORDS.add("自信"); POSITIVE_WORDS.add("乐观"); POSITIVE_WORDS.add("积极");
        POSITIVE_WORDS.add("阳光"); POSITIVE_WORDS.add("甜蜜"); POSITIVE_WORDS.add("温馨");
        POSITIVE_WORDS.add("惬意"); POSITIVE_WORDS.add("满足"); POSITIVE_WORDS.add("感恩");
        POSITIVE_WORDS.add("期待"); POSITIVE_WORDS.add("憧憬"); POSITIVE_WORDS.add("美好");
        POSITIVE_WORDS.add("完美"); POSITIVE_WORDS.add("顺利"); POSITIVE_WORDS.add("吉祥");

        NEGATIVE_WORDS.add("难过"); NEGATIVE_WORDS.add("伤心"); NEGATIVE_WORDS.add("痛苦");
        NEGATIVE_WORDS.add("失望"); NEGATIVE_WORDS.add("绝望"); NEGATIVE_WORDS.add("沮丧");
        NEGATIVE_WORDS.add("焦虑"); NEGATIVE_WORDS.add("紧张"); NEGATIVE_WORDS.add("担心");
        NEGATIVE_WORDS.add("害怕"); NEGATIVE_WORDS.add("恐惧"); NEGATIVE_WORDS.add("烦躁");
        NEGATIVE_WORDS.add("生气"); NEGATIVE_WORDS.add("愤怒"); NEGATIVE_WORDS.add("郁闷");
        NEGATIVE_WORDS.add("压抑"); NEGATIVE_WORDS.add("孤独"); NEGATIVE_WORDS.add("寂寞");
        NEGATIVE_WORDS.add("痛苦"); NEGATIVE_WORDS.add("难过"); NEGATIVE_WORDS.add("委屈");
        NEGATIVE_WORDS.add("悲伤"); NEGATIVE_WORDS.add("难受"); NEGATIVE_WORDS.add("低落");
        NEGATIVE_WORDS.add("消极"); NEGATIVE_WORDS.add("悲观"); NEGATIVE_WORDS.add("绝望");
        NEGATIVE_WORDS.add("悔恨"); NEGATIVE_WORDS.add("后悔"); NEGATIVE_WORDS.add("自责");
        NEGATIVE_WORDS.add("愧疚"); NEGATIVE_WORDS.add("痛苦"); NEGATIVE_WORDS.add("烦恼");
        NEGATIVE_WORDS.add("压力"); NEGATIVE_WORDS.add("疲惫"); NEGATIVE_WORDS.add("累");
        NEGATIVE_WORDS.add("困"); NEGATIVE_WORDS.add("烦躁"); NEGATIVE_WORDS.add("急躁");
        NEGATIVE_WORDS.add("不安"); NEGATIVE_WORDS.add("不安"); NEGATIVE_WORDS.add("困惑");
        NEGATIVE_WORDS.add("迷茫"); NEGATIVE_WORDS.add("无助"); NEGATIVE_WORDS.add("无奈");
        NEGATIVE_WORDS.add("失望"); NEGATIVE_WORDS.add("崩溃");

        NEUTRAL_WORDS.add("日常"); NEUTRAL_WORDS.add("生活"); NEUTRAL_WORDS.add("工作");
        NEUTRAL_WORDS.add("学习"); NEUTRAL_WORDS.add("休息"); NEUTRAL_WORDS.add("吃饭");
        NEUTRAL_WORDS.add("睡觉"); NEUTRAL_WORDS.add("逛街"); NEUTRAL_WORDS.add("看书");
        NEUTRAL_WORDS.add("电影"); NEUTRAL_WORDS.add("音乐"); NEUTRAL_WORDS.add("运动");
        NEUTRAL_WORDS.add("散步"); NEUTRAL_WORDS.add("购物"); NEUTRAL_WORDS.add("做饭");
        NEUTRAL_WORDS.add("打扫"); NEUTRAL_WORDS.add("电话"); NEUTRAL_WORDS.add("消息");
        NEUTRAL_WORDS.add("时间"); NEUTRAL_WORDS.add("日子"); NEUTRAL_WORDS.add("天气");
        NEUTRAL_WORDS.add("下雨"); NEUTRAL_WORDS.add("晴天"); NEUTRAL_WORDS.add("阴天");
        NEUTRAL_WORDS.add("风"); NEUTRAL_WORDS.add("雪"); NEUTRAL_WORDS.add("云");
    }

    public static class WordData {
        public String word;
        public int count;
        public int emotionType; // 0=neutral, 1=positive, 2=negative
    }

    private static final Pattern CHINESE_WORD_PATTERN = Pattern.compile("[\\u4e00-\\u9fa5]+");

    public EmotionStatManager(Context context) {
        this.context = context;
        this.diaryManager = new DiaryManager(context);
    }

    public static class EmotionStatData {
        public int totalCount;
        public int positiveCount;
        public int neutralCount;
        public int negativeCount;
        public double positiveRate;
        public double neutralRate;
        public double negativeRate;
        public String dominantEmotion;
        public List<Diary> monthDiaries;
        public Map<String, Integer> keywordMap;
        public List<WordData> wordDataList;
        public int year;
        public int month;
    }

    public EmotionStatData getMonthEmotionStat(int year, int month) {
        EmotionStatData data = new EmotionStatData();
        data.year = year;
        data.month = month;

        int userId = SpUtil.getInt(context, "userId", -1);
        if (userId == -1) {
            return getEmptyData(year, month);
        }

        List<Diary> allDiaries = diaryManager.getDiaryListByUserId(userId);
        if (allDiaries == null) allDiaries = new ArrayList<>();

        List<Diary> monthDiaries = new ArrayList<>();
        for (Diary diary : allDiaries) {
            Date date = parseDiaryDate(diary.getCreateTime());
            if (date == null) continue;

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month) {
                monthDiaries.add(diary);
            }
        }

        data.monthDiaries = monthDiaries;
        data.totalCount = monthDiaries.size();

        int positive = 0, neutral = 0, negative = 0;
        for (Diary diary : monthDiaries) {
            String emotion = diary.getEmotionType();
            if ("积极".equals(emotion)) positive++;
            else if ("中性".equals(emotion)) neutral++;
            else if ("消极".equals(emotion)) negative++;
        }

        data.positiveCount = positive;
        data.neutralCount = neutral;
        data.negativeCount = negative;

        if (data.totalCount > 0) {
            data.positiveRate = positive * 100.0 / data.totalCount;
            data.neutralRate = neutral * 100.0 / data.totalCount;
            data.negativeRate = negative * 100.0 / data.totalCount;

            if (positive >= neutral && positive >= negative) {
                data.dominantEmotion = "积极";
            } else if (negative >= neutral && negative >= positive) {
                data.dominantEmotion = "消极";
            } else {
                data.dominantEmotion = "中性";
            }
        } else {
            data.positiveRate = 0;
            data.neutralRate = 0;
            data.negativeRate = 0;
            data.dominantEmotion = "";
        }

        data.keywordMap = extractKeywords(monthDiaries);
        data.wordDataList = extractWordData(monthDiaries);

        return data;
    }

    private Map<String, Integer> extractKeywords(List<Diary> diaries) {
        Map<String, Integer> keywordMap = new HashMap<>();

        for (Diary diary : diaries) {
            String content = diary.getContent();
            if (content == null || content.isEmpty()) continue;

            String title = diary.getTitle();
            if (title != null && !title.isEmpty()) {
                extractWordsFromText(title, keywordMap, 2);
            }

            extractWordsFromText(content, keywordMap, 1);
        }

        return keywordMap;
    }

    private List<WordData> extractWordData(List<Diary> diaries) {
        Map<String, WordData> wordDataMap = new HashMap<>();

        for (Diary diary : diaries) {
            String content = diary.getContent();
            if (content == null || content.isEmpty()) continue;

            String title = diary.getTitle();
            if (title != null && !title.isEmpty()) {
                extractWordDataFromText(title, wordDataMap, 2);
            }

            extractWordDataFromText(content, wordDataMap, 1);
        }

        List<WordData> wordDataList = new ArrayList<>(wordDataMap.values());
        wordDataList.sort((a, b) -> Integer.compare(b.count, a.count));
        return wordDataList.subList(0, Math.min(20, wordDataList.size()));
    }

    private void extractWordsFromText(String text, Map<String, Integer> keywordMap, int weight) {
        if (text == null || text.isEmpty()) return;

        String[] words = text.split("[\\s\\.\\,\\!\\?\\;\\:\\，\\。\\！\\？\\；\\：\\\"\\'\\（\\）\\(\\)\\[\\]\\{\\}\\【\\】\\《\\》\\〈\\〉\\、\\n\\r\\t]+");
        
        for (String word : words) {
            word = word.trim();
            
            if (word.length() < 2) continue;
            
            if (!CHINESE_WORD_PATTERN.matcher(word).matches()) continue;
            
            if (STOP_WORDS.contains(word)) continue;
            
            keywordMap.put(word, keywordMap.getOrDefault(word, 0) + weight);
        }
    }

    private void extractWordDataFromText(String text, Map<String, WordData> wordDataMap, int weight) {
        if (text == null || text.isEmpty()) return;

        String[] words = text.split("[\\s\\.\\,\\!\\?\\;\\:\\，\\。\\！\\？\\；\\：\\\"\\'\\（\\）\\(\\)\\[\\]\\{\\}\\【\\】\\《\\》\\〈\\〉\\、\\n\\r\\t]+");
        
        for (String word : words) {
            word = word.trim();
            
            if (word.length() < 2) continue;
            
            if (!CHINESE_WORD_PATTERN.matcher(word).matches()) continue;
            
            if (STOP_WORDS.contains(word)) continue;
            
            WordData wordData = wordDataMap.get(word);
            if (wordData == null) {
                wordData = new WordData();
                wordData.word = word;
                wordData.count = 0;
                wordData.emotionType = getEmotionType(word);
                wordDataMap.put(word, wordData);
            }
            wordData.count += weight;
        }
    }

    private int getEmotionType(String word) {
        if (POSITIVE_WORDS.contains(word)) {
            return 1;
        } else if (NEGATIVE_WORDS.contains(word)) {
            return 2;
        }
        return 0;
    }

    private EmotionStatData getEmptyData(int year, int month) {
        EmotionStatData data = new EmotionStatData();
        data.year = year;
        data.month = month;
        data.totalCount = 0;
        data.positiveCount = 0;
        data.neutralCount = 0;
        data.negativeCount = 0;
        data.positiveRate = 0;
        data.neutralRate = 0;
        data.negativeRate = 0;
        data.dominantEmotion = "";
        data.monthDiaries = new ArrayList<>();
        data.keywordMap = new HashMap<>();
        data.wordDataList = new ArrayList<>();
        return data;
    }

    private Date parseDiaryDate(String createTime) {
        if (createTime == null || createTime.trim().isEmpty()) return null;
        String source = createTime.trim();
        String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
                sdf.setLenient(false);
                String raw = source;
                if (raw.length() > pattern.length()) {
                    raw = raw.substring(0, pattern.length());
                }
                return sdf.parse(raw);
            } catch (Exception ignored) {
            }
        }
        return null;
    }
}
