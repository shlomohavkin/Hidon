package com.example.hidon_home.question_gen;

import android.util.Log;
import com.example.hidon_home.Question;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuestionGenerator {
    private final int MAX_RETRIES = 3; // Maximum number of retry attempts

    public QuestionGenerator() {} // empty constructor

    /**
     * Function that when called, sends a request to the API
     * of ChatGPT for the questions for the current questionnaire.
     * @param callback an interface, which has a function onQuestionsGenerated
     * which receives a questions array when the response of ChatGPT comes through.
     */
    public void generateQuestion(QuestionCallBack callback) {
        // Call the helper method with initial retry count of 0
        generateQuestionWithRetry(callback, 0);
    }

    /**
     * Helper method that handles the retry logic
     * @param callback the question callback interface
     * @param retryCount current retry count
     */
    private void generateQuestionWithRetry(QuestionCallBack callback, int retryCount) {
        if (retryCount >= MAX_RETRIES) {
            Log.e("QuestionGenerator", "Maximum retry attempts reached. Giving up.");
            callback.onQuestionGenerated(null);
            return;
        }

        OpenAIApi openAIApi = ApiClient.getClient().create(OpenAIApi.class);

        // Create Message objects
        ChatGPTRequest.Message systemMessage = new ChatGPTRequest.Message(
                "system",
                "You are an assistant for generating different trivia questions in JSON format for a trivia game."
        );

        ChatGPTRequest.Message userMessage = new ChatGPTRequest.Message(
                "user",
                "Generate 22 hard trivia questions, choose random 3 from those and generate a JSON string containing those trivia questions in this format: " +
                        "{ \"questions\": [ { \"questionContent\": \"\", \"answers\": [\"\", \"\", \"\", \"\"], \"correctAnswer\": 0 } ] } " +
                        "Ensure all questions and answers are valid and accurate. Very Important: Don't!! add any text or symbols other than the format above.");

        // Create Request Object
        ChatGPTRequest request = new ChatGPTRequest(
                "gpt-4o-mini", // Model
                new ChatGPTRequest.Message[]{systemMessage, userMessage},
                1000, 1f, 1f
        );

        Call<ChatGPTResponse> call = openAIApi.generateQuestion(request);
        call.enqueue(new Callback<ChatGPTResponse>() {
            @Override
            public void onResponse(Call<ChatGPTResponse> call, Response<ChatGPTResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String jsonString = response.body().choices[0].message.content.trim();
                        Log.d("QuestionGenerator", jsonString);
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
                        Log.e("QuestionGenerator", "Failed to parse JSON or create Question object: " + e.getMessage(), e);
                        // Retry with incremented retry count
                        int newRetryCount = retryCount + 1;
                        Log.d("QuestionGenerator", "Retrying... Attempt " + newRetryCount + " of " + MAX_RETRIES);
                        generateQuestionWithRetry(callback, newRetryCount);
                    }
                } else {
                    Log.e("QuestionGenerator", "API Error - Code: " + response.code() + ", Message: " + response.message());
                    // Retry with incremented retry count
                    int newRetryCount = retryCount + 1;
                    Log.d("QuestionGenerator", "Retrying... Attempt " + newRetryCount + " of " + MAX_RETRIES);
                    generateQuestionWithRetry(callback, newRetryCount);
                }
            }

            @Override
            public void onFailure(Call<ChatGPTResponse> call, Throwable t) {
                Log.e("QuestionGenerator", "API Call Failure: " + t.getMessage(), t);
                // Retry with incremented retry count
                int newRetryCount = retryCount + 1;
                Log.d("QuestionGenerator", "Retrying... Attempt " + newRetryCount + " of " + MAX_RETRIES);
                generateQuestionWithRetry(callback, newRetryCount);
            }
        });
    }
}