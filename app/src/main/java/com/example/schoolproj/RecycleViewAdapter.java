package com.example.schoolproj;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproj.classes.SearchItemParameter;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    static final int FIXED_TYPE = 0;
    static final int EDITABLE_TYPE = 1;
    static final int FIRST_ITEM_TYPE = 2;
    private List<SearchItemParameter> parameters;
    private OnParameterChangedListener listener;

    public interface OnParameterChangedListener {
        void onParameterUpdated();
    }

    public RecycleViewAdapter(List<SearchItemParameter> parameters) {
        this.parameters = parameters;
    }

    public void setOnParameterChangedListener(OnParameterChangedListener listener) {
        this.listener = listener;
    }

    public List<SearchItemParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<SearchItemParameter> parameters) {
        this.parameters = parameters;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return FIRST_ITEM_TYPE;
        if (parameters != null && position < parameters.size()) {
            return parameters.get(position).getEditable() ? EDITABLE_TYPE : FIXED_TYPE;
        }
        return FIXED_TYPE;
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes;
        if (viewType == FIRST_ITEM_TYPE) {
            layoutRes = R.layout.search_product_first_layout;
        } else {
            layoutRes = R.layout.create_item_attribute_in_search_items;
        }
        
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapter.ViewHolder holder, int position) {
        SearchItemParameter parameter = parameters.get(position);

        // Remove old watchers before setting text to avoid triggering them
        if (holder.attributeWatcher != null) {
            holder.itemAttributeED.removeTextChangedListener(holder.attributeWatcher);
        }
        if (holder.settingWatcher != null) {
            holder.itemSettingED.removeTextChangedListener(holder.settingWatcher);
        }

        // Set values
        holder.itemAttributeED.setText(parameter.getAttribute());
        holder.itemSettingED.setText(parameter.getSetting());

        int viewType = getItemViewType(position);
        boolean isEditable = (viewType == EDITABLE_TYPE);

        // Adjust UI based on type
        holder.itemAttributeED.setEnabled(isEditable);
        holder.itemSettingED.setEnabled(isEditable || viewType == FIRST_ITEM_TYPE);
        
        if (holder.deleteBtn != null) {
            holder.deleteBtn.setVisibility(isEditable ? View.VISIBLE : View.GONE);
        }

        if (isEditable) {
            // Re-add Attribute Watcher
            holder.attributeWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override public void afterTextChanged(Editable s) {
                    parameter.setAttribute(s.toString());
                    if (listener != null) listener.onParameterUpdated();
                }
            };
            holder.itemAttributeED.addTextChangedListener(holder.attributeWatcher);

            // Delete logic
            if (holder.deleteBtn != null) {
                holder.deleteBtn.setOnClickListener(v -> {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        parameters.remove(pos);
                        notifyItemRemoved(pos);
                        notifyItemRangeChanged(pos, parameters.size());
                        if (listener != null) listener.onParameterUpdated();
                    }
                });
            }
        } else {
            // Ensure delete button is hidden for non-editable items (like the first one)
            if (holder.deleteBtn != null) {
                holder.deleteBtn.setVisibility(View.GONE);
            }
        }

        // Re-add Setting Watcher (always needed if value can change)
        holder.settingWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                parameter.setSetting(s.toString());
                if (listener != null) listener.onParameterUpdated();
            }
        };
        holder.itemSettingED.addTextChangedListener(holder.settingWatcher);
    }

    @Override
    public int getItemCount() {
        return parameters != null ? parameters.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemAttributeED;
        private final TextView itemSettingED;
        private final ImageView deleteBtn;
        TextWatcher attributeWatcher, settingWatcher;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            
            TextView attr = itemView.findViewById(R.id.itemAttributeED);
            if (attr == null) attr = itemView.findViewById(R.id.itemAttribute);
            itemAttributeED = attr;
            
            TextView sett = itemView.findViewById(R.id.itemSettingED);
            if (sett == null) sett = itemView.findViewById(R.id.itemSetting);
            itemSettingED = sett;

            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }
}
