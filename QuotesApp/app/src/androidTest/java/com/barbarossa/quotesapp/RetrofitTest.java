package com.barbarossa.quotesapp;

import android.test.AndroidTestCase;
import android.util.Log;

import com.barbarossa.quotesapp.model.EndpointInterface;
import com.barbarossa.quotesapp.model.QuoteResponse;
import com.barbarossa.quotesapp.Utility;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Ioan on 11.05.2016.
 */
public class RetrofitTest extends AndroidTestCase {

    public void testJsonParsing() {
        String exampleJson = "{\"success\":{\"total\":1},\"contents\":{\"quote\":\"We do not remember days, we remember moments.\",\"author\":\"Cesare Pavese\",\"id\":\"PBoILi3Y1CMKlIi4h91k2geF\",\"requested_category\":\"inspirational\",\"categories\":[\"inspirational\",\"memory\",\"thought-provoking\"]}}";

        Gson gson = new GsonBuilder().create();
        QuoteResponse example = gson.fromJson(exampleJson, QuoteResponse.class);

        assertTrue(example.getContents().getQuote().equals("We do not remember days, we remember moments."));
    }

    public void testRetrofitQuery() {
        final String BASE_URL = Utility.BASE_URL;
        final String API_KEY = Utility.API_KEY;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        EndpointInterface apiService = retrofit.create(EndpointInterface.class);
        Call<QuoteResponse> quoteResponseCall = apiService.getQuoteResponse(API_KEY);

        quoteResponseCall.enqueue(new Callback<QuoteResponse>() {
            @Override
            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
                assertTrue(response.isSuccessful());
                assertTrue(response.body().getContents().getQuote() != null);
            }

            @Override
            public void onFailure(Call<QuoteResponse> call, Throwable t) {
                fail();
            }
        });
    }
}
