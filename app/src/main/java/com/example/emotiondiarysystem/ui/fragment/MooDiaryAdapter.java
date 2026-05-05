package com.example.emotiondiarysystem.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.utils.MoodQuotesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Moo日记风格的适配器
 * 支持引导卡片和日记卡片两种类型
 * 支持日记卡片翻转显示心情日签
 */
public class MooDiaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_GUIDE = 0;  // 引导卡片类型
    private static final int TYPE_DIARY = 1;  // 日记卡片类型

    private List<Diary> diaryList;
    private OnDiaryActionListener listener;

    public interface OnDiaryActionListener {
        void onWriteDiaryClick();  // 点击引导卡片的"记录我的今天"
        void onDiaryClick(Diary diary, int position);  // 点击日记卡片
    }

    public MooDiaryAdapter(List<Diary> diaryList, OnDiaryActionListener listener) {
        this.diaryList = diaryList;
        this.listener = listener;
    }

    public void updateData(List<Diary> newList) {
        this.diaryList = newList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        // 第0项是引导卡片，其他是日记卡片
        return position == 0 ? TYPE_GUIDE : TYPE_DIARY;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GUIDE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_guide_card, parent, false);
            // 设置卡片宽度为屏幕的80%
            int screenWidth = getScreenWidth(parent.getContext());
            int cardWidth = (int) (screenWidth * 0.8);
            view.getLayoutParams().width = cardWidth;
            return new GuideViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_diary_card, parent, false);
            // 设置卡片宽度为屏幕的80%
            int screenWidth = getScreenWidth(parent.getContext());
            int cardWidth = (int) (screenWidth * 0.8);
            view.getLayoutParams().width = cardWidth;
            return new DiaryViewHolder(view);
        }
    }

    /**
     * 获取屏幕宽度
     */
    private int getScreenWidth(android.content.Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return displayMetrics.widthPixels;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GuideViewHolder) {
            ((GuideViewHolder) holder).bind(listener);
        } else if (holder instanceof DiaryViewHolder) {
            // 日记列表从第1项开始，对应diaryList的第0项
            Diary diary = diaryList.get(position - 1);
            ((DiaryViewHolder) holder).bind(diary, position, listener);
        }
    }

    @Override
    public int getItemCount() {
        // 引导卡片 + 日记数量
        return 1 + (diaryList != null ? diaryList.size() : 0);
    }

    /**
     * 引导卡片ViewHolder
     */
    static class GuideViewHolder extends RecyclerView.ViewHolder {
        private TextView tvDay;
        private TextView tvYearMonth;
        private Button btnRecordToday;

        public GuideViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvYearMonth = itemView.findViewById(R.id.tv_year_month);
            btnRecordToday = itemView.findViewById(R.id.btn_record_today);
        }

        public void bind(OnDiaryActionListener listener) {
            // 设置今天的日期 - 完整显示「数字+年份+月份」
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd", Locale.getDefault());
            SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy年M月", Locale.getDefault());
            Date today = new Date();
            
            tvDay.setText(dayFormat.format(today));
            tvYearMonth.setText(yearMonthFormat.format(today));

            // 点击按钮跳转到写日记
            btnRecordToday.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onWriteDiaryClick();
                }
            });
        }
    }

    /**
     * 日记卡片ViewHolder，支持翻转
     */
    static class DiaryViewHolder extends RecyclerView.ViewHolder {
        private CardView cardFront;
        private CardView cardBack;
        private TextView tvDay;
        private TextView tvMonth;
        private TextView tvTitle;
        private TextView tvContent;
        private Button btnMoodTag;
        private TextView tvDayBack;
        private TextView tvMonthBack;
        private TextView tvMoodQuote;
        private TextView tvMoodQuoteAuthor;
        private Button btnFlipBack;

        private boolean isFlipped = false;  // 是否已翻转

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFront = itemView.findViewById(R.id.card_front);
            cardBack = itemView.findViewById(R.id.card_back);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
            btnMoodTag = itemView.findViewById(R.id.btn_mood_tag);
            tvDayBack = itemView.findViewById(R.id.tv_day_back);
            tvMonthBack = itemView.findViewById(R.id.tv_month_back);
            tvMoodQuote = itemView.findViewById(R.id.tv_mood_quote);
            tvMoodQuoteAuthor = itemView.findViewById(R.id.tv_mood_quote_author);
            btnFlipBack = itemView.findViewById(R.id.btn_flip_back);
        }

        public void bind(Diary diary, int position, OnDiaryActionListener listener) {
            // 设置正面日期 - 不显示年份
            String date = diary.getCreateTime();
            String day = "--";
            String month = "";
            if (date != null && date.length() >= 10) {
                day = date.substring(8, 10);
                String monthNum = date.substring(5, 7);
                month = Integer.parseInt(monthNum) + "月";
            }
            
            // 正面日期
            tvDay.setText(day);
            tvMonth.setText(month);
            
            // 背面日期（和正面一致）
            tvDayBack.setText(day);
            tvMonthBack.setText(month);

            // 设置标题 - 有标题就显示，没有就隐藏
            String title = diary.getTitle();
            if (title != null && !title.isEmpty()) {
                tvTitle.setText(title);
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }

            // 设置内容预览（显示前两行，由maxLines=2控制）
            String content = diary.getContent();
            if (content != null && !content.isEmpty()) {
                tvContent.setText(content);
            } else {
                tvContent.setText("");
            }

            // 设置心情日签语录
            String moodTag = diary.getMoodTag();
            if (moodTag == null || moodTag.isEmpty()) {
                // 根据情绪类型推断心情标签
                moodTag = MoodQuotesUtil.inferMoodTag(diary.getEmotionType());
            }
            String[] quote = MoodQuotesUtil.getRandomQuote(moodTag);
            tvMoodQuote.setText(quote[0]);
            tvMoodQuoteAuthor.setText(quote[1]);

            // 点击心情日签按钮 - 翻转到背面
            btnMoodTag.setOnClickListener(v -> {
                if (!isFlipped) {
                    flipCard(cardFront, cardBack);
                    isFlipped = true;
                }
            });

            // 点击返回按钮 - 翻转回正面
            btnFlipBack.setOnClickListener(v -> {
                if (isFlipped) {
                    flipCard(cardBack, cardFront);
                    isFlipped = false;
                }
            });

            // 点击卡片正面跳转到编辑页
            cardFront.setOnClickListener(v -> {
                if (listener != null && !isFlipped) {
                    listener.onDiaryClick(diary, position);
                }
            });
        }

        /**
         * 卡片翻转动画
         */
        private void flipCard(View fromView, View toView) {
            // 正面翻转出去（Y轴从0到-90度）
            ObjectAnimator flipOut = ObjectAnimator.ofFloat(fromView, "rotationY", 0f, -90f);
            flipOut.setDuration(150);

            // 背面翻转进来（Y轴从90到0度）
            ObjectAnimator flipIn = ObjectAnimator.ofFloat(toView, "rotationY", 90f, 0f);
            flipIn.setDuration(150);

            flipOut.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fromView.setVisibility(View.GONE);
                    toView.setVisibility(View.VISIBLE);
                    flipIn.start();
                }
            });

            flipOut.start();
        }
    }
}