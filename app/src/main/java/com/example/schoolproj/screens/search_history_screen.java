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
import java.util.List;
import java.util.Locale;

/**
 * Activity for displaying the user's personal search history.
 * Fetches search records from Firebase and displays them in a ListView using a CustomAdapter.
 * Allows users to re-view the results of previous searches.
 */
public class search_history_screen extends MasterActivity implements AdapterView.OnItemClickListener
{
    /** ListView for displaying search history items. */
    ListView personalHistory;
    /** Local list of SearchDetails retrieved from Firebase. */
    List<SearchDetails> historyList;
    /** Custom adapter for search history items. */
    CustomAdapter adp;

    /**
     * Called when the activity is starting.
     * Initializes UI components, sets up the adapter, and triggers history loading.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

        personalHistory = findViewById(R.id.personalHistory);
        historyList = new ArrayList<>();

        adp = new CustomAdapter(this, historyList);
        personalHistory.setAdapter(adp);
        personalHistory.setOnItemClickListener(this);

        com.google.firebase.auth.FirebaseUser fbUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser != null)
        {
            loadSearchHistory(fbUser.getUid());
        }
        else if (connected_user != null && connected_user.getUserID() != null && !connected_user.getUserID().isEmpty())
        {
            loadSearchHistory(connected_user.getUserID());
        }
        else
        {
            Toast.makeText(this, "Error: user not logged in", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Attaches a listener to the Firebase 'SearchHistory' node for the specified user.
     * Loads, sorts (latest first), and refreshes the history list.
     * @param userID The unique ID of the user whose history is being loaded.
     */
    private void loadSearchHistory(String userID)
    {
        searchHistoryRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                historyList.clear();
                for (DataSnapshot data : snapshot.getChildren())
                {
                    SearchDetails search = data.getValue(SearchDetails.class);
                    if (search != null)
                    {
                        historyList.add(search);
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
                Toast.makeText(search_history_screen.this, "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Callback for when a history item is clicked.
     * Transitions to the search results screen to show the products from that specific search.
     * @param parent The AdapterView where the click happened.
     * @param view The view within the AdapterView that was clicked.
     * @param position The position of the view in the adapter.
     * @param id The row id of the item that was clicked.
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

    /**
     * UI callback for the home button to close the activity.
     * @param view The view that was clicked.
     */
    public void home(View view)
    {
        finish();
    }

    /**
     * Custom ArrayAdapter for displaying SearchDetails objects.
     * Renders the query string along with the formatted date and search type.
     */
    private class CustomAdapter extends ArrayAdapter<SearchDetails>
    {
        /** Formatter for the search date and time. */
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm • dd/MM/yy", Locale.getDefault());

        /**
         * Constructor for CustomAdapter.
         * @param context Current context.
         * @param objects List of SearchDetails to display.
         */
        public CustomAdapter(@NonNull Context context, @NonNull List<SearchDetails> objects) {
            super(context, 0, objects);
        }

        /**
         * Provides a view for a single item in the list.
         * @param position Position in the list.
         * @param convertView Recycled view if available.
         * @param parent Parent ViewGroup.
         * @return The rendered view for the item.
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
                
                String type = "Keyword"; //at the moment, the user can both compare and search for products depending on his search
//                if (currentSearch.getCompare_price() != null && currentSearch.getCompare_price()) {
//                    type = "Comparison";
//                } else {
//                    type = "Keyword";
//                }
                
                tvSearchDetails.setText(dateStr + " • " + type);
            }

            return convertView;
        }
    }
}
