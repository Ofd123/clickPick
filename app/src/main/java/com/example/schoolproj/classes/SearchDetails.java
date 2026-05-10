package com.example.schoolproj.classes;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data model representing the details of a search performed by the user.
 * Stores information about the query, results, and timestamps for deletion.
 */
@IgnoreExtraProperties
public class SearchDetails
{
    /** Unique identifier for the search record. */
    private String search_id;
    /** Timestamp when the search was performed. */
    private Long search_date;
//    private Long delete_date;
    /** The search query string entered by the user. */
    private String search_query;
    /** List of products found as a result of the search. */
    private List<Product> search_result;
    /** Boolean flag indicating if this was a price comparison search. */
//    private Boolean compare_price;

    /**
     * Default constructor required for Firebase Realtime Database.
     * Initializes search_date and sets delete_date to 2 weeks from now.
     */
    public SearchDetails()
    {
        // Default constructor required for calls to DataSnapshot.getValue(SearchDetails.class)
        this.search_date = System.currentTimeMillis();
//        this.delete_date = this.search_date + (14L * 24 * 60 * 60 * 1000); // 2 weeks
    }

    /**
     * Constructor for a standard search.
     * @param search_query The query string.
     * @param search_result The list of results.
     */
    public SearchDetails(String search_query, List<Product> search_result)
    {
        this();
        this.search_query = search_query;
        this.search_result = search_result;
    }

    /**
     * Constructor for a search with price comparison option.
     * @param search_query The query string.
     * @param search_result The list of results.
     *
     */
//    public SearchDetails(String search_query, List<Product> search_result, boolean compare_price)
//    {
//        this(search_query, search_result);
////        this.compare_price = compare_price;
//    }

    /** @return The search ID. */
    public String getSearch_id()
    {
        return search_id;
    }

    /** @param search_id The new search ID. */
    public void setSearch_id(String search_id)
    {
        this.search_id = search_id;
    }

    /** @return The search date timestamp. */
    public Long getSearch_date()
    {
        return search_date;
    }

    /** @param search_date The new search date timestamp. */
    public void setSearch_date(Long search_date)
    {
        this.search_date = search_date;
    }
//    public Long getDelete_date()
//    {
//        return delete_date;
//    }

//    public void setDelete_date(Long delete_date) {
//        this.delete_date = delete_date;
//    }

    /** @return The search query string. */
    public String getSearch_query() {
        return search_query;
    }

    /** @param search_query The new search query string. */
    public void setSearch_query(String search_query) {
        this.search_query = search_query;
    }

    /** @return The list of products in the search result. */
    public List<Product> getSearch_result() {
        return search_result;
    }

    /** @param search_result The new list of search results. */
    public void setSearch_result(List<Product> search_result) {
        this.search_result = search_result;
    }

//    public Boolean getCompare_price() {
//        return compare_price;
//    }

//    public void setCompare_price(Boolean compare_price) {
//        this.compare_price = compare_price;
//    }

//    /**
//     * Converts the SearchDetails object into a Map for Firebase updates.
//     * @return A map containing the object fields.
//     */
//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("search_id", search_id);
//        result.put("search_date", search_date);
//        result.put("delete_date", delete_date);
//        result.put("search_query", search_query);
//        result.put("search_result", search_result);
//        result.put("compare_price", compare_price);
//        return result;
//    }
}
