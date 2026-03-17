package com.example.schoolproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SearchResultsAdapter extends BaseAdapter
{
    private Context context;
    private String images_url[];
    private String names[];
    private String prices[];
    private String companies[];
    private LayoutInflater inflater;

    public SearchResultsAdapter(Context context, String images[], String names[], String prices[], String companies[])
    {
        this.context = context;
        this.images_url = images;
        this.names = names;
        this.prices = prices;
        this.companies = companies;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount()
    {
        return names.length;
    }

    @Override
    public Object getItem(int position)
    {
        return names[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

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
