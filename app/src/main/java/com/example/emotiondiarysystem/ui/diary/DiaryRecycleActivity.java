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
            View view = getLayoutInflater().inflate(R.layout.item_diary_card, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Diary diary = diaries.get(position);
            holder.tvContent.setText(diary.getContent());
            holder.tvDate.setText(diary.getCreateTime().substring(0, 10));
            holder.tvEmotion.setText(diary.getEmotionType());

            // 设置情感标签颜色
            switch (diary.getEmotionType()) {
                case "积极":
                    holder.tvEmotion.setTextColor(0xFF48BB78);
                    break;
                case "消极":
                    holder.tvEmotion.setTextColor(0xFFF56565);
                    break;
                default:
                    holder.tvEmotion.setTextColor(0xFFECC94B);
                    break;
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
            TextView tvContent;
            TextView tvDate;
            TextView tvEmotion;
            TextView btnRestore;
            TextView btnDelete;

            public ViewHolder(View itemView) {
                super(itemView);
                tvContent = itemView.findViewById(R.id.tv_content);
                tvDate = itemView.findViewById(R.id.tv_date);
                tvEmotion = itemView.findViewById(R.id.tv_emotion);

                // 添加恢复和删除按钮
                LinearLayout buttonLayout = new LinearLayout(DiaryRecycleActivity.this);
                buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
                buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                buttonLayout.setPadding(0, 12, 0, 0);

                btnRestore = new TextView(DiaryRecycleActivity.this);
                btnRestore.setText("恢复");
                btnRestore.setTextSize(14);
                btnRestore.setTextColor(0xFF3182CE);
                btnRestore.setPadding(16, 8, 16, 8);
                btnRestore.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));
                btnRestore.setGravity(View.TEXT_ALIGNMENT_CENTER);

                btnDelete = new TextView(DiaryRecycleActivity.this);
                btnDelete.setText("彻底删除");
                btnDelete.setTextSize(14);
                btnDelete.setTextColor(0xFFF56565);
                btnDelete.setPadding(16, 8, 16, 8);
                btnDelete.setLayoutParams(new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                ));
                btnDelete.setGravity(View.TEXT_ALIGNMENT_CENTER);

                buttonLayout.addView(btnRestore);
                buttonLayout.addView(btnDelete);

                // 获取CardView内部的LinearLayout并添加按钮布局
                LinearLayout linearLayout = itemView.findViewById(android.R.id.content);
                if (linearLayout == null) {
                    // 如果找不到android.R.id.content，尝试获取直接子视图
                    if (itemView instanceof androidx.cardview.widget.CardView) {
                        androidx.cardview.widget.CardView cardView = (androidx.cardview.widget.CardView) itemView;
                        if (cardView.getChildCount() > 0 && cardView.getChildAt(0) instanceof LinearLayout) {
                            linearLayout = (LinearLayout) cardView.getChildAt(0);
                        }
                    }
                }
                if (linearLayout != null) {
                    linearLayout.addView(buttonLayout);
                }
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