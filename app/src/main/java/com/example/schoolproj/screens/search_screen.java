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

import com.example.schoolproj.GeminiRelevant.ExaManager;
import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.RecycleViewAdapter;
import com.example.schoolproj.classes.Product;
import com.example.schoolproj.classes.SearchDetails;
import com.example.schoolproj.classes.SearchItemParameter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class search_screen extends MasterActivity {

    RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    List<SearchItemParameter> searchParameters;
    List<Product> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        recyclerView = findViewById(R.id.rvListItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        results = new ArrayList<>();
        searchParameters = new ArrayList<>();

        Intent intent = getIntent();
        String searchDetailsJson = intent.getStringExtra("searchDetails");

        if (searchDetailsJson != null) {
            parseInitialSearchDetails(searchDetailsJson);
        } else {
            List<SearchItemParameter> params = (List<SearchItemParameter>) intent.getSerializableExtra("item details");
            if (params != null) {
                searchParameters.addAll(params);
            }
        }

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
        } catch (Exception e) {
            Log.e(TAG, "Error parsing initial search details", e);
        }
    }

    private void parseGeminiResponse(String response) {
        try {
            Log.d(TAG, "Exa Response to parse: " + response);

            // Clean up potentially markdown-wrapped response
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
            results.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Product result = new Product();

                // Correctly map Exa API fields (title, url, author) to your Product model
                result.setProduct_name(obj.optString("title", "No Title"));
                result.setStore_url(obj.optString("url", ""));
                result.setStore_name(obj.optString("author", "Web Search"));
                result.setStore_location(""); // Placeholder as Exa doesn't provide this directly
                result.setDescription(obj.optString("publishedDate", "No description available"));
                result.setImageUrl(obj.optString("image", ""));
                result.setOther_details(obj.optString("id", ""));

                if (obj.has("price") && !obj.isNull("price")) {
                    result.setPrice(obj.optDouble("price", 0.0));
                } else {
                    result.setPrice(null);
                }

                results.add(result);
            }
        } catch (Exception e) {
            Log.e(TAG, "parseGeminiResponse failed", e);
        }
    }

    public void searchBtn(View view) {
        searchParameters = adapter.getParameters();
        final StringBuilder searchQueryBuilder = new StringBuilder();
        for (int i = 0; i < searchParameters.size(); i++) {
            searchQueryBuilder.append(searchParameters.get(i).toString());
            if (i != searchParameters.size() - 1) searchQueryBuilder.append(", ");
        }
        final String searchQuery = searchQueryBuilder.toString();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Searching...");
        pd.setMessage("Extracting data from the web...");
        pd.setCancelable(false);
        pd.show();

        // --- Run Exa search in background thread ---
        new Thread(new Runnable() {
            @Override
            public void run()
            {
                try
                {
                    JSONArray exaResults = ExaManager.getInstance().search(searchQuery);
                    final String exaString = exaResults.toString();

                    // --- Update UI on main thread ---
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run() {
                            pd.dismiss();
                            System.out.println(exaString);
                            parseGeminiResponse(exaString);

                            if (!results.isEmpty())
                            {
                                finalizeSearch(searchQuery);
                            }
                            else
                            {
                                Toast.makeText(search_screen.this, "No products found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                catch (Exception e)
                {
                    Log.e(TAG, "Exa search failed", e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(search_screen.this, "Exa search failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void finalizeSearch(String searchQuery) {
        SearchDetails thisSearch = new SearchDetails(searchQuery, results);

        if (connected_user != null && connected_user.getUserID() != null) {
            String userID = connected_user.getUserID();
            String searchID = searchHistoryRef.child(userID).push().getKey();
            thisSearch.setSearch_id(searchID);
            searchHistoryRef.child(userID).child(searchID).setValue(thisSearch)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Search history uploaded successfully"))
                    .addOnFailureListener(e -> Log.e(TAG, "Failed to upload search history", e));
        } else {
            Log.w(TAG, "User not logged in, search history not uploaded");
        }

        Intent resultsScreen = new Intent(this, search_result_screen.class);
        resultsScreen.putExtra("results", (ArrayList<Product>) results);
        startActivity(resultsScreen);
        finish();
    }

    public void addItem(View view) {
        searchParameters.add(new SearchItemParameter(true));
        adapter.notifyItemInserted(searchParameters.size() - 1);
        recyclerView.scrollToPosition(searchParameters.size() - 1);
    }

    public void back(View view) { finish(); }
}
