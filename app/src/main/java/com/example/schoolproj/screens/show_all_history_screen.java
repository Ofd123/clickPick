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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class show_all_history_screen extends MasterActivity implements AdapterView.OnItemClickListener
{
    ListView History;
    List<SearchDetails> historyList;
    CustomAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_history_screen);

        History = findViewById(R.id.lvSearchHistory);
        historyList = new ArrayList<>();

        adp = new CustomAdapter(this, historyList);
        History.setAdapter(adp);
        History.setOnItemClickListener(this);

        loadAllSearchHistory();
    }

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
                adp.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(show_all_history_screen.this, "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void home(View view)
    {
        finish();
    }

    private class CustomAdapter extends ArrayAdapter<SearchDetails>
    {
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm • dd/MM/yy", Locale.getDefault());

        public CustomAdapter(@NonNull Context context, @NonNull List<SearchDetails> objects) {
            super(context, 0, objects);
        }

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
                String type = (currentSearch.getCompare_price() != null && currentSearch.getCompare_price()) ? "Comparison" : "Keyword";
                tvSearchDetails.setText(dateStr + " • " + type);
            }

            return convertView;
        }
    }

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
