package com.example.schoolproj.screens;

import static android.content.ContentValues.TAG;
import static com.example.schoolproj.FireBaseFiles.FBRef.searchHistoryRef;
import static com.example.schoolproj.GeminiRelevant.Prompts.SEARCH_PROMPT;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproj.GeminiRelevant.ExaManager;
import com.example.schoolproj.GeminiRelevant.GeminiCallback;
import com.example.schoolproj.GeminiRelevant.GeminiManager;
import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.RecycleViewAdapter;
import com.example.schoolproj.classes.Product;
import com.example.schoolproj.classes.SearchDetails;
import com.example.schoolproj.classes.SearchItemParameter;

import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class search_screen extends MasterActivity
{

    RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    List<SearchItemParameter> searchParameters;
    ArrayList<Product> results;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        recyclerView = findViewById(R.id.rvListItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        results = new ArrayList<>();
        searchParameters = new ArrayList<>();

        Intent intent = getIntent();
        String json = intent.getStringExtra("searchDetails");

        if (json != null) parseInitialSearchDetails(json);
        else
        {
            @SuppressWarnings("unchecked")
            List<SearchItemParameter> params = (List<SearchItemParameter>) intent.getSerializableExtra("item details");
            if (params != null)
            {
                searchParameters.addAll(params);
            }
        }

        if (searchParameters.isEmpty())
        {
            searchParameters.add(new SearchItemParameter("product name", ""));
        }

        adapter = new RecycleViewAdapter(searchParameters);
        recyclerView.setAdapter(adapter);
    }

    // -----------------------------------------------------------------------------------------

    private void parseInitialSearchDetails(String json)
    {
        try
        {
            JSONObject obj = new JSONObject(json);

            searchParameters.add(new SearchItemParameter("item",
                    obj.optString("item", ""), true));

            JSONObject settings = obj.optJSONObject("settings");
            if (settings != null)
            {
                Iterator<String> keys = settings.keys();
                while (keys.hasNext())
                {
                    String key = keys.next();
                    searchParameters.add(new SearchItemParameter(
                            key,
                            settings.getString(key),
                            true
                    ));
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error parsing initial JSON", e);
        }
    }

    // -----------------------------------------------------------------------------------------

    private void parseGeminiResponse(String response)
    {
        try
        {
            results.clear();
            JSONArray products = null;

            String trimmed = response.trim();
            // Try to parse as array first
            if (trimmed.startsWith("["))
            {
                products = new JSONArray(trimmed);
            }
            else if (trimmed.startsWith("{"))
            {
                JSONObject root = new JSONObject(trimmed);
                products = root.optJSONArray("products");
            }

            if (products == null)
            {
                Log.e(TAG, "No valid product array found in Gemini response");
                return;
            }

            for (int i = 0; i < products.length(); i++)
            {
                JSONObject p = products.getJSONObject(i);

                String name = getSafeString(p, "product_name");
                if (name == null) name = getSafeString(p, "name");

                String priceStr = getSafeString(p, "price");
                String description = getSafeString(p, "description");

                String url = getSafeString(p, "store_url");
                if (url == null) url = getSafeString(p, "url");

                String image = getSafeString(p, "image");
                String storeName = getSafeString(p, "store_name");
                String storeLocation = getSafeString(p, "store_location");
                String otherDetails = getSafeString(p, "other_details");

                if (name == null || url == null) continue;

                double price = 0;
                if (priceStr != null)
                {
                    try
                    {
                        price = Double.parseDouble(priceStr.replaceAll("[^0-9.]", ""));
                    }
                    catch (Exception ignored) {}
                }

                String priceType = getSafeString(p, "price_type");
                if (priceType == null) priceType = "USD"; // Default or logic to extract

                results.add(new Product(name, price, priceType, image, description, storeName, url, storeLocation, otherDetails));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Error parsing Gemini response", e);
        }
    }

    private String getSafeString(JSONObject obj, String key)
    {
        if (!obj.has(key) || obj.isNull(key)) return null;

        String value = obj.optString(key, "");
        value = value.trim();

        if (value.equalsIgnoreCase("null") || value.isEmpty()) return null;

        return value;
    }

    // -----------------------------------------------------------------------------------------

    public void startSearch(View view)
    {
        StringBuilder queryBuilder = new StringBuilder();
        for (SearchItemParameter p : searchParameters)
        {
            String setting = p.getSetting();
            if (setting != null && !setting.isEmpty())
            {
                queryBuilder.append(setting).append(" ");
            }
        }

        String query = queryBuilder.toString().trim();

        if (query.isEmpty())
        {
            Toast.makeText(this, "Please enter some search details.", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Searching the web...");
        pd.setCancelable(false);
        pd.show();

        String finalQuery = query;
        new Thread(() -> {
            try
            {
                Log.d(TAG, "Starting search for: " + finalQuery);
                List<String> urls = ExaManager.getInstance().search(finalQuery);
                if (urls == null || urls.isEmpty())
                {
                    throw new Exception("No search results found for: " + finalQuery);
                }

                StringBuilder text = new StringBuilder();
                int keptCount = 0;

                for (String url : urls)
                {
                    if (keptCount >= 5) break;

                    runOnUiThread(() -> pd.setMessage("Reading from " + url + "..."));

                    String raw = ExaManager.getInstance().getContents(url);
                    if (raw == null || raw.isEmpty()) continue;

                    if (raw.length() < 100) continue;

                    String lower = raw.toLowerCase();
                    if ((lower.contains("robot") && lower.contains("check")) ||
                            lower.contains("captcha") ||
                            (lower.contains("access denied") && !lower.contains("product")) ||
                            lower.contains("please enable cookies"))
                    {
                        continue;
                    }

                    String cleaned = raw.replaceAll("\\s+", " ");
                    if (cleaned.length() > 3000)
                        cleaned = cleaned.substring(0, 3000);

                    text.append("SOURCE_URL: ").append(url).append("\n");
                    text.append(cleaned).append("\n\n");
                    keptCount++;
                }

                if (text.length() == 0)
                {
                    throw new Exception("Could not extract valid product data from the found pages.");
                }

                runOnUiThread(() -> pd.setMessage("Extracting products..."));

                String msg = SEARCH_PROMPT  + text.toString();

                GeminiManager.getInstance().sendTextPrompt(msg, new GeminiCallback()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        runOnUiThread(() -> {
                            pd.dismiss();
                            parseGeminiResponse(result);

                            if (!results.isEmpty())
                                finalizeSearch(finalQuery);
                            else
                                Toast.makeText(search_screen.this,
                                        "No products could be parsed from the search results.", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        runOnUiThread(() -> {
                            pd.dismiss();
                            Toast.makeText(search_screen.this,
                                    "Gemini Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });

            }
            catch (Exception e)
            {
                Log.e(TAG, "Search Pipeline Error", e);
                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(search_screen.this,
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    // -----------------------------------------------------------------------------------------

    private void finalizeSearch(String query) {

        SearchDetails search = new SearchDetails(query, results);

        // Ensure we have the latest UID directly from Firebase Auth
        com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String uid = (fbUser != null) ? fbUser.getUid() : (connected_user != null ? connected_user.getUserID() : null);

        if (uid != null)
        {
            DatabaseReference newSearchRef = searchHistoryRef.child(uid).push();
            String id = newSearchRef.getKey();
            search.setSearch_id(id);

            Log.d(TAG, "Attempting to upload search to: " + newSearchRef.toString());
            Log.d(TAG, "Firebase user: " + fbUser);
            Log.d(TAG, "Connected user: " + connected_user);
            Log.d(TAG, "UID: " + uid);

            newSearchRef.setValue(search)
                    .addOnSuccessListener(aVoid -> Log.d(TAG, "Search history uploaded successfully to: " + newSearchRef.toString()))
                    .addOnFailureListener(e -> Log.e(TAG, "Search history upload failed: " + e.getMessage()));
        }
        else
        {
            Log.w(TAG, "Cannot upload search: No authenticated user found.");
            Toast.makeText(this, "Search not saved: Please sign in.", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(this, search_result_screen.class);
        intent.putExtra("results", results);
        startActivity(intent);
        finish();
    }

    public void back(View view) {
        finish();
    }

    public void addItem(View view) {
        searchParameters.add(new SearchItemParameter("", ""));
        adapter.notifyItemInserted(searchParameters.size() - 1);
    }

    public void searchBtn(View view) {
        startSearch(view);
    }
}
