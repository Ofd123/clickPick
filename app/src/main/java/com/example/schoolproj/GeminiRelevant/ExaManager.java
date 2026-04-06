package com.example.schoolproj.GeminiRelevant;

import android.util.Log;

import com.example.schoolproj.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.*;

public class ExaManager
{

    private static final String API_KEY = BuildConfig.Exa_API_Key;
    private static final String SEARCH_URL = "https://api.exa.ai/search";

    private static ExaManager instance;
    private OkHttpClient client;

    private ExaManager()
    {
        client = new OkHttpClient.Builder()
                .connectTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .build();
    }

    public static synchronized ExaManager getInstance()
    {
        if (instance == null)
        {
            instance = new ExaManager();
        }
        return instance;
    }

    public List<String> search(String queryInput) throws Exception
    {
        // Escape quotes
        String query = queryInput.replace("\"", "\\\"");

        String jsonBody = "{\n" +
                "  \"query\": \"" + query + "\",\n" +
                "  \"type\": \"auto\",\n" +
                "  \"num_results\": 10\n" +
                "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(SEARCH_URL)
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();
            Log.d("EXA_RESPONSE", responseBody);

            JSONObject json = new JSONObject(responseBody);
            JSONArray results = json.getJSONArray("results");
            List<String> urls = new ArrayList<>();
            for (int i = 0; i < results.length(); i++) {
                urls.add(results.getJSONObject(i).getString("url"));
            }
            return urls;
        }
    }

    public String getContents(String url) throws Exception
    {
        String jsonBody = "{\n" +
                "  \"urls\": [\"" + url + "\"],\n" +
                "  \"text\": { \"max_characters\": 8000 }\n" +
                "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.exa.ai/contents")
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Contents failed: " + response);
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            JSONArray results = json.getJSONArray("results");
            if (results.length() > 0) {
                return results.getJSONObject(0).optString("text", "");
            }
            return null;
        }
    }

    public JSONArray getContents(JSONArray urlsArray) throws Exception
    {
        StringBuilder urlsBuilder = new StringBuilder("[");
        for (int i = 0; i < urlsArray.length(); i++)
        {
            urlsBuilder.append("\"").append(urlsArray.getString(i)).append("\"");
            if (i != urlsArray.length() - 1) urlsBuilder.append(",");
        }
        urlsBuilder.append("]");

        String jsonBody = "{\n" +
                "  \"urls\": " + urlsBuilder.toString() + ",\n" +
                "  \"text\": { \"max_characters\": 8000 }\n" +
                "}";

        RequestBody body = RequestBody.create(
                jsonBody,
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.exa.ai/contents")
                .addHeader("x-api-key", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Contents failed: " + response);
            }

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            return json.getJSONArray("results");
        }
    }
}