package com.example.emotiondiarysystem.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.emotion.MonthLineChartView;
import com.example.emotiondiarysystem.ui.emotion.WeekBarChartView;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmotionStatFragment extends Fragment {

    private TextView tvTotalDiary;
    private TextView tvPositiveDiary;
    private TextView tvNeutralDiary;
    private TextView tvNegativeDiary;
    private TextView tvWeeklySummary;
    private TextView tvAdvice;
    private TextView tvEmotionAnalysisResult;
    private TextView tvQuote;
    private TextView tvQuoteAuthor;
    private LinearLayout topBar;
    private WeekBarChartView weekBarChart;
    private TextView tvWeekNoData;
    private MonthLineChartView monthLineChart;
    private TextView tvMonthNoData;

    private DiaryManager diaryManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_emotion_stat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        diaryManager = new DiaryManager(requireContext());
    }

    private void initViews(View view) {
        tvTotalDiary = view.findViewById(R.id.tv_total_diary);
        tvPositiveDiary = view.findViewById(R.id.tv_positive_diary);
        tvNeutralDiary = view.findViewById(R.id.tv_neutral_diary);
        tvNegativeDiary = view.findViewById(R.id.tv_negative_diary);
        tvWeeklySummary = view.findViewById(R.id.tv_weekly_summary);
        tvAdvice = view.findViewById(R.id.tv_advice);
        tvEmotionAnalysisResult = view.findViewById(R.id.tv_emotion_analysis_result);
        tvQuote = view.findViewById(R.id.tv_quote);
        tvQuoteAuthor = view.findViewById(R.id.tv_quote_author);
        topBar = view.findViewById(R.id.top_bar);
        weekBarChart = view.findViewById(R.id.week_bar_chart);
        tvWeekNoData = view.findViewById(R.id.tv_week_no_data);
        monthLineChart = view.findViewById(R.id.month_line_chart);
        tvMonthNoData = view.findViewById(R.id.tv_month_no_data);
    }

    private static final String[] QUOTES = {
        "「生活中不缺少美，缺少的是发现美的眼睛。」", "「每一个不曾起舞的日子，都是对生命的辜负。」",
        "「世界上只有一种真正的英雄主义，那就是认清生活的真相后依然热爱生活。」",
        "「不要着急，最好的总会在最不经意的时候出现。」",
        "「人的一切痛苦，本质上都是对自己无能的愤怒。」",
        "「世界上最宽阔的是海洋，比海洋更宽阔的是天空，比天空更宽阔的是人的心灵。」",
        "「人生没有白走的路，每一步都算数。」",
        "「真正的平静，不是远离车马喧嚣，而是在内心修篱种菊。」",
        "「愿你所有的日子，都比不上明天的光辉。」",
        "「温柔的晚风，傍晚的晚霞，人间的美好多着呢。」"
    };
    private static final String[] QUOTE_AUTHORS = {
        "— 罗丹", "— 尼采", "— 罗曼·罗兰",
        "— 泰戈尔", "— 王小波", "— 雨果",
        "— 李宗盛", "— 林徽因", "— 莎士比亚", "— 网络语录"
    };

    @Override
    public void onResume() {
        super.onResume();
        loadAllData();
        applyFullTheme();
    }

    private void loadAllData() {
        int userId = SpUtil.getInt(requireContext(), "userId", -1);
        if (userId == -1) {
            resetStats();
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

        loadEmotionStats(total, positive, neutral, negative);
        loadEmotionAnalysisResult(diaries, positive, neutral, negative);
        loadWeeklyChart(diaries);
        loadMonthlyChart(diaries);
        loadMoodQuote(positive, neutral, negative);
        updateAdvice(total, positive, neutral, negative);
    }

    private void loadEmotionStats(int total, int positive, int neutral, int negative) {
        if (tvTotalDiary != null) tvTotalDiary.setText(String.valueOf(total));
        if (tvPositiveDiary != null) tvPositiveDiary.setText(String.valueOf(positive));
        if (tvNeutralDiary != null) tvNeutralDiary.setText(String.valueOf(neutral));
        if (tvNegativeDiary != null) tvNegativeDiary.setText(String.valueOf(negative));
    }

    private void loadEmotionAnalysisResult(List<Diary> diaries, int positive, int neutral, int negative) {
        if (tvEmotionAnalysisResult == null) return;
        if (diaries.isEmpty()) {
            tvEmotionAnalysisResult.setText("暂无分析结果，请先记录几篇日记吧");
            return;
        }
        int total = diaries.size();
        double pR = positive * 100.0 / total;
        double nR = neutral * 100.0 / total;
        double ngR = negative * 100.0 / total;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.getDefault(),
            "共 %d 篇，积极 %.0f%%，中性 %.0f%%，消极 %.0f%%。\n\n", total, pR, nR, ngR));

        if (positive >= neutral && positive >= negative) {
            sb.append("主导情绪：积极 — 您的状态非常阳光！");
        } else if (negative >= neutral && negative >= positive) {
            sb.append("主导情绪：消极 — 最近压��可能较大，记得照顾好自己。");
        } else {
            sb.append("主导情绪：中性 — 情绪整体稳定，保持记录的习惯。");
        }
        tvEmotionAnalysisResult.setText(sb.toString());
    }

    private void loadWeeklyChart(List<Diary> diaries) {
        if (weekBarChart == null || tvWeekNoData == null) return;
        int[] pos = new int[7];
        int[] neu = new int[7];
        int[] neg = new int[7];

        // 仅统计本周（周一 00:00:00 到 周日 23:59:59）
        Calendar weekStart = Calendar.getInstance();
        weekStart.setFirstDayOfWeek(Calendar.MONDAY);
        weekStart.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);
        weekStart.set(Calendar.MILLISECOND, 0);

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_MONTH, 6);
        weekEnd.set(Calendar.HOUR_OF_DAY, 23);
        weekEnd.set(Calendar.MINUTE, 59);
        weekEnd.set(Calendar.SECOND, 59);
        weekEnd.set(Calendar.MILLISECOND, 999);

        for (Diary d : diaries) {
            Date date = parseDiaryDate(d.getCreateTime());
            if (date == null) continue;

            Calendar itemCal = Calendar.getInstance();
            itemCal.setTime(date);
            if (itemCal.before(weekStart) || itemCal.after(weekEnd)) continue;

            int weekday = itemCal.get(Calendar.DAY_OF_WEEK);
            int idx;
            if (weekday == Calendar.SUNDAY) {
                idx = 6;
            } else {
                idx = weekday - Calendar.MONDAY; // 周一->0 ... 周六->5
            }

            String emotion = d.getEmotionType();
            if ("积极".equals(emotion)) {
                pos[idx]++;
            } else if ("中性".equals(emotion)) {
                neu[idx]++;
            } else if ("消极".equals(emotion)) {
                neg[idx]++;
            }
        }

        String[] labels = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
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

    private void loadMonthlyChart(List<Diary> diaries) {
        if (monthLineChart == null || tvMonthNoData == null) return;
        int[] pos = new int[31];
        int[] neu = new int[31];
        int[] neg = new int[31];

        Calendar now = Calendar.getInstance();
        int curYear = now.get(Calendar.YEAR);
        int curMonth = now.get(Calendar.MONTH);
        int daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH);

        for (Diary d : diaries) {
            Date date = parseDiaryDate(d.getCreateTime());
            if (date == null) continue;

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) == curYear && cal.get(Calendar.MONTH) == curMonth) {
                int day = cal.get(Calendar.DAY_OF_MONTH);
                String emotion = d.getEmotionType();
                if ("积极".equals(emotion)) {
                    pos[day - 1]++;
                } else if ("中性".equals(emotion)) {
                    neu[day - 1]++;
                } else if ("消极".equals(emotion)) {
                    neg[day - 1]++;
                }
            }
        }

        boolean hasData = false;
        for (int i = 0; i < daysInMonth; i++) {
            if (pos[i] > 0 || neu[i] > 0 || neg[i] > 0) {
                hasData = true;
                break;
            }
        }

        if (hasData) {
            monthLineChart.setVisibility(View.VISIBLE);
            tvMonthNoData.setVisibility(View.GONE);
            monthLineChart.setData(pos, neu, neg, daysInMonth);
        } else {
            monthLineChart.setVisibility(View.GONE);
            tvMonthNoData.setVisibility(View.VISIBLE);
        }
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

    private void loadMoodQuote(int positive, int neutral, int negative) {
        if (tvQuote == null || tvQuoteAuthor == null) return;
        int total = positive + neutral + negative;
        int idx = total == 0 ? 0 : (negative * 100.0 / total >= 50 ? 4 : (positive * 100.0 / total >= 50 ? 0 : 2));
        idx += (int)(Math.random() * 3);
        idx = Math.max(0, Math.min(idx, QUOTES.length - 1));
        tvQuote.setText(QUOTES[idx]);
        tvQuoteAuthor.setText(QUOTE_AUTHORS[idx]);
    }

    private void updateAdvice(int total, int positive, int neutral, int negative) {
        if (total == 0) {
            tvWeeklySummary.setText("暂无数据，开始写日记后查看趋势");
            tvAdvice.setText("每天记录日记可以帮助您更好地了解自己的情绪变化哦");
            return;
        }

        double positiveRate = total > 0 ? (positive * 100.0 / total) : 0;
        double negativeRate = total > 0 ? (negative * 100.0 / total) : 0;

        String summary = String.format("您已记录 %d 篇日记，其中积极 %.0f%%，消极 %.0f%%", total, positiveRate, negativeRate);
        tvWeeklySummary.setText(summary);

        String advice;
        if (positiveRate >= 70) {
            advice = "太棒了！您最近的情绪非常积极阳光，请继续保持！\n建议：可以尝试分享您的快乐，让更多人感受到正能量。";
        } else if (positiveRate >= 40) {
            advice = "您的情绪整体稳定，保持得很好！\n建议：适当增加一些户外活动，可以让心情更加愉悦。";
        } else if (negativeRate >= 50) {
            advice = "最近可能遇到了一些困难或压力，请不要担心，这是很正常的。\n建议：可以尝试写日记记录负面情绪，或者找朋友倾诉一下。";
        } else {
            advice = "您的情绪处于波动期，这是正常的。\n建议：保持记录日记的习惯，关注自己的情绪变化，必要时寻求帮助。";
        }
        tvAdvice.setText(advice);
    }

    private void resetStats() {
        tvTotalDiary.setText("0");
        tvPositiveDiary.setText("0");
        tvNeutralDiary.setText("0");
        tvNegativeDiary.setText("0");
        tvWeeklySummary.setText("请先登录后查看情感统计");
        tvAdvice.setText("登录后开始记录日记，我会为您分析情绪趋势");
    }

    private void applyFullTheme() {
        if (getView() == null) return;
        ThemeColorUtil.ThemeColors colors = ThemeColorUtil.getCurrentTheme(requireContext());
        boolean isDark = ThemeColorUtil.isDarkMode(requireContext());

        getView().setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);

        applyRecursive(getView(), colors);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getView(), colors, isDark);
    }

    private void applyRecursive(View view, ThemeColorUtil.ThemeColors colors) {
        if (view == null) return;
        if (view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup) view;
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                if (child instanceof TextView) {
                    TextView tv = (TextView) child;
                    int color = tv.getCurrentTextColor();
                    if (isPrimaryColor(color)) {
                        tv.setTextColor(colors.textPrimary);
                    } else if (isSecondaryColor(color)) {
                        tv.setTextColor(colors.textSecondary);
                    }
                }
                if (child instanceof ViewGroup) {
                    applyRecursive(child, colors);
                }
            }
        }
    }

    private boolean isPrimaryColor(int color) {
        return matches(color, 0xFF2D3748) || matches(color, 0xFF4A5568)
            || matches(color, 0xFF702459) || matches(color, 0xFF2A4365)
            || matches(color, 0xFF22543D);
    }

    private boolean isSecondaryColor(int color) {
        return matches(color, 0xFF718096) || matches(color, 0xFF888888)
            || matches(color, 0xFFB83280) || matches(color, 0xFF2B6CB0)
            || matches(color, 0xFF276749);
    }

    private boolean matches(int c1, int c2) {
        return Math.abs(((c1 >> 16) & 0xFF) - ((c2 >> 16) & 0xFF)) < 30
            && Math.abs(((c1 >> 8) & 0xFF) - ((c2 >> 8) & 0xFF)) < 30
            && Math.abs((c1 & 0xFF) - (c2 & 0xFF)) < 30;
    }
}
