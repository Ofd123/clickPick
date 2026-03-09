package com.example.schoolproj.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolproj.R;
import com.example.schoolproj.classes.Product;

import java.util.ArrayList;

public class product_details_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details_screen);

        TextView pName = findViewById(R.id.pName);
        TextView pPrice = findViewById(R.id.pPrice);
        TextView pCompany = findViewById(R.id.pCompany);
        TextView extraData = findViewById(R.id.extraData);


        //get the product from the intent and convert it to a product object
        Intent productDetails = getIntent();
        ArrayList<Product> tmpList = (ArrayList<Product>) productDetails.getSerializableExtra("product");
        Product p = tmpList.get(0);

        //extract the data
        pName.setText(p.getProduct_name());
        pPrice.setText(String.valueOf(p.getPrice()));
        pCompany.setText(p.getStore_name());
        String extra = "store url: " + p.getStore_url() + "\n" + "store location: " + p.getStore_location() + "\n";
        extra += p.getOther_details();
        extraData.setText(extra);
    }

    public void back(View view)
    {
        finish();
    }
}