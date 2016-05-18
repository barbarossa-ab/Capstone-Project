package com.barbarossa.quotesapp.model;

import com.barbarossa.quotesapp.Utility;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Ioan on 11.05.2016.
 */

public interface EndpointInterface {

    @GET("/quote.json")
    Call<QuoteResponse> getQuoteResponse(@Header(Utility.API_KEY_HEADER) String apiKey);

    @GET("/quote.json")
    Call<QuoteResponse> getQuoteForCategoryResponse(@Header(Utility.API_KEY_HEADER) String apiKey, @Query("category") String category);

}
