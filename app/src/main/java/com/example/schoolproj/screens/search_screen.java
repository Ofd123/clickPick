package com.example.schoolproj.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolproj.R;
import com.example.schoolproj.RecycleViewAdapter;
import com.example.schoolproj.classes.SearchItemParameter;

import java.util.List;

public class search_screen extends AppCompatActivity
{
    RecyclerView recyclerView;
    RecycleViewAdapter adapter;
    List<SearchItemParameter> searchParameters;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        //TODO:  work on GUI
        recyclerView = findViewById(R.id.rvListItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Intent dataSent = getIntent();
        searchParameters = (List<SearchItemParameter>) dataSent.getSerializableExtra("item details");

        adapter = new RecycleViewAdapter(searchParameters); //initialize the adapter
        recyclerView.setAdapter(adapter); //set the adapter to the recycle view
    }
    void searchWithGemini()
    {

    }
    void multiSearchWithGemini()
    {

    }
    void manualSearch()
    {

    }
    public void addItem(View view)
    {
        SearchItemParameter newItem = new SearchItemParameter("", "", true);
        searchParameters.add(newItem);
        adapter.notifyItemInserted(searchParameters.size() - 1);
        recyclerView.scrollToPosition(searchParameters.size() - 1);
    }

    public void back(View view)
    {
        finish();
    }
    public void searchBtn(View view)
    {
        //TODO: search the item on the webb and then move it to the result screen
        try
        {
            multiSearchWithGemini();
        }
        catch(Exception e)
        {
            manualSearch();
        }
    }
}