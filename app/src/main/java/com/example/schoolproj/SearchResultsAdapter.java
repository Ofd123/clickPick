package com.example.schoolproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchResultsAdapter extends BaseAdapter
{
    private Context context;
    private int images[];
    private String names[];
    private String prices[];
    private String companies[];
    private LayoutInflater inflater;

    public SearchResultsAdapter(Context context, int images[], String names[], String prices[], String companies[])
    {
        this.context = context;
        this.images = images;
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
        img.setImageResource(images[i]);

        return view;
    }
}
