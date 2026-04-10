package com.example.schoolproj.screens;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.SearchResultsAdapter;
import com.example.schoolproj.classes.Product;

import java.util.ArrayList;

/**
 * Activity for displaying the results of a product search.
 * Shows a list of products with their names, prices, and store information.
 * Users can click on an item to see more details.
 */
public class search_result_screen extends MasterActivity implements AdapterView.OnItemClickListener {

    /** The list of products returned by the search. */
    ArrayList<Product> results;

    /**
     * Called when the activity is starting.
     * Initializes the ListView and fills it with results passed via the intent.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

        ListView listView = findViewById(R.id.results);
        Intent searchResults = getIntent();
        results = (ArrayList<Product>) searchResults.getSerializableExtra("results");

        if (results != null)
        {
            int size = results.size();
            String[] names = new String[size];
            String[] prices = new String[size];
            String[] companies = new String[size];
            String[] images = new String[size];

            for (int i = 0; i < size; i++) {
                Product p = results.get(i);


                if (p.getProduct_name() != null) {
                    names[i] = p.getProduct_name();
                } else {
                    names[i] = "Unknown Product";
                }

                if(p.getPrice() == null)
                {
                    prices[i] = "Price unavailable";
                }
                else if (!(p.getPrice() > 0))
                {
                    prices[i] = "Store page";
                }
                else
                {
                    prices[i] = String.format("%.2f", p.getPrice()) + "$";
                }


                if (p.getStore_name() != null) {
                    companies[i] = p.getStore_name();
                } else {
                    companies[i] = "Unknown Store";
                }


                if (p.getImageUrl() != null &&
                        !p.getImageUrl().isEmpty() &&
                        !p.getImageUrl().equals("null")) {

                    images[i] = p.getImageUrl();
                } else {
                    images[i] = ""; // fallback
                }
            }
            ArrayAdapter<String> adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, names);
            SearchResultsAdapter resultsAdp = new SearchResultsAdapter(this, images, names, prices, companies);
            listView.setOnItemClickListener(this);
            listView.setAdapter(resultsAdp);
        }
    }

    /**
     * Callback for when a result item is clicked.
     * Transitions to the product details screen for the selected item.
     * @param adapterView The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked.
     * @param pos The position of the view in the adapter.
     * @param rowId The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowId)
    {
        Intent productDetails = new Intent(this, product_details_screen.class);
        ArrayList<Product> tmpList = new ArrayList<>();
        tmpList.add(results.get(pos)); //since the product could not be transferred directly, i am packing it in a list
        productDetails.putExtra("product", (ArrayList<Product>) tmpList);
        startActivity(productDetails);
    }

    /**
     * UI callback for the home button to close the activity.
     * @param view The view that was clicked.
     */
    public void home(View view)
    {
        finish();
    }
}