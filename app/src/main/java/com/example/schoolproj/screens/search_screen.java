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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        Intent dataSent = getIntent();
        //TODO:  work on GUI
        recyclerView = findViewById(R.id.rvListItems);
        recyclerView.setHasFixedSize(true);


        searchParameters = (List<SearchItemParameter>) intent.getSerializableExtra("item details");
        adapter.setParameters(searchParameters);

        adapter = new RecycleViewAdapter(/*this,*/ searchParameters);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



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

    public void search(View view)
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

    public void addItem(View view)
    {
        //first i will try changing the textView to edit text, if it works then i will keep it that way
        //if not, then i will implement a new .xml file and adapter -> 1 for fixed lists and 1 for dynamic lists (custom)

    }
}