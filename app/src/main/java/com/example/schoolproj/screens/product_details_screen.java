package com.example.schoolproj.screens;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.schoolproj.FireBaseFiles.FBRef.favoritesRef;
import static com.example.schoolproj.FireBaseFiles.FBRef.refAuth;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolproj.R;
import com.example.schoolproj.classes.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.klinker.android.link_builder.Link;
import com.klinker.android.link_builder.LinkBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Activity for displaying detailed information about a specific product.
 * Allows users to view product images, names, prices, and direct store links.
 * Also provides functionality to save or remove products from the user's favorites in Firebase.
 */
public class product_details_screen extends AppCompatActivity
{
    /** The specific product being detailed in this screen. */
    private Product product;
    /** The database key for this product if it's already saved in favorites; null otherwise. */
    private String favoriteKey = null;
    /** UI element for saving or removing from favorites. */
    private ImageView saveBtn;

    /**
     * Called when the activity is starting.
     * Initializes UI elements, retrieves product data from the intent, and checks if it's a favorite.
     * Sets up link builders for the store URL.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

        TextView pName = findViewById(R.id.pName);
        TextView pPrice = findViewById(R.id.pPrice);
        TextView pPriceType = findViewById(R.id.pPriceType);
        TextView pCompany = findViewById(R.id.pCompany);
        TextView extraData = findViewById(R.id.extraData);
        ImageView productImage = findViewById(R.id.productImage);
        saveBtn = findViewById(R.id.saveBtn); // Assuming the ID is saveBtn based on common naming

        //get the product from the intent and convert it to a product object
        Intent productDetails = getIntent();
        ArrayList<Product> tmpList = (ArrayList<Product>) productDetails.getSerializableExtra("product");

        if (tmpList != null && !tmpList.isEmpty()) 
        {
            product = tmpList.get(0);
            checkIfFavorite();

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

            if(product.getPrice() == null)
            {
                pPrice.setText("Price unavailable");
                pPriceType.setText("");
            }
            else if (!(product.getPrice() > 0))
            {
                pPrice.setText("This is a Store page");
                pPriceType.setText("");
            }
            else
            {
                pPrice.setText(String.format("%.2f", product.getPrice()) + "$");
                pPriceType.setText(product.getPrice_type());
            }
            pCompany.setText(product.getStore_name());
            
            String storeUrlStr = product.getStore_url();
            String extra = "Store URL: " + storeUrlStr + "\n";
            if (product.getStore_location() != null && !product.getStore_location().isEmpty())
            {
                extra += "store location: " + product.getStore_location() + "\n";
            }

            if (product.getOther_details() != null && !product.getOther_details().isEmpty())
            {
                extra += product.getOther_details() + "\n";
            }

            extra += product.getDescription();


            extraData.setText(extra);

            if (storeUrlStr != null && !storeUrlStr.isEmpty() && !storeUrlStr.equals("null")) {
                Link link = new Link(storeUrlStr)
                        .setTextColor(Color.BLUE)
                        .setTextColorOfHighlightedLink(Color.CYAN)
                        .setUnderlined(true)
                        .setBold(true)
                        .setOnClickListener(new Link.OnClickListener() {
                            @Override
                            public void onClick(String url) {
                                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                    url = "https://" + url;
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(intent);
                            }
                        });

                LinkBuilder.on(extraData)
                        .addLink(link)
                        .build();
            }
        }
        else
        {
            Toast.makeText(this, "Product details not found", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * UI callback for the back button to close the activity.
     * @param view The view that was clicked.
     */
    public void back(View view)
    {
        finish();
    }

    /**
     * UI callback to save or remove the product from the user's favorites in Firebase.
     * Checks if the user is authenticated before performing database operations.
     * @param view The view that was clicked (Save/Star button).
     */
    public void save(View view) //saves or removes the product from the user's firebase
    {
        FirebaseUser user = refAuth.getCurrentUser();
        if (user != null)
        {
            String uid = user.getUid();
            
            if (favoriteKey == null) {
                // Not a favorite, so SAVE it
                String savedItemKey = favoritesRef.child(uid).push().getKey();
                if (savedItemKey != null) {
                    favoritesRef.child(uid).child(savedItemKey).setValue(product)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                favoriteKey = savedItemKey;
                                updateSaveButtonIcon();
                                Toast.makeText(product_details_screen.this, "Product saved to favorites!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(product_details_screen.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            } else {
                // Already a favorite, so REMOVE it
                favoritesRef.child(uid).child(favoriteKey).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            favoriteKey = null;
                            updateSaveButtonIcon();
                            Toast.makeText(product_details_screen.this, "Removed from favorites", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(product_details_screen.this, "Failed to remove: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        }
        else
        {
            Toast.makeText(this, "Please log in to manage favorites", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Queries Firebase to check if the current product is already in the user's favorites.
     * Updates favoriteKey and the save button icon accordingly.
     */
    private void checkIfFavorite() {
        FirebaseUser user = refAuth.getCurrentUser();
        if (user == null || product == null) return;

        favoritesRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Product p = child.getValue(Product.class);
                    if (p != null && p.getProduct_name() != null && 
                        p.getProduct_name().equals(product.getProduct_name()) &&
                        p.getStore_url() != null && p.getStore_url().equals(product.getStore_url())) {
                        favoriteKey = child.getKey();
                        updateSaveButtonIcon();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    /**
     * Updates the save button image depending on whether the product is a favorite (full star vs empty star).
     */
    private void updateSaveButtonIcon() {
        if (saveBtn != null)
        {
            if (favoriteKey != null)
            {
                saveBtn.setImageResource(R.drawable.full_star);
            }
            else
            {
                saveBtn.setImageResource(R.drawable.empty_star);
            }
        }
    }
}
