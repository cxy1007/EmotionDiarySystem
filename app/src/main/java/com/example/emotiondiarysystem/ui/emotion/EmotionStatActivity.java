package com.example.emotiondiarysystem.ui.emotion;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EmotionStatActivity extends BaseActivity {

    // ===== 视图声明 =====
    private LinearLayout topBar;
    private View divider;
    private TextView tvTitle;

    // 统计概览
    private TextView tvTotalDiary;
    private TextView tvPositiveDiary;
    private TextView tvNeutralDiary;
    private TextView tvNegativeDiary;

    // 情感分析结果
    private TextView tvEmotionAnalysisResult;

    // 周柱状图 + 无数据提示
    private WeekBarChartView weekBarChart;
    private TextView tvWeekNoData;

    // 月折线图 + 无数据提示
    private MonthLineChartView monthLineChart;
    private TextView tvMonthNoData;

    // 心情语录
    private TextView tvQuote;
    private TextView tvQuoteAuthor;

    // 情绪建议
    private TextView tvWeeklySummary;
    private TextView tvAdvice;

    private DiaryManager diaryManager;

    // ===== 心情语录库 =====
    private static final String[] QUOTES = {
        "「生活中不缺少美，缺少的是发现美的眼睛。」",
        "「每一个不曾起舞的日子，都是对生命的辜负。」",
        "「你那么憎恨那些人，和他们斗了那么久，最终却变得和他们一样。」",
        "「世界上只有一种真正的英雄主义，那就是认清生活的真相后依然热爱生活。」",
        "「当你老了，回顾一生，就会发觉：什么时候出国读书、什么时候决定做第一份职业、何时选定了对象而恋爱、什么时候结婚，其实都是命运的巨变。」",
        "「我们听过无数的道理，却仍旧过不好这一生。」",
        "「人生就像一杯茶，不会苦一辈子，但总会苦一阵子。」",
        "「不是因为事情难，我们不敢做；而是因为我们不敢做，事情才难的。」",
        "「世界上最宽阔的是海洋，比海洋更宽阔的是天空，比天空更宽阔的是人的心灵。」",
        "「把每一个黎明看作是生命的开始，把每一个黄昏看作是你生命的小结。」",
        "「不要着急，最好的总会在最不经意的时候出现。」",
        "「人的一切痛苦，本质上都是对自己无能的愤怒。」",
        "「活着不是为了改变世界，而是为了不让世界改变自己。」",
        "「有时候，远方最打动你的，不是风景，而是你想象它的方式。」",
        "「真正的平静，不是远离车马喧嚣，而是在内心修篱种菊。」",
        "「不管前方的路有多苦，只要走的方向正确，不管多么崎岖不平，都比站在原地更接近幸福。」",
        "「你要做一个不动声色的大人了，不准情绪化，不准偷偷想念，不准回头看。」",
        "「温柔的晚风，傍晚的晚霞，解暑的西瓜，冒泡的可乐，人间的美好多着呢。」",
        "「愿你所有的日子，都比不上明天的光辉。」",
        "「人生没有白走的路，每一步都算数。」"
    };

    private static final String[] QUOTE_AUTHORS = {
        "— 罗丹",        "— 尼采",        "— 尼采",        "— 罗曼·罗兰",
        "— 陶杰",        "— 韩寒",        "— 网络语录",    "— 曼德拉",
        "— 雨果",        "— 约翰·罗斯福","— 泰戈尔",      "— 王小波",
        "— 电影台词",    "— 网络语录",    "— 林徽因",      "— 宫崎骏",
        "— 村上春树",    "— 网络语录",    "— 莎士比亚",    "— 李宗盛"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotion_stat);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        diaryManager = new DiaryManager(this);
        applyFullTheme();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllData();
        applyFullTheme();
    }

    private void initViews() {
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);
        tvTitle = findViewById(R.id.tv_title);

        tvTotalDiary = findViewById(R.id.tv_total_diary);
        tvPositiveDiary = findViewById(R.id.tv_positive_diary);
        tvNeutralDiary = findViewById(R.id.tv_neutral_diary);
        tvNegativeDiary = findViewById(R.id.tv_negative_diary);

        tvEmotionAnalysisResult = findViewById(R.id.tv_emotion_analysis_result);

        weekBarChart = findViewById(R.id.week_bar_chart);
        tvWeekNoData = findViewById(R.id.tv_week_no_data);

        monthLineChart = findViewById(R.id.month_line_chart);
        tvMonthNoData = findViewById(R.id.tv_month_no_data);

        tvQuote = findViewById(R.id.tv_quote);
        tvQuoteAuthor = findViewById(R.id.tv_quote_author);

        tvWeeklySummary = findViewById(R.id.tv_weekly_summary);
        tvAdvice = findViewById(R.id.tv_advice);
    }

    private void loadAllData() {
        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            resetAllViews();
            return;
        }

        List<Diary> diaries = diaryManager.getDiaryListByUserId(userId);
        if (diaries == null) diaries = new ArrayList<>();

        int total = diaries.size();
        int positive = 0, neutral = 0, negative = 0;
        for (Diary d : diaries) {
            String e = d.getEmotionType();
            if ("积极".equals(e)) positive++;
            else if ("中性".equals(e)) neutral++;
            else if ("消极".equals(e)) negative++;
        }

        // 1. 统计概览
        loadEmotionStats(total, positive, neutral, negative);

        // 2. 情感分析结果
        loadEmotionAnalysisResult(diaries, positive, neutral, negative);

        // 3. 周情感统计图
        loadWeeklyChart(diaries);

        // 4. 月情感趋势图
        loadMonthlyChart(diaries);

        // 5. 心情语录推荐
        loadMoodQuote(positive, neutral, negative);

        // 6. 情绪建议
        updateAdvice(total, positive, neutral, negative);
    }

    // ==================== 功能1：情感分析结果 ====================
    private void loadEmotionAnalysisResult(List<Diary> diaries, int positive, int neutral, int negative) {
        if (tvEmotionAnalysisResult == null) return;

        if (diaries.isEmpty()) {
            tvEmotionAnalysisResult.setText("暂无分析结果，请先记录几篇日记吧");
            return;
        }

        int total = diaries.size();
        double pRate = positive * 100.0 / total;
        double nRate = neutral * 100.0 / total;
        double ngRate = negative * 100.0 / total;

        StringBuilder sb = new StringBuilder();

        // 主导情绪
        String dominant;
        int domCount;
        double domRate;
        if (positive >= neutral && positive >= negative) {
            dominant = "积极";
            domCount = positive;
            domRate = pRate;
        } else if (neutral >= positive && neutral >= negative) {
            dominant = "中性";
            domCount = neutral;
            domRate = nRate;
        } else {
            dominant = "消极";
            domCount = negative;
            domRate = ngRate;
        }

        sb.append(String.format(Locale.getDefault(),
            "您共记录 %d 篇日记，其中积极 %d 篇（%.0f%%），中性 %d 篇（%.0f%%），消极 %d 篇（%.0f%%）。\n\n",
            total, positive, pRate, neutral, nRate, negative, ngRate));

        sb.append(String.format("主导情绪：%s（占 %.0f%%）\n\n", dominant, domRate));

        // 情绪分布描述
        if (pRate >= 60) {
            sb.append("您的情绪状态非常积极，阳光的心态贯穿大多数日子！");
        } else if (pRate >= 40) {
            sb.append("您的情绪整体稳定，积极与平静交织，心理健康状态良好。");
        } else if (ngRate >= 40) {
            sb.append("近期您可能承受了较大压力或面临挑战，请记得给自己一些时间和空间。");
        } else {
            sb.append("您的情绪波动较为明显，建议多关注内心感受，保持记录日记的习惯。");
        }

        // 趋势描述
        if (total >= 7) {
            List<Diary> recent7 = diaries.size() > 7 ? diaries.subList(diaries.size() - 7, diaries.size()) : diaries;
            int recentP = 0, recentN = 0;
            for (Diary d : recent7) {
                if ("积极".equals(d.getEmotionType())) recentP++;
                else if ("消极".equals(d.getEmotionType())) recentN++;
            }
            if (recentP > positive / total * 7 + 1) {
                sb.append("\n\n最近一周情绪明显改善，保持得很好！");
            } else if (recentN > negative / total * 7 + 1) {
                sb.append("\n\n最近一周情绪有所下降，建议适当放松心情。");
            }
        }

        tvEmotionAnalysisResult.setText(sb.toString());
    }

    // ==================== 功能2+3：周情感统计图 ====================
    private void loadWeeklyChart(List<Diary> diaries) {
        if (weekBarChart == null || tvWeekNoData == null) return;

        int[] pos = new int[7];
        int[] neu = new int[7];
        int[] neg = new int[7];

        Calendar cal = Calendar.getInstance();
        int today = cal.get(Calendar.DAY_OF_WEEK);
        // 转换：Calendar 周一是1，周日是7，调整为 0=周一 ... 6=周日
        int[] dayOfWeekMap = {7, 1, 2, 3, 4, 5, 6}; // Calendar值 -> 数组索引（0=周一）

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Diary d : diaries) {
            String dateStr = d.getCreateTime();
            if (dateStr == null || dateStr.length() < 10) continue;
            try {
                cal.setTime(sdf.parse(dateStr.substring(0, 10)));
                int dow = cal.get(Calendar.DAY_OF_WEEK);
                int idx = dayOfWeekMap[dow - 1];
                String emotion = d.getEmotionType();
                if ("积极".equals(emotion)) pos[idx]++;
                else if ("中性".equals(emotion)) neu[idx]++;
                else if ("消极".equals(emotion)) neg[idx]++;
            } catch (Exception ignored) {}
        }

        // 生成星期标签（从今天起往前7天）
        String[] labels = new String[7];
        Calendar labelCal = Calendar.getInstance();
        String[] chineseDays = {"一", "二", "三", "四", "五", "六", "日"};
        int startDow = labelCal.get(Calendar.DAY_OF_WEEK);
        int[] startMap = {7, 1, 2, 3, 4, 5, 6};
        int startIdx = startMap[startDow - 1];
        for (int i = 0; i < 7; i++) {
            int idx = (startIdx + i - 1) % 7;
            labels[i] = "周" + chineseDays[idx];
        }

        boolean hasData = false;
        for (int i = 0; i < 7; i++) {
            if (pos[i] > 0 || neu[i] > 0 || neg[i] > 0) {
                hasData = true;
                break;
            }
        }

        if (hasData) {
            weekBarChart.setVisibility(View.VISIBLE);
            tvWeekNoData.setVisibility(View.GONE);
            weekBarChart.setDayLabels(labels);
            weekBarChart.setData(pos, neu, neg);
        } else {
            weekBarChart.setVisibility(View.GONE);
            tvWeekNoData.setVisibility(View.VISIBLE);
        }
    }

    // ==================== 功能4：月情感趋势图 ====================
    private void loadMonthlyChart(List<Diary> diaries) {
        if (monthLineChart == null || tvMonthNoData == null) return;

        int[] pos = new int[31];
        int[] neu = new int[31];
        int[] neg = new int[31];

        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentDay = cal.get(Calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (Diary d : diaries) {
            String dateStr = d.getCreateTime();
            if (dateStr == null || dateStr.length() < 10) continue;
            try {
                cal.setTime(sdf.parse(dateStr.substring(0, 10)));
                if (cal.get(Calendar.YEAR) != currentYear || cal.get(Calendar.MONTH) + 1 != currentMonth) continue;
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String emotion = d.getEmotionType();
                if ("积极".equals(emotion)) pos[day - 1]++;
                else if ("中性".equals(emotion)) neu[day - 1]++;
                else if ("消极".equals(emotion)) neg[day - 1]++;
            } catch (Exception ignored) {}
        }

        boolean hasData = false;
        for (int i = 0; i < currentDay; i++) {
            if (pos[i] > 0 || neu[i] > 0 || neg[i] > 0) {
                hasData = true;
                break;
            }
        }

        if (hasData) {
            monthLineChart.setVisibility(View.VISIBLE);
            tvMonthNoData.setVisibility(View.GONE);
            monthLineChart.setData(pos, neu, neg, currentDay);
        } else {
            monthLineChart.setVisibility(View.GONE);
            tvMonthNoData.setVisibility(View.VISIBLE);
        }
    }

    // ==================== 功能5：心情语录推荐 ====================
    private void loadMoodQuote(int positive, int neutral, int negative) {
        if (tvQuote == null || tvQuoteAuthor == null) return;

        int total = positive + neutral + negative;
        int index;

        if (total == 0) {
            // 还没有日记，随机推荐积极语录
            index = (int) (Math.random() * 5);
        } else {
            double pRate = positive * 100.0 / total;
            double ngRate = negative * 100.0 / total;

            if (ngRate >= 50) {
                // 消极情绪 → 推荐治愈/温暖语录（后半部分）
                index = 5 + (int) (Math.random() * 8);
            } else if (pRate >= 50) {
                // 积极情绪 → 推荐激励语录（前半部分）
                index = (int) (Math.random() * 8);
            } else {
                // 中性 → 推荐哲理语录
                index = 8 + (int) (Math.random() * (QUOTES.length - 8));
            }
        }

        index = Math.max(0, Math.min(index, QUOTES.length - 1));
        tvQuote.setText(QUOTES[index]);
        tvQuoteAuthor.setText(QUOTE_AUTHORS[index]);
    }

    // ==================== 统计概览 & 情绪建议 ====================
    private void loadEmotionStats(int total, int positive, int neutral, int negative) {
        if (tvTotalDiary != null) tvTotalDiary.setText(String.valueOf(total));
        if (tvPositiveDiary != null) tvPositiveDiary.setText(String.valueOf(positive));
        if (tvNeutralDiary != null) tvNeutralDiary.setText(String.valueOf(neutral));
        if (tvNegativeDiary != null) tvNegativeDiary.setText(String.valueOf(negative));
    }

    private void updateAdvice(int total, int positive, int neutral, int negative) {
        if (tvWeeklySummary == null || tvAdvice == null) return;

        if (total == 0) {
            tvWeeklySummary.setText("暂无数据，开始写日记后查看趋势");
            tvAdvice.setText("每天记录日记可以帮助您更好地了解自己的情绪变化哦");
            return;
        }

        double pRate = total > 0 ? positive * 100.0 / total : 0;
        double nRate = total > 0 ? negative * 100.0 / total : 0;

        tvWeeklySummary.setText(String.format(Locale.getDefault(),
            "您已记录 %d 篇日记，其中积极 %.0f%%，消极 %.0f%%",
            total, pRate, nRate));

        String advice;
        if (pRate >= 70) {
            advice = "太棒了！您最近的情绪非常积极阳光，请继续保持！\n\n💡 建议：可以尝试分享您的快乐，让更多人感受到正能量。";
        } else if (pRate >= 40) {
            advice = "您的情绪整体稳定，保持得很好！\n\n💡 建议：适当增加一些户外活动，可以让心情更加愉悦。";
        } else if (nRate >= 50) {
            advice = "最近可能遇到了一些困难或压力，请不要担心，这是很正常的。\n\n💡 建议：可以尝试写日记记录负面情绪，或者找朋友倾诉一下。";
        } else {
            advice = "您的情绪处于波动期，这是正常的。\n\n💡 建议：保持记录日记的习惯，关注自己的情绪变化，必要时寻求帮助。";
        }
        tvAdvice.setText(advice);
    }

    private void resetAllViews() {
        if (tvTotalDiary != null) tvTotalDiary.setText("0");
        if (tvPositiveDiary != null) tvPositiveDiary.setText("0");
        if (tvNeutralDiary != null) tvNeutralDiary.setText("0");
        if (tvNegativeDiary != null) tvNegativeDiary.setText("0");
        if (tvEmotionAnalysisResult != null) tvEmotionAnalysisResult.setText("请先登录后查看情感统计");
        if (tvWeeklySummary != null) tvWeeklySummary.setText("请先登录后查看情感统计");
        if (tvAdvice != null) tvAdvice.setText("登录后开始记录日记，我会为您分析情绪趋势");

        if (weekBarChart != null) weekBarChart.setVisibility(View.GONE);
        if (tvWeekNoData != null) tvWeekNoData.setVisibility(View.VISIBLE);
        if (monthLineChart != null) monthLineChart.setVisibility(View.GONE);
        if (tvMonthNoData != null) tvMonthNoData.setVisibility(View.VISIBLE);
    }

    // ==================== 主题色应用 ====================
    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);
        if (divider != null) divider.setBackgroundColor(colors.divider);
        if (tvTitle != null) tvTitle.setTextColor(colors.textPrimary);

        // 统计数字
        if (tvTotalDiary != null) tvTotalDiary.setTextColor(colors.textPrimary);
        if (tvPositiveDiary != null) tvPositiveDiary.setTextColor(isDark ? 0xFF48BB78 : colors.primary);
        if (tvNeutralDiary != null) tvNeutralDiary.setTextColor(colors.textSecondary);
        if (tvNegativeDiary != null) tvNegativeDiary.setTextColor(isDark ? 0xFFF687B3 : colors.secondary);

        // 情感分析结果 & 建议
        if (tvEmotionAnalysisResult != null) tvEmotionAnalysisResult.setTextColor(colors.textSecondary);
        if (tvWeeklySummary != null) tvWeeklySummary.setTextColor(colors.textPrimary);
        if (tvAdvice != null) tvAdvice.setTextColor(colors.textSecondary);

        // 心情语录
        if (tvQuote != null) tvQuote.setTextColor(colors.textPrimary);

        // 深色模式递归
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }
}
