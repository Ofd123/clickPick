package com.example.schoolproj.GeminiRelevant;

import android.util.Log;

import com.example.schoolproj.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.*;

public class ExaManager
{

    private static final String API_KEY = BuildConfig.Exa_API_Key;
    private static final String SEARCH_URL = "https://api.exa.ai/search";

    private static ExaManager instance;
    private OkHttpClient client;

    private ExaManager()
    {
        client = new OkHttpClient();
    }

    public static synchronized ExaManager getInstance()
    {
        if (instance == null)
        {
            instance = new ExaManager();
        }
        return instance;
    }
    public JSONArray search(String queryInput) throws Exception
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

        Response response = client.newCall(request).execute();

        if (!response.isSuccessful())
        {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();

        Log.d("EXA_RESPONSE", responseBody);

        JSONObject json = new JSONObject(responseBody);
        return json.getJSONArray("results");
    }
}