package com.example.emotiondiarysystem.ui.adapter;

import android.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Tag;

import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {

    private List<Tag> tagList;
    private OnTagClickListener onTagClickListener;

    public interface OnTagClickListener {
        void onTagClick(Tag tag);
        void onCustomTagInput(String customText);
    }

    public TagAdapter(List<Tag> tagList, OnTagClickListener listener) {
        this.tagList = tagList;
        this.onTagClickListener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.tvTag.setText(tag.getName());
        holder.tvTag.setSelected(tag.isSelected());

        holder.itemView.setOnClickListener(v -> {
            if (tag.isCustom()) {
                showCustomInputDialog(v.getContext());
            } else {
                tag.setSelected(!tag.isSelected());
                notifyItemChanged(position);
                if (onTagClickListener != null) {
                    onTagClickListener.onTagClick(tag);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public List<Tag> getTagList() {
        return tagList;
    }

    private void showCustomInputDialog(android.content.Context context) {
        EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setHint("请输入自定义标签");

        new AlertDialog.Builder(context)
                .setTitle("自定义标签")
                .setView(editText)
                .setPositiveButton("确定", (dialog, which) -> {
                    String input = editText.getText().toString().trim();
                    if (!input.isEmpty() && onTagClickListener != null) {
                        onTagClickListener.onCustomTagInput(input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    static class TagViewHolder extends RecyclerView.ViewHolder {
        TextView tvTag;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTag = itemView.findViewById(R.id.tv_tag);
        }
    }
}
