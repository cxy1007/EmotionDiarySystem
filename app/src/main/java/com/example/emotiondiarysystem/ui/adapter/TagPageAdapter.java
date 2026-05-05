package com.example.emotiondiarysystem.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.Tag;

import java.util.List;

public class TagPageAdapter extends RecyclerView.Adapter<TagPageAdapter.TagPageViewHolder> {

    private List<List<Tag>> pages;
    private List<String> greetings;
    private TagAdapter.OnTagClickListener tagClickListener;

    public interface OnPageDataChangedListener {
        void onPageDataChanged();
    }

    private OnPageDataChangedListener pageDataChangedListener;

    public TagPageAdapter(List<List<Tag>> pages, List<String> greetings, TagAdapter.OnTagClickListener listener) {
        this.pages = pages;
        this.greetings = greetings;
        this.tagClickListener = listener;
    }

    public void setPageDataChangedListener(OnPageDataChangedListener listener) {
        this.pageDataChangedListener = listener;
    }

    @NonNull
    @Override
    public TagPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_tag_selector, parent, false);
        return new TagPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagPageViewHolder holder, int position) {
        holder.tvGreeting.setText(greetings.get(position));

        List<Tag> tagList = pages.get(position);
        TagAdapter adapter = new TagAdapter(tagList, new TagAdapter.OnTagClickListener() {
            @Override
            public void onTagClick(Tag tag) {
                if (tagClickListener != null) {
                    tagClickListener.onTagClick(tag);
                }
                if (pageDataChangedListener != null) {
                    pageDataChangedListener.onPageDataChanged();
                }
            }

            @Override
            public void onCustomTagInput(String customText) {
                Tag customTag = new Tag(customText, false);
                customTag.setSelected(true);
                tagList.add(tagList.size() - 1, customTag);
                notifyItemChanged(position);
                if (tagClickListener != null) {
                    tagClickListener.onCustomTagInput(customText);
                }
                if (pageDataChangedListener != null) {
                    pageDataChangedListener.onPageDataChanged();
                }
            }
        });

        holder.rvTags.setLayoutManager(new GridLayoutManager(holder.itemView.getContext(), 3));
        holder.rvTags.addItemDecoration(new GridSpacingItemDecoration(3, 12, true));
        holder.rvTags.setAdapter(adapter);
        holder.rvTags.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    public List<List<Tag>> getPages() {
        return pages;
    }

    static class TagPageViewHolder extends RecyclerView.ViewHolder {
        TextView tvGreeting;
        RecyclerView rvTags;

        public TagPageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGreeting = itemView.findViewById(R.id.tv_greeting);
            rvTags = itemView.findViewById(R.id.rv_tags);
        }
    }
}
