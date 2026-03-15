package com.example.schoolproj;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproj.classes.SearchItemParameter;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder>
{
    static final int FIXED_TYPE = 0;
    static final int EDITABLE_TYPE = 1;
    private List<SearchItemParameter> parameters;

    public RecycleViewAdapter(List<SearchItemParameter> parameters)
    {
        this.parameters = parameters;
    }

    public List<SearchItemParameter> getParameters()
    {
        return parameters;
    }

    public void setParameters(List<SearchItemParameter> parameters)
    {
        this.parameters = parameters;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (parameters != null && position < parameters.size()) {
            return parameters.get(position).getEditable() ? EDITABLE_TYPE : FIXED_TYPE;
        }
        return FIXED_TYPE;
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        int layoutRes = (viewType == EDITABLE_TYPE) 
                ? R.layout.create_item_attribute_in_search_items 
                : R.layout.change_item_settings_in_search_items;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position)
    {
        SearchItemParameter parameter = parameters.get(position);
        
        // Remove existing text watchers to prevent recursive updates
        if (holder.attributeWatcher != null) {
            if (holder.itemDescED != null) holder.itemDescED.removeTextChangedListener(holder.attributeWatcher);
        }
        if (holder.settingWatcher != null) {
            if (holder.itemDetailsED != null) holder.itemDetailsED.removeTextChangedListener(holder.settingWatcher);
        }

        if (getItemViewType(position) == EDITABLE_TYPE) {
            if (holder.itemDescED != null) {
                holder.itemDescED.setEnabled(true);
                holder.itemDescED.setText(parameter.getAttribute());
                holder.itemDescED.setHint("enter attribute");
                
                holder.attributeWatcher = new TextWatcher() {
                    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                    @Override public void afterTextChanged(Editable s) {
                        parameter.setAttribute(s.toString());
                    }
                };
                holder.itemDescED.addTextChangedListener(holder.attributeWatcher);
            }
        } else {
            if (holder.itemDescTV != null) {
                holder.itemDescTV.setText(parameter.getAttribute());
            }
        }

        if (holder.itemDetailsED != null) {
            holder.itemDetailsED.setEnabled(true);
            holder.itemDetailsED.setText(parameter.getSetting());
            holder.itemDetailsED.setHint("enter settings");
            
            holder.settingWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    parameter.setSetting(s.toString());
                }
            };
            holder.itemDetailsED.addTextChangedListener(holder.settingWatcher);
        }
    }

    @Override
    public int getItemCount()
    {
        return parameters != null ? parameters.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView itemDescTV;
        private EditText itemDetailsED, itemDescED;
        TextWatcher attributeWatcher, settingWatcher;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            itemDescTV = itemView.findViewById(R.id.itemAttribute);
            itemDescED = itemView.findViewById(R.id.itemAttributeED);
            
            itemDetailsED = itemView.findViewById(R.id.itemSetting);
            if (itemDetailsED == null) {
                itemDetailsED = itemView.findViewById(R.id.itemSettingED);
            }
        }
    }
}
