package com.example.schoolproj.screens;

import static android.content.ContentValues.TAG;
import static com.example.schoolproj.FireBaseFiles.FBRef.searchHistoryRef;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproj.GeminiRelevant.GeminiCallback;
import com.example.schoolproj.GeminiRelevant.GeminiManager;
import com.example.schoolproj.GeminiRelevant.Prompts;
import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.RecycleViewAdapter;
import com.example.schoolproj.classes.Product;
import com.example.schoolproj.classes.SearchDetails;
import com.example.schoolproj.classes.SearchItemParameter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class search_screen extends MasterActivity
{
    RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    List<SearchItemParameter> searchParameters;
    GeminiManager geminiManager;
    List<Product> results;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        geminiManager = GeminiManager.getInstance();
        recyclerView = findViewById(R.id.rvListItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        results = new ArrayList<>();
        searchParameters = new ArrayList<>();

        Intent intent = getIntent();

        // 1. Check if we came from Image Search (JSON string)
        String searchDetailsJson = intent.getStringExtra("searchDetails");
        if (searchDetailsJson != null) {
            parseInitialSearchDetails(searchDetailsJson);
        }
        // 2. Check if we came from a manual flow (Serializable List)
        else {
            List<SearchItemParameter> params = (List<SearchItemParameter>) intent.getSerializableExtra("item details");
            if (params != null) {
                searchParameters.addAll(params);
            }
        }

        // Always ensure at least one empty item if list is empty
        if (searchParameters.isEmpty()) {
            searchParameters.add(new SearchItemParameter("item", ""));
        }

        adapter = new RecycleViewAdapter(searchParameters);
        recyclerView.setAdapter(adapter);
    }

    private void parseInitialSearchDetails(String json) {
        try {
            JSONObject obj = new JSONObject(json);
            String itemName = obj.optString("item", "");
            searchParameters.add(new SearchItemParameter("item", itemName, true));

            JSONObject settings = obj.optJSONObject("settings");
            if (settings != null) {
                Iterator<String> keys = settings.keys();
                while (keys.hasNext()) {
                    String key = keys.next();
                    String value = settings.getString(key);
                    searchParameters.add(new SearchItemParameter(key, value, true));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing initial search details", e);
        }
    }

    void parseGeminiResponse(String response)
    {
        try {
            String cleanResponse = response.trim()
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .replace("\n", " ")
                    .replace("\r", " ")
                    .trim();

            if (!cleanResponse.startsWith("[")) {
                cleanResponse = "[" + cleanResponse + "]";
            }

            JSONArray jsonArray = new JSONArray(cleanResponse);
            results.clear(); // Clear previous results

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                Product result = new Product(
                        obj.optString("product_name", "N/A"),
                        obj.has("price") && !obj.isNull("price") ? obj.getDouble("price") : null,
                        obj.optString("image", ""),
                        obj.optString("description", ""),
                        obj.optString("store_name", "Unknown"),
                        obj.optString("store_url", ""),
                        obj.optString("store_location", ""),
                        obj.optString("other_details", "")
                );
                results.add(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseGeminiResponse failed", e);
        }
    }

    void searchWithGemini(String finalPrompt, String searchQuery)
    {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Searching...");
        pd.setMessage("Finding the best deals for you...");
        pd.show();

        geminiManager.sendTextPrompt(finalPrompt, new GeminiCallback()
        {
            @Override
            public void onSuccess(String result)
            {
                runOnUiThread(() -> {
                    pd.dismiss();
                    parseGeminiResponse(result);

                    if (!results.isEmpty()) {
                        finalizeSearch(searchQuery);
                    } else {
                        Toast.makeText(search_screen.this, "No products found", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable error)
            {
                runOnUiThread(() -> {
                    pd.dismiss();
                    Log.e(TAG, "Gemini Failure", error);
                    Toast.makeText(search_screen.this, "Search failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void finalizeSearch(String searchQuery) {
        // Generate the search Object
        SearchDetails thisSearch = new SearchDetails(searchQuery, results);

        // Upload to Firebase
        if (connected_user != null) {
            String userID = connected_user.getUserID();
            String searchID = searchHistoryRef.child(userID).push().getKey();
            thisSearch.setSearch_id(searchID);
            searchHistoryRef.child(userID).child(searchID).setValue(thisSearch);
        }

        // Move to Results Screen
        Intent resultsScreen = new Intent(this, search_result_screen.class);
        resultsScreen.putExtra("results", (ArrayList<Product>) results);
        startActivity(resultsScreen);
        finish();
    }

    public void addItem(View view)
    {
        searchParameters.add(new SearchItemParameter(true));
        adapter.notifyItemInserted(searchParameters.size() - 1);
        recyclerView.scrollToPosition(searchParameters.size() - 1);
    }

    public void back(View view) { finish(); }

    public void searchBtn(View view)
    {
        searchParameters = adapter.getParameters();
        StringBuilder searchQuery = new StringBuilder();
        for (int i = 0; i < searchParameters.size(); i++) {
            searchQuery.append(searchParameters.get(i).toString());
            if (i != searchParameters.size() - 1) searchQuery.append(", ");
        }

        String finalPrompt = Prompts.SEARCH_ITEMS_RELEVANT + searchQuery.toString();
        searchWithGemini(finalPrompt, searchQuery.toString());
    }
}