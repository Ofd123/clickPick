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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

/**
 * Activity for configuring and executing product searches.
 * Allows users to define search parameters (attributes/settings), performs a web search via Exa,
 * and extracts structured product data using Gemini AI.
 * Results are automatically saved to the user's search history in Firebase.
 */
public class search_screen extends MasterActivity
{
    /** RecyclerView for displaying and editing search parameters. */
    RecyclerView recyclerView;
    /** Adapter for managing the search parameter list items. */
    RecycleViewAdapter adapter;
    /** List of search parameters (attributes and their values) defined by the user. */
    List<SearchItemParameter> searchParameters;
    /** List of product results extracted from the search. */
    ArrayList<Product> results;

    /**
     * Called when the activity is starting.
     * Initializes UI components, parses incoming search details (if any),
     * and sets up the recycler view for search parameters.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

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
        adapter.setOnParameterChangedListener(new RecycleViewAdapter.OnParameterChangedListener() {
            @Override
            public void onParameterUpdated() {
                updateAddButtonVisibility();
            }
        });
        recyclerView.setAdapter(adapter);

        updateAddButtonVisibility();
    }

    /**
     * Parses initial search details from a JSON string (typically from image analysis).
     * @param json The JSON string containing 'item' and 'settings' keys.
     */
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

    /**
     * Parses the JSON response from Gemini AI into a list of Product objects.
     * Handles both raw arrays and nested "products" arrays.
     * @param response The raw JSON string from Gemini.
     */
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

    /**
     * Safely retrieves a string from a JSONObject, handling nulls and trimming values.
     * @param obj Target JSONObject.
     * @param key Key to retrieve.
     * @return The trimmed string, or null if key missing/invalid.
     */
    private String getSafeString(JSONObject obj, String key)
    {
        if (!obj.has(key) || obj.isNull(key)) return null;

        String value = obj.optString(key, "");
        value = value.trim();

        if (value.equalsIgnoreCase("null") || value.isEmpty()) return null;

        return value;
    }

    /**
     * Initiates the multi-step search pipeline:
     * 1. Constructs a web search query from user parameters.
     * 2. Performs a web search using Exa.
     * 3. Scrapes and cleans content from search results.
     * 4. Sends content to Gemini for product extraction.
     * @param view The view that was clicked (Search button).
     */
    public void startSearch(View view)
    {
        StringBuilder queryBuilder = new StringBuilder();
        for (SearchItemParameter p : searchParameters)
        {
            String setting = p.getSetting();
            if (setting != null && !setting.isEmpty() && !setting.equalsIgnoreCase("null") && !setting.equalsIgnoreCase("undefined") && !setting.equalsIgnoreCase("any") && !setting.equalsIgnoreCase("anything"))
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
        new Thread(new Runnable() {
            @Override
            public void run() {
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
                    if (keptCount >= 5) {
                        break;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pd.setMessage("Reading from " + url + "...");
                        }
                    });

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.setMessage("Extracting products...");
                    }
                });

                String msg = SEARCH_PROMPT  + text.toString();

                GeminiManager.getInstance().sendTextPrompt(msg, new GeminiCallback()
                {
                    @Override
                    public void onSuccess(String result)
                    {
                        Log.d(TAG, "========== GEMINI RAW RESPONSE ==========");
                        Log.d(TAG, result);
                        Log.d(TAG, "=========================================");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();

                                if (result == null || result.trim().isEmpty())
                                {
                                    Toast.makeText(search_screen.this,"Empty response from AI", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (result.contains("\"error\":503"))
                                {
                                    Toast.makeText(search_screen.this,"AI is currently busy (server overloaded). Try again shortly.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                String clean = result.trim()
                                        .replaceAll("```json\\s*", "")
                                        .replaceAll("```\\s*", "")
                                        .trim();

                                if (clean.contains("\"error\"") || clean.contains("UNAVAILABLE"))
                                {
                                    Log.e(TAG, "Gemini returned error: " + clean);

                                    Toast.makeText(search_screen.this, "AI is currently busy (server overloaded). Try again shortly.", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                if (!clean.startsWith("[") && !clean.startsWith("{"))
                                {
                                    Log.e(TAG, "Invalid Gemini format: " + clean);

                                    Toast.makeText(search_screen.this,
                                            "Unexpected AI response format.",
                                            Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                parseGeminiResponse(clean);

                                if (!results.isEmpty())
                                {
                                    finalizeSearch(finalQuery);
                                }
                                else
                                {
                                    Toast.makeText(search_screen.this,
                                            "No valid products found.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        Log.e(TAG, "========== GEMINI FAILURE ==========", error);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pd.dismiss();

                                {
                                    Toast.makeText(search_screen.this, "Gemini failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        return;
                    }
                });

            }
            catch (Exception e)
            {
                Log.e(TAG, "Search Pipeline Error", e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pd.dismiss();
                        Toast.makeText(search_screen.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
        }).start();
    }

    /**
     * Finalizes the search by saving the results to history in Firebase and transitioning to the results screen.
     * @param query The final search query string used.
     */
    private void finalizeSearch(String query) {

        SearchDetails search = new SearchDetails(query, results);

        // Ensure we have the latest UID directly from Firebase Auth
        com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        String uid = null;
        if (fbUser != null) {
            uid = fbUser.getUid();
        } else if (connected_user != null) {
            uid = connected_user.getUserID();
        }

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
                    .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Search history uploaded successfully to: " + newSearchRef.toString());
                        }
                    })
                    .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Search history upload failed: " + e.getMessage());
                        }
                    });
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

    /**
     * UI callback for the back button to close the activity.
     * @param view The view that was clicked.
     */
    public void back(View view) {
        finish();
    }

    /**
     * UI callback to add a new search parameter attribute card.
     * Limits the total number of parameters to 8.
     * @param view The view that was clicked (Add button).
     */
    public void addItem(View view) {
        if (searchParameters.size() >= 8) {
            Toast.makeText(this, "Maximum 8 attributes allowed", Toast.LENGTH_SHORT).show();
            return;
        }
        searchParameters.add(new SearchItemParameter("", "", true));
        adapter.notifyItemInserted(searchParameters.size() - 1);
        updateAddButtonVisibility();

        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.smoothScrollToPosition(searchParameters.size() - 1);
            }
        });
    }

    /**
     * Updates the visibility of "Add" buttons based on whether the last parameter is correctly filled.
     * Enforces a consistency rule where parameters must be completed before adding new ones.
     */
    private void updateAddButtonVisibility() {
        if (searchParameters == null || searchParameters.isEmpty()) return;

        SearchItemParameter lastItem = searchParameters.get(searchParameters.size() - 1);
        boolean lastFilled = !lastItem.getAttribute().trim().isEmpty() &&
                            !lastItem.getSetting().trim().isEmpty();

        // If it's the first item, we only care about the setting (product name is fixed)
        if (searchParameters.size() == 1) {
            lastFilled = !lastItem.getSetting().trim().isEmpty();
        }

        boolean canAdd = lastFilled && searchParameters.size() < 8;
        int visibility;
        if (canAdd) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }

        View btnCard = findViewById(R.id.btnAddAttributeCard);
        View btnBottom = findViewById(R.id.btnAddItemBottom);

        if (btnCard != null) btnCard.setVisibility(visibility);
        if (btnBottom != null) btnBottom.setVisibility(visibility);
    }

    /**
     * UI callback for the secondary search button.
     * Proxies the request to startSearch.
     * @param view The view that was clicked.
     */
    public void searchBtn(View view)
    {
        startSearch(view);
    }
}
