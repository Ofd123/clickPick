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
        results = (ArrayList<Product>) getIntent().getSerializableExtra("results");

        if (results != null)
        {
            int size = results.size();
            String[] names = new String[size];
            String[] prices = new String[size];
            String[] companies = new String[size];
            int[] images = new int[size];

            for (int i = 0; i < size; i++) {
                Product p = results.get(i);
                names[i] = p.getProduct_name();
                prices[i] = String.valueOf(p.getPrice());
                companies[i] = p.getStore_name();
                try {
                    //TODO: HANDLE TAKING A PIC FROM THE SITE URL
                    //images[i] = getResources().getIdentifier(p.getImage(), "drawable", getPackageName());
                } catch (Exception e) {
                    images[i] = R.drawable.picture_not_found;
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
        tmpList.add(results.get(pos)); //since i could not transfer the object directly, i am packing it in a list
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