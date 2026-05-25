package com.example.emotiondiarysystem.utils;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DoubaoApiHelper {

    private static final String BASE_URL = "https://ark.cn-beijing.volces.com/api/v3/chat/completions";
    private static final String API_KEY = "ark-c60f6c54-4753-494b-8f02-709d0d2053e1-99e0f";
    private static final String MODEL = "ep-20260514084818-sxn26";

    private static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    private static final int MAX_HISTORY_MESSAGES = 20; // 10轮对话

    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();

    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    // 两种模式的提示词
    public static final String SYSTEM_PROMPT_CHAT = "你是温柔治愈的情绪陪伴助手，根据上下文自然聊天，不说教，温暖贴心。";
    public static final String SYSTEM_PROMPT_ANALYSIS = "你是温柔治愈的情绪陪伴助手，用自然的对话语气分析日记：\n1.判断整体情绪（开心/低落/焦虑/疲惫/平淡）\n2.总结心情变化\n3.共情安慰\n4.一句治愈语录\n不用序号，自然分段即可。";

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    /**
     * 发送通用聊天请求
     */
    public static void sendChatRequest(List<ChatMessage> messages, ApiCallback callback) {
        sendRequest(messages, SYSTEM_PROMPT_CHAT, callback);
    }

    /**
     * 发送情绪分析请求
     */
    public static void sendAnalysisRequest(List<ChatMessage> messages, ApiCallback callback) {
        sendRequest(messages, SYSTEM_PROMPT_ANALYSIS, callback);
    }

    private static void sendRequest(List<ChatMessage> messages, String systemPrompt, ApiCallback callback) {
        new Thread(() -> {
            try {
                // 构建请求体
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", MODEL);

                JSONArray messagesArray = new JSONArray();

                // 添加系统提示
                JSONObject systemMessage = new JSONObject();
                systemMessage.put("role", "system");
                systemMessage.put("content", systemPrompt);
                messagesArray.add(systemMessage);

                // 保留最近10轮对话
                List<ChatMessage> recentMessages = trimHistory(messages);
                for (ChatMessage msg : recentMessages) {
                    JSONObject msgJson = new JSONObject();
                    msgJson.put("role", msg.role);
                    msgJson.put("content", msg.content);
                    messagesArray.add(msgJson);
                }

                requestBody.put("messages", messagesArray);

                String requestJson = requestBody.toJSONString();
                android.util.Log.d("DoubaoApi", "请求 URL: " + BASE_URL);
                android.util.Log.d("DoubaoApi", "请求体: " + requestJson);

                Request request = new Request.Builder()
                        .url(BASE_URL)
                        .addHeader("Authorization", "Bearer " + API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(RequestBody.create(requestJson, JSON_TYPE))
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    android.util.Log.d("DoubaoApi", "响应码: " + response.code());
                    
                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        android.util.Log.d("DoubaoApi", "响应体: " + responseBody);
                        
                        JSONObject responseJson = JSON.parseObject(responseBody);
                        JSONArray choices = responseJson.getJSONArray("choices");
                        if (choices != null && choices.size() > 0) {
                            JSONObject choice = choices.getJSONObject(0);
                            JSONObject message = choice.getJSONObject("message");
                            String content = message.getString("content");
                            mainHandler.post(() -> callback.onSuccess(content));
                        } else {
                            mainHandler.post(() -> callback.onError("AI服务暂时不可用"));
                        }
                    } else {
                        String errorBody = response.body() != null ? response.body().string() : "无响应体";
                        android.util.Log.e("DoubaoApi", "请求失败: " + response.code() + ", " + errorBody);
                        mainHandler.post(() -> callback.onError("AI服务暂时不可用 (" + response.code() + ")"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                android.util.Log.e("DoubaoApi", "IO异常: " + e.getMessage());
                mainHandler.post(() -> callback.onError("网络异常，请稍后再试"));
            } catch (Exception e) {
                e.printStackTrace();
                android.util.Log.e("DoubaoApi", "其他异常: " + e.getMessage());
                mainHandler.post(() -> callback.onError("AI服务暂时不可用"));
            }
        }).start();
    }

    private static List<ChatMessage> trimHistory(List<ChatMessage> messages) {
        if (messages.size() <= MAX_HISTORY_MESSAGES) {
            return messages;
        }
        return new ArrayList<>(messages.subList(messages.size() - MAX_HISTORY_MESSAGES, messages.size()));
    }

    public static class ChatMessage {
        public String role;
        public String content;

        public ChatMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
