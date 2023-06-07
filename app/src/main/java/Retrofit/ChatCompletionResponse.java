package Retrofit;

import com.google.gson.annotations.SerializedName;

public class ChatCompletionResponse {
    public String getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public long getCreated() {
        return created;
    }

    public String getModel() {
        return model;
    }

    public UsageData getUsage() {
        return usage;
    }

    public Choice[] getChoices() {
        return choices;
    }

    @SerializedName("id")
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setUsage(UsageData usage) {
        this.usage = usage;
    }

    public void setChoices(Choice[] choices) {
        this.choices = choices;
    }

    @SerializedName("object")
    private String object;

    @SerializedName("created")
    private long created;

    public ChatCompletionResponse(String id, String object, long created, String model, UsageData usage, Choice[] choices) {
        this.id = id;
        this.object = object;
        this.created = created;
        this.model = model;
        this.usage = usage;
        this.choices = choices;
    }

    @SerializedName("model")
    private String model;

    @SerializedName("usage")
    private UsageData usage;

    @SerializedName("choices")
    private Choice[] choices;

    // Getters and setters

    public static class UsageData {
        @SerializedName("prompt_tokens")
        private int promptTokens;

        @SerializedName("completion_tokens")
        private int completionTokens;

        @SerializedName("total_tokens")
        private int totalTokens;

        // Getters and setters
    }

    public static class Choice {
        public Choice(Message message, String finishReason, int index) {
            this.message = message;
            this.finishReason = finishReason;
            this.index = index;
        }

        public void setMessage(Message message) {
            this.message = message;
        }

        public void setFinishReason(String finishReason) {
            this.finishReason = finishReason;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @SerializedName("message")
        private Message message;

        public Message getMessage() {
            return message;
        }

        public String getFinishReason() {
            return finishReason;
        }

        public int getIndex() {
            return index;
        }

        @SerializedName("finish_reason")
        private String finishReason;

        @SerializedName("index")
        private int index;

        // Getters and setters
    }

    public static class Message {
        @SerializedName("role")
        private String role;

        public String getContent() {
            return content;
        }

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @SerializedName("content")
        private String content;

        // Getters and setters
    }
}
