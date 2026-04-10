package com.example.schoolproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Adapter for a ListView that displays product search results.
 * Manages arrays of product names, prices, companies, and image URLs.
 * Uses Picasso for asynchronous image loading.
 */
public class SearchResultsAdapter extends BaseAdapter
{
    /** The activity context. */
    private Context context;
    /** Array of image URLs for each product. */
    private String images_url[];
    /** Array of product names. */
    private String names[];
    /** Array of formatted product prices. */
    private String prices[];
    /** Array of store or company names. */
    private String companies[];
    /** Inflater for rendering list item layouts. */
    private LayoutInflater inflater;

    /**
     * Constructor for SearchResultsAdapter.
     * @param context Current context.
     * @param images Array of product image URLs.
     * @param names Array of product names.
     * @param prices Array of product prices.
     * @param companies Array of product companies/stores.
     */
    public SearchResultsAdapter(Context context, String images[], String names[], String prices[], String companies[])
    {
        this.context = context;
        this.images_url = images;
        this.names = names;
        this.prices = prices;
        this.companies = companies;
        inflater = LayoutInflater.from(context);
    }

    /**
     * Returns the number of items in the search results.
     * @return Size of the names array.
     */
    @Override
    public int getCount()
    {
        return names.length;
    }

    /**
     * Returns the name of the product at a specific position.
     * @param position Position in the list.
     * @return Product name string.
     */
    @Override
    public Object getItem(int position)
    {
        return names[position];
    }

    /**
     * Returns the row ID associated with a specific position.
     * @param position Position in the list.
     * @return The position as the ID.
     */
    @Override
    public long getItemId(int position)
    {
        return position;
    }

    /**
     * Renders the view for a product result item.
     * Populates text fields and loads product images using Picasso.
     * @param i Position in list.
     * @param view Recycled view.
     * @param parent Parent ViewGroup.
     * @return The rendered view.
     */
    @Override
    public View getView(int i, View view, ViewGroup parent)
    {
        view = inflater.inflate(R.layout.list_product_layout, parent,false);
        ImageView img = view.findViewById(R.id.imageView);
        TextView p_name = view.findViewById(R.id.p_name);
        TextView p_price = view.findViewById(R.id.p_price);
        TextView p_company = view.findViewById(R.id.pCompany);

        p_name.setText(names[i]);
        p_price.setText(prices[i]);
        p_company.setText(companies[i]);
        if(images_url[i] != null && !images_url[i].isEmpty() && !images_url[i].equals("null"))
        {
            Picasso.get().load(images_url[i]).into(img);
        }
        else
        {
            img.setImageResource(R.drawable.picture_not_found);
        }

        return view;
    }
}
