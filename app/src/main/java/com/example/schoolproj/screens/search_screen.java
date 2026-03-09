package com.example.schoolproj.screens;

import static android.content.ContentValues.TAG;

import static com.example.schoolproj.FireBaseFiles.FBRef.searchHistoryRef;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.DatabaseReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class search_screen extends MasterActivity
{
    RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    List<SearchItemParameter> searchParameters;
    GeminiManager geminiManager;
    List<Product> results;
    int status = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        //TODO:  work on GUI
        recyclerView = findViewById(R.id.rvListItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        results = new ArrayList<>();
        Product tmp = new Product("null", 0.0, "null", "null", "null", "null", "null", "null");
        results.add(tmp); //add empty value to make sure to avoid FB errors

        Intent dataSent = getIntent();
        searchParameters = (List<SearchItemParameter>) dataSent.getSerializableExtra("item details");

        adapter = new RecycleViewAdapter(searchParameters); //initialize the adapter
        recyclerView.setAdapter(adapter); //set the adapter to the recycle view
    }
    // -----------------------------------------------------------------------------------------
    void parseGeminiResponse(String response)
    {
        String cleanResponse;

        if(response == null || response.trim().isEmpty())
        {
            results = null;
        }
        try
        {
            //clean the response:
            cleanResponse = response.trim().
                    replaceAll("```json\\s*", "")
                    .replaceAll("```\\s*", "")
                    .replace("\n", " ")
                    .replace("\r", " ")
                    .trim();

            // If it doesn't look like a JSON array, try to wrap it
            if (!cleanResponse.startsWith("[")) {
                cleanResponse = "[" + cleanResponse + "]";
            }

            JSONArray jsonArray = new JSONArray(cleanResponse);

            for (int i = 0; i < jsonArray.length(); i++)
            {
                JSONObject obj = jsonArray.getJSONObject(i);

                String product_name   = obj.optString("product_name", "error");
                String image          = obj.optString("image", "error");
                String description    = obj.optString("description", "error");
                String store_name     = obj.optString("store_name", "error");
                String store_url      = obj.optString("store_url", "error");
                String store_location = obj.optString("store_location", "error");
                String other_details  = obj.optString("other_details", "error");

                // Price as Double (null if missing or null in JSON)
                Double price = null;
                if (obj.has("price") && !obj.isNull("price"))
                {
                    price = obj.getDouble("price");
                }

                // Use the exact constructor you specified
                Product result = new Product(product_name, price, image, description, store_name, store_url, store_location, other_details);

                results.add(result);
            }

            Log.d(TAG, "Successfully parsed " + results.size() + " products directly to List<Product>");

        } catch (Exception e) {
            Log.e(TAG, "parseGeminiResponse failed", e);
            results = null;
        }

    }
    // -----------------------------------------------------------------------------------------
    void searchWithGemini(String finalPrompt)
    {
        geminiManager.sendTextPrompt(finalPrompt, new GeminiCallback()
        {
            @Override
            public void onSuccess(String result)
            {
                runOnUiThread(() -> {
                    parseGeminiResponse(result);
                    status = 1;
                });
            }

            @Override
            public void onFailure(Throwable error)
            {
                runOnUiThread(() -> {
                    Log.e(TAG, "Gemini Failure", error);
                    Toast.makeText(getApplicationContext(), "Search failed: "+error, Toast.LENGTH_SHORT).show();
                    results = null;
                });
            }
        });
    }
    // -----------------------------------------------------------------------------------------
    void manualSearch()
    {

    }
    // -----------------------------------------------------------------------------------------
    public void addItem(View view)
    {
        SearchItemParameter newItem = new SearchItemParameter("", "", true);
        searchParameters.add(newItem);
        adapter.notifyItemInserted(searchParameters.size() - 1);
        recyclerView.scrollToPosition(searchParameters.size() - 1);
    }
    // -----------------------------------------------------------------------------------------
    public void back(View view)
    {
        finish();
    }
    // -----------------------------------------------------------------------------------------
    public void searchBtn(View view)
    {
        searchParameters = adapter.getParameters();

        //convert the list to a string:
        StringBuilder search_query = new StringBuilder();
        for (int i = 0; i < searchParameters.size(); i++)
        {
            search_query.append(searchParameters.get(i).toString());
            if (i != searchParameters.size() - 1)
            {
                search_query.append(",");
            }
        }

        String finalPrompt = Prompts.SEARCH_ITEMS_RELEVANT + search_query;
        //TODO: search the item on the webb and then move it to the result screen
        try
        {
            searchWithGemini(finalPrompt);
        }
        catch(Exception e)
        {
            Log.e(TAG, "searchBtn failed", e);
            manualSearch();
        }

        if(status == 1)
        {
            //generate the search Object:
            SearchDetails thisSearch = new SearchDetails(search_query.toString(), results); //TODO: update if it was compared or no

            //upload the search to the database:
            String userID = connected_user.getUserID();
            String searchID = searchHistoryRef.child(userID).push().getKey();
            thisSearch.setSearch_id(searchID);
            searchHistoryRef.child(userID).child(thisSearch.getSearch_id()).setValue(thisSearch);

            Intent resultsScreen = new Intent(this, search_result_screen.class);
            resultsScreen.putExtra("results", (ArrayList<Product>) results);
            startActivity(resultsScreen);
            finish(); //close this page and return back to the home page
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Search failed", Toast.LENGTH_SHORT).show();
        }
    }
}