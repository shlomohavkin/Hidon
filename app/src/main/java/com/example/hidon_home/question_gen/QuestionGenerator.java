package com.example.hidon_home.question_gen;

import android.util.Log;

import com.example.hidon_home.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionGenerator {
    public QuestionGenerator() {
    }
    public void generateQuestion(QuestionCallBack callback) {
        OpenAIApi openAIApi = ApiClient.getClient().create(OpenAIApi.class);

        // Create Message objects
        ChatGPTRequest.Message systemMessage = new ChatGPTRequest.Message(
                "system",
                "You are an assistant for generating different trivia questions in JSON format for a trivia game."
        );
        Random rnd = new Random();

        ChatGPTRequest.Message userMessage = new ChatGPTRequest.Message(
                "user",
                "Generate a JSON string containing 5 hard, random trivia questions in this format: " +
                        "{ \"questions\": [ { \"questionContent\": \"\", \"answers\": [\"\", \"\", \"\", \"\"], \"correctAnswer\": 0 } ] } " +
                        "Ensure all questions and answers are valid and accurate. Don't!! include any text other than the JSON string!");

        // Create Request Object
        ChatGPTRequest request = new ChatGPTRequest(
                "gpt-4o-mini", // Model
                new ChatGPTRequest.Message[]{systemMessage, userMessage},
                500, 1f, 1f
        );



        Call<ChatGPTResponse> call = openAIApi.generateQuestion(request);
        call.enqueue(new Callback<ChatGPTResponse>() {
            @Override
            public void onResponse(Call<ChatGPTResponse> call, Response<ChatGPTResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().choices[0].message.content.trim();
                        Log.d("ChatGPT response", jsonString);
                        JSONObject jsonObject = new JSONObject(jsonString);
                        JSONArray questionsArray = jsonObject.getJSONArray("questions");

                        ArrayList<Question> questions = new ArrayList<>();
                        for (int i = 0; i < questionsArray.length(); i++) {
                            JSONObject questionJson = questionsArray.getJSONObject(i);

                            questions.add(new Question("", new ArrayList<>(), 0));
                            questions.get(i).fromJson(questionJson);
                        }

                        // Pass the question to the callback
                        callback.onQuestionGenerated(questions);

                    } catch (Exception e) {
                        Log.e("JSON Error", "Failed to parse JSON or create Question object: " + e.getMessage());
                        callback.onQuestionGenerated(null); // Pass null in case of error
                    }
                } else {
                    Log.e("API Error", "Code: " + response.code() + ", Message: " + response.message());
                    callback.onQuestionGenerated(null); // Pass null in case of error
                }
                }

                @Override
                public void onFailure(Call<ChatGPTResponse> call, Throwable t) {
                    Log.e("API Error", t.getMessage());
                    callback.onQuestionGenerated(null); // Pass null in case of failure
                }
            });
        }

    }


