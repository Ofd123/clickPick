package com.example.schoolproj.screens;

import static com.example.schoolproj.FireBaseFiles.FBRef.searchHistoryRef;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.classes.SearchDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class search_history_screen extends MasterActivity {

    ListView personalHistory;
    List<SearchDetails> historyList;
    List<String> searchQueries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_history_screen);

        personalHistory = findViewById(R.id.personalHistory);
        historyList = new ArrayList<>();
        //TODO: later features
        //personalHistory.setOnItemClickListener(this);

        if (connected_user != null)
        {
            loadSearchHistory();
        }
        else
        {
            Toast.makeText(this, "Error: user not logged in", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adp = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, searchQueries);
        personalHistory.setAdapter(adp);
    }

    private void loadSearchHistory()
    {
        String userID = connected_user.getUserID();
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
                        //happened the search object (for re-search and the query itself
                        historyList.add(search);
                        searchQueries.add(search.getSearch_query());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {
                Toast.makeText(search_history_screen.this, "Failed to load history: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    //TODO: add listener to show the results for the clicked search

    public void home(View view)
    {
        finish();
    }
}
