package com.example.userinterface;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AladinApiSevice {
    @GET("ItemSearch.aspx")
    Call<AladinResponse> searchBooks(
            @Query("ttbkey") String ttbKey,
            @Query("Query") String query,
            @Query("QueryType") String queryType,
            @Query("MaxResults") int maxResults,
            @Query("output") String output,
            @Query("Version") String version
    );
}
