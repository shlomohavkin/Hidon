package com.example.hidon_home.question_gen;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

// Request Body Model
class ChatGPTRequest {
    String model;
    Message[] messages;
    int max_tokens;
    float temperature; // randomness
    float top_p; // how wide does he look

    public ChatGPTRequest(String model, Message[] messages, int max_tokens, float temperature, float top_p) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
        this.temperature = temperature;
        this.top_p = top_p;
    }

    static class Message {
        String role; // "system", "user", or "assistant"
        String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

// Response Model
class ChatGPTResponse {
    public Choice[] choices;

    static class Choice {
        Message message;

        static class Message {
            String role; // "system", "user", or "assistant"
            String content;
        }
    }
}

// API Interface
public interface OpenAIApi {
    @Headers({
            "Content-Type: application/json",
            "Authorization: Bearer "
    })
    @POST("v1/chat/completions")
    Call<ChatGPTResponse> generateQuestion(@Body ChatGPTRequest request);
}
