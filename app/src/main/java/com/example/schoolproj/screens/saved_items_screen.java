package com.example.schoolproj.screens;

import static com.example.schoolproj.FireBaseFiles.FBRef.favoritesRef;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.SearchResultsAdapter;
import com.example.schoolproj.classes.Product;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class saved_items_screen extends MasterActivity implements AdapterView.OnItemClickListener {

    private ListView listView;
    private SearchResultsAdapter adapter;
    private List<Product> savedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_items_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

        listView = findViewById(R.id.savedItems);
        savedProducts = new ArrayList<>();

        if (connected_user != null)
        {
            loadSavedItems();
        }
        else
        {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSavedItems()
    {
        String userID = connected_user.getUserID();
        favoritesRef.child(userID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                savedProducts.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    Product product = data.getValue(Product.class);
                    if (product != null)
                    {
                        savedProducts.add(product);
                    }
                }
                updateList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(saved_items_screen.this, "Failed to load saved items: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateList()
    {
        int size = savedProducts.size();
        String[] names = new String[size];
        String[] prices = new String[size];
        String[] companies = new String[size];
        String[] images = new String[size];

        for (int i = 0; i < size; i++)
        {
            Product p = savedProducts.get(i);
            names[i] = p.getProduct_name();
            prices[i] = String.valueOf(p.getPrice());
            companies[i] = p.getStore_name();
            if(p.getImageUrl() != null && !p.getImageUrl().isEmpty() && !p.getImageUrl().equals("null"))
            {
                images[i] = p.getImageUrl();
            }
            else
            {
                images[i] = ""; //in case there is no image, i would prefer handling it here and have a decisive 'empty' value
            }
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, names);
        SearchResultsAdapter resultsAdp = new SearchResultsAdapter(this, images, names, prices, companies);
        listView.setOnItemClickListener(this);
        listView.setAdapter(resultsAdp);
    }

    public void home(View view) {
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Product selectedProduct = savedProducts.get(position);
        ArrayList<Product> productList = new ArrayList<>();
        productList.add(selectedProduct);
        Intent intent = new Intent(this, product_details_screen.class);
        intent.putExtra("product", productList);
        startActivity(intent);
    }
}
