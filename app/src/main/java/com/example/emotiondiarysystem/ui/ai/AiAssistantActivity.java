package com.example.emotiondiarysystem.ui.ai;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.emotiondiarysystem.R;
import com.example.emotiondiarysystem.bean.ChatMessage;
import com.example.emotiondiarysystem.bean.Diary;
import com.example.emotiondiarysystem.manager.DiaryManager;
import com.example.emotiondiarysystem.ui.adapter.ChatMessageAdapter;
import com.example.emotiondiarysystem.utils.DateUtil;
import com.example.emotiondiarysystem.utils.DoubaoApiHelper;
import com.example.emotiondiarysystem.utils.SessionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AiAssistantActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "ai_chat_prefs";
    private static final String KEY_NORMAL_MODE_HISTORY = "normal_mode_history";
    private static final String KEY_ANALYSIS_MODE_HISTORY = "analysis_mode_history";
    private static final String KEY_CURRENT_MODE = "current_mode"; // 0 = 正常模式, 1 = 分析模式

    private RecyclerView rvChat;
    private EditText etInput;
    private Button btnSend;
    private ImageView ivBack;
    private Switch switchMode;
    private Button btnCancelDelete;
    private Button btnDelete;

    private ChatMessageAdapter adapter;
    private DiaryManager diaryManager;
    private int userId;
    private List<Diary> todayDiaryList = new ArrayList<>();
    private boolean[] selectedDiaries;
    private SharedPreferences sharedPreferences;
    
    // 当前模式: true = 分析模式, false = 正常模式
    private boolean isAnalysisMode = false;
    
    // 两个模式的会话历史
    private List<ChatMessage> normalModeMessages = new ArrayList<>();
    private List<ChatMessage> analysisModeMessages = new ArrayList<>();
    private List<DoubaoApiHelper.ChatMessage> conversationHistory = new ArrayList<>();

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AiAssistantActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);

        initViews();
        initData();
        loadAllHistories();
        loadTodayDiaries();
        
        // 恢复上次的模式
        int lastMode = sharedPreferences.getInt(KEY_CURRENT_MODE, 0);
        isAnalysisMode = (lastMode == 1);
        switchMode.setChecked(isAnalysisMode);
        
        // 显示当前模式的内容
        refreshCurrentModeDisplay();
    }

    private void initViews() {
        rvChat = findViewById(R.id.rv_chat);
        etInput = findViewById(R.id.et_input);
        btnSend = findViewById(R.id.btn_send);
        ivBack = findViewById(R.id.iv_back);
        switchMode = findViewById(R.id.switch_mode);
        btnCancelDelete = findViewById(R.id.btn_cancel_delete);
        btnDelete = findViewById(R.id.btn_delete);

        ivBack.setOnClickListener(v -> {
            if (adapter.isDeleteMode()) {
                exitDeleteMode();
            } else {
                finish();
            }
        });

        adapter = new ChatMessageAdapter(this);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        btnSend.setOnClickListener(v -> {
            String content = etInput.getText().toString().trim();
            if (!TextUtils.isEmpty(content)) {
                sendChatMessage(content);
                etInput.setText("");
            }
        });

        switchMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked != isAnalysisMode) {
                switchMode(isChecked);
            }
        });

        btnCancelDelete.setOnClickListener(v -> exitDeleteMode());
        
        btnDelete.setOnClickListener(v -> deleteSelectedMessages());
        
        adapter.setOnItemClickListener(new ChatMessageAdapter.OnItemClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showLongPressDialog(position);
            }

            @Override
            public void onItemClick(int position) {
            }
        });
    }

    private void initData() {
        diaryManager = new DiaryManager(this);
        userId = SessionUtil.ensureUserId(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }
    
    private void switchMode(boolean toAnalysisMode) {
        // 保存当前模式的内容
        saveCurrentModeContent();
        
        // 切换模式
        isAnalysisMode = toAnalysisMode;
        sharedPreferences.edit().putInt(KEY_CURRENT_MODE, toAnalysisMode ? 1 : 0).apply();
        
        // 显示新模式的内容
        refreshCurrentModeDisplay();
        
        if (isAnalysisMode) {
            // 打开分析模式时，先选择日记
            showDiarySelectionDialog();
        }
    }
    
    private void saveCurrentModeContent() {
        List<ChatMessage> currentMessages = adapter.getAllMessages();
        if (isAnalysisMode) {
            analysisModeMessages.clear();
            analysisModeMessages.addAll(currentMessages);
            saveAnalysisModeHistory();
        } else {
            normalModeMessages.clear();
            normalModeMessages.addAll(currentMessages);
            saveNormalModeHistory();
        }
    }
    
    private void refreshCurrentModeDisplay() {
        adapter.setDeleteMode(false);
        btnCancelDelete.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        
        if (isAnalysisMode) {
            adapter.setMessages(analysisModeMessages);
        } else {
            adapter.setMessages(normalModeMessages);
        }
        syncConversationHistory();
        scrollToBottom();
    }

    private void loadAllHistories() {
        loadNormalModeHistory();
        loadAnalysisModeHistory();
    }
    
    private void loadNormalModeHistory() {
        String historyJson = sharedPreferences.getString(KEY_NORMAL_MODE_HISTORY, null);
        if (!TextUtils.isEmpty(historyJson)) {
            try {
                List<ChatMessage> history = JSON.parseArray(historyJson, ChatMessage.class);
                if (history != null) {
                    normalModeMessages.clear();
                    normalModeMessages.addAll(history);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void loadAnalysisModeHistory() {
        String historyJson = sharedPreferences.getString(KEY_ANALYSIS_MODE_HISTORY, null);
        if (!TextUtils.isEmpty(historyJson)) {
            try {
                List<ChatMessage> history = JSON.parseArray(historyJson, ChatMessage.class);
                if (history != null) {
                    analysisModeMessages.clear();
                    analysisModeMessages.addAll(history);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveNormalModeHistory() {
        String historyJson = JSON.toJSONString(normalModeMessages);
        sharedPreferences.edit().putString(KEY_NORMAL_MODE_HISTORY, historyJson).apply();
    }
    
    private void saveAnalysisModeHistory() {
        String historyJson = JSON.toJSONString(analysisModeMessages);
        sharedPreferences.edit().putString(KEY_ANALYSIS_MODE_HISTORY, historyJson).apply();
    }

    private void syncConversationHistory() {
        List<ChatMessage> allMessages = adapter.getAllMessages();
        conversationHistory.clear();
        
        for (ChatMessage msg : allMessages) {
            String role = (msg.getType() == ChatMessage.TYPE_USER) ? "user" : "assistant";
            conversationHistory.add(new DoubaoApiHelper.ChatMessage(role, msg.getContent()));
        }
    }

    private void loadTodayDiaries() {
        new Thread(() -> {
            List<Diary> allDiaries = diaryManager.getDiaryListByUserId(userId);
            todayDiaryList = filterTodayDiaries(allDiaries);
            selectedDiaries = new boolean[todayDiaryList.size()];
        }).start();
    }

    private List<Diary> filterTodayDiaries(List<Diary> diaries) {
        String todayDate = DateUtil.getCurrentTime("yyyy-MM-dd");
        List<Diary> todayDiaries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        for (Diary diary : diaries) {
            if (diary.isDeleted()) continue;
            try {
                Date createDate = sdf.parse(diary.getCreateTime());
                String diaryDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(createDate);
                if (todayDate.equals(diaryDate)) {
                    todayDiaries.add(diary);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(todayDiaries, (d1, d2) -> d1.getCreateTime().compareTo(d2.getCreateTime()));
        return todayDiaries;
    }

    private void sendChatMessage(String content) {
        ChatMessage userMsg = new ChatMessage(ChatMessage.TYPE_USER, content);
        adapter.addMessage(userMsg);
        conversationHistory.add(new DoubaoApiHelper.ChatMessage("user", content));
        scrollToBottom();
        saveCurrentModeHistory();

        showThinkingMessage();

        // 根据当前模式选择不同的提示词
        if (isAnalysisMode) {
            DoubaoApiHelper.sendAnalysisRequest(conversationHistory, new DoubaoApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    removeThinkingMessage();
                    ChatMessage aiMsg = new ChatMessage(ChatMessage.TYPE_AI, response);
                    adapter.addMessage(aiMsg);
                    conversationHistory.add(new DoubaoApiHelper.ChatMessage("assistant", response));
                    scrollToBottom();
                    saveCurrentModeHistory();
                }

                @Override
                public void onError(String error) {
                    removeThinkingMessage();
                    String fallback = getFallbackResponse(content);
                    ChatMessage aiMsg = new ChatMessage(ChatMessage.TYPE_AI, fallback);
                    adapter.addMessage(aiMsg);
                    conversationHistory.add(new DoubaoApiHelper.ChatMessage("assistant", fallback));
                    scrollToBottom();
                    saveCurrentModeHistory();
                }
            });
        } else {
            DoubaoApiHelper.sendChatRequest(conversationHistory, new DoubaoApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    removeThinkingMessage();
                    ChatMessage aiMsg = new ChatMessage(ChatMessage.TYPE_AI, response);
                    adapter.addMessage(aiMsg);
                    conversationHistory.add(new DoubaoApiHelper.ChatMessage("assistant", response));
                    scrollToBottom();
                    saveCurrentModeHistory();
                }

                @Override
                public void onError(String error) {
                    removeThinkingMessage();
                    String fallback = getFallbackResponse(content);
                    ChatMessage aiMsg = new ChatMessage(ChatMessage.TYPE_AI, fallback);
                    adapter.addMessage(aiMsg);
                    conversationHistory.add(new DoubaoApiHelper.ChatMessage("assistant", fallback));
                    scrollToBottom();
                    saveCurrentModeHistory();
                }
            });
        }
    }
    
    private void saveCurrentModeHistory() {
        saveCurrentModeContent();
    }

    private void enterDeleteMode() {
        adapter.setDeleteMode(true);
        btnCancelDelete.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
        switchMode.setVisibility(View.GONE);
        etInput.setEnabled(false);
        btnSend.setEnabled(false);
    }

    private void exitDeleteMode() {
        adapter.setDeleteMode(false);
        btnCancelDelete.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        switchMode.setVisibility(View.VISIBLE);
        etInput.setEnabled(true);
        btnSend.setEnabled(true);
    }

    private void deleteSelectedMessages() {
        List<ChatMessage> selected = adapter.getSelectedMessages();
        if (selected.isEmpty()) {
            Toast.makeText(this, "请先选择要删除的消息", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
            .setTitle("确认删除")
            .setMessage("确定要删除选中的 " + selected.size() + " 条消息吗？")
            .setPositiveButton("删除", (dialog, which) -> {
                adapter.deleteSelectedMessages();
                syncConversationHistory();
                saveCurrentModeHistory();
                exitDeleteMode();
                Toast.makeText(AiAssistantActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("取消", null)
            .show();
    }

    private void showDiarySelectionDialog() {
        if (todayDiaryList.isEmpty()) {
            Toast.makeText(this, "今日还没有日记", Toast.LENGTH_SHORT).show();
            switchMode.setChecked(false);
            return;
        }

        for (int i = 0; i < selectedDiaries.length; i++) {
            selectedDiaries[i] = true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择要分析的日记");

        String[] items = new String[todayDiaryList.size()];
        for (int i = 0; i < todayDiaryList.size(); i++) {
            Diary diary = todayDiaryList.get(i);
            String title = TextUtils.isEmpty(diary.getTitle()) ? "无标题" : diary.getTitle();
            items[i] = formatTime(diary.getCreateTime()) + " " + title;
        }

        builder.setMultiChoiceItems(items, selectedDiaries, (dialog, which, isChecked) -> {
            selectedDiaries[which] = isChecked;
        });

        builder.setPositiveButton("分析", (dialog, which) -> {
            analyzeSelectedDiaries();
        });

        builder.setNegativeButton("取消", (dialog, which) -> {
            switchMode.setChecked(false);
        });

        builder.show();
    }

    private void analyzeSelectedDiaries() {
        List<Diary> selectedList = new ArrayList<>();
        for (int i = 0; i < selectedDiaries.length; i++) {
            if (selectedDiaries[i]) {
                selectedList.add(todayDiaryList.get(i));
            }
        }

        if (selectedList.isEmpty()) {
            Toast.makeText(this, "请至少选择一篇日记", Toast.LENGTH_SHORT).show();
            switchMode.setChecked(false);
            return;
        }

        String diaryContent = formatDiaries(selectedList);
        // 添加自然的开头
        String[] openingLines = {
            "看完你的日记，我好像读懂了你藏在文字里的情绪～",
            "关于这篇日记，我有一些温柔的话想对你说",
            "读了你的日记，我感受到了你今天的心情变化"
        };
        String randomOpening = openingLines[new java.util.Random().nextInt(openingLines.length)];
        String userContent = randomOpening + "\n\n【今日日记】：" + diaryContent;

        showThinkingMessage();

        List<DoubaoApiHelper.ChatMessage> tempHistory = new ArrayList<>(conversationHistory);
        tempHistory.add(new DoubaoApiHelper.ChatMessage("user", userContent));

        DoubaoApiHelper.sendAnalysisRequest(tempHistory, new DoubaoApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                removeThinkingMessage();
                displayAnalysisResult(response);
            }

            @Override
            public void onError(String error) {
                removeThinkingMessage();
                displayAnalysisResult(getFallbackAnalysis());
            }
        });
    }

    private void displayAnalysisResult(String response) {
        // 直接显示，保留自然的对话式开头
        ChatMessage aiMsg = new ChatMessage(ChatMessage.TYPE_AI, response);
        adapter.addMessage(aiMsg);
        conversationHistory.add(new DoubaoApiHelper.ChatMessage("assistant", response));
        scrollToBottom();
        saveCurrentModeHistory();
    }

    private String formatDiaries(List<Diary> diaries) {
        StringBuilder sb = new StringBuilder();
        sb.append("今日共有 ").append(diaries.size()).append(" 篇日记：\n\n");

        for (int i = 0; i < diaries.size(); i++) {
            Diary diary = diaries.get(i);
            String formattedTime = formatTime(diary.getCreateTime());
            sb.append(formattedTime);
            if (!TextUtils.isEmpty(diary.getTitle())) {
                sb.append("：").append(diary.getTitle()).append(" ");
            } else {
                sb.append("：");
            }
            sb.append(diary.getContent()).append("\n");
            if (i < diaries.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private String formatTime(String timeStr) {
        try {
            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat sdfOutput = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
            Date date = sdfInput.parse(timeStr);
            return "[" + sdfOutput.format(date) + "]";
        } catch (Exception e) {
            return "[" + timeStr + "]";
        }
    }

    private void showThinkingMessage() {
        ChatMessage thinkingMsg = new ChatMessage(ChatMessage.TYPE_AI, "AI正在思考…");
        adapter.addMessage(thinkingMsg);
        scrollToBottom();
    }

    private void removeThinkingMessage() {
        adapter.removeLastMessage();
    }

    private void scrollToBottom() {
        if (adapter.getItemCount() > 0) {
            rvChat.post(() -> {
                rvChat.smoothScrollToPosition(adapter.getItemCount() - 1);
            });
        }
    }

    private String getFallbackResponse(String content) {
        String[] replies = {
            "我理解你的感受，谢谢你和我分享这些。",
            "听起来你今天有一些特别的经历，想和我多聊聊吗？",
            "每个人都会有情绪起伏，这很正常。重要的是我们能正视它。",
            "你已经做得很好了，记得给自己一些鼓励和肯定。",
            "慢慢来，一切都会好起来的。我在这里陪着你。"
        };
        java.util.Random random = new java.util.Random();
        return replies[random.nextInt(replies.length)];
    }

    private String getFallbackAnalysis() {
        return "看完你的日记，我好像读懂了你藏在文字里的情绪～\n\n你今天记录了一些日常的点点滴滴，感觉心情比较平稳，是平静的一天呢。\n\n平淡的日子也很珍贵呀，好好享受当下的每一刻。希望明天也能这样安安稳稳的。";
    }
    
    private void showLongPressDialog(int position) {
        List<ChatMessage> allMessages = adapter.getAllMessages();
        if (position < 0 || position >= allMessages.size()) return;
        
        ChatMessage message = allMessages.get(position);
        
        new AlertDialog.Builder(this)
            .setItems(new String[]{"复制", "多选删除"}, (dialog, which) -> {
                if (which == 0) {
                    copyToClipboard(message.getContent());
                } else if (which == 1) {
                    message.setSelected(true);
                    enterDeleteMode();
                }
            })
            .show();
    }

    private void copyToClipboard(String content) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) 
            getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("ChatMessage", content);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, R.string.ai_copied, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (adapter.isDeleteMode()) {
            exitDeleteMode();
        } else {
            super.onBackPressed();
        }
    }
}
