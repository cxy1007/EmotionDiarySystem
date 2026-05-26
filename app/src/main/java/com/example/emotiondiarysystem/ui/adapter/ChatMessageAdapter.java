package com.example.emotiondiarysystem.ui.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemLongClick(int position);
        void onItemClick(int position);
    }

    private List<ChatMessage> messages;
    private Context context;
    private boolean isDeleteMode = false;
    private OnItemClickListener listener;

    public ChatMessageAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setDeleteMode(boolean isDeleteMode) {
        this.isDeleteMode = isDeleteMode;
        if (!isDeleteMode) {
            for (ChatMessage msg : messages) {
                msg.setSelected(false);
            }
        }
        notifyDataSetChanged();
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    public List<ChatMessage> getSelectedMessages() {
        List<ChatMessage> selected = new ArrayList<>();
        for (ChatMessage msg : messages) {
            if (msg.isSelected()) {
                selected.add(msg);
            }
        }
        return selected;
    }

    public void deleteSelectedMessages() {
        List<ChatMessage> newMessages = new ArrayList<>();
        for (ChatMessage msg : messages) {
            if (!msg.isSelected()) {
                newMessages.add(msg);
            }
        }
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public List<ChatMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }

    public void setMessages(List<ChatMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void removeLastMessage() {
        if (!messages.isEmpty()) {
            int position = messages.size() - 1;
            messages.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clearMessages() {
        int size = messages.size();
        messages.clear();
        notifyItemRangeRemoved(0, size);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.tvMessage.setText(message.getContent());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.llMessageContainer.getLayoutParams();
        switch (message.getType()) {
            case ChatMessage.TYPE_SYSTEM:
                holder.llMessageContainer.setBackgroundResource(R.drawable.bg_bubble_system);
                params.gravity = android.view.Gravity.CENTER_HORIZONTAL;
                break;
            case ChatMessage.TYPE_USER:
                holder.llMessageContainer.setBackgroundResource(R.drawable.bg_bubble_user);
                params.gravity = android.view.Gravity.END;
                break;
            case ChatMessage.TYPE_AI:
            default:
                holder.llMessageContainer.setBackgroundResource(R.drawable.bg_bubble_ai);
                params.gravity = android.view.Gravity.START;
                break;
        }
        holder.llMessageContainer.setLayoutParams(params);

        // 处理删除模式
        if (isDeleteMode) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.cbSelect.setChecked(message.isSelected());
            
            holder.cbSelect.setOnClickListener(v -> {
                message.setSelected(holder.cbSelect.isChecked());
            });
            
            holder.itemView.setOnClickListener(v -> {
                message.setSelected(!message.isSelected());
                holder.cbSelect.setChecked(message.isSelected());
            });
        } else {
            holder.cbSelect.setVisibility(View.GONE);
            
            holder.itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClick(position);
                }
                return true;
            });
            
            holder.itemView.setOnClickListener(v -> {
                // 普通模式点击不做处理
            });
        }
        
        // 只有在非删除模式下才处理长按复制或进入删除模式
        if (!isDeleteMode) {
            holder.itemView.setOnLongClickListener(v -> {
                if (listener != null) {
                    listener.onItemLongClick(position);
                }
                return true;
            });
        }
    }

    private void showPopupMenu(View view, String content) {
        PopupMenu popup = new PopupMenu(context, view);
        popup.inflate(R.menu.chat_menu);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_copy) {
                copyToClipboard(content);
                Toast.makeText(context, R.string.ai_copied, Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void copyToClipboard(String content) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("ChatMessage", content);
        clipboard.setPrimaryClip(clip);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        LinearLayout llMessageContainer;
        TextView tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cb_select);
            llMessageContainer = itemView.findViewById(R.id.ll_message_container);
            tvMessage = itemView.findViewById(R.id.tv_message);
        }
    }
}
