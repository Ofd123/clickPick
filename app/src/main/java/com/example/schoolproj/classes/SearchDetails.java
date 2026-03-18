package com.example.schoolproj.classes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class SearchDetails
{
    private String search_id;
    private Long search_date;
    private Long delete_date;
    private String search_query;
    private List<Product> search_result;
    private Boolean compare_price;

    public SearchDetails()
    {
        // Default constructor required for calls to DataSnapshot.getValue(SearchDetails.class)
        this.search_date = System.currentTimeMillis();
        this.delete_date = this.search_date + (14L * 24 * 60 * 60 * 1000); // 2 weeks
    }

    public SearchDetails(String search_query, List<Product> search_result)
    {
        this();
        this.search_query = search_query;
        this.search_result = search_result;
    }

    public SearchDetails(String search_query, List<Product> search_result, boolean compare_price)
    {
        this(search_query, search_result);
        this.compare_price = compare_price;
    }

    public String getSearch_id()
    {
        return search_id;
    }
    public void setSearch_id(String search_id)
    {
        this.search_id = search_id;
    }
    public Long getSearch_date()
    {
        return search_date;
    }
    public void setSearch_date(Long search_date)
    {
        this.search_date = search_date;
    }
    public Long getDelete_date()
    {
        return delete_date;
    }

    public void setDelete_date(Long delete_date) {
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

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("search_id", search_id);
        result.put("search_date", search_date);
        result.put("delete_date", delete_date);
        result.put("search_query", search_query);
        result.put("search_result", search_result);
        result.put("compare_price", compare_price);
        return result;
    }
}
