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

/**
 * Adapter for a RecyclerView that manages a list of SearchItemParameters.
 * Handles different view types for fixed attributes, editable attributes, and the first product name item.
 * Supports real-time text watching and item deletion.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    /** View type for fixed (non-editable) attributes. */
    static final int FIXED_TYPE = 0;
    /** View type for editable attributes. */
    static final int EDITABLE_TYPE = 1;
    /** View type for the first item (Product Name). */
    static final int FIRST_ITEM_TYPE = 2;
    /** The list of search parameters being managed. */
    private List<SearchItemParameter> parameters;
    /** Listener for parameter change events. */
    private OnParameterChangedListener listener;

    /**
     * Interface for observing changes to the search parameters.
     */
    public interface OnParameterChangedListener {
        /** Called whenever an attribute or setting is updated or an item is deleted. */
        void onParameterUpdated();
    }

    /**
     * Constructor for RecycleViewAdapter.
     * @param parameters Initial list of parameters.
     */
    public RecycleViewAdapter(List<SearchItemParameter> parameters) {
        this.parameters = parameters;
    }

    /**
     * Sets the listener for parameter updates.
     * @param listener The listener to attach.
     */
    public void setOnParameterChangedListener(OnParameterChangedListener listener) {
        this.listener = listener;
    }

    /**
     * Returns the current list of parameters.
     * @return List of SearchItemParameter.
     */
    public List<SearchItemParameter> getParameters() {
        return parameters;
    }

    /**
     * Updates the data set and refreshes the UI.
     * @param parameters The new list of parameters.
     */
    public void setParameters(List<SearchItemParameter> parameters) {
        this.parameters = parameters;
        notifyDataSetChanged();
    }

    /**
     * Determines the view type for an item based on its position and editability.
     * @param position Position in the adapter.
     * @return The view type integer.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return FIRST_ITEM_TYPE;
        }
        if (parameters != null && position < parameters.size()) {
            if (parameters.get(position).getEditable()) {
                return EDITABLE_TYPE;
            } else {
                return FIXED_TYPE;
            }
        }
        return FIXED_TYPE;
    }

    /**
     * Creates a new ViewHolder for a specific view type.
     * @param parent The parent ViewGroup.
     * @param viewType The view type constant.
     * @return A new ViewHolder instance.
     */
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

    /**
     * Binds data to a ViewHolder and sets up text watchers and click listeners.
     * Manages removal/re-addition of TextWatchers to prevent infinite loops during recycling.
     * @param holder The ViewHolder to bind data to.
     * @param position The position in the data set.
     */
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
        boolean isEditable;
        if (viewType == EDITABLE_TYPE) {
            isEditable = true;
        } else {
            isEditable = false;
        }

        // Adjust UI based on type
        holder.itemAttributeED.setEnabled(isEditable);
        boolean settingEnabled;
        if (isEditable || viewType == FIRST_ITEM_TYPE) {
            settingEnabled = true;
        } else {
            settingEnabled = false;
        }
        holder.itemSettingED.setEnabled(settingEnabled);
        
        if (holder.deleteBtn != null) {
            int deleteVisibility;
            if (isEditable) {
                deleteVisibility = View.VISIBLE;
            } else {
                deleteVisibility = View.GONE;
            }
            holder.deleteBtn.setVisibility(deleteVisibility);
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
                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            parameters.remove(pos);
                            notifyItemRemoved(pos);
                            notifyItemRangeChanged(pos, parameters.size());
                            if (listener != null) {
                                listener.onParameterUpdated();
                            }
                        }
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

    /**
     * Returns the total number of items in the data set.
     * @return Size of the parameters list.
     */
    @Override
    public int getItemCount() {
        if (parameters != null) {
            return parameters.size();
        } else {
            return 0;
        }
    }

    /**
     * ViewHolder class for SearchItemParameter rows.
     * Holds references to the UI elements and their associated TextWatchers.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /** Editable text for the attribute name (e.g., "Color"). */
        private final TextView itemAttributeED;
        /** Editable text for the attribute setting (e.g., "Red"). */
        private final TextView itemSettingED;
        /** Button to remove the current attribute row. */
        private final ImageView deleteBtn;
        /** Watchers to track changes in real-time. */
        TextWatcher attributeWatcher, settingWatcher;

        /**
         * Constructor for ViewHolder.
         * Maps UI elements from the layout.
         * @param itemView The inflated row view.
         */
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
