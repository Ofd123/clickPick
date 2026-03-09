package com.example.schoolproj.classes;

import android.os.Build;

import java.time.LocalDateTime;
import java.util.List;

public class SearchDetails
{
    String search_id;
    LocalDateTime search_date;
    LocalDateTime delete_date;
    String search_query;
    List<Product> search_result;
    Boolean compare_price;

    public SearchDetails()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            this.search_date = LocalDateTime.now();
            this.delete_date = LocalDateTime.now().plusWeeks(2); //delete after 2 weeks
        }
    }
    public SearchDetails(String search_query, List<Product> search_result, boolean compare_price)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            this.search_date = LocalDateTime.now();
            this.delete_date = LocalDateTime.now().plusWeeks(2); //delete after 2 weeks
        }
        this.search_query = search_query;
        this.search_result = search_result;
        this.compare_price = compare_price;
    }

    public SearchDetails(String search_query, List<Product> search_result)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            this.search_date = LocalDateTime.now();
            this.delete_date = LocalDateTime.now().plusWeeks(2); //delete after 2 weeks
        }
        this.search_query = search_query;
        this.search_result = search_result;
    }

    public String getSearch_id()
    {
        return search_id;
    }
    public void setSearch_id(String search_id)
    {
        this.search_id = search_id;
    }
    public LocalDateTime getSearch_date()
    {
        return search_date;
    }
    public void setSearch_date(LocalDateTime search_date)
    {
        this.search_date = search_date;
    }
    public LocalDateTime getDelete_date()
    {
        return delete_date;
    }

    public void setDelete_date(LocalDateTime delete_date) {
        this.delete_date = delete_date;
    }

    public String getSearch_query() {
        return search_query;
    }

    public void setSearch_query(String search_query) {
        this.search_query = search_query;
    }

    public List<Product> getSearch_result() {
        return search_result;
    }

    public void setSearch_result(List<Product> search_result) {
        this.search_result = search_result;
    }

    public Boolean getCompare_price() {
        return compare_price;
    }

    public void setCompare_price(Boolean compare_price) {
        this.compare_price = compare_price;
    }
}
