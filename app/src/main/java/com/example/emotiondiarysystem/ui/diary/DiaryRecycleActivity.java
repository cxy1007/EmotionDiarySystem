package com.example.emotiondiarysystem.ui.diary;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.base.BaseActivity;
import com.example.emotiondiarysystem.utils.SpUtil;
import com.example.emotiondiarysystem.utils.ThemeColorUtil;
import com.example.emotiondiarysystem.utils.ToastUtil;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class DiaryRecycleActivity extends BaseActivity implements View.OnClickListener {

    private ImageButton btnBack;
    private TextView tvTitle;
    private RecyclerView rvRecycle;
    private TextView tvEmpty;
    private LinearLayout topBar;
    private View divider;

    private DiaryManager diaryManager;
    private RecycleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_recycle);

        initViews();
        initManagers();
        setListeners();
        loadRecycledDiaries();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyFullTheme();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvTitle = findViewById(R.id.tv_title);
        rvRecycle = findViewById(R.id.rv_recycle);
        tvEmpty = findViewById(R.id.tv_empty);
        topBar = findViewById(R.id.top_bar);
        divider = findViewById(R.id.divider);

        rvRecycle.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initManagers() {
        diaryManager = new DiaryManager(this);
    }

    private void setListeners() {
        btnBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            finish();
        }
    }

    private void loadRecycledDiaries() {
        int userId = SpUtil.getInt(this, "userId", -1);
        if (userId == -1) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvRecycle.setVisibility(View.GONE);
            return;
        }

        List<Diary> diaries = diaryManager.getDeletedDiaryListByUserId(userId);
        if (diaries == null || diaries.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvRecycle.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvRecycle.setVisibility(View.VISIBLE);
            adapter = new RecycleAdapter(diaries);
            rvRecycle.setAdapter(adapter);
        }
    }

    private class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {

        private List<Diary> diaries;

        public RecycleAdapter(List<Diary> diaries) {
            this.diaries = diaries;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.item_recycle_diary, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Diary diary = diaries.get(position);
            
            // 显示日期
            String createTime = diary.getCreateTime();
            if (createTime != null && createTime.length() >= 10) {
                holder.tvDate.setText(createTime.substring(0, 10));
            } else {
                holder.tvDate.setText("");
            }
            
            // 显示情感类型
            String emotionType = diary.getEmotionType();
            if (emotionType != null) {
                holder.tvEmotion.setText(emotionType);
                // 设置情感标签颜色
                switch (emotionType) {
                    case "积极":
                        holder.tvEmotion.setBackgroundColor(0xFF48BB78);
                        break;
                    case "消极":
                        holder.tvEmotion.setBackgroundColor(0xFFF56565);
                        break;
                    default:
                        holder.tvEmotion.setBackgroundColor(0xFFECC94B);
                        break;
                }
            } else {
                holder.tvEmotion.setText("");
                holder.tvEmotion.setBackgroundColor(0xFF888888);
            }
            
            // 显示标题
            String title = diary.getTitle();
            if (title != null && !title.isEmpty()) {
                holder.tvTitle.setText(title);
                holder.tvTitle.setVisibility(View.VISIBLE);
            } else {
                holder.tvTitle.setVisibility(View.GONE);
            }
            
            // 显示内容
            String content = diary.getContent();
            if (content != null) {
                holder.tvContent.setText(content);
            } else {
                holder.tvContent.setText("");
            }

            holder.btnRestore.setOnClickListener(v -> {
                int result = diaryManager.restoreDiary(diary.getDiaryId());
                if (result > 0) {
                    ToastUtil.showShort(DiaryRecycleActivity.this, "日记已恢复");
                    diaries.remove(position);
                    notifyItemRemoved(position);
                    if (diaries.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvRecycle.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.showShort(DiaryRecycleActivity.this, "恢复失败");
                }
            });

            holder.btnDelete.setOnClickListener(v -> {
                int result = diaryManager.deleteDiaryPermanently(diary.getDiaryId());
                if (result > 0) {
                    ToastUtil.showShort(DiaryRecycleActivity.this, "日记已彻底删除");
                    diaries.remove(position);
                    notifyItemRemoved(position);
                    if (diaries.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvRecycle.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.showShort(DiaryRecycleActivity.this, "删除失败");
                }
            });
        }

        @Override
        public int getItemCount() {
            return diaries.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate;
            TextView tvEmotion;
            TextView tvTitle;
            TextView tvContent;
            TextView btnRestore;
            TextView btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvEmotion = itemView.findViewById(R.id.tv_emotion);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvContent = itemView.findViewById(R.id.tv_content);
                btnRestore = itemView.findViewById(R.id.btn_restore);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }

    private void applyFullTheme() {
        ThemeColorUtil.ThemeColors colors = getCurrentColors();
        if (colors == null) colors = ThemeColorUtil.getCurrentTheme(this);
        boolean isDark = ThemeColorUtil.isDarkMode(this);

        View main = findViewById(R.id.main);
        if (main != null) main.setBackgroundColor(colors.background);

        if (topBar != null) topBar.setBackgroundColor(colors.surface);
        if (divider != null) divider.setBackgroundColor(colors.divider);

        btnBack.setColorFilter(colors.iconTint);
        tvTitle.setTextColor(colors.textPrimary);
        tvEmpty.setTextColor(colors.textSecondary);

        // 递归处理所有子视图的浅色背景
        ThemeColorUtil.applyDarkModeRecursive(getWindow().getDecorView(), colors, isDark);
    }
}