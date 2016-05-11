package com.barbarossa.quotesapp.model;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;

/**
 * Created by Ioan on 11.05.2016.
 */

public interface EndpointInterface {

    @GET("/quote.json")
    Call<QuoteResponse> getQuoteResponse(@Header(Utility.API_KEY_HEADER) String apiKey);


}
