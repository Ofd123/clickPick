package com.example.schoolproj.screens;

import static com.example.schoolproj.FireBaseFiles.FBRef.searchHistoryRef;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.classes.Product;
import com.example.schoolproj.classes.SearchDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Activity for displaying a unified view of all unique searches in the application.
 * Aggregates search history across all users from Firebase and displays it in a ListView.
 * Filters duplicate queries and sorts them by date.
 */
public class show_all_history_screen extends MasterActivity implements AdapterView.OnItemClickListener
{
    /** ListView for displaying the aggregated search history. */
    ListView History;
    /** Local list of unique SearchDetails aggregated from all users. */
    List<SearchDetails> historyList;
    /** Custom adapter for search history items. */
    CustomAdapter adp;

    /**
     * Called when the activity is starting.
     * Initializes UI components and triggers the aggregator logic.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_history_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

        History = findViewById(R.id.lvSearchHistory);
        historyList = new ArrayList<>();

        adp = new CustomAdapter(this, historyList);
        History.setAdapter(adp);
        History.setOnItemClickListener(this);

        loadAllSearchHistory();
    }

    /**
     * Queries the entire 'SearchHistory' node from Firebase.
     * Iterates through all users, collects unique search queries, and sorts them latest-first.
     */
    private void loadAllSearchHistory()
    {
        searchHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                Set<String> uniqueQueries = new HashSet<>();
                historyList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren())
                {
                    for (DataSnapshot searchSnapshot : userSnapshot.getChildren())
                    {
                        SearchDetails search = searchSnapshot.getValue(SearchDetails.class);
                        if (search != null && search.getSearch_query() != null)
                        {
                            if (!uniqueQueries.contains(search.getSearch_query())) {
                                uniqueQueries.add(search.getSearch_query());
                                historyList.add(search);
                            }
                        }
                    }
                }

                // Sort by date descending (latest first)
                Collections.sort(historyList, new java.util.Comparator<SearchDetails>() {
                    @Override
                    public int compare(SearchDetails o1, SearchDetails o2) {
                        if (o1.getSearch_date() == null || o2.getSearch_date() == null) {
                            return 0;
                        }
                        return o2.getSearch_date().compareTo(o1.getSearch_date());
                    }
                });

                adp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(show_all_history_screen.this, "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * UI callback for the home button to close the activity.
     * @param view The view that was clicked.
     */
    public void home(View view)
    {
        finish();
    }

    /**
     * Custom ArrayAdapter for displaying search records from multiple users.
     */
    private class CustomAdapter extends ArrayAdapter<SearchDetails>
    {
        /** Formatter for displaying the search date. */
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm • dd/MM/yy", Locale.getDefault());

        /**
         * Constructor for CustomAdapter.
         * @param context Current context.
         * @param objects List of search records to display.
         */
        public CustomAdapter(@NonNull Context context, @NonNull List<SearchDetails> objects) {
            super(context, 0, objects);
        }

        /**
         * Renders the view for a search history item.
         * @param position Position in list.
         * @param convertView Recycled view.
         * @param parent Parent ViewGroup.
         * @return The rendered view.
         */
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_search_history, parent, false);
            }

            SearchDetails currentSearch = getItem(position);

            TextView tvSearchQuery = convertView.findViewById(R.id.tvSearchQuery);
            TextView tvSearchDetails = convertView.findViewById(R.id.tvSearchDetails);

            if (currentSearch != null)
            {
                tvSearchQuery.setText(currentSearch.getSearch_query());
                
                String dateStr = dateFormat.format(new Date(currentSearch.getSearch_date()));
                
                String type;
                if (currentSearch.getCompare_price() != null && currentSearch.getCompare_price()) {
                    type = "Comparison";
                } else {
                    type = "Keyword";
                }
                
                tvSearchDetails.setText(dateStr + " • " + type);
            }

            return convertView;
        }
    }

    /**
     * Callback for when an aggregated history item is clicked.
     * Displays the results associated with that particular search.
     * @param parent The AdapterView.
     * @param view The clicked view.
     * @param position Position in list.
     * @param id Row ID.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        SearchDetails selectedSearch = historyList.get(position);
        List<Product> searchResults = selectedSearch.getSearch_result();
        
        if (searchResults != null) {
            Intent intent = new Intent(this, search_result_screen.class);
            intent.putExtra("results", (ArrayList<Product>) searchResults);
            startActivity(intent);
        } else {
            Toast.makeText(this, "No results found for this search", Toast.LENGTH_SHORT).show();
        }
    }
}
