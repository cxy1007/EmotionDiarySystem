package com.example.emotiondiarysystem.manager;

import android.content.Context;

import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.utils.SpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class EmotionStatManager {

    private DiaryManager diaryManager;
    private Context context;

    private static final String[] STOP_WORDS = {
        "的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这", "那", "他", "她", "它", "们", "这个", "那个", "什么", "怎么", "为什么", "因为", "所以", "但是", "如果", "或者", "然后", "还有", "而且", "已经", "还是", "还是", "可以", "能够", "应该", "必须", "非常", "特别", "比较", "更加", "最", "很", "太", "真", "假", "大", "小", "多", "少", "高", "低", "快", "慢", "新", "旧", "好", "坏", "美", "丑", "爱", "恨", "想", "做", "走", "跑", "吃", "喝", "睡", "玩", "学", "工作", "今天", "明天", "昨天", "前天", "后天", "上午", "下午", "晚上", "早上", "中午", "夜里", "现在", "刚才", "马上", "立刻", "一会儿", "一下", "一下下", "一点", "一点点", "一些", "有些", "有的", "所有", "全部", "整个", "全体", "每个", "每一个", "各个", "各种", "各种各样", "各类", "各类各样", "这样", "那样", "这么", "那么", "这样的", "那样的", "这样那样", "那样这样", "其实", "实际上", "事实上", "当然", "自然", "显然", "明显", "明显地", "显然地", "自然地", "当然地", "其实地", "实际上地", "事实上地"
    };

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

    private void extractWordsFromText(String text, Map<String, Integer> keywordMap, int weight) {
        if (text == null || text.isEmpty()) return;

        String[] words = text.split("[\\s\\.\\,\\!\\?\\;\\:\\，\\。\\！\\？\\；\\：\\\"\\'\\（\\）\\(\\)\\[\\]\\{\\}\\【\\】\\《\\》\\〈\\〉\\、\\n\\r\\t]+");
        
        for (String word : words) {
            word = word.trim();
            
            if (word.length() < 2) continue;
            
            if (!CHINESE_WORD_PATTERN.matcher(word).matches()) continue;
            
            boolean isStopWord = false;
            for (String stopWord : STOP_WORDS) {
                if (word.equals(stopWord)) {
                    isStopWord = true;
                    break;
                }
            }
            if (isStopWord) continue;
            
            keywordMap.put(word, keywordMap.getOrDefault(word, 0) + weight);
        }
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
