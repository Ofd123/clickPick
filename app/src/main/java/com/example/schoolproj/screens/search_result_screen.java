package com.example.schoolproj.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.SearchResultsAdapter;
import com.example.schoolproj.classes.Product;

import java.util.ArrayList;

public class search_result_screen extends MasterActivity implements AdapterView.OnItemClickListener {

    ArrayList<Product> results;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_screen);

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

                // ✅ Name
                names[i] = (p.getProduct_name() != null)
                        ? p.getProduct_name()
                        : "Unknown Product";

                // ✅ Price formatting
                if (p.getPrice() != null) {
                    prices[i] = "$" + String.format("%.2f", p.getPrice());
                } else {
                    prices[i] = "Price unavailable";
                }

                // ✅ Store
                companies[i] = (p.getStore_name() != null)
                        ? p.getStore_name()
                        : "Unknown Store";

                // ✅ Image
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long rowId)
    {
        Intent productDetails = new Intent(this, product_details_screen.class);
        ArrayList<Product> tmpList = new ArrayList<>();
        tmpList.add(results.get(pos)); //since the product could not be transferred directly, i am packing it in a list
        productDetails.putExtra("product", (ArrayList<Product>) tmpList);
        startActivity(productDetails);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture)
    {
        super.onPointerCaptureChanged(hasCapture);
    }

    public void home(View view)
    {
        finish();
    }
}