package com.example.emotiondiarysystem.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.emotiondiarysystem.R;

import java.io.File;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_PHOTO = 1;

    private List<String> photoPaths;
    private OnPhotoClickListener onPhotoClickListener;
    private boolean isEditMode = true;

    public interface OnPhotoClickListener {
        void onAddClick();
        void onPhotoClick(int position);
        void onDeleteClick(int position);
    }

    public PhotoAdapter(List<String> photoPaths, OnPhotoClickListener listener) {
        this.photoPaths = photoPaths;
        this.onPhotoClickListener = listener;
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (isEditMode && position == photoPaths.size()) {
            return TYPE_ADD;
        }
        return TYPE_PHOTO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_add, parent, false);
            return new AddViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
            return new PhotoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddViewHolder) {
            AddViewHolder addViewHolder = (AddViewHolder) holder;
            addViewHolder.itemView.setOnClickListener(v -> {
                if (onPhotoClickListener != null) {
                    onPhotoClickListener.onAddClick();
                }
            });
        } else if (holder instanceof PhotoViewHolder) {
            PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
            String path = photoPaths.get(position);
            
            // 使用Glide加载图片
            File file = new File(path);
            if (file.exists()) {
                Glide.with(photoViewHolder.ivPhoto.getContext())
                        .load(file)
                        .centerCrop()
                        .into(photoViewHolder.ivPhoto);
            } else {
                photoViewHolder.ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // 设置删除按钮
            if (isEditMode) {
                photoViewHolder.btnDelete.setVisibility(View.VISIBLE);
                photoViewHolder.btnDelete.setOnClickListener(v -> {
                    if (onPhotoClickListener != null) {
                        onPhotoClickListener.onDeleteClick(position);
                    }
                });
            } else {
                photoViewHolder.btnDelete.setVisibility(View.GONE);
            }

            // 点击预览
            photoViewHolder.ivPhoto.setOnClickListener(v -> {
                if (onPhotoClickListener != null) {
                    onPhotoClickListener.onPhotoClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (isEditMode) {
            return photoPaths.size() + 1;
        }
        return photoPaths.size();
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        public AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPhoto;
        ImageButton btnDelete;

        public PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.iv_photo);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
