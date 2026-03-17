package com.example.schoolproj.screens;

import static com.example.schoolproj.FireBaseFiles.FBRef.favoritesRef;
import static com.example.schoolproj.FireBaseFiles.FBRef.refAuth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolproj.R;
import com.example.schoolproj.classes.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class product_details_screen extends AppCompatActivity
{

    private Product product;

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
        ImageView productImage = findViewById(R.id.productImage);


        //get the product from the intent and convert it to a product object
        Intent productDetails = getIntent();
        ArrayList<Product> tmpList = (ArrayList<Product>) productDetails.getSerializableExtra("product");
        if (tmpList != null && !tmpList.isEmpty()) 
        {
            product = tmpList.get(0);

            //get the product image and set it
            String image_url = product.getImageUrl();
            if(image_url != null && !image_url.isEmpty() && !image_url.equals("null"))
            {
                Picasso.get().load(image_url).into(productImage);
            }
            else
            {
                productImage.setImageResource(R.drawable.picture_not_found);
            }

            //extract the data
            pName.setText(product.getProduct_name());
            pPrice.setText(String.valueOf(product.getPrice()));
            pCompany.setText(product.getStore_name());
            String extra = "store url: " + product.getStore_url() + "\n" + "store location: " + product.getStore_location() + "\n";
            extra += product.getOther_details();
            extra += "\n" + product.getDescription();
            extraData.setText(extra);
        }
        else
        {
            Toast.makeText(this, "Product details not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    public void back(View view)
    {
        finish();
    }

    public void save(View view) //saves the product onto the user's firebase
    {
        FirebaseUser user = refAuth.getCurrentUser();
        if (user != null)
        {
            String uid = user.getUid();
            // Create a unique key for the saved item
            String savedItemKey = favoritesRef.child(uid).push().getKey();

            if (savedItemKey != null)
            {
                favoritesRef.child(uid).child(savedItemKey).setValue(product)
                    .addOnSuccessListener(new OnSuccessListener<Void>()
                    {
                        @Override
                        public void onSuccess(Void aVoid)
                        {
                            Toast.makeText(product_details_screen.this, "Product saved to favorites!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            Toast.makeText(product_details_screen.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }
        else
        {
            Toast.makeText(this, "Please log in to save items", Toast.LENGTH_SHORT).show();
        }
    }
}