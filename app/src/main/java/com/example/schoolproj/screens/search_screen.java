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
    List<Product> results;

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
            Log.e(TAG, "Parse error", e);
        }
    }

    // -----------------------------------------------------------------------------------------

    private boolean isValidStore(String url)
    {
        if (url == null) return false;

        url = url.toLowerCase();


        if (url.contains("/p/") /* ebay catalog page */ || url.contains("youtube") || url.contains("review"))
        {
            return false;
        }

        //check if it is a real item's url
        if(url.contains("/itm/") /*ebay real product */ || url.contains("/dp/") /*amazon*/ || url.contains("/product") )
        {
            return true;
        }
        return false;

    }

    // -----------------------------------------------------------------------------------------

    private void parseGeminiResponse(String response)
    {
        try {
            Log.d(TAG, "Gemini RAW: " + response);

            String clean = response.trim()
                    .replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            JSONArray arr = new JSONArray(clean);
            results.clear();

            java.util.HashSet<String> seenUrls = new java.util.HashSet<>();

            for (int i = 0; i < arr.length(); i++)
            {
                JSONObject obj = arr.getJSONObject(i);

                Product p = new Product();

                p.setProduct_name(getSafeString(obj, "product_name"));
                p.setStore_name(getSafeString(obj, "store_name"));
                p.setStore_url(getSafeString(obj, "store_url"));
                p.setStore_location(getSafeString(obj, "store_location"));
                p.setDescription(getSafeString(obj, "description"));
                p.setImageUrl(getSafeString(obj, "image"));
                p.setOther_details(getSafeString(obj, "other_details"));


                if (obj.has("price") && !obj.isNull("price"))
                {
                    try
                    {
                        p.setPrice(obj.getDouble("price"));
                    }
                    catch (Exception e)
                    {
                        p.setPrice(null);
                    }
                }
                else
                {
                    p.setPrice(null);
                }

                //skip broken
                if (p.getProduct_name() == null || p.getProduct_name().isEmpty()) continue;
                if (p.getStore_url() == null || p.getStore_url().isEmpty()) continue;

                // remove links from the same url
                if (seenUrls.contains(p.getStore_url()))
                {
                    continue;
                }
                seenUrls.add(p.getStore_url());

                results.add(p);
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, "Parse failed", e);
        }
    }
    private String getSafeString(JSONObject obj, String key)
    {
        if (!obj.has(key) || obj.isNull(key)) return null;

        String value = obj.optString(key, null);

        if (value == null) return null;

        value = value.trim();

        if (value.equalsIgnoreCase("null") || value.isEmpty())
            return null;

        return value;
    }

    // -----------------------------------------------------------------------------------------

    public void searchBtn(View view)
    {
        searchParameters = adapter.getParameters();

        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < searchParameters.size(); i++)
        {
            String attribute = searchParameters.get(i).getAttribute();
            String setting = searchParameters.get(i).getSetting();


            if (attribute == null || attribute.trim().isEmpty())
            {
                continue; //skip empty attributes
            }
            if (setting == null || setting.trim().isEmpty() || setting.equalsIgnoreCase("any") || setting.equalsIgnoreCase("unknown"))
            {
                continue; //skip attributes where the user does not want to specify anything
            }

            queryBuilder.append(setting.trim()).append(" "); //only adding the value (for more accurate result)
        }
        queryBuilder.append("buy online price"); //add intent booster

        final String query = queryBuilder.toString().trim();
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Searching...");
        pd.setMessage("Finding product pages...");
        pd.setCancelable(false);
        pd.show();

        new Thread(() -> {
            try
            {
                //search
                JSONArray searchResults = ExaManager.getInstance().search(query);

                //filter urls
                JSONArray urls = new JSONArray();
                for (int i = 0; i < searchResults.length(); i++)
                {
                    String url = searchResults.getJSONObject(i).optString("url");

                    if (isValidStore(url))
                    {
                        urls.put(url);
                    }
                }

                if (urls.length() == 0)
                    throw new Exception("No valid stores found");

                runOnUiThread(() -> pd.setMessage("Reading product pages..."));

                JSONArray contents = ExaManager.getInstance().getContents(urls);

                StringBuilder text = new StringBuilder();

                for (int i = 0; i < contents.length(); i++)
                {
                    JSONObject obj = contents.getJSONObject(i);

                    String url = obj.optString("url");
                    String raw = obj.optString("text");

                    if (raw == null || raw.length() < 200) continue;

                    String cleaned = raw
                            .replaceAll("\\s+", " ")
                            .replaceAll("(?i)(home|menu|login|signup|cart|footer).*", "");

                    if (cleaned.length() > 4000)
                        cleaned = cleaned.substring(0, 4000);

                    //the url for the product
                    text.append("SOURCE_URL: ").append(url).append("\n");
                    text.append(cleaned).append("\n\n");
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
                                finalizeSearch(query);
                            else
                                Toast.makeText(search_screen.this,
                                        "No products found", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onFailure(Throwable error)
                    {
                        runOnUiThread(() -> {
                            pd.dismiss();
                            Toast.makeText(search_screen.this,
                                    "Gemini failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                });

            }
            catch (Exception e)
            {
                Log.e(TAG, "Pipeline failed", e);

                runOnUiThread(() -> {
                    pd.dismiss();
                    Toast.makeText(search_screen.this,
                            "Search failed: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    // -----------------------------------------------------------------------------------------

    private void finalizeSearch(String query) {

        SearchDetails search = new SearchDetails(query, results);

        if (connected_user != null && connected_user.getUserID() != null) {
            String uid = connected_user.getUserID();
            String id = searchHistoryRef.child(uid).push().getKey();
            search.setSearch_id(id);

            searchHistoryRef.child(uid).child(id).setValue(search);
        }

        Intent intent = new Intent(this, search_result_screen.class);
        intent.putExtra("results", (ArrayList<Product>) results);
        startActivity(intent);
        finish();
    }

    // -----------------------------------------------------------------------------------------

    public void addItem(View view) {
        searchParameters.add(new SearchItemParameter(true));
        adapter.notifyItemInserted(searchParameters.size() - 1);
    }

    public void back(View view) {
        finish();
    }
}
