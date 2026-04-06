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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class search_history_screen extends MasterActivity implements AdapterView.OnItemClickListener
{

    ListView personalHistory;
    List<SearchDetails> historyList;
    CustomAdapter adp;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history_screen);

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
                Collections.sort(historyList, (o1, o2) -> {
                    if (o1.getSearch_date() == null || o2.getSearch_date() == null) return 0;
                    return o2.getSearch_date().compareTo(o1.getSearch_date());
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
}
