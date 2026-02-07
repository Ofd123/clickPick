package com.example.schoolproj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproj.classes.SearchItemParameter;

import java.util.List;
import java.util.Objects;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>
{
//    Context context;
    private List<SearchItemParameter> parameters;
    public RecycleViewAdapter(/*Context context,*/ List<SearchItemParameter> parameters)
    {
//        this.context = context;
        this.parameters = parameters;
    }

    public List<SearchItemParameter> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<SearchItemParameter> parameters)
    {
        this.parameters = parameters;
    }

    public RecycleViewAdapter(@NonNull View itemView)
    {
        super();

    }
//  ------------------------------------------------------------
    // handles the new gui while scrolling
    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_in_search_items, Objects.requireNonNull(parent), false);
        return new ViewHolder(view);
    }

    // bind the data from our list to the corersponding view
    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position)
    {
        SearchItemParameter parameter = parameters.get(position);

        holder.itemDescTV.setText(parameter.getAttribute());
        holder.itemDetailsED.setText(parameter.getSetting());
    }

    @Override
    public int getItemCount()
    {
        return parameters.toArray().length;
    }
//  ------------------------------------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView itemDescTV;
        private EditText itemDetailsED, itemDescED;
        Button deleteParamBtn,newParamBtn;
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            itemDescTV = itemView.findViewById(R.id.itemDesc);
            itemDetailsED = itemView.findViewById(R.id.itemDetails);

        }
    }
}
