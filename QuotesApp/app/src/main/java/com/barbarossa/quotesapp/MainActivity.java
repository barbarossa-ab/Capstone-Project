package com.barbarossa.quotesapp;

import android.content.ContentValues;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.barbarossa.quotesapp.data.QuotesContract;
import com.barbarossa.quotesapp.model.EndpointInterface;
import com.barbarossa.quotesapp.model.QuoteResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        final String BASE_URL = "http://quotes.rest/";
//        final String API_KEY = "1h52AU4uTBa5GuMzXJMJugeF";
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        EndpointInterface apiService = retrofit.create(EndpointInterface.class);
//        Call<QuoteResponse> quoteResponseCall = apiService.getQuoteResponse(API_KEY);
//
//        quoteResponseCall.enqueue(new Callback<QuoteResponse>() {
//            @Override
//            public void onResponse(Call<QuoteResponse> call, Response<QuoteResponse> response) {
//                if(response.isSuccessful()) {
//                    Log.e("retrofit-test", "A mers");
//                    Log.e("retrofit-test", response.body().getContents().getQuote());
//                } else {
//                    Log.e("retrofit-test","Nu a mers");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<QuoteResponse> call, Throwable t) {
//                Log.e("retrofit-test","muie");
//            }
//        });





//        getContentResolver()
    }
}
